SUMMARY = "Solidsense miscellaneous scripts"
DESCRIPTION = "Solidsense miscellaneous scripts"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://../gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
   file://check_solidsense.service \
   file://check_solidsense.sh \
   file://gpl-2.0.txt \
   file://bind9.init \
"
SYSTEMD_SERVICE_${PN} = "check_solidsense.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

inherit systemd

do_install () {
    install -d ${D}/opt/scripts
    install -m 0755 ${WORKDIR}/check_solidsense.sh ${D}/opt/scripts/check_solidsense

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/check_solidsense.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/check_solidsense.service

    install -d ${D}/etc/init.d
    install -m 0755 ${WORKDIR}/bind9.init ${D}/etc/init.d/bind9

    ln -s /etc/solidsense ${D}/etc/solidsense_device
}

FILES_${PN} = " \
    /etc/init.d/bind9 \
    /etc/solidsense_device \
    /opt/scripts/check_solidsense \
"

COMPATIBLE_MACHINE = "solidsense|n6gq|n6gsdl"
