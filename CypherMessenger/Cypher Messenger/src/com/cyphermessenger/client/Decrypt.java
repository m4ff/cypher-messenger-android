/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyphermessenger.client;

import com.cyphermessenger.utils.Utils;
import java.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 *
 * @author halfblood
 */

public class Decrypt extends Encryption {

    /**
     *
     * @param key Decryption key
     * @param iv Initialization Vector (IV) used for the encryption
     */
    public Decrypt(byte[] key, byte[] iv) {
        KeyParameter keyParam = new KeyParameter(key);
        AEADParameters param = new AEADParameters(keyParam, TAG_LENGTH * 8, iv);
        gcm = new GCMBlockCipher(new AESEngine());
        gcm.init(false, param);
    }
    
    public static byte[] process(String key, byte[] cypherText) throws IllegalStateException, InvalidCipherTextException {
        byte[] iv = Arrays.copyOf(cypherText, IV_LENGTH);
        byte[] onlyCypherText = Arrays.copyOfRange(cypherText, IV_LENGTH, cypherText.length);
        Decrypt decrypt = new Decrypt(Utils.sha256(key), iv);
        return decrypt.process(onlyCypherText);
    }

}
