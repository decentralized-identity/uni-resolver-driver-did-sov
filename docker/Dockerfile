# Dockerfile for universalresolver/driver-did-sov

FROM maven:3-eclipse-temurin-17-focal AS build
MAINTAINER Markus Sabadello <markus@danubetech.com>

# build driver-did-sov

WORKDIR /opt/driver-did-sov
ADD pom.xml /opt/driver-did-sov
RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.3.0:go-offline
ADD src/main/webapp/WEB-INF /opt/driver-did-sov/src/main/webapp/WEB-INF
RUN mvn clean package -P war
ADD . /opt/driver-did-sov
RUN mvn clean package -P war

# build image

FROM jetty:9.4.53-jre17-eclipse-temurin
MAINTAINER Markus Sabadello <markus@danubetech.com>

# install dependencies

USER root

RUN export DEBIAN_FRONTEND=noninteractive && \
    apt-get -y update && \
    wget http://archive.ubuntu.com/ubuntu/pool/main/o/openssl/libssl1.1_1.1.1f-1ubuntu2.20_amd64.deb && \
    dpkg -i libssl1.1_1.1.1f-1ubuntu2.20_amd64.deb && \
    apt-get install -y --no-install-recommends software-properties-common gnupg && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88 && \
    add-apt-repository "deb https://repo.sovrin.org/sdk/deb bionic stable" && \
    apt-get -y update && \
    apt-get install -y libindy=1.16.0-bionic && \
    apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

USER jetty

# variables

ENV uniresolver_driver_did_sov_libIndyPath=
ENV uniresolver_driver_did_sov_poolConfigs=_;./sovrin/_.txn;staging;./sovrin/staging.txn;builder;./sovrin/builder.txn;danube;./sovrin/danube.txn;idunion;./sovrin/idunion.txn;idunion:test;./sovrin/idunion-test.txn;indicio;./sovrin/indicio.txn;indicio:test;./sovrin/indicio-test.txn;indicio:demo;./sovrin/indicio-demo.txn;bbu;./sovrin/bbu.txn;nxd;./sovrin/nxd.txn;findy:test;./sovrin/findy-test.txn
ENV uniresolver_driver_did_sov_poolVersions=_;2;staging;2;builder;2;danube;2;idunion;2;idunion:test;2;indicio;2;indicio:test;2;indicio:demo;2;bbu;2;nxd;2;findy:test;2
ENV uniresolver_driver_did_sov_walletNames=_;w1;staging;w2;builder;w3;danube;w4;idunion;w5;idunion:test;w6;indicio;w7;indicio:test;w8;indicio:demo;w9;bbu;w10;nxd;w11;findy:test;w12
ENV uniresolver_driver_did_sov_submitterDidSeeds=_;_;staging;_;builder;_;danube;_;idunion;_;idunion:test;_;indicio;_;indicio:test;_;indicio:demo;_;bbu;_;nxd;_;findy:test;_

# copy from build stage

COPY --from=build --chown=jetty /opt/driver-did-sov/target/*.war /var/lib/jetty/webapps/ROOT.war
COPY --from=build --chown=jetty /opt/driver-did-sov/sovrin/ /var/lib/jetty/sovrin/

# done

EXPOSE 8080
CMD java -Djetty.http.port=8080 -jar /usr/local/jetty/start.jar
