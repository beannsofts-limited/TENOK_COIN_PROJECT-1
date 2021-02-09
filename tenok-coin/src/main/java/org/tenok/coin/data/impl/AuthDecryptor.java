package org.tenok.coin.data.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * API Key, Secret Key 복호화 클래스
 */
public class AuthDecryptor {
    private static Logger logger = Logger.getLogger(AuthDecryptor.class);
    private String apiKeyEncrypted;
    private String secretKeyEncrypted;
    private String validationEncrypted;
    private String slackWebhookURLEncrypted;
    private String pw = null;

    private AuthDecryptor(File... authFile) {
        try {
            File authExistFile = null;
            for (File file : authFile) {
                if (file.getCanonicalFile().exists()) {
                    authExistFile = file;
                    logger.info(String.format("auth file found in path: %s", authExistFile.getCanonicalPath()));
                    break;
                }
            }
            if (authExistFile == null) {
                throw new FileNotFoundException("Auth Secret File 못 찾겠음.");
            }
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(authExistFile.getCanonicalFile()));
            this.apiKeyEncrypted = (String) jsonObject.get("apiKey");
            this.secretKeyEncrypted = (String) jsonObject.get("secretKey");
            this.validationEncrypted = (String) jsonObject.get("validation");
            this.slackWebhookURLEncrypted = (String) jsonObject.get("slackWebhookURL");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Secret Key File Path 체크 요망", e);
        }
    }

    private static class AuthHolder {
        public static final AuthDecryptor INSTANCE = new AuthDecryptor(new File("./../secret.auth"),
                new File("./secret.auth")); // 상대주소 입력
    }

    public static AuthDecryptor getInstance() {
        return AuthHolder.INSTANCE;
    }

    public void setPassword(String password) {
        this.pw = password;
    }

    private String getApiKey(String password) {
        try {
            return decrypt(this.apiKeyEncrypted, password);
        } catch (FailedLoginException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    private String getApiSecretKey(String password) {
        try {
            return decrypt(this.secretKeyEncrypted, password);
        } catch (FailedLoginException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    public String decrypt(String cipherText, String password) throws FailedLoginException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(md5.digest(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(md5.digest());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new FailedLoginException("복호화 실패");
    }

    public String encrypt(String plainText, String password) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(md5.digest(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(md5.digest());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("암호화 실패");
    }

    /**
     * API Key 리턴
     * 
     * @return API Key in String
     */
    public String getApiKey() {
        return getApiKey(pw);
    }

    /**
     * Bybit Signature
     * 
     * @return Bybit Signature
     */
    public String generateSignature(long expires) {
        return sha256HMAC("GET/realtime" + expires, getApiSecretKey(pw));
    }

    /**
     * rest에서 query parameter를 토대로 암호키 생성
     * 
     * @param param query parameter
     * @return signature
     */
    public String generateSignature(Map<String, Object> param) {
        StringBuilder sb = new StringBuilder();
        param.entrySet().stream().sorted((ent1, ent2) -> ent1.getKey().compareTo(ent2.getKey())).map(ent -> {
            if (ent.getValue() instanceof Boolean) {
                return String.format("%s=%b&", ent.getKey(), (boolean) ent.getValue());
            } else {
                return String.format("%s=%s&", ent.getKey(), ent.getValue());
            }
        }).forEachOrdered(sb::append);
        return sha256HMAC(sb.deleteCharAt(sb.length() - 1).toString(), getApiSecretKey(pw));
    }

    /**
     * auth 만기일
     * 
     * @return 현재시간 + 1000 [ms]
     */
    public long generateExpire() {
        return System.currentTimeMillis() + 1000L;
    }

    /**
     * 비밀번호 로그인 성공 여부 리턴
     * 
     * @return 로그인 성공여부
     */
    public boolean validate() {
        if (this.pw == null) {
            throw new RuntimeException("비밀번호 set 요망");
        }
        boolean success = false;
        try {
            success = decrypt(validationEncrypted, this.pw).equals("success");
        } catch(LoginException e) {
            return false;
        }
        return success;
    }

    /**
     * slack webhook 전용 url
     * 
     * @return slack webhook url in String
     */
    public String getSlackWebhookURL() {
        try {
            return decrypt(this.slackWebhookURLEncrypted, pw);
        } catch (FailedLoginException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    private String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    private String sha256HMAC(String message, String secret) {
        String hash = "";
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256HMAC.init(secretKey);
            byte[] bytes = sha256HMAC.doFinal(message.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ===========" + e.getMessage());
        }
        return hash;

    }

}
