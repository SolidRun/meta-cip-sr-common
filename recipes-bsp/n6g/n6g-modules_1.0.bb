SUMMARY = "Solidsense N6G rtc"
DESCRIPTION = "Solidsense N6G rtc"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://../gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    file://gpl-2.0.txt \
    file://modules-load.d-rtc0.conf \
    file://modules-load.d-rtc1.conf \
"

do_install () {
    install -d ${D}${sysconfdir}/modules-load.d
    install -m 0644 ${WORKDIR}/modules-load.d-rtc0.conf ${D}${sysconfdir}/modules-load.d/rtc0.conf
    install -m 0644 ${WORKDIR}/modules-load.d-rtc1.conf ${D}${sysconfdir}/modules-load.d/rtc1.conf
}

COMPATIBLE_MACHINE = "(n6gq|n6gsdl)"
