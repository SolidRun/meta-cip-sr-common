SUMMARY = "Get/Set RS485 configuration of TTY"
HOMEPAGE = "https://github.com/mniestroj/rs485conf"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ccd0d515ae4a40d00b51fa2f87043685"

SRC_URI = " \
    git://github.com/mniestroj/rs485conf.git;branch=master;destsuffix=rs485conf;name=rs485conf \
"

SRCREV_rs485conf = "5c8d00cf70950fab3454549b81dea843d844492a"
S = "${WORKDIR}/rs485conf"

do_install () {
    install -d ${D}${base_bindir}
    install -m 0755 ${S}/rs485conf ${D}${base_bindir}/rs485conf
}

FILES_${PN} = " \
    ${base_bindir}/rs485conf \
"
