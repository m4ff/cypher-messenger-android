/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyphermessenger.crypto;

import com.cyphermessenger.utils.Utils;

import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;


import java.util.Arrays;

/**
 * @author halfblood
 */

public class Decrypt extends Encryption {

    /**
     * @param key Decryption key
     * @param iv  Initialization Vector (IV) used for the encryption
     */
    public Decrypt(byte[] key, byte[] iv) {
        KeyParameter keyParam = new KeyParameter(key);
        AEADParameters param = new AEADParameters(keyParam, TAG_LENGTH * 8, iv);
        gcm = new GCMBlockCipher(new AESEngine());
        gcm.init(false, param);
    }

    /**
     * Convenience method to decrypt data.
     *
     * @param key        Encryption plain password
     * @param cipherText Ciphertext
     * @param aad        Additional authenticated data
     * @return Original plaintext
     * @throws InvalidCipherTextException
     */
    public static byte[] process(String key, byte[] cipherText, byte[]... aad) throws InvalidCipherTextException {
        return oneStepDecryption(Utils.sha256(key), cipherText, aad);
    }

    /**
     * Convenience method to decrypt data.
     *
     * @param key        Encryption key
     * @param cipherText Ciphertext
     * @param aad        Additional authenticated data
     * @return Original plaintext
     * @throws InvalidCipherTextException
     */
    public static byte[] process(byte[] key, byte[] cipherText, byte[]... aad) throws InvalidCipherTextException {
        return oneStepDecryption(key, cipherText, aad);
    }

    private static byte[] oneStepDecryption(byte[] key, byte[] cipherText, byte[] aad[]) throws InvalidCipherTextException {
        byte[] iv = Arrays.copyOf(cipherText, IV_LENGTH);
        byte[] onlyCypherText = Arrays.copyOfRange(cipherText, IV_LENGTH, cipherText.length);
        Decrypt decrypt = new Decrypt(key, iv);
        for (int i = 0; i < aad.length; i++) {
            decrypt.updateAuthenticatedData(aad[i]);
        }
        return decrypt.process(onlyCypherText);
    }

}
