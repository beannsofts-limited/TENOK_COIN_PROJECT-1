package org.tenok.coin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONObject;

public class AuthIndex {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("welcome to auth index");
        System.out.printf("please enter password%n> ");
        String password = scan.nextLine();

        System.out.printf("%nplease enter bybit api key%n> ");
        String apiKey = scan.nextLine();

        System.out.printf("%nplease enter bybit secret key%n> ");
        String secretKey = scan.nextLine();

        System.out.printf("%nplease enter slack url%n> ");
        String slackUrl = scan.nextLine();

        File outFile = new File("." + File.separator + "secret.auth");

        outFile.createNewFile();

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("apiKey", encrypt(apiKey, password));
        jsonObj.put("secretKey", encrypt(secretKey, password));
        jsonObj.put("slackWebhookURL", encrypt(slackUrl, password));
        jsonObj.put("validation", encrypt("success", password));
        try(var ops = new FileOutputStream(outFile)) {
            ops.write(jsonObj.toJSONString().getBytes());
        } catch(Exception e) {
            e.printStackTrace();
        }
        scan.close();
    }

    public static String encrypt(String plainText, String password) {
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
}
