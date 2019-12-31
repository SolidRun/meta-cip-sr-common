# Update package groups to remove unavailable packages
#
# Copyright (C) 2016-2018, TOSHIBA Corp., Daniel Sangorrin <daniel.sangorrin@toshiba.co.jp>
# SPDX-License-Identifier:	MIT
RDEPENDS_${PN} += "${PKG_BOOT} ${PKG_SYSTEM} ${PKG_MIN} ${PKG_DEBUG}"

# required for system to boot up
#--------------------------------
PKG_BOOT = "\
	kernel-image \
	kernel-modules \
"

# system requirements
#--------------------
PKG_SYSTEM = "\
	busybox \
	init-files \
	glib-2.0 \
	libgcc \
	libstdc++ \
"

# minimum package list
# TODO: agree on cip-core tiny profile minimal list
#--------------------------------------------------
PKG_MIN = "\
	openssh \
	openssh-sftp \
	openssh-sftp-server \
	gzip \
	e2fsprogs \
	libaio \
	wget \
	curl \
	bash \
	procps \
	coreutils \
	findutils \
	iproute2 \
	iptables \
	gawk \
"

# packages for debugging purposes
#--------------------------------
PKG_DEBUG = "\
	netbase \
	ethtool \
	libpcap \
	tcpdump \
	pciutils \
	i2c-tools \
	htop \
	traceroute \
	iputils-ping \
	usbutils \
	util-linux \
	libpng \
"
