/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyphermessenger.utils;

import com.cyphermessenger.crypto.SCrypt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paolo
 */
public class Utils {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final SecureRandom RANDOM = new SecureRandom();
    public static final BaseEncoding BASE32 = BaseEncoding.base32Hex().lowerCase().omitPadding();
    public static final BaseEncoding BASE64_URL = BaseEncoding.base64Url().omitPadding();

    public static final int SESSION_ID_LEN = 32;
    public static final int HASH_LEN = 32;
    public static final int SALT_LEN = 8;

    public static byte[] sha256(byte[] s) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(s);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static byte[] sha256(String s) {
        return sha256(s.getBytes());
    }

    public static byte[] scrypt_1024_4_2(byte[] p, byte[] s) {
        try {
            return SCrypt.scrypt(p, s, 1024, 4, 2, HASH_LEN);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static byte[] cryptPassword(byte[] b, String username) {
        byte[] usernameBytes = username.getBytes();
        byte[] salt = new byte[SALT_LEN];
        int i;
        for(i = 0; i < SALT_LEN && i < usernameBytes.length; i++) {
            salt[i] = usernameBytes[i];
        }
        for(; i < SALT_LEN; i++) {
            salt[i] = 0;
        }
        RANDOM.nextBytes(salt);
        byte[] hash = scrypt_1024_4_2(b, salt);
        byte[] ret = new byte[HASH_LEN + SALT_LEN];
        System.arraycopy(hash, 0, ret, 0, HASH_LEN);
        System.arraycopy(salt, 0, ret, HASH_LEN, SALT_LEN);
        return ret;
    }

    public static boolean checkPassword(byte[] pass, byte[] hash) {
        byte[] salt = new byte[SALT_LEN];
        System.arraycopy(hash, HASH_LEN, salt, 0, SALT_LEN);
        byte[] hash1 = scrypt_1024_4_2(pass, salt);
        for (int i = 0; i < HASH_LEN; i++) {
            if (hash[i] != hash1[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static void main(String args[]) {
        byte[] pass = {1, 2, 3,4 ,5 ,6 ,7, 8, 9};
        String username = "maff";
        byte[] hash = cryptPassword(pass, username);
        if(checkPassword(pass, hash)) {
            System.out.println("OK");
        }
    }
}
