SUMMARY = "Linux-CAN SocketCAN user space applications"
HOMEPAGE = "https://github.com/linux-can/can-utils"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSES/BSD-3-Clause;md5=4c00cf8b0a04a9441d8fa24850231d00"

SRC_URI = " \
    git://github.com/linux-can/can-utils.git;branch=master;destsuffix=can-utils;name=can-utils \
"

SRCREV_can-utils = "da65fdfe0d1986625ee00af0b56ae17ec132e700"
S = "${WORKDIR}/can-utils"

inherit autotools cmake

FILES_${PN} = " \
  /usr/bin/jspy \
  /usr/bin/isotpsend \
  /usr/bin/isotpdump \
  /usr/bin/canbusload \
  /usr/bin/isotpsniffer \
  /usr/bin/slcand \
  /usr/bin/log2asc \
  /usr/bin/canplayer \
  /usr/bin/cangw \
  /usr/bin/jsr \
  /usr/bin/testj1939 \
  /usr/bin/slcan_attach \
  /usr/bin/isotpserver \
  /usr/bin/asc2log \
  /usr/bin/canlogserver \
  /usr/bin/bcmserver \
  /usr/bin/jcat \
  /usr/bin/isotpperf \
  /usr/bin/slcanpty \
  /usr/bin/isotprecv \
  /usr/bin/can-calc-bit-timing \
  /usr/bin/log2long \
  /usr/bin/canfdtest \
  /usr/bin/jacd \
  /usr/bin/isotptun \
  /usr/bin/candump \
  /usr/bin/cangen \
  /usr/bin/cansend \
  /usr/bin/cansniffer \
"
