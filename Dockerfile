ARG VERSION=0.0
FROM dev-sysint-501.np.st1.yellowpages.com:5000/capuz_base_os:$VERSION
ARG VERSION=$VERSION
FROM dev-sysint-501.np.st1.yellowpages.com:5000/capuz_base_os:$VERSION

LABEL maintainer=capuz-dev@lists.yellowpages.com

# Note Quarterly releases - Jan 21, April 22, etc...
ENV JDK_VERSION 11.0.8.hs-adpt
ENV SCALA_VERSION 2.13.3
ENV SBT_VERSION 1.3.13

#RUN /bin/bash -l -c 'sdk use java "${JDK_VERSION}"'
#RUN /bin/bash -l -c 'sdk use scala "${SCALA_VERSION}"'
#RUN /bin/bash -l -c 'sdk use sbt "${SBT_VERSION}"'

# HOME holds various installation/build artifacts like .sbt and .coursier
ENV HOME /apps

# APP_HOME holds the non-built project (git repo contents)
ENV APP_HOME /capuz_docker

# DIST_HOME holds the built project artifacts
ENV DIST_HOME /apps/dist_capuz

# Presence Project Setup
RUN /bin/bash -l -c "mkdir -p $DIST_HOME && \
   mkdir -p $HOME/.sbt"

COPY ./project/build.properties /apps/dist_capuz/.

# Copy Files to Docker Container
COPY . /capuz_docker

ENV PATH /apps/sdkman/candidates/scala/current/bin:/apps/sdkman/candidates/sbt/current/bin:/apps/sdkman/candidates/java/current/bin:/apps/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/tpkg_install/bin

# Copy Certificates


# Using CA-Authority certificate
#COPY ./mycert.crt /usr/local/share/ca-certificates/mycert.crt
# RUN update-ca-certificates

# Trim the image down before doing more
RUN rm -rf /capuz_docker/target/ /capuz_docker/project/project /capuz_docker/project/target

# Setup artifactory cache references
# ADD project/artifactory.repositories $HOME/.sbt/repositories

# Configure coursier from project level coursier.sbt
#RUN /bin/bash -l -c "mkdir -p ~/.sbt/0.13/plugins && \
#      cat $APP_HOME/project/coursier.sbt >> $HOME/.sbt/0.13/plugins/build.sbt"

# Init sbt
#RUN /bin/bash -l -c "cd /apps && mkdir -p sbtinit_${SBT_VERSION}/project && \
#        echo 'sbt.version=${SBT_VERSION}' >> /apps/sbtinit_${SBT_VERSION}/project/build.properties && \
#        cd /apps/sbtinit_${SBT_VERSION} && sbt version"

# Init sbt 0.13.18 -- still used in some apps
#RUN /bin/bash -l -c "cd /apps && mkdir -p sbtinit_0.13.18/project && \
#        echo 'sbt.version=0.13.18' >> /apps/sbtinit_0.13.18/project/build.properties && \
#        cd /apps/sbtinit_0.13.18 && sbt version"

#      source "/root/.sdkman/bin/sdkman-init.sh" && \
# -J-Dsbt.override.build.repos=true
RUN /bin/bash -l -c 'cd $APP_HOME && \
		  echo $HOME && \
      export JAVA_OPTS="-Xms256m -Xmx1200m" && \
      cp /capuz_docker/project/build.properties "${DIST_HOME}/." && \
	    source "/apps/sdkman/bin/sdkman-init.sh" && \
		  cat "${DIST_HOME}/build.properties" && \
		  export PATH="/apps/sdkman/candidates/scala/current/bin:/apps/sdkman/candidates/sbt/current/bin:/apps/sdkman/candidates/java/current/bin:${PATH}" && \
		  sdk use java "${JDK_VERSION}" && \
		  sdk use scala "${SCALA_VERSION}" && \
			sdk use sbt "${SBT_VERSION}" && \
      echo "PATH=${PATH}" && \
      sbt -J-Dsbt.override.build.repos=true -J-Dsbt.repository.config=/apps/.sbt/repositories clean universal:package-zip-tarball && \
      cd "${DIST_HOME}" && \
		  echo "\n Tar Zip package" && \
      tar xvzf "${APP_HOME}"/target/universal/capuz-*.tgz && \
		  echo "Removing unused directories" && \
      rm -rf /capuz_docker/target/ /capuz_docker/project/project /capuz_docker/project/target && \
      rm -rf ~/.coursier && \
      rm -rf ~/.ivy2 && \
      rm -rf ~/.m2'

#RUN cp -R $PROJECT_WORKPLACE/build/target/universal/stage $PROJECT_WORKPLACE/app
WORKDIR /capuz_docker

EXPOSE 9111

# There are no suitable defaults to run so drop into bash
CMD ["/bin/bash -l"]

