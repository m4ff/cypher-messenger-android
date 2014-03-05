package com.cyphermessenger;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.GCMBlockCipher;

public abstract class Encryption {
	
	protected static int TAG_LENGTH = 16;
	protected static int IV_LENGTH = 12;
	protected static int KEY_LENGTH = 32;
	
	protected byte[] iv;
	protected GCMBlockCipher gcm;
	
	/**
	 * Provide additional authenticated data (AAD). This function can be called multiple times.
	 * @param aad Buffer containing optional AAD
	 * @param offset Offset where to start reading
	 * @param len Number of bytes to read
	 */
	public void updateAuthenticatedData(byte[] aad, int offset, int len) {
		gcm.processAADBytes(aad, offset, len);
	}
	
	/**
	 * @see #updateAuthenticatedData(byte[], int, int)
	 * @param aad
	 */
	public void updateAuthenticatedData(byte[] aad) {
		updateAuthenticatedData(aad, 0, aad.length);
	}
	
	/**
	 * @param len Length of the input buffer
	 * @return Size of the output buffer
	 */
	public int getUpdateOutputSize(int len) {
		return gcm.getOutputSize(len);
	}
	
	/**
	 * Encrypt or decrypt data from in to out. You can call this method multiple times.
	 * @param in Input buffer
	 * @param inOffset Input buffer offset
	 * @param inLen Number of bytes to process
	 * @param out Output buffer
	 * @param outOffset Offset where to start writing
	 * @return Number of bytes written to out
	 */
	public int update(byte[] in, int inOffset, int inLen, byte[] out, int outOffset) {
		return gcm.processBytes(in, inOffset, inLen, out, outOffset);
	}
	
	/**
	 * Finalizes encryption or verifies decryption. Call only once
	 * @param out
	 * @param outOffset
	 * @return Number of bytes written to out
	 * @throws IllegalStateException
	 * @throws InvalidCipherTextException
	 */
	public int doFinal(byte[] out, int outOffset) throws IllegalStateException, InvalidCipherTextException {
		return gcm.doFinal(out, outOffset);
	}
	
	/**
	 * Encrypts or decrypts bytes in one step
	 * @param in
	 * @return Buffer containing encrypted or decrypted data
	 * @throws IllegalStateException
	 * @throws InvalidCipherTextException
	 */
	public byte[] process(byte[] in) throws IllegalStateException, InvalidCipherTextException {
		byte[] out = new byte[getUpdateOutputSize(in.length)];
		int offset = update(in, 0, in.length, out, 0);
		offset += doFinal(out, offset);
		return out;
	}
	
	/**
	 * Get the Initialization Vector (IV) used by the encryption process
	 * @return
	 */
	public byte[] getIV() {
		return iv;
	}
 }
