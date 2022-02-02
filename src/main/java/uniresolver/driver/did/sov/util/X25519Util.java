package uniresolver.driver.did.sov.util;

import com.goterl.lazysodium.LazySodiumJava;
import com.goterl.lazysodium.SodiumJava;
import io.leonard.Base58;

public class X25519Util {

    private static final LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava());

    public static String ed25519Tox25519(String ed25519Key) {

        byte[] ed25519bytes = Base58.decode(ed25519Key);
        byte[] x25519bytes = new byte[ed25519bytes.length];
        lazySodium.convertPublicKeyEd25519ToCurve25519(x25519bytes, ed25519bytes);
        return Base58.encode(x25519bytes);
    }
}
