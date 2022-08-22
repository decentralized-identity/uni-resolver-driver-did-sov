package uniresolver.driver.did.sov.ledger;

import foundation.identity.did.DID;
import foundation.identity.did.DIDDocument;
import foundation.identity.did.Service;
import foundation.identity.did.VerificationMethod;
import foundation.identity.jsonld.JsonLDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uniresolver.ResolutionException;
import uniresolver.driver.did.sov.crypto.VerkeyUtil;
import uniresolver.driver.did.sov.crypto.X25519Util;

import java.net.URI;
import java.util.*;

public class DidDocAssembler {

    public static final List<URI> DIDDOCUMENT_CONTEXTS = List.of(
            URI.create("https://w3id.org/security/suites/ed25519-2018/v1"),
            URI.create("https://w3id.org/security/suites/x25519-2019/v1")
    );

    public static final String[] DIDDOCUMENT_VERIFICATIONMETHOD_KEY_TYPES = new String[] { "Ed25519VerificationKey2018" };
    public static final String[] DIDDOCUMENT_VERIFICATIONMETHOD_KEY_AGREEMENT_TYPES = new String[] { "X25519KeyAgreementKey2019" };

    private static final Logger log = LoggerFactory.getLogger(DidDocAssembler.class);

    public static DIDDocument assembleDIDDocument(DID did, TransactionData nymTransactionData, TransactionData attribTransactionData) throws ResolutionException {

        // check if transactions are complete

        if (nymTransactionData == null || ! nymTransactionData.isCompleteForNym()) {
            throw new ResolutionException("Not complete for NYM: " + nymTransactionData);
        }

        if (attribTransactionData != null && ! attribTransactionData.isCompleteForAttrib()) {
            throw new ResolutionException("Not complete for ATTRIB: " + attribTransactionData);
        }

        // DID DOCUMENT verificationMethods

        String ed25519Key = VerkeyUtil.getExpandedVerkey(did.getDidString(), nymTransactionData.getVerkey());
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

        Map<String, Object> serviceEndpoints;

        if (attribTransactionData == null || attribTransactionData.getRawValue() == null) {
            serviceEndpoints = Collections.emptyMap();
        } else {
            serviceEndpoints = attribTransactionData.getRawValue();
        }
        if (log.isDebugEnabled()) log.debug("Service endpoints for " + did + ": " + serviceEndpoints);

        List<Service> services = serviceEndpoints.isEmpty() ? null : new ArrayList<> ();

        for (Map.Entry<String, Object> serviceEndpoint : serviceEndpoints.entrySet()) {

            String serviceEndpointType = serviceEndpoint.getKey();
            Object serviceEndpointValue = serviceEndpoint.getValue();

            Service service = Service.builder()
                    .type(serviceEndpointType)
                    .serviceEndpoint(serviceEndpointValue)
                    .build();

            services.add(service);

            if ("endpoint".equals(service.getType())) {

                Service service2 = Service.builder()
                        .id(URI.create(did + "#did-communication"))
                        .type("did-communication")
                        .serviceEndpoint(serviceEndpointValue)
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
                        .serviceEndpoint(serviceEndpointValue)
                        .build();
                JsonLDUtils.jsonLdAddAll(service3, Map.of(
                        "routingKeys", List.of(),
                        "accept", List.of("didcomm/v2", "didcomm/aip2;env=rfc19")
                ));

                services.add(service2);
                services.add(service3);
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

        // done

        return didDocument;
    }

    public static DIDDocument assembleDeactivatedDIDDocument(DID did) {

        // create DID DOCUMENT

        DIDDocument didDocument = DIDDocument.builder()
                .contexts(DIDDOCUMENT_CONTEXTS)
                .id(did.toUri())
                .build();

        // done

        return didDocument;
    }
}
