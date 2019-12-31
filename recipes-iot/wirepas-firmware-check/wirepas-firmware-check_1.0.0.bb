SUMMARY = "Wirepas firmware check"
HOMEPAGE = ""

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=cb6bb17b0d0cca188339074207e9f4d8"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=V0.911;destsuffix=SolidSense-V1;name=SolidSense-V1 \
    file://LICENSE \
"

SRCREV_SolidSense-V1 = "25052bbc277a0b690fec8c94512c0c005b9ac1aa"
S-V1 = "${WORKDIR}/SolidSense-V1"

do_compile_prepend () {
    cd ${S-V1}/wirepas/wirepas_firmware_check
}

do_install () {
    install -d ${D}${base_bindir}
    install -m 0755 ${S-V1}/wirepas/wirepas_firmware_check/wp-get-fw-version ${D}${base_bindir}/wp-get-fw-version
}

FILES_${PN} = " \
    ${base_bindir}/wp-get-fw-version \
"
