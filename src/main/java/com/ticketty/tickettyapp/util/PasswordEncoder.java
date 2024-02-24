package com.ticketty.tickettyapp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Component
public class PasswordEncoder {

    @Value("${encryption.iterations}")
    private int iterations;

    @Value("${encryption.keyLength}")
    private int keyLength;

    @Value("${encryption.algorithm}")
    private String algorithm;

    @Value("${encryption.digestAlgorithm}")
    private String digestAlgorithm;

    @Value("${encryption.charset}")
    private String charset;

    public String encrypt(String email, String password) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), getSalt(email), iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);

            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException |
                 InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getSalt(String email)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest digest = MessageDigest.getInstance(digestAlgorithm);
        byte[] keyBytes = email.getBytes(charset);

        return digest.digest(keyBytes);
    }
    
    // 저장된 암호화된 비밀번호와 사용자가 제출한 비밀번호를 비교하는 메서드
    public boolean matches(String email, String password, String encryptedPassword) {
        String encryptedSubmittedPassword = encrypt(email, password);
        return encryptedSubmittedPassword.equals(encryptedPassword);
    }
}