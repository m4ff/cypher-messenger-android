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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.cyphermessenger.utils.Utils;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.BasicAgreement;
import org.spongycastle.crypto.agreement.ECDHBasicAgreement;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.BigIntegers;

public class ECKey {

    private byte[] priv;
    private byte[] pub;
    private long time;

    private static HashMap<byte[], HashMap<byte[], byte[]>> secretCache = new HashMap<>();

    private final ECPrivateKeyParameters privParams;
    private final ECPublicKeyParameters pubParams;

    private final static X9ECParameters params = SECNamedCurves.getByName("secp256k1");
    private final static ECDomainParameters domainParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());

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
     * Builds the key pair from its encoded representation. At least one key
     * MUST be provided. If publicBytes is null the public key will be computed,
     * this is a very expansive operation.
     *
     * @param publicBytes Encoded public key
     * @param privateBytes Encoded private key
     */
    public ECKey(byte[] publicBytes, byte[] privateBytes) {
        BigInteger key = null;
        ECPoint pkey = null;
        if (privateBytes != null) {
            key = BigIntegers.fromUnsignedByteArray(privateBytes);
        }
        if (publicBytes != null) {
            pkey = domainParams.getCurve().decodePoint(publicBytes);
        } else if (key != null) {
            pkey = domainParams.getG().multiply(key);
        } else {
            throw new RuntimeException("No public key provided");
        }
        if(key != null) {
            privParams = new ECPrivateKeyParameters(key, domainParams);
        } else {
            privParams = null;
        }
        pubParams = new ECPublicKeyParameters(pkey, domainParams);
    }

    public ECKey(byte[] pubBytes, byte[] privBytes, long time) {
        this(pubBytes, privBytes);
        this.time = time;
    }

    /**
     * Returns the encoded public key
     *
     * @return
     */
    public byte[] getPublicKey() {
        if (pub == null) {
            pub = compressPoint(pubParams.getQ()).getEncoded();
        }
        return pub;
    }

    /**
     * Returns the encoded private key
     *
     * @return
     */
    public byte[] getPrivateKey() {
        if (priv == null && privParams != null) {
            priv = BigIntegers.asUnsignedByteArray(privParams.getD());
        }
        return priv;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public long getTime() { return time; }

    /**
     * Return the SHA-256 digest of the shared secret
     *
     * @param peer
     * @return
     */
    public byte[] getSharedSecret(ECKey peer) {
        HashMap<byte[], byte[]> secrets = secretCache.get(getPublicKey());
        if(secrets == null) {
            secrets = new HashMap<>();
            secretCache.put(pub, secrets);
        }
        byte[] secret = secrets.get(peer.getPublicKey());
        if(secret == null) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            BasicAgreement agr = new ECDHBasicAgreement();
            agr.init(privParams);
            secret = BigIntegers.asUnsignedByteArray(agr.calculateAgreement(peer.pubParams));
            secret = digest.digest(secret);
            secrets.put(peer.pub, secret);
        }
        return secret;
    }

    @SuppressWarnings("deprecation")
    private static ECPoint compressPoint(ECPoint uncompressed) {
        return new ECPoint.Fp(domainParams.getCurve(), uncompressed.getX(), uncompressed.getY(), true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ECKey ecKey = (ECKey) o;

        if (!Arrays.equals(getPublicKey(), ecKey.getPublicKey())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getPublicKey());
    }

    @Override
    public String toString() {
        return Utils.BASE32.encode(getPublicKey());
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

    }
}
