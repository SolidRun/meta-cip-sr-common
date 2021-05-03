SUMMARY = "PM2 files for cerfification testing"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = " \
    file://bluetooth1.js \
    file://bluetooth2.js \
    file://bluetooth3.js \
    file://disks.sh \
    file://gstreamer.sh \
    file://jobfile.fio \
    file://lte.js \
    file://test.config.js \
    file://wifi.js \
    file://LICENSE.GPL2 \
"

do_install () {
    install -d ${D}/opt/certification/test
    install -m 0755 ${WORKDIR}/bluetooth1.js ${D}/opt/certification/test/bluetooth1.js
    install -m 0755 ${WORKDIR}/bluetooth2.js ${D}/opt/certification/test/bluetooth2.js
    install -m 0755 ${WORKDIR}/bluetooth3.js ${D}/opt/certification/test/bluetooth3.js
    install -m 0755 ${WORKDIR}/disks.sh ${D}/opt/certification/test/disks.sh
    install -m 0755 ${WORKDIR}/gstreamer.sh ${D}/opt/certification/test/gstreamer.sh
    install -m 0755 ${WORKDIR}/jobfile.fio ${D}/opt/certification/test/jobfile.fio
    install -m 0755 ${WORKDIR}/lte.js ${D}/opt/certification/test/lte.js
    install -m 0755 ${WORKDIR}/test.config.js ${D}/opt/certification/test/test.config.js
    install -m 0755 ${WORKDIR}/wifi.js ${D}/opt/certification/test/wifi.js
}

FILES_${PN} = " \
  /opt \
  /opt/certification \
  /opt/certification/test \
  /opt/certification/test/jobfile.fio \
  /opt/certification/test/disks.sh \
  /opt/certification/test/bluetooth3.js \
  /opt/certification/test/bluetooth1.js \
  /opt/certification/test/wifi.js \
  /opt/certification/test/bluetooth2.js \
  /opt/certification/test/gstreamer.sh \
  /opt/certification/test/lte.js \
  /opt/certification/test/test.config.js \
"
