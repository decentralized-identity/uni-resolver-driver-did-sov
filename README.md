![DIF Logo](https://raw.githubusercontent.com/decentralized-identity/universal-resolver/master/docs/logo-dif.png)

# Universal Resolver Driver: did:sov

This is a [Universal Resolver](https://github.com/decentralized-identity/universal-resolver/) driver for **did:sov** identifiers.

## Specifications

* [Decentralized Identifiers](https://w3c.github.io/did-core/)
* [DID Method Specification](https://sovrin-foundation.github.io/sovrin/spec/did-method-spec-template.html)

## Example DIDs

```
did:sov:WRfXPg8dantKVubE3HX8pw
did:sov:stn:WRfXPg8dantKVubE3HX8pw
```

## Build and Run (Docker)

```
docker build -f ./docker/Dockerfile . -t universalresolver/driver-did-sov
docker run -p 8080:8080 universalresolver/driver-did-sov
curl -X GET http://localhost:8080/1.0/identifiers/did:sov:WRfXPg8dantKVubE3HX8pw
```

## Build (native Java)

Maven build:

    mvn clean install

## Driver Environment Variables

The driver recognizes the following environment variables:

### `uniresolver_driver_did_sov_libIndyPath`

 * Specifies the path to the Indy SDK library.
 * Default value: (empty string)

### `uniresolver_driver_did_sov_openParallel`

* Specifies whether to open Indy pools in parallel threads. This speeds up startup, but may consume more memory.
* Default value: false

### `uniresolver_driver_did_sov_poolConfigs`

 * Specifies a semi-colon-separated list of Indy network names and pool configuration files. The default network is `_`.
 * Default value: `_;./sovrin/_.txn;staging;./sovrin/staging.txn;builder;./sovrin/builder.txn;danube;./sovrin/danube.txn;idu;./sovrin/idu.txn;eesdi;./sovrin/eesdi.txn;indicio;./sovrin/indicio.txn;indicio:test;./sovrin/indicio-test.txn;indicio:demo;./sovrin/indicio-demo.txn;bbu;./sovrin/bbu.txn`

### `uniresolver_driver_did_sov_poolVersions`

 * Specifies a semi-colon-separated list of Indy network names and pool protocol versions. The default network is `_`.
 * Default value: `_;2;staging;2;builder;2;danube;2;idu;2;eesdi;2;indicio;2;indicio:test;2;indicio:demo;2;bbu;2`

### `uniresolver_driver_did_sov_walletNames`

 * Specifies a semi-colon-separated list of Indy network names and wallet names. The default network is `_`.
 * Default value: `_;w1;staging;w2;builder;w3;danube;w4;idu;w5;eesdi;w6;indicio;w7;indicio:test;w8;indicio:demo;w9;bbu;w10`

### `uniresolver_driver_did_sov_submitterDidSeeds`

* Specifies a semi-colon-separated list of Indy network names and seeds for submitter DIDs. The default network is `_`.
* Default value: `_;_;staging;_;builder;_;danube;_;idu;_;eesdi;_;indicio;_;indicio:test;_;indicio:demo;_;bbu;_`

## Driver Metadata

The driver returns the following metadata in addition to a DID document:

* `nymResponse`: Response to the Indy `GET_NYM` operation, including `txnTime`, `state_proof`, and other information.
* `attrResponse`: Response to the Indy `GET_ATTR` operation, including `txnTime`, `state_proof`, and other information.
