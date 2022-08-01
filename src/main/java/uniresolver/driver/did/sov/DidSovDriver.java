package uniresolver.driver.did.sov;

import com.danubetech.libindy.IndyConnection;
import com.danubetech.libindy.IndyConnectionException;
import com.danubetech.libindy.IndyConnector;
import com.danubetech.libindy.LibIndyInitializer;
import com.google.gson.*;
import foundation.identity.did.DID;
import foundation.identity.did.DIDDocument;
import foundation.identity.did.Service;
import foundation.identity.did.VerificationMethod;
import foundation.identity.jsonld.JsonLDUtils;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.ledger.Ledger;
import org.hyperledger.indy.sdk.pool.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uniresolver.ResolutionException;
import uniresolver.driver.Driver;
import uniresolver.driver.did.sov.util.VerkeyUtil;
import uniresolver.driver.did.sov.util.X25519Util;
import uniresolver.result.ResolveDataModelResult;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DidSovDriver implements Driver {

	private static Logger log = LoggerFactory.getLogger(DidSovDriver.class);

	public static final Pattern DID_SOV_PATTERN = Pattern.compile("^did:sov:(?:(\\w[-\\w]*(?::\\w[-\\w]*)*):)?([1-9A-HJ-NP-Za-km-z]{21,22})$");

	public static final List<URI> DIDDOCUMENT_CONTEXTS = List.of(
		URI.create("https://w3id.org/security/suites/ed25519-2018/v1"),
		URI.create("https://w3id.org/security/suites/x25519-2019/v1")
	);

	public static final String[] DIDDOCUMENT_VERIFICATIONMETHOD_KEY_TYPES = new String[] { "Ed25519VerificationKey2018" };
	public static final String[] DIDDOCUMENT_VERIFICATIONMETHOD_KEY_AGREEMENT_TYPES = new String[] { "X25519KeyAgreementKey2019" };

	private static final Gson gson = new Gson();

	private Map<String, Object> properties;

	private LibIndyInitializer libIndyInitializer;
	private IndyConnector indyConnector;

	public DidSovDriver(Map<String, Object> properties) {

		this.setProperties(properties);
	}

	public DidSovDriver() {

		this(getPropertiesFromEnvironment());
	}

	private static Map<String, Object> getPropertiesFromEnvironment() {

		if (log.isDebugEnabled()) log.debug("Loading from environment: " + System.getenv());

		Map<String, Object> properties = new HashMap<> ();

		try {

			String env_libIndyPath = System.getenv("uniresolver_driver_did_sov_libIndyPath");
			String env_poolConfigs = System.getenv("uniresolver_driver_did_sov_poolConfigs");
			String env_poolVersions = System.getenv("uniresolver_driver_did_sov_poolVersions");
			String env_walletNames = System.getenv("uniresolver_driver_did_sov_walletNames");
			String env_submitterDidSeeds = System.getenv("uniresolver_driver_did_sov_submitterDidSeeds");
			String env_genesisTimestamps = System.getenv("uniresolver_driver_did_sov_genesisTimestamps");

			if (env_libIndyPath != null) properties.put("libIndyPath", env_libIndyPath);
			if (env_poolConfigs != null) properties.put("poolConfigs", env_poolConfigs);
			if (env_poolVersions != null) properties.put("poolVersions", env_poolVersions);
			if (env_walletNames != null) properties.put("walletNames", env_walletNames);
			if (env_submitterDidSeeds != null) properties.put("submitterDidSeeds", env_submitterDidSeeds);
			if (env_genesisTimestamps != null) properties.put("genesisTimestamps", env_genesisTimestamps);
		} catch (Exception ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}

		return properties;
	}

	private void configureFromProperties() {

		if (log.isDebugEnabled()) log.debug("Configuring from properties: " + this.getProperties());

		try {

			String prop_libIndyPath = (String) this.getProperties().get("libIndyPath");

			this.setLibIndyInitializer(new LibIndyInitializer(
					prop_libIndyPath));

			String prop_poolConfigs = (String) this.getProperties().get("poolConfigs");
			String prop_poolVersions = (String) this.getProperties().get("poolVersions");
			String prop_walletNames = (String) this.getProperties().get("walletNames");
			String prop_submitterDidSeeds = (String) this.getProperties().get("submitterDidSeeds");
			String prop_genesisTimestamps = (String) this.getProperties().get("genesisTimestamps");

			this.setIndyConnector(new IndyConnector(
					prop_poolConfigs,
					prop_poolVersions,
					prop_walletNames,
					prop_submitterDidSeeds,
					prop_genesisTimestamps));
		} catch (Exception ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	@Override
	public ResolveDataModelResult resolve(DID did, Map<String, Object> resolveOptions) throws ResolutionException {

		// init

		if (!this.getLibIndyInitializer().isInitialized()) {
			this.getLibIndyInitializer().initializeLibIndy();
			if (log.isInfoEnabled()) log.info("Successfully initialized libindy.");
		}

		// open indy connections

		if (!this.getIndyConnector().isOpened()) {
			try {
				this.getIndyConnector().openIndyConnections(true, false);
				if (log.isInfoEnabled()) log.info("Successfully opened Indy connections.");
			} catch (IndyConnectionException ex) {
				throw new ResolutionException("Cannot open Indy connections: " + ex.getMessage(), ex);
			}
		}

		// parse identifier

		Matcher matcher = DID_SOV_PATTERN.matcher(did.getDidString());
		if (! matcher.matches()) return null;

		String network = matcher.group(1);
		String targetDid = matcher.group(2);
		if (network == null || network.trim().isEmpty()) network = "_";

		// find Indy connection

		IndyConnection indyConnection;

		try {
			indyConnection = this.getIndyConnector().getIndyConnection(network, true, true, false);
			if (indyConnection == null) {
				if (log.isInfoEnabled()) log.info("Unknown network: " + network);
				return null;
			}
		} catch (IndyConnectionException ex) {
			throw new ResolutionException("Cannot get Indy connection for network " + network + ": " + ex.getMessage(), ex);
		}

		// send GET_NYM request

		String getNymResponse;

		try {

			synchronized(indyConnection) {

				Pool.setProtocolVersion(indyConnection.getPoolVersion());

				String getNymRequest = Ledger.buildGetNymRequest(indyConnection.getSubmitterDid(), targetDid).get();
				getNymResponse = Ledger.signAndSubmitRequest(indyConnection.getPool(), indyConnection.getWallet(), indyConnection.getSubmitterDid(), getNymRequest).get();
			}
		} catch (IndyException | InterruptedException | ExecutionException ex) {

			throw new ResolutionException("Cannot send GET_NYM request: " + ex.getMessage(), ex);
		}

		if (log.isInfoEnabled()) log.info("GET_NYM for " + targetDid + ": " + getNymResponse);

		// GET_NYM response data

		JsonObject jsonGetNymResponse = gson.fromJson(getNymResponse, JsonObject.class);
		JsonObject jsonGetNymResult = jsonGetNymResponse == null ? null : jsonGetNymResponse.getAsJsonObject("result");
		JsonElement jsonGetNymData = jsonGetNymResult == null ? null : jsonGetNymResult.get("data");
		JsonObject jsonGetNymDataContent = (jsonGetNymData == null || jsonGetNymData instanceof JsonNull) ? null : gson.fromJson(jsonGetNymData.getAsString(), JsonObject.class);

		if (jsonGetNymDataContent == null) return null;

		// send GET_ATTR request

		String getAttrResponse;

		try {

			synchronized(indyConnection) {

				Pool.setProtocolVersion(indyConnection.getPoolVersion());

				String getAttrRequest = Ledger.buildGetAttribRequest(indyConnection.getSubmitterDid(), targetDid, "endpoint", null, null).get();
				getAttrResponse = Ledger.signAndSubmitRequest(indyConnection.getPool(), indyConnection.getWallet(), indyConnection.getSubmitterDid(), getAttrRequest).get();
			}
		} catch (IndyException | InterruptedException | ExecutionException ex) {

			throw new ResolutionException("Cannot send GET_NYM request: " + ex.getMessage(), ex);
		}

		if (log.isInfoEnabled()) log.info("GET_ATTR for " + targetDid + ": " + getAttrResponse);

		// GET_ATTR response data

		JsonObject jsonGetAttrResponse = gson.fromJson(getAttrResponse, JsonObject.class);
		JsonObject jsonGetAttrResult = jsonGetAttrResponse == null ? null : jsonGetAttrResponse.getAsJsonObject("result");
		JsonElement jsonGetAttrData = jsonGetAttrResult == null ? null : jsonGetAttrResult.get("data");
		JsonObject jsonGetAttrDataContent = (jsonGetAttrData == null || jsonGetAttrData instanceof JsonNull) ? null : gson.fromJson(jsonGetAttrData.getAsString(), JsonObject.class);

		// DID DOCUMENT verificationMethods

		JsonPrimitive jsonGetNymVerkey = jsonGetNymDataContent == null ? null : jsonGetNymDataContent.getAsJsonPrimitive("verkey");
		String verkey = jsonGetNymVerkey == null ? null : jsonGetNymVerkey.getAsString();

		String ed25519Key = VerkeyUtil.getExpandedVerkey(did.getDidString(), verkey);
		String x25519Key = X25519Util.ed25519Tox25519(ed25519Key);

		List<VerificationMethod> verificationMethods = new ArrayList<>();

		VerificationMethod verificationMethodKey = VerificationMethod.builder()
				.id(URI.create(did + "#key-1"))
				.controller(did.toString())
				.types(Arrays.asList(DIDDOCUMENT_VERIFICATIONMETHOD_KEY_TYPES))
				.publicKeyBase58(ed25519Key)
				.build();

		VerificationMethod verificationMethodKeyAgreement = VerificationMethod.builder()
				.id(URI.create(did + "#key-agreement-1"))
				.controller(did.toString())
				.types(Arrays.asList(DIDDOCUMENT_VERIFICATIONMETHOD_KEY_AGREEMENT_TYPES))
				.publicKeyBase58(x25519Key)
				.build();

		verificationMethods.add(verificationMethodKey);
		verificationMethods.add(verificationMethodKeyAgreement);

		// DID DOCUMENT services

		JsonObject jsonGetAttrEndpoint = jsonGetAttrDataContent == null ? null : jsonGetAttrDataContent.getAsJsonObject("endpoint");

		List<Service> services = new ArrayList<> ();

		if (jsonGetAttrEndpoint != null) {

			for (Map.Entry<String, JsonElement> jsonService : jsonGetAttrEndpoint.entrySet()) {

				JsonPrimitive jsonGetAttrEndpointValue = jsonGetAttrEndpoint == null ? null : jsonGetAttrEndpoint.getAsJsonPrimitive(jsonService.getKey());
				String value = jsonGetAttrEndpointValue == null ? null : jsonGetAttrEndpointValue.getAsString();

				Service service = Service.builder()
						.type(jsonService.getKey())
						.serviceEndpoint(value)
						.build();

				services.add(service);

				if ("endpoint".equals(service.getType())) {

					Service service2 = Service.builder()
							.id(URI.create(did + "#did-communication"))
							.type("did-communication")
							.serviceEndpoint(value)
							.build();
					JsonLDUtils.jsonLdAddAll(service2, Map.of(
							"priority", 0,
							"recipientKeys", List.of(JsonLDUtils.uriToString(verificationMethodKey.getId())),
							"routingKeys", List.of(),
							"accept", List.of("didcomm/aip2;env=rfc19")
					));

					Service service3 = Service.builder()
							.id(URI.create(did + "#didcomm-1"))
							.type("DIDComm")
							.serviceEndpoint(value)
							.build();
					JsonLDUtils.jsonLdAddAll(service3, Map.of(
							"routingKeys", List.of(),
							"accept", List.of("didcomm/v2", "didcomm/aip2;env=rfc19")
					));

					services.add(service2);
					services.add(service3);
				}
			}
		}

		// create DID DOCUMENT

		DIDDocument didDocument = DIDDocument.builder()
				.contexts(DIDDOCUMENT_CONTEXTS)
				.id(did.toUri())
				.verificationMethods(verificationMethods)
				.authenticationVerificationMethod(VerificationMethod.builder().id(verificationMethodKey.getId()).build())
				.assertionMethodVerificationMethod(VerificationMethod.builder().id(verificationMethodKey.getId()).build())
				.keyAgreementVerificationMethod(VerificationMethod.builder().id(verificationMethodKeyAgreement.getId()).build())
				.services(services)
				.build();

		// create DID DOCUMENT METADATA

		Map<String, Object> didDocumentMetadata = new LinkedHashMap<> ();
		didDocumentMetadata.put("network", indyConnection.getPoolConfigName());
		didDocumentMetadata.put("poolVersion", indyConnection.getPoolVersion());
		didDocumentMetadata.put("submitterDid", indyConnection.getSubmitterDid());
		didDocumentMetadata.put("nymResponse", gson.fromJson(jsonGetNymResponse, Map.class));
		didDocumentMetadata.put("attrResponse", gson.fromJson(jsonGetAttrResponse, Map.class));

		// create RESOLVE RESULT

		ResolveDataModelResult resolveDataModelResult = ResolveDataModelResult.build(null, didDocument, didDocumentMetadata);

		// done

		return resolveDataModelResult;
	}

	@Override
	public Map<String, Object> properties() {

		return this.getProperties();
	}

	/*
	 * Getters and setters
	 */

	public Map<String, Object> getProperties() {
		return this.properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
		this.configureFromProperties();
	}

	public LibIndyInitializer getLibIndyInitializer() {
		return libIndyInitializer;
	}

	public void setLibIndyInitializer(LibIndyInitializer libIndyInitializer) {
		this.libIndyInitializer = libIndyInitializer;
	}

	public IndyConnector getIndyConnector() {
		return indyConnector;
	}

	public void setIndyConnector(IndyConnector indyConnector) {
		this.indyConnector = indyConnector;
	}
}
