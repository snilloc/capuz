# Base Image as CentOS
# APP_HOME contains the project root and all files from git
# DIST_HOME only contains the built version
# Use APP_HOME for scripts/chronos actions

# use specific centos version to prevent image churning
FROM centos:centos7.6.1810

MAINTAINER capuz-dev@lists.yellowpages.com

# Reduce malloc areas to reduce virtual address space overhead
# http://info.prelert.com/blog/java-8-and-virtual-memory-on-linux
ENV MALLOC_ARENA_MAX 3

RUN gpg2 --keyserver hkp://keys.gnupg.net --recv-keys D39DC0E3

# Update aptitude with new repo
RUN yum -y update; yum clean all ; yum makecache fast; 
RUN yum -y install epel-release
RUN yum upgrade ca-certificates --disablerepo="epel"
RUN yum -y install bzip2 tar gzip git pwgen wget logrotate zip unzip zlib-devel bzip2-devel, libcsv libxml2-devel libxslt-devel bzip2-devel libcsv libcurl-devel openssl openssl-devel
#RUN yum -y install zlib-devel  bzip2-devel
#RUN yum -y install libcsv
#RUN yum -y install libxml2-devel libxslt-devel bzip2-devel libcsv
#RUN yum -y install libcurl-devel
#RUN yum -y install openssl openssl-devel
RUN yum -y install gcc-c++ patch readline readline-devel zlib zlib-devel libyaml-devel libffi-devel e2fsprogs-devel net-tools strace htop diffstat tcpdump screen vim lsof smem
#RUN yum -y install e2fsprogs-devel
#RUN yum -y install net-tools strace htop diffstat tcpdump screen vim lsof smem

# Required for Ruby to run
#sudo yum install git-core zlib zlib-devel gcc-c++ patch readline readline-devel libyaml-devel libffi-devel openssl-devel make bzip2 autoconf automake libtool bison curl sqlite-devel


RUN yum groupinstall -y "Development Tools"
# is this needed for locale settings? RUN yum -y -q reinstall glibc-common

RUN localedef --quiet -v -c -i en_US -f UTF-8 en_US.UTF-8 | true

#RUN bash -c "mkdir -p /tpkg_install && \
#   cd /tpkg_install && \
#   curl -LO https://rubygems.org/downloads/tpkg-2.3.5.gem && \
#   tar xvf tpkg-2.3.5.gem && \
#   tar xvzf *.tar.gz && \
#   gem install facter"
#
#RUN yum clean all

# Set correct environment variables. - These never change
# but we should set them again after localedef
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8
ENV LC_ALL en_US.UTF-8
ENV TZ UTC
ENV NLS_LANG american_america.UTF8
ENV PATH /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/tpkg_install/bin

# maven sometimes installs old jdk
RUN yum -y remove java-1.7.0-openjdk java-1.7.0-openjdk-headless

# install libs for graphviz; gts needs glib2-devel
#RUN yum -y install graphviz-dev graphviz-gd gd-devel fontconfig libpng-devel freetype-devel libtiff-devel libgd-dev
#RUN yum -y install glib2-devel lua-devel
# GTS is required for graphviz sfdf  see http://gts.sourceforge.net/install.html
#RUN bash -c "cd /tmp && \
#    curl -LO "http://gts.sourceforge.net/tarballs/gts-snapshot-121130.tar.gz" && \
#    tar xvzf gts-snapshot-121130.tar.gz && \
#    cd /tmp/gts-snapshot-121130 && \
#    ./configure && make && make install && \
#    rm -rf /tmp/gts-snapshot-121130 && \
#    rm /tmp/gts-snapshot-121130.tar.gz"
# Unfortunately, graphviz team quit building binaries - this is the hard way
#RUN bash -c "mkdir -p /tmp/graphviz_install && cd /tmp/graphviz_install && \
#    curl -LO https://graphviz.gitlab.io/pub/graphviz/stable/SOURCES/graphviz.tar.gz && \
#    tar xvzf graphviz.tar.gz && \
#    cd graphviz* && \
#    ./configure --with-gts=yes && make && make install && \
#    cd /tmp && rm -rf /tmp/graphviz_install"

RUN yum clean all

