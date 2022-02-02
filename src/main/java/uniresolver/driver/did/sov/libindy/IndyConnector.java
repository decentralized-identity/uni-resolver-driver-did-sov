package uniresolver.driver.did.sov.libindy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class IndyConnector {

    private static Logger log = LoggerFactory.getLogger(IndyConnector.class);

    private String poolConfigs;
    private String poolVersions;
    private String walletNames;
    private String submitterDidSeeds;
    private String genesisTimestamps;

    private Map<String, IndyConnection> indyConnections;

    public IndyConnector(String poolConfigs, String poolVersions, String walletNames, String submitterDidSeeds, String genesisTimestamps) {
        this.poolConfigs = poolConfigs;
        this.poolVersions = poolVersions;
        this.walletNames = walletNames;
        this.submitterDidSeeds = submitterDidSeeds;
        this.genesisTimestamps = genesisTimestamps;
    }

    public IndyConnector() {
        this.indyConnections = null;
    }

    public boolean isOpened() {
        return this.getIndyConnections() != null;
    }

    public void closeIndyConnections() throws IndyConnectionException {
        for (IndyConnection indyConnection : this.getIndyConnections().values()) {
            indyConnection.close();
        }
        this.getIndyConnections().clear();
        this.setIndyConnections(null);
        System.gc();
    }

    public void openIndyConnections(boolean createSubmitterDid, boolean retrieveTaa) throws IndyConnectionException {

        if (this.getPoolConfigs() == null || this.getPoolConfigs().isEmpty()) throw new IllegalStateException("No configuration found for Indy connections.");

        // parse pool configs

        String[] poolConfigStrings = this.getPoolConfigs() == null ? new String[0] : this.getPoolConfigs().split(";");
        Map<String, String> poolConfigs = new HashMap<>();
        for (int i=0; i<poolConfigStrings.length; i+=2) poolConfigs.put(poolConfigStrings[i], poolConfigStrings[i+1]);

        if (log.isInfoEnabled()) log.info("Pool configs: " + poolConfigs);

        // parse pool versions

        String[] poolVersionStrings = this.getPoolVersions() == null ? new String[0] : this.getPoolVersions().split(";");
        Map<String, Integer> poolVersions = new HashMap<>();
        for (int i=0; i<poolVersionStrings.length; i+=2) poolVersions.put(poolVersionStrings[i], Integer.parseInt(poolVersionStrings[i+1]));

        if (log.isInfoEnabled()) log.info("Pool versions: " + poolVersions);

        // parse wallet names

        String[] walletNameStrings = this.getWalletNames() == null ? new String[0] : this.getWalletNames().split(";");
        Map<String, String> walletNames = new HashMap<>();
        for (int i=0; i<walletNameStrings.length; i+=2) walletNames.put(walletNameStrings[i], walletNameStrings[i+1]);

        if (log.isInfoEnabled()) log.info("Wallet names: " + walletNames);

        // parse submitter DID seeds

        String[] submitterDidSeedStrings = this.getSubmitterDidSeeds() == null ? new String[0] : this.getSubmitterDidSeeds().split(";");
        Map<String, String> submitterDidSeeds = new HashMap<>();
        for (int i=0; i<submitterDidSeedStrings.length; i+=2) submitterDidSeeds.put(submitterDidSeedStrings[i], submitterDidSeedStrings[i+1]);

        if (log.isInfoEnabled()) log.info("Submitter DID seeds: " + submitterDidSeeds);

        // parse genesis timestamps

        String[] genesisTimestampStrings = this.getGenesisTimestamps() == null ? new String[0] : this.getGenesisTimestamps().split(";");
        Map<String, Long> genesisTimestamps = new HashMap<>();
        for (int i=0; i<genesisTimestampStrings.length; i+=2) genesisTimestamps.put(genesisTimestampStrings[i], Long.parseLong(genesisTimestampStrings[i+1]));

        if (log.isInfoEnabled()) log.info("Genesis timestamps: " + genesisTimestamps);

        // create indy connections

        Map<String, IndyConnection> indyConnections = new HashMap<>();

        for (Map.Entry<String, String> poolConfigMapEntry : poolConfigs.entrySet()) {

            String poolConfigName = poolConfigMapEntry.getKey();
            String poolConfigFile = poolConfigMapEntry.getValue();
            Integer poolVersion = poolVersions.get(poolConfigName);
            String walletName = walletNames.get(poolConfigName);
            String submitterDidSeed = submitterDidSeeds.get(poolConfigName);
            Long genesisTimestamp = genesisTimestamps.get(poolConfigName);

            if (poolConfigFile == null) throw new IndyConnectionException("No pool config file for pool: " + poolConfigName);
            if (poolVersion == null) throw new IndyConnectionException("No pool version for pool: " + poolConfigName);
            if (walletName == null) throw new IndyConnectionException("No wallet name for pool: " + poolConfigName);
            if (submitterDidSeed == null) throw new IndyConnectionException("No submitter DID seed for pool: " + poolConfigName);

            IndyConnection indyConnection = new IndyConnection(poolConfigName, poolConfigFile, poolVersion, walletName, submitterDidSeed, genesisTimestamp);
            indyConnection.open(createSubmitterDid, retrieveTaa);

            indyConnections.put(poolConfigName, indyConnection);
        }

        if (log.isInfoEnabled()) log.info("Opened " + indyConnections.size() + " Indy connections: " + indyConnections.keySet());
        this.indyConnections = indyConnections;
    }

    /*
     * Getters and setters
     */

    public String getPoolConfigs() {
        return poolConfigs;
    }

    public void setPoolConfigs(String poolConfigs) {
        this.poolConfigs = poolConfigs;
    }

    public String getPoolVersions() {
        return poolVersions;
    }

    public void setPoolVersions(String poolVersions) {
        this.poolVersions = poolVersions;
    }

    public String getWalletNames() {
        return walletNames;
    }

    public void setWalletNames(String walletNames) {
        this.walletNames = walletNames;
    }

    public String getSubmitterDidSeeds() {
        return submitterDidSeeds;
    }

    public void setSubmitterDidSeeds(String submitterDidSeeds) {
        this.submitterDidSeeds = submitterDidSeeds;
    }

    public String getGenesisTimestamps() {
        return genesisTimestamps;
    }

    public void setGenesisTimestamps(String genesisTimestamps) {
        this.genesisTimestamps = genesisTimestamps;
    }

    public Map<String, IndyConnection> getIndyConnections() {
        return indyConnections;
    }

    public void setIndyConnections(Map<String, IndyConnection> indyConnections) {
        this.indyConnections = indyConnections;
    }
}
