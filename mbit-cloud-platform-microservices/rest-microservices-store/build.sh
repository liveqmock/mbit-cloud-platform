#!/bin/bash

set -e

# mvn clean install -DskipTests

docker build -t boostrack/debian:microservices-store .
