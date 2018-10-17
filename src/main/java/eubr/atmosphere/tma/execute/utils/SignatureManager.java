package eubr.atmosphere.tma.execute.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class SignatureManager {
    //Method for signature verification that initializes with the Public Key,
    //updates the data to be verified and then verifies them using the signature
    public static boolean verifySignature(byte[] data, byte[] signature, PublicKey key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance(KeyManager.SIGNATURE_ALGORITHM);
        sig.initVerify(key);
        sig.update(data);

        return sig.verify(signature);
    }
}
