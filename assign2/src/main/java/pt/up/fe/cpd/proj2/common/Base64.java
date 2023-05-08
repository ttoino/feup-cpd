package pt.up.fe.cpd.proj2.common;

import java.nio.charset.StandardCharsets;

public final class Base64 {
    static final java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
    static final java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();

    public static String decode(String encoded) {
        return new String(decoder.decode(encoded.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    public static String encode(String src) {
        return new String(encoder.encode(src.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
}
