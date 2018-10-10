package eubr.atmosphere.tma.actuator.crypto;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricKeyManager {
    private SecretKeySpec secretKey;
    private Cipher cipher;

    public SymmetricKeyManager(String secret, int length, String algorithm)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] key = new byte[length];

        String fixedSecret = fixSecret(secret, length);
        key = fixedSecret.getBytes("UTF-8");
        this.secretKey = new SecretKeySpec(key, algorithm);
        this.cipher = Cipher.getInstance(algorithm);
        try {
            this.saveSecretKey(fixedSecret.toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String fixSecret(String s, int length) throws UnsupportedEncodingException {
        if (s.length() < length) {
            int missingLength = length - s.length();
            for (int i = 0; i < missingLength; i++) {
                s += " ";
            }
        }
        return s.substring(0, length);
    }

    public byte[] encrypt(String message)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        byte[] output = this.cipher.doFinal(message.getBytes());
        return output;
    }

    public byte[] decrypt(byte[] message)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
        byte[] output = this.cipher.doFinal(message);
        return output;
    }

    public void saveSecretKey(char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        /* save the secret key in a file */
        byte[] key = this.secretKey.getEncoded();
        FileOutputStream keyfos;
        try {
            keyfos = new FileOutputStream("/home/virt-atm/Documents/symmetricKey");
            keyfos.write(key);
            keyfos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