# Create Common App Directories once
RUN  mkdir -p /apps


#RUN /bin/bash -l -c "openssl req -x509 -sha256 -nodes -days 365 -newkey rsa:2048 -keyout privateKey.key -out /work/certificate.crt"

# Install SDKMAN!
# RUN curl -s "https://get.sdkman.io" | bash
ENV SDKMAN_DIR=/apps/sdkman
RUN /bin/bash -l -c "echo SKDMAN_DIR ${SDKMAN_DIR}"

RUN curl -s "https://get.sdkman.io" | bash
#RUN source "$HOME/.sdkman/bin/sdkman-init.sh"

#RUN  echo '[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"' >> $HOME/.bash_profile
RUN  echo '[[ -s "/apps/sdkman/bin/sdkman-init.sh" ]] && source "/apps/sdkman/bin/sdkman-init.sh"' >> $HOME/.bash_profile

# Note Quarterly releases - Jan 21, April 22, etc...
ENV JDK_VERSION 11.0.8.hs-adpt
#ENV SCALA_VERSION 2.13.3
ENV SCALA_VERSION 2.12.12

#ENV SBT_VERSION 1.3.13
#ENV SBT_VERSION 1.2.1
# Bug in 1.1.0 when executing sbt
ENV SBT_VERSION 1.1.6

# Important - these will run in the current user home dir
# which is /root from the base_os image
# If you change the HOME, it'll screw these up
RUN /bin/bash -l -c 'sdk update'

RUN /bin/bash -l -c 'sdk install java "${JDK_VERSION}"'
RUN /bin/bash -l -c 'sdk install scala "${SCALA_VERSION}"'
RUN /bin/bash -l -c 'sdk install sbt "${SBT_VERSION}"'

# The piccolo.link url shortener used by SBT team is trash and throws 503s
# SBT and sdkman teams should stop using piccolo.link
##RUN /bin/bash -l -c "curl -L https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.zip -o /apps//root/.sdkman/archives/sbt-${SBT_VERSION}.zip"
#RUN /bin/bash -l -c "curl -L https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.zip -o /apps/sdkman/archives/sbt-${SBT_VERSION}.zip"
#RUN /bin/bash -l -c 'sdk install sbt "${SBT_VERSION}"'

RUN /bin/bash -l -c "rm -rf /apps/sdkman/archives"  ### reduce image size

RUN /bin/bash -l -c 'sdk use java "${JDK_VERSION}"'
RUN /bin/bash -l -c 'sdk use scala "${SCALA_VERSION}"'
RUN /bin/bash -l -c 'sdk use sbt "${SBT_VERSION}"'


# Install coursier for faster resolver
# requires further project level config using coursier.sbt
RUN /bin/bash -l -c "mkdir -p /apps/bin && \
      curl -L -o /apps/bin/coursier https://git.io/vgvpD && \
      chmod +x /apps/bin/coursier"

ENV PATH /apps/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/tpkg_install/bin

# Init sbt 
RUN /bin/bash -l -c "cd /apps && mkdir -p sbtinit_${SBT_VERSION}/project && echo 'sbt.version=${SBT_VERSION}' >> /apps/sbtinit_${SBT_VERSION}/project/build.properties  && \
         cd /apps/sbtinit_${SBT_VERSION} && sbt version"

#RUN /bin/bash -l -c "echo 'sbt.version=${SBT_VERSION}' >> /apps/sbtinit_${SBT_VERSION}/project/build.properties "
#RUN /bin/bash -l -c "ls -al /apps/sbtinit_${SBT_VERSION}"
#RUN /bin/bash -l -c "cd /apps/sbtinit_${SBT_VERSION} && sbt version"
#RUN /bin/bash -l -c "sdk use sbt ${SBT_VERSION}"

# Init sbt 1.3.18 -- still used in some apps
#RUN /bin/bash -l -c "cd /apps && mkdir -p sbtinit_1.3.13/project && \
#        echo 'sbt.version=1.3.13' >> /apps/sbtinit_1.3.13/project/build.properties && \
#        cd /apps/sbtinit_1.3.13 && sbt version"

# There are no suitable defaults to run so drop into bash
CMD ["bash"]

