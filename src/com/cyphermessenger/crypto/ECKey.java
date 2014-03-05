package com.cyphermessenger.crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

public class ECKey {
	
	private byte[] priv;
	private byte[] pub;
	
	private ECPrivateKeyParameters privParams;
	private ECPublicKeyParameters pubParams;

    private static X9ECParameters params = SECNamedCurves.getByName("secp256k1");
    private static ECDomainParameters domainParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
	
    /**
     * Generates a new key pair
     */
	public ECKey() {
		ECKeyPairGenerator generator = new ECKeyPairGenerator();
		ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(domainParams, new SecureRandom());
		generator.init(keygenParams);
        AsymmetricCipherKeyPair keypair = generator.generateKeyPair();
        privParams = (ECPrivateKeyParameters) keypair.getPrivate();
        pubParams = (ECPublicKeyParameters) keypair.getPublic();    
	}
	
	/**
	 * Builds the key pair from its encoded representation.
	 * At least one key MUST be provided.
	 * If publicBytes is null the public key will be computed, this is a very expansive operation.
	 * 
	 * @param publicBytes Encoded public key
	 * @param privateBytes Encoded private key
	 * @throws Exception 
	 */
	public ECKey(byte[] publicBytes, byte[] privateBytes) throws Exception {
		
		BigInteger key = null;
		ECPoint pkey = null;
		if(privateBytes != null)
			key = BigIntegers.fromUnsignedByteArray(privateBytes);
		if(publicBytes != null)
			pkey = domainParams.getCurve().decodePoint(publicBytes);
		else if(key != null)
			pkey = domainParams.getG().multiply(key);
		else
			throw new Exception("No public key provided");
		privParams = new ECPrivateKeyParameters(key, domainParams);
		pubParams= new ECPublicKeyParameters(pkey, domainParams);
	}
	
	/**
	 * Returns the encoded public key
	 * @return
	 */
	public byte[] getPublicKey() {
		if(pub == null) {
			pub = compressPoint(pubParams.getQ()).getEncoded();
		}
		return pub;
	}
	
	/**
	 * Returns the encoded private key
	 * @return
	 */
	public byte[] getPrivateKey() {
		if(priv == null) {
			priv = BigIntegers.asUnsignedByteArray(privParams.getD());
		}
		return priv;
	}
	
	/**
	 * Return the SHA-256 digest of the shared secret
	 * @param peer
	 * @return
	 */
	public byte[] getSharedSecret(ECKey peer) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BasicAgreement agr = new ECDHBasicAgreement();
		agr.init(privParams);
		byte[] secret = BigIntegers.asUnsignedByteArray(agr.calculateAgreement(peer.pubParams));
		return digest.digest(secret);
	}
	
	@SuppressWarnings("deprecation")
	private static ECPoint compressPoint(ECPoint uncompressed) {
        return new ECPoint.Fp(domainParams.getCurve(), uncompressed.getX(), uncompressed.getY(), true);
    }
}
