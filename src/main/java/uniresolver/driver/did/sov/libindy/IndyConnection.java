package uniresolver.driver.did.sov.libindy;

import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidJSONParameters;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.ledger.Ledger;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.pool.PoolJSONParameters;
import org.hyperledger.indy.sdk.pool.PoolLedgerConfigExistsException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.wallet.WalletExistsException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class IndyConnection {

    private static Logger log = LoggerFactory.getLogger(IndyConnection.class);

    private String poolConfigName;
    private String poolConfigFile;
    private Integer poolVersion;
    private String walletName;
    private String submitterDidSeed;
    private Long genesisTimestamp;

    private Pool pool;
    private Wallet wallet;
    private String submitterDid;
    private String taa;

    public IndyConnection(String poolConfigName, String poolConfigFile, Integer poolVersion, String walletName, String submitterDidSeed, Long genesisTimestamp) {
        this.poolConfigName = poolConfigName;
        this.poolConfigFile = poolConfigFile;
        this.poolVersion = poolVersion;
        this.walletName = walletName;
        this.submitterDidSeed = submitterDidSeed;
        this.genesisTimestamp = genesisTimestamp;
    }

    public IndyConnection() {
    }

    public void open(boolean createSubmitterDid, boolean retrieveTaa) throws IndyConnectionException {

        this.openPool();
        this.openWallet();
        if (createSubmitterDid) this.createSubmitterDid();
        if (retrieveTaa) this.retrieveTaa();
    }

    public void close() throws IndyConnectionException {

        // close wallet

        if (this.wallet != null) {
            if (log.isDebugEnabled()) log.debug("On connection " + this.getPoolConfigName() + " closing wallet: " + this.wallet.getWalletHandle());
            synchronized(this) {
                try {
                    this.wallet.close();
                    if (log.isInfoEnabled()) log.info("Wallet " + this.wallet.getWalletHandle() + " successfully closed.");
                    this.wallet = null;
                } catch (IndyException | InterruptedException | ExecutionException ex) {
                    throw new IndyConnectionException("Cannot close wallet " + this.wallet.getWalletHandle() + ": " + ex.getMessage(), ex);
                }
            }
        }

        // close pool

        if (this.pool != null) {
            if (log.isDebugEnabled()) log.debug("On connection " + this.getPoolConfigName() + " closing pool: " + this.pool.getPoolHandle());
            synchronized(this) {
                try {
                    this.pool.close();
                    if (log.isInfoEnabled()) log.info("Pool " + this.pool.getPoolHandle() + " successfully closed.");
                    this.pool = null;
                } catch (IndyException | InterruptedException | ExecutionException ex) {
                    throw new IndyConnectionException("Cannot close pool " + this.pool.getPoolHandle() + ": " + ex.getMessage(), ex);
                }
            }
        }

        // close

        this.submitterDid = null;
        this.taa = null;

        // done

        if (log.isDebugEnabled()) log.debug("On connection " + this.getPoolConfigName() + " closed pool and wallet.");
    }

    public boolean isOpen() {
        return this.getPool() != null && this.getWallet() != null;
    }

    private void openPool() throws IndyConnectionException {

        // create pool config

        synchronized(this) {

            try {

                Pool.setProtocolVersion(this.getPoolVersion());
                PoolJSONParameters.CreatePoolLedgerConfigJSONParameter createPoolLedgerConfigJSONParameter = new PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(poolConfigFile);
                Pool.createPoolLedgerConfig(this.getPoolConfigName(), createPoolLedgerConfigJSONParameter.toJson()).get();
                if (log.isInfoEnabled()) log.info("Pool config \"" + this.getPoolConfigName() + "\" successfully created.");
            } catch (IndyException | InterruptedException | ExecutionException ex) {

                IndyException iex = null;
                if (ex instanceof IndyException) iex = (IndyException) ex;
                if (ex instanceof ExecutionException && ex.getCause() instanceof IndyException) iex = (IndyException) ex.getCause();
                if (iex instanceof PoolLedgerConfigExistsException) {
                    if (log.isInfoEnabled()) log.info("Pool config \"" + this.getPoolConfigName() + "\" has already been created.");
                } else {
                    throw new IndyConnectionException("Cannot create pool config \"" + this.getPoolConfigName() + "\": " + ex.getMessage(), ex);
                }
            }
        }

        // open pool

        synchronized(this) {

            try {

                Pool.setProtocolVersion(this.getPoolVersion());

                PoolJSONParameters.OpenPoolLedgerJSONParameter openPoolLedgerJSONParameter = new PoolJSONParameters.OpenPoolLedgerJSONParameter(null, null);
                this.pool = Pool.openPoolLedger(this.getPoolConfigName(), openPoolLedgerJSONParameter.toJson()).get();
                if (log.isInfoEnabled()) log.info("Pool \"" + this.getPoolConfigName() + "\" (" + this.pool.getPoolHandle() + ") successfully opened.");
            } catch (IndyException | InterruptedException | ExecutionException ex) {

                this.pool = null;
                if (log.isWarnEnabled()) log.warn("Cannot open pool \"" + this.getPoolConfigName() + "\": " + ex.getMessage(), ex);
            }
        }
    }

    private void openWallet() throws IndyConnectionException {

        // create wallet

        synchronized(this) {

            try {

                String walletConfig = "{ \"id\":\"" + this.getWalletName() + "\", \"storage_type\":\"" + "default" + "\"}";
                String walletCredentials = "{ \"key\":\"key\" }";
                Wallet.createWallet(walletConfig, walletCredentials).get();
                if (log.isInfoEnabled()) log.info("Wallet \"" + this.getWalletName() + "\" successfully created.");
            } catch (IndyException | InterruptedException | ExecutionException ex) {

                IndyException iex = null;
                if (ex instanceof IndyException) iex = (IndyException) ex;
                if (ex instanceof ExecutionException && ex.getCause() instanceof IndyException) iex = (IndyException) ex.getCause();
                if (iex instanceof WalletExistsException) {
                    if (log.isInfoEnabled()) log.info("Wallet \"" + this.getWalletName() + "\" has already been created.");
                } else {
                    throw new IndyConnectionException("Cannot create wallet \"" + this.getWalletName() + "\": " + ex.getMessage(), ex);
                }
            }
        }

        // open wallet

        synchronized(this) {

            try {

                String walletConfig = "{ \"id\":\"" + this.getWalletName() + "\", \"storage_type\":\"" + "default" + "\"}";
                String walletCredentials = "{ \"key\":\"key\" }";
                this.wallet = Wallet.openWallet(walletConfig, walletCredentials).get();
                if (log.isInfoEnabled()) log.info("Wallet \"" + this.getWalletName() + "\" (" + this.wallet.getWalletHandle() + ") successfully opened.");
            } catch (IndyException | InterruptedException | ExecutionException ex) {

                this.wallet = null;
                throw new IndyConnectionException("Cannot open wallet \"" + this.getWalletName() + "\": " + ex.getMessage(), ex);
            }
        }
    }

    public void createSubmitterDid() throws IndyConnectionException {

        // create submitter DID

        synchronized(this) {

            try {

                String submitterDidSeed = this.getSubmitterDidSeed();
                if (submitterDidSeed != null && submitterDidSeed.isEmpty()) submitterDidSeed = null;
                if ("_".equals(submitterDidSeed)) submitterDidSeed = null;
                DidJSONParameters.CreateAndStoreMyDidJSONParameter createAndStoreMyDidJSONParameterTrustee = new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, submitterDidSeed, null, null);
                DidResults.CreateAndStoreMyDidResult createAndStoreMyDidResultTrustee = Did.createAndStoreMyDid(this.getWallet(), createAndStoreMyDidJSONParameterTrustee.toJson()).get();
                this.submitterDid = createAndStoreMyDidResultTrustee.getDid();
                if (log.isInfoEnabled()) log.info("Submitter DID \"" + this.submitterDid + "\" successfully created.");
            } catch (IndyException | InterruptedException | ExecutionException ex) {

                this.submitterDid = null;
                throw new IndyConnectionException("Cannot create submitter DID: " + ex.getMessage(), ex);
            }
        }
    }

    public void retrieveTaa() throws IndyConnectionException {

        // retrieve TAA

        synchronized(this) {

            try {

                Pool.setProtocolVersion(this.getPoolVersion());

                String getTxnAuthorAgreementRequest = Ledger.buildGetTxnAuthorAgreementRequest(this.getSubmitterDid(), null).get();
                String getTxnAuthorAgreementResult = Ledger.signAndSubmitRequest(this.getPool(), this.getWallet(), this.getSubmitterDid(), getTxnAuthorAgreementRequest).get();
                if (log.isDebugEnabled()) log.debug("getTxnAuthorAgreementResult: (" + getTxnAuthorAgreementResult.length() + ") " + getTxnAuthorAgreementResult);

                JSONObject jsonObjectTAA = new JSONObject(getTxnAuthorAgreementResult);
                JSONObject jsonObjectTAAResult = (jsonObjectTAA.has("result") && jsonObjectTAA.get("result") instanceof JSONObject) ? jsonObjectTAA.getJSONObject("result") : null;
                JSONObject jsonObjectTAAResultData = (jsonObjectTAAResult != null && jsonObjectTAAResult.has("data") && jsonObjectTAAResult.get("data") instanceof JSONObject) ? jsonObjectTAAResult.getJSONObject("data") : null;
                this.taa = jsonObjectTAAResultData == null ? null : jsonObjectTAAResultData.getString("text");
                if (log.isInfoEnabled()) log.info("TAA \"" + this.taa + "\" successfully retrieved.");
            } catch (IndyException | InterruptedException | ExecutionException ex) {

                this.taa = null;
                throw new IndyConnectionException("Cannot retrieve TAA: " + ex.getMessage(), ex);
            }
        }
    }

    public String getDidNetworkPrefix() {
        return this.getPoolConfigName().equals("_") ? "" : (this.getPoolConfigName() + ":");
    }

    /*
     * Getters and setters
     */

    public String getPoolConfigName() {
        return poolConfigName;
    }

    public void setPoolConfigName(String poolConfigName) {
        this.poolConfigName = poolConfigName;
    }

    public String getPoolConfigFile() {
        return poolConfigFile;
    }

    public void setPoolConfigFile(String poolConfigFile) {
        this.poolConfigFile = poolConfigFile;
    }

    public Integer getPoolVersion() {
        return poolVersion;
    }

    public void setPoolVersion(Integer poolVersion) {
        this.poolVersion = poolVersion;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getSubmitterDidSeed() {
        return submitterDidSeed;
    }

    public void setSubmitterDidSeed(String submitterDidSeed) {
        this.submitterDidSeed = submitterDidSeed;
    }

    public Long getGenesisTimestamp() {
        return genesisTimestamp;
    }

    public void setGenesisTimestamp(Long genesisTimestamp) {
        this.genesisTimestamp = genesisTimestamp;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getSubmitterDid() {
        return submitterDid;
    }

    public void setSubmitterDid(String submitterDid) {
        this.submitterDid = submitterDid;
    }

    public String getTaa() {
        return taa;
    }

    public void setTaa(String taa) {
        this.taa = taa;
    }

    /*
     * Object methods
     */

    public String toString() {
        return this.getPoolConfigFile() + " / " + this.getPoolVersion() + " / " + this.getPool();
    }
}
