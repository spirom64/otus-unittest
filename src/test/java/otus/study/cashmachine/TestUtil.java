package otus.study.cashmachine;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;


public class TestUtil {
    private TestUtil() {
    }

    public static String getHash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(value.getBytes());
            String result = HexFormat.of().formatHex(digest.digest());
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}