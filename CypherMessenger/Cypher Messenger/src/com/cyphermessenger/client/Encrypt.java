/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cyphermessenger.client;

/**
 *
 * @author halfblood
 */
import com.cyphermessenger.utils.Utils;
import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class Encrypt extends Encryption {

    /**
     *
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
        iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        byte[] keyBytes = Utils.sha256(key);
        KeyParameter keyParam = new KeyParameter(keyBytes);
        AEADParameters param = new AEADParameters(keyParam, TAG_LENGTH * 8, iv);
        gcm = new GCMBlockCipher(new AESEngine());
        gcm.init(true, param);
    }
    
    @Override
    public byte[] process(byte[] in) throws IllegalStateException, InvalidCipherTextException {
        byte[] cypherText = super.process(in);
        byte[] ret = new byte[IV_LENGTH + cypherText.length];
        System.arraycopy(iv, 0, ret, 0, IV_LENGTH);
        System.arraycopy(cypherText, 0, ret, IV_LENGTH, cypherText.length);
        return ret;
    }

}
