package uniresolver.driver.did.sov.libindy;

import org.hyperledger.indy.sdk.LibIndy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LibIndyInitializer {

    private static Logger log = LoggerFactory.getLogger(LibIndyInitializer.class);

    private String libIndyPath;

    public LibIndyInitializer(String libIndyPath) {
        this.libIndyPath = libIndyPath;
    }

    public boolean isInitialized() {
        return LibIndy.isInitialized();
    }

    public void initializeLibIndy() {
        if (this.getLibIndyPath() != null && ! this.getLibIndyPath().isEmpty()) {
            if (log.isInfoEnabled()) log.info("Initializing libindy: " + this.getLibIndyPath() + " (" + new File(this.getLibIndyPath()).getAbsolutePath() + ")");
            LibIndy.init(new File(this.getLibIndyPath()));
        } else {
            if (log.isInfoEnabled()) log.info("Initializing libindy.");
            if (!LibIndy.isInitialized()) LibIndy.init();
        }
    }

    /*
     * Getters and setters
     */

    public String getLibIndyPath() {
        return libIndyPath;
    }

    public void setLibIndyPath(String libIndyPath) {
        this.libIndyPath = libIndyPath;
    }
}
