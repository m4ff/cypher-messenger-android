package com.cyphermessenger.crypto;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

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

}
