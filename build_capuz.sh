#!/bin/bash

export DOCKER_BUILDKIT=1
export CACHE_CONFIG="--no-cache"
export CACHE_CONFIG="--progress=plain "
export CACHE_CONFIG=""

# Trim down the local dir because the whole things gets
# sent as context and dragged into the image
rm -f logs/app_trace.log
rm -rf target/
rm -rf project/project/
rm -rf project/target

# Build the base_os image using cache if possible
# Do this in a sub-dir that's empty to keep it as light as possible
#cd docker_base_os
#docker build --rm -t="${DOCKER_REGISTRY}/gumpv2_base_centos7.6:0.0.1" -f Dockerfile.base_os  .
#cd ..
# Based upon CentOs 7.6 with Java 8.0.x
#DOCKER_IMAGE_TAG="${MAJOR_VERSION}.${MINOR_VERSION}.${PATCH_VERSION}-${BUILD_VERSION}"
source ./version_os
BASE_DOCKER_IMAGE_TAG="${BASE_MAJOR_VERSION}.${BASE_MINOR_VERSION}.${BASE_PATCH_VERSION}"

DOCKER_IMAGE_APP_NAME=capuz_base_os

#DOCKER_USER="pl"
#DOCKER_REGISTRY="docker.yp.com"
DOCKER_REGISTRY=dev-sysint-501.np.st1.yellowpages.com:5000

# Build the base_os image using cache if possible
# Do this in a sub-dir that's empty to keep it as light as possible
echo
echo "CentOs 7.6 Java 11.0.x Scala 2.12.12 SBT 1.2.1"
echo "----------------------------------------------------------------"
echo
echo "Capuz BASE: ${BASE_DOCKER_IMAGE_TAG}"
echo
echo "----------------------------------------------------------------"
echo

docker build --rm -t="${DOCKER_IMAGE_FULL_NAME}" -f Dockerfile.base_os  .

# Update the Image Tag Version
awk -F '=' '/PATCH_VERSION/{$2=$2+1}1' OFS='=' version_capuz > version_capuz.new
mv version_capuz.new version_capuz
cat version_capuz

# the release number should match build.sbt version number
source ./version_capuz

DOCKER_IMAGE_FULL_NAME="${DOCKER_REGISTRY}/${DOCKER_IMAGE_APP_NAME}:${DOCKER_IMAGE_TAG}"
echo "Building Image: ${DOCKER_IMAGE_FULL_NAME}"

#DOCKER_IMAGE_TAG="${MAJOR_VERSION}.${MINOR_VERSION}.${PATCH_VERSION}-${BUILD_VERSION}"
DOCKER_IMAGE_TAG="${MAJOR_VERSION}.${MINOR_VERSION}.${PATCH_VERSION}"
DOCKER_IMAGE_FULL_NAME="${DOCKER_REGISTRY}/${DOCKER_IMAGE_APP_NAME}:${DOCKER_IMAGE_TAG}"
echo "Building Image: ${DOCKER_IMAGE_FULL_NAME}"

# Build the gumpv2 image with no-cache to ensure it's clean
docker build --no-cache -t "${DOCKER_IMAGE_FULL_NAME}" --build-arg "VERSION=${BASE_DOCKER_IMAGE_TAG}"  . -f Dockerfile

echo "----------------------------------------------------------------"
echo
echo "Capuz Image: ${DOCKER_IMAGE_FULL_NAME}"
echo
echo "----------------------------------------------------------------"
echo

