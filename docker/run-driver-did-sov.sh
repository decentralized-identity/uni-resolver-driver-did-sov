#!/bin/sh

cd /opt/driver-did-sov/
mvn --settings settings.xml jetty:run -P war
