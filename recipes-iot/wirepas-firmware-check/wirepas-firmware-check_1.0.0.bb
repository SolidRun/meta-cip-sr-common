SUMMARY = "Wirepas firmware check"
HOMEPAGE = ""

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=cb6bb17b0d0cca188339074207e9f4d8"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=master;destsuffix=SolidSense-V1;name=SolidSense-V1 \
    file://LICENSE \
"

SRCREV_SolidSense-V1 = "789c7e7fa6990f94e5a3c3bea5de95bb7b1e687b"
S = "${WORKDIR}/SolidSense-V1"

DEPENDS = " \
    c-mesh-api \
"

do_compile_prepend () {
    cd ${S}/wirepas/wirepas_firmware_check
}

do_install () {
    install -d ${D}${base_bindir}
    install -m 0755 ${S}/wirepas/wirepas_firmware_check/wp-get-fw-version ${D}${base_bindir}/wp-get-fw-version
    ln -s ${base_bindir}/wp-get-fw-version ${D}${base_bindir}/sink1-get-version
    ln -s ${base_bindir}/wp-get-fw-version ${D}${base_bindir}/sink2-get-version
}

FILES_${PN} = " \
    ${base_bindir}/wp-get-fw-version \
    ${base_bindir}/sink1-get-version \
    ${base_bindir}/sink2-get-version \
"
