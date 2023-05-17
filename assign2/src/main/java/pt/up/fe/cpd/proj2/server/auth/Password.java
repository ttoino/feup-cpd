package pt.up.fe.cpd.proj2.server.auth;


import pt.up.fe.cpd.proj2.common.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public final class Password {
    private static final SecureRandom random = new SecureRandom();
    private static final SecretKeyFactory factory;

    static {
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] generateSalt() {
        var bytes = new byte[16];
        random.nextBytes(bytes);
        return bytes;
    }

    public static byte[] hash(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            byte[] result = new byte[16 + hash.length];
            System.arraycopy(salt, 0, result, 0, 16);
            System.arraycopy(hash, 0, result, 16, hash.length);
            return result;
        } catch (InvalidKeySpecException e) {
            return null;
        }
    }

    public static boolean verify(String password, byte[] hash) {
        var salt = new byte[16];
        System.arraycopy(hash, 0, salt, 0, 16);
        var hashed = hash(password, salt);
        return Arrays.equals(hash, hashed);
    }

    private Password() {}
}
