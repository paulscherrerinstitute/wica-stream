###############################################################################
# 1.0 Create a cutdown JDK image tailored to the needs of the application
###############################################################################

FROM alpine:3.7

ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
RUN ZULU_ARCH=zulu11.2.3-jdk11.0.1-linux_musl_x64.tar.gz && \
    INSTALL_DIR=/usr/lib/jvm && \
	BIN_DIR=/usr/bin && \
	MAN_DIR=/usr/share/man/man1 && \
	ZULU_DIR=$( basename ${ZULU_ARCH} .tar.gz ) && \
	wget -q https://cdn.azul.com/zulu/bin/${ZULU_ARCH} && \
	mkdir -p ${INSTALL_DIR} && \
	tar -xf ./${ZULU_ARCH} -C ${INSTALL_DIR} && rm -f ${ZULU_ARCH} && \
	cd ${BIN_DIR} && find ${INSTALL_DIR}/${ZULU_DIR}/bin -type f -perm -a=x -exec ln -s {} . \; && \
	mkdir -p ${MAN_DIR} && \
	cd ${MAN_DIR} && find ${INSTALL_DIR}/${ZULU_DIR}/man/man1 -type f -name "*.1" -exec ln -s {} . \; && \
	java -version


###############################################################################
# 2.0 Get support for EPICS (if required)
###############################################################################

##
## Enable the following block when EPICS is needed in the container for eg
## debugging purposes.
##
## FROM debian:stable-slim as build_epics
##
## WORKDIR /epics
##
#
# RUN DEBIAN_FRONTEND=noninteractive apt-get update && \
#     apt-get install -y wget &&  \
#     apt-get install -y perl &&  \
#     apt-get install -y gcc &&  \
#     apt-get install -y make &&  \
#     apt-get install -y g++ &&  \
#     apt-get install -y libreadline-dev &&  \
#     rm -rf /var/lib/apt/lists/* /tmp/*
#
# RUN wget https://epics.anl.gov/download/base/baseR3.14.12.7.tar.gz && \
#     tar xvf baseR3.14.12.7.tar.gz
#
# RUN cd base-3.14.12.7 ; make


################################################################################
## 3.0 Define the rest of our application
################################################################################

# This script takes one argument - the name of the jar file containing
# the Spring Boot application.
ARG JAR_FILE

##
## Enable the following block when EPICS is needed in the container for eg
## debugging purposes.
##
#
#ENV EPICS_HOME=/epics
#COPY --from=build_epics /epics/base-3.14.12.7/ $EPICS_HOME
#
#RUN DEBIAN_FRONTEND=noninteractive apt-get update && \
#    apt-get install -y libreadline-dev


# This port must be open for TCP and UDP when the connection
# is via a channel access gateway.
EXPOSE 5062

# This port must be open for TCP and UDP when the connection
# is va normal IOC
EXPOSE 5064

# This port must be open for UDP to ensure that the EPICS client
# application sees the beacon messages sent to the local
# CA repeater.
EXPOSE 5065

# The keystore password must be supplied as an argument to the Docker
# run command. The keystore itself must be provided in the config
# directory via an external mount.
ENV KEYSTORE_PASS "XXXXXX"

# Document the ports that will be exposed by the Spring Boot Application
EXPOSE 8443

# Setup the container so that it defaults to the timezone of PSI. This can
# always be overridden later. This step is important as the timezone is used
# in all log messages and is reported on the GUI.
ENV TZ="Europe/Zurich"

# Document the fact that this image will normally be run as root. The
# fact that the deployment script may need to manipulate file ownership
# and permissions forces this.
USER root

# Set the working directory
WORKDIR /root


###############################################################################
# 4.0 Install any additional applications
###############################################################################

# Add the dependencies of the deploy scripts, including python and git
# Added basic vim editor to ease debugging (can be removed later in production).
#RUN DEBIAN_FRONTEND=noninteractive apt-get update && \
#    apt-get install -y openssh-client &&             \
#    apt-get clean &&                                 \
#    rm -rf /var/lib/apt/lists/* /tmp/*

RUN apk update && \
    apk add --no-cache bash && \
    apk add --no-cache openssh-client


###############################################################################
# 5.0 Set up the application project structure
###############################################################################

# Create the directories needed by this application
RUN mkdir log config lib

# Populate the application directories as appropriate
COPY ./target/${JAR_FILE} lib/jarfile.jar

COPY ./src/main/resources/config/keystore.jks config
COPY ./src/main/resources/application-docker-run.properties config
COPY src/main/resources/logback_config.xml config


###############################################################################
# 6.0 Define the exposed volumes
###############################################################################

VOLUME /root/.ssh
VOLUME /root/log
VOLUME /root/config


###############################################################################
# 7.0 Define the ENTRYPOINT
###############################################################################

# Run the application on the Java 10 module path invoking the docker-run configuration profile
# and passing the contents of the SSH Deploy Key.
# Note the 'file.encoding' setting determines how the application reconstitutes
# String information that has been sent down the wire. By choosing the ISO8859-1
# character set this means we can cater for DB files that are encoded the old-school
# (ISO8859-1) way.
ENTRYPOINT java -Dfile.encoding=ISO8859-1 \
           -Dspring.config.location=config/application-docker-run.properties \
           -p lib/jarfile.jar \
           --add-modules ALL-DEFAULT \
           -m jarfile \
           "$KEYSTORE_PASS"
