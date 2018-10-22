package eubr.atmosphere.tma.actuator.crypto;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class KeyManager {

    public static final String ALGORITHM = "RSA";
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    public static byte[] decrypt(byte[] text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dectyptedText;
    }

    public static byte[] encrypt(byte[] text, PublicKey key) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public static PrivateKey getPrivateKey(String filenamePrivKey) {
        try {
            File privKeyFile = new File(filenamePrivKey);

            // read private key DER file
            DataInputStream dis = new DataInputStream(new FileInputStream(privKeyFile));
            byte[] privKeyBytes = new byte[(int)privKeyFile.length()];
            dis.read(privKeyBytes);
            dis.close();

            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

            // decode private key
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privKeyBytes);
            RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);

            return privKey;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static PublicKey getPublicKey(String filenamePubKey) {
        try {
            File pubKeyFile = new File(filenamePubKey);

            DataInputStream dis = new DataInputStream(new FileInputStream(pubKeyFile));
            byte[] pubKeyBytes = new byte[(int)pubKeyFile.length()];
            dis.readFully(pubKeyBytes);
            dis.close();

            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

            // decode public key
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubKeyBytes);
            RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);

            return pubKey;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    //The method that signs the data using the private key that is stored in keyFile path
    public static byte[] sign(String data, PrivateKey keyFile)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature privateSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
        privateSignature.initSign(keyFile);
        privateSignature.update(data.getBytes(UTF_8));

        byte[] signature = privateSignature.sign();
        return signature;
    }

    public static boolean verify(String plainText, String signature, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature publicSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return publicSignature.verify(signatureBytes);
    }
}
