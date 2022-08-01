# Dockerfile for universalresolver/driver-did-sov

FROM maven:3-jdk-11 AS build
MAINTAINER Markus Sabadello <markus@danubetech.com>

# build driver-did-sov

ADD . /opt/driver-did-sov
RUN cd /opt/driver-did-sov && mvn clean install package -P war -N -DskipTests

FROM jetty:9.4-jre11-slim
MAINTAINER Markus Sabadello <markus@danubetech.com>

# install dependencies

USER root

RUN export DEBIAN_FRONTEND=noninteractive && \
    apt-get -y update && \
    apt-get install -y --no-install-recommends software-properties-common gnupg && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88 && \
    add-apt-repository "deb https://repo.sovrin.org/sdk/deb bionic stable" && \
    apt-get -y update && \
    apt-get install -y libindy=1.16.0-bionic && \
    apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

USER jetty

# variables

ENV uniresolver_driver_did_sov_libIndyPath=
ENV uniresolver_driver_did_sov_poolConfigs=_;./sovrin/_.txn;staging;./sovrin/staging.txn;builder;./sovrin/builder.txn;danube;./sovrin/danube.txn;idunion;./sovrin/idunion.txn;idunion:test;./sovrin/idunion-test.txn;eesdi;./sovrin/eesdi.txn;indicio;./sovrin/indicio.txn;indicio:test;./sovrin/indicio-test.txn;indicio:demo;./sovrin/indicio-demo.txn;bbu;./sovrin/bbu.txn
ENV uniresolver_driver_did_sov_poolVersions=_;2;staging;2;builder;2;danube;2;idunion;2;idunion:test;2;eesdi;2;indicio;2;indicio:test;2;indicio:demo;2;bbu;2
ENV uniresolver_driver_did_sov_walletNames=_;w1;staging;w2;builder;w3;danube;w4;idunion;w5;idunion:test;w6;eesdi;w7;indicio;w8;indicio:test;w9;indicio:demo;w10;bbu;w11
ENV uniresolver_driver_did_sov_submitterDidSeeds=_;_;staging;_;builder;_;danube;_;idunion;_;idunion:test;_;eesdi;_;indicio;_;indicio:test;_;indicio:demo;_;bbu;_

# copy from build stage

COPY --from=build --chown=jetty /opt/driver-did-sov/target/*.war /var/lib/jetty/webapps/ROOT.war
COPY --from=build --chown=jetty /opt/driver-did-sov/sovrin/ /var/lib/jetty/sovrin/

# done

EXPOSE 8080
CMD java -Djetty.http.port=8080 -jar /usr/local/jetty/start.jar
