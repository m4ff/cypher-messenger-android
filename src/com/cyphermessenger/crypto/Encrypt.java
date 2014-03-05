package com.cyphermessenger;

import java.security.SecureRandom;

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

}
