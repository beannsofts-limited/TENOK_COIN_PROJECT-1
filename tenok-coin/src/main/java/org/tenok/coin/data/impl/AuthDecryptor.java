package org.tenok.coin.data.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * API Key, Secret Key 복호화 클래스
 */
public class AuthDecryptor {
    private String apiKeyEncrypted;
    private String secretKeyEncrypted;

    /**
     * AuthDecryptor
     * 
     * @param authFile 암호키 파일
     * @throws FileNotFoundException    암호키 파일 Not Found
     * @throws IllegalArgumentException IV String ascii 16바이트 불충족
     */
    public AuthDecryptor(File authFile) throws FileNotFoundException {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(authFile));
            this.apiKeyEncrypted = (String) jsonObject.get("apiKey");
            this.secretKeyEncrypted = (String) jsonObject.get("secretKey");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public String getApiKey(String password) {
        return decrypt(this.apiKeyEncrypted, password);
    }

    public String getApiSecretKey(String password) {
        return decrypt(this.secretKeyEncrypted, password);
    }

    public static String decrypt(String cipherText, String password) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes("UTF-8"));
            SecretKeySpec keySpec = new SecretKeySpec(md5.digest(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(md5.digest());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("복호화 실패");
    }
    
}
