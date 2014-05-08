/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyphermessenger.crypto;

/**
 *
 * @author halfblood
 */

import com.cyphermessenger.utils.Utils;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import java.security.SecureRandom;

public class Encrypt extends Encryption {

    /**
     * @param key Encryption key
     */
    public Encrypt(byte[] key) {
        iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        KeyParameter keyParam = new KeyParameter(key);
        AEADParameters param = new AEADParameters(keyParam, TAG_LENGTH * 8, iv);
        gcm = new GCMBlockCipher(new AESEngine());
        gcm.init(true, param);
    }

    public Encrypt(String key) {
        this(key.getBytes());
    }

    /**
     * Convenience method to encrypt text in one step.
     * This method requires the key to be 32 bytes long.
     *
     * @param key  Encryption key
     * @param text Plaintext to be encrypted
     * @param aad  Additional authenticated data
     * @return Returns the ciphertext
     */
    public static byte[] process(byte[] key, byte[] text, byte[]... aad) throws InvalidCipherTextException {
        return oneStepDecryption(key, text, aad);
    }

    /**
     * Convenience method to encrypt text in one step.
     * This method should be used if the key is a string provided directly from the user.
     *
     * @param key  Encryption plain password
     * @param text Plaintext to be encrypted
     * @param aad  Additional authenticated data
     * @return Returns the ciphertext
     */
    public static byte[] process(String key, byte[] text, byte[]... aad) throws InvalidCipherTextException {
        return oneStepDecryption(Utils.sha256(key), text, aad);
    }

    private static byte[] oneStepDecryption(byte[] key, byte[] text, byte[] aad[]) throws InvalidCipherTextException {
        Encrypt encrypt = new Encrypt(key);
        for (int i = 0; i < aad.length; i++) {
            encrypt.updateAuthenticatedData(aad[i]);
        }
        return encrypt.process(text);
    }

    @Override
    public byte[] process(byte[] in) throws InvalidCipherTextException {
        byte[] cypherText = super.process(in);
        byte[] ret = new byte[IV_LENGTH + cypherText.length];
        System.arraycopy(iv, 0, ret, 0, IV_LENGTH);
        System.arraycopy(cypherText, 0, ret, IV_LENGTH, cypherText.length);
        return ret;
    }

}
