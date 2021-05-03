DESCRIPTION = "NRF52 Direct Test Mode firmware for Nina B1/B3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://../nRF5_Nordic_license.txt;md5=78bb1d35754de6fec4817e9a8c20405f \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    file://dtm-nina-b1-${PV}.hex \
    file://dtm-nina-b3-${PV}.hex \
    file://nRF5_Nordic_license.txt \
    file://nrf52832_xxaa.hex \
"

FIRMWARE_DIR = "/opt/certification/nrf52"

do_install () {
    install -d ${D}${FIRMWARE_DIR}
    install -m 0644 ${WORKDIR}/nrf52832_xxaa.hex ${D}${FIRMWARE_DIR}/nrf52832_xxaa.hex
    install -m 0644 ${WORKDIR}/dtm-nina-b1-${PV}.hex ${D}${FIRMWARE_DIR}/dtm-nina-b1-${PV}.hex
    install -m 0644 ${WORKDIR}/dtm-nina-b3-${PV}.hex ${D}${FIRMWARE_DIR}/dtm-nina-b3-${PV}.hex
    install -m 0644 ${WORKDIR}/nRF5_Nordic_license.txt ${D}${FIRMWARE_DIR}/nRF5_Nordic_license.txt
    ln -s ${FIRMWARE_DIR}/dtm-nina-b1-${PV}.hex ${D}${FIRMWARE_DIR}/dtm-nina-b1.hex
    ln -s ${FIRMWARE_DIR}/dtm-nina-b3-${PV}.hex ${D}${FIRMWARE_DIR}/dtm-nina-b3.hex
}

FILES_${PN} = " \
  /opt \
  /opt/certification \
  /opt/certification/nrf52 \
  /opt/certification/nrf52/dtm-nina-b3.hex \
  /opt/certification/nrf52/dtm-nina-b1.hex \
  /opt/certification/nrf52/dtm-nina-b3-17.0.2.hex \
  /opt/certification/nrf52/dtm-nina-b1-17.0.2.hex \
  /opt/certification/nrf52/nrf52832_xxaa.hex \
  /opt/certification/nrf52/nRF5_Nordic_license.txt \
"
