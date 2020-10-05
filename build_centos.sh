#!/bin/bash
# Based upon CentOs 7.6 with Java 11.0.x

export DOCKER_BUILDKIT=1

source ./version_os

#DOCKER_USER="pl"
#DOCKER_REGISTRY="docker.yp.com"
DOCKER_REGISTRY=dev-sysint-501.np.st1.yellowpages.com:5000

DOCKER_IMAGE_APP_NAME=capuz_base_os
#BUILD_VERSION="b1"
#DOCKER_IMAGE_TAG="${MAJOR_VERSION}.${MINOR_VERSION}.${PATCH_VERSION}-${BUILD_VERSION}"
DOCKER_IMAGE_TAG="${BASE_MAJOR_VERSION}.${BASE_MINOR_VERSION}.${BASE_PATCH_VERSION}"

rm -f logs/app_trace.log
rm -rf target/
rm -rf project/project/
rm -rf project/target

# Build the base_os image using cache if possible
# Do this in a sub-dir that's empty to keep it as light as possible
DOCKER_IMAGE_FULL_NAME="${DOCKER_REGISTRY}/${DOCKER_IMAGE_APP_NAME}:${DOCKER_IMAGE_TAG}"
echo "Building Image: ${DOCKER_IMAGE_FULL_NAME}"

docker build --rm -t="${DOCKER_IMAGE_FULL_NAME}" -f Dockerfile.os  .

echo "Capuz CentOs Complete"
