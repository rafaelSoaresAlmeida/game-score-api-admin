package com.gamescore.gamescoreapiadmin.configuration;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Component
@SuppressWarnings({"PMD.UnusedPrivateMethod"})
public class AESEncoder implements PasswordEncoder {

    @Value("${springbootwebfluxjjwt.password.encoder.secret}")
    private String PWD_KEY;

    @Value("${springbootwebfluxjjwt.password.encoder.keylength}")
    private int KEY_SIZE;

    @Value("${springbootwebfluxjjwt.password.encoder.iteration}")
    private int ITERATION_COUNT;

    @Value("${springbootwebfluxjjwt.password.encoder.IV}")
    private String IV;

    @Value("${springbootwebfluxjjwt.password.encoder.salt}")
    private String SALT;

    private final Cipher cipher;

    {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encode(CharSequence cs) {
        try {
            SecretKey key = generateKey(SALT, PWD_KEY);
            byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, IV, cs.toString().getBytes("UTF-8"));
            return base64(encrypted);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String decode(final String text) {
        try {
            SecretKey key = generateKey(SALT, PWD_KEY);
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, IV, base64(text));
            return new String(decrypted, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean matches(CharSequence cs, String string) {
        return encode(cs).equals(string);
    }

    private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
        try {
            cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
            return cipher.doFinal(bytes);
        } catch (InvalidKeyException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt), ITERATION_COUNT, KEY_SIZE);
            SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return key;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private String base64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    private byte[] base64(String str) {
        return Base64.decodeBase64(str);
    }

    private String hex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    private byte[] hex(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        } catch (DecoderException e) {
            throw new IllegalStateException(e);
        }
    }

}