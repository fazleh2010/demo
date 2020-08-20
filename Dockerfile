FROM maven:3.5.2-jdk-8-alpine AS MAVEN_TOOL_CHAIN
# copy in tbx2rdf sources
COPY pom.xml /tmp/
COPY src /tmp/src/
COPY repo /tmp/repo/
COPY doc /tmp/doc/
WORKDIR /tmp/
# compile and package tbx2rdf
RUN mvn package

#FROM tomcat:9.0-jre8-alpine
FROM openjdk:8-jre-stretch
#COPY --from=MAVEN_TOOL_CHAIN /tmp/target/wizard*.war $CATALINA_HOME/webapps/wizard.war

#FROM alpine:latest
#COPY --from=MAVEN_TOOL_CHAIN /tmp/target/* 

# base image enhancements
RUN apt-get update -yq 
RUN apt-get update && apt-get install -y bash git python-setuptools psmisc procps curl vim gnupg raptor2-utils apt-transport-https

# node installation
RUN curl -sL https://deb.nodesource.com/setup_11.x -o node_setup.sh
RUN chmod +x node_setup.sh && ./node_setup.sh

RUN apt-get -y install nodejs
RUN npm cache clean -f

# environment variable for headless php installation
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y php7.0-cli php7.0-mbstring php7.0-mcrypt php7.0-xml

#WORKDIR /tmp/

WORKDIR /tmp/virtinstall/

# virtuoso - install adapted from https://github.com/askomics/docker-virtuoso and https://github.com/tenforce/docker-virtuoso/blob/master/Dockerfile

# virtuoso - Environment variables
ENV VIRTUOSO https://github.com/openlink/virtuoso-opensource.git
ENV VIRTUOSO_DIR /virtuoso-opensource
ENV VIRTUOSO_VERSION 7.2.5.1

# virtuoso - Install prerequisites, Download, Patch, compile and install
RUN apt-get update \
        && apt-get install -y git build-essential autotools-dev autoconf automake unzip wget net-tools libtool flex bison gperf gawk m4 libssl-dev libreadline-dev openssl crudini \
        && apt-get install -y libssl1.0-dev \
	&& git clone -b v${VIRTUOSO_VERSION} --single-branch --depth=1 ${VIRTUOSO} ${VIRTUOSO_DIR} \
	&& sed -i 's/maxrows\ \:\= 1024\*1024/maxrows\ \:\=  64\*1024\*1024\-2/' ${VIRTUOSO_DIR}/libsrc/Wi/sparql_io.sql \
	&& cd ${VIRTUOSO_DIR} \ 
        && ./autogen.sh \
        && export CFLAGS="-O2 -m64" && ./configure --disable-bpel-vad --enable-conductor-vad --enable-fct-vad --disable-dbpedia-vad --disable-demo-vad --disable-isparql-vad --disable-ods-vad --disable-sparqldemo-vad --disable-syncml-vad --disable-tutorial-vad --with-readline --program-transform-name="s/isql/isql-v/" \
        && make -j`nproc` && make install \
        && ln -s /usr/local/virtuoso-opensource/var/lib/virtuoso/ /var/lib/virtuoso \
        && ln -s /var/lib/virtuoso/db /data \
        && cd .. \
        && rm -r ${VIRTUOSO_DIR} \
        && apt remove --purge -y build-essential autotools-dev autoconf automake unzip wget net-tools libtool flex bison gperf gawk m4 libssl-dev libreadline-dev \
        && apt autoremove -y \
        && apt autoclean

# mariadb
RUN curl -LsS https://downloads.mariadb.com/MariaDB/mariadb_repo_setup | bash
RUN apt-get -yq update && \
    echo 'debconf debconf/frontend select Noninteractive' | debconf-set-selections && \ 
   DEBIAN_FRONTEND=noninteractive apt-get -yq install mariadb-server mariadb-client 
RUN mkdir -p /var/run/mysqld && chown mysql:mysql /var/run/mysqld/

# virtuoso - Add Virtuoso bin to the PATH
ENV PATH /usr/local/virtuoso-opensource/bin/:$PATH

# virtuoso - Copy files
RUN mkdir -p /virtuoso_data/
COPY ./docker-virtuoso/virtuoso.ini /virtuoso_data/virtuoso.ini
COPY ./docker-virtuoso/dump_nquads_procedure.sql /virtuoso_data/dump_nquads_procedure.sql
COPY ./docker-virtuoso/clean-logs.sh /virtuoso_data/clean-logs.sh
COPY ./docker-virtuoso/virtuoso.sh /virtuoso_data/virtuoso.sh
COPY ./container-files/startall.sh /startall.sh

# project and maven setup from previous chain
COPY --from=MAVEN_TOOL_CHAIN /tmp/ /tmp/
COPY samples /tmp/samples/
COPY *.properties /tmp/
COPY *.yaml /tmp/
COPY mappings.default /tmp/
COPY ontology /tmp/ontology/
COPY server /tmp/server/

# switch to API server and install requirements
WORKDIR /tmp/server/
RUN npm install

WORKDIR /tmp/

# configure periodic health check
HEALTHCHECK --interval=1m --timeout=3s CMD wget --quiet --tries=1 --spider http://localhost:8080/health_check || exit 1

EXPOSE 8080

# CMD npm start 
# CMD /virtuoso.sh
WORKDIR /tmp/server/
CMD /startall.sh
