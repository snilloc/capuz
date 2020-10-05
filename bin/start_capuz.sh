#!/bin/bash

APP_NAME="capuz"
DOCKER_REGISTRY=dev-sysint-501.np.st1.yellowpages.com:5000

if [ "$#" -ne 2 ]; then
  echo
  echo "Illegal number of parameters"
  echo "Need version and environment"
  echo "Usages: "
  echo "    <version Ex. 4.12.2> <env - dev, test, stage, prod>"
  echo
  echo "   Example: start_capuz.sh 1.11.1 dev"
  echo
  exit
else
  VERSION=$1
  ENVIRONMENT=$2
  APP_ENV=$2
  LOGGING_ENV=$2
fi

CONTAINER_NAME="${APP_NAME}"
DOCKER_IMAGE_NAME="${DOCKER_REGISTRY}/${APP_NAME}:${VERSION}"

echo
echo "Running Docker Image: ${DOCKER_IMAGE_NAME}"
echo

mkdir -p `pwd`/logs

case $ENVIRONMENT in
    prod)
        JVM_MS_ARG="-J-Xms1G"
        JVM_MX_ARG="-J-Xmx8G"
        ;;
    test | stage)
        JVM_MS_ARG="-J-Xms1G"
        JVM_MX_ARG="-J-Xmx2G"
        ;;
    dev | local)
        JVM_MS_ARG="-J-Xms1G"
        JVM_MX_ARG="-J-Xmx2G"
        ;;
    *)
        JVM_MS_ARG="-J-Xms1G"
        JVM_MX_ARG="-J-Xmx2G"
        ;;
esac

echo "APP_ENV=${APP_ENV} LOGGING_ENV=${LOGGING_ENV} JVM_MS_ARG=${JVM_MS_ARG}  JVM_MX_ARG=${JVM_MX_ARG} "

mkdir -p `pwd`/logs

ignorecmd=`sudo docker stop ${CONTAINER_NAME} && sudo docker rm ${CONTAINER_NAME}`

OS=`uname -s`
if [ "${OS}" = "Darwin" ] ; then
    PORT_SPEC="-P"  # explicit port mapping doesnt work so we have to take the random ones in container
else
    PORT_SPEC="--net=host -p 9211:9211"
fi

echo "sudo docker run -d ${PORT_SPEC} \
    -e DOCKER_HOST=`hostname` \
    -e ENVIRONMENT="${APP_ENV}" \
    -e APP_ENV="${APP_ENV}" \
    -e LOGGING_ENV="${LOGGING_ENV}" \
    -v `pwd`/logs:/capuz_docker/logs \
    --name "${CONTAINER_NAME}" \
    $DOCKER_IMAGE_NAME  \
    bash -l -c "/apps/dist_capuz/capuz/bin/capuz  \
        ${JVM_MS_ARG} ${JVM_MX_ARG} -J-Xss2m  \
        -J-Dlog4j.debug \
        -Dhttp.port=4111 \
        -Dconfig.file=/apps/dist_capuz/capuz/conf/capuz-${APP_ENV}.conf \
        -Dlogger.file=/apps/dist_capuz/capuz/conf/${LOGGING_ENV}-logback.xml \
        -Dlogback.configurationFile=/apps/dist_capuz/capuz/conf/${LOGGING_ENV}-logback.xml \
        -Dlogger.resource=${LOGGING_ENV}-logback.xml"



sudo docker run -d ${PORT_SPEC} \
    -e DOCKER_HOST=`hostname` \
    -e ENVIRONMENT="${APP_ENV}" \
    -e APP_ENV="${APP_ENV}" \
    -e LOGGING_ENV="${LOGGING_ENV}" \
    -v `pwd`/logs:/capuz_docker/logs \
    --name "${CONTAINER_NAME}" \
    $DOCKER_IMAGE_NAME  \
    bash -l -c "/apps/dist_capuz/capuz/bin/capuz  \
        ${JVM_MS_ARG} ${JVM_MX_ARG} -J-Xss2m  \
        -J-Dlog4j.info \
        -Dhttp.port=4111 \
        -Dconfig.file=/apps/dist_capuz/capuz/conf/capuz-${APP_ENV}.conf \
        -Dlogger.file=/apps/dist_capuz/capuz/conf/${LOGGING_ENV}-logback.xml \
        -Dlogback.configurationFile=/apps/dist_capuz/capuz-2.0/conf/${LOGGING_ENV}-logback.xml \
        -Dlogger.resource=${LOGGING_ENV}-logback.xml"

