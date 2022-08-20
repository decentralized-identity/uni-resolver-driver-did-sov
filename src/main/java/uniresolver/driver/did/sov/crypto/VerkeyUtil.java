package uniresolver.driver.did.sov.crypto;

import io.leonard.Base58;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerkeyUtil {

    private static final Logger log = LoggerFactory.getLogger(VerkeyUtil.class);

    public static String getExpandedVerkey(String did, String verkey) {

        if (verkey == null) return verkey;
        if (! verkey.startsWith("~")) return verkey;

        byte[] didBytes = Base58.decode(did.substring(did.lastIndexOf(":") + 1));
        byte[] verkeyBytes = Base58.decode(verkey.substring(1));

        if (didBytes.length != 16) throw new IllegalArgumentException("DID bytes are not 16: " + didBytes.length);
        if (verkeyBytes.length != 16) throw new IllegalArgumentException("Verkey bytes are not 16: " + didBytes.length);

        byte[] didVerkeyBytes = new byte[didBytes.length + verkeyBytes.length];
        System.arraycopy(didBytes, 0, didVerkeyBytes, 0, 16);
        System.arraycopy(verkeyBytes, 0, didVerkeyBytes, 16, 16);

        String didVerkey = Base58.encode(didVerkeyBytes);
        if (log.isDebugEnabled()) log.debug("Expanded " + did + " and " + verkey + " to " + didVerkey);

        return didVerkey;
    }
}
