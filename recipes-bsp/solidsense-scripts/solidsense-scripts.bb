SUMMARY = "Solidsense miscellaneous scripts"
DESCRIPTION = "Solidsense miscellaneous scripts"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://../gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
   file://bind9.init \
   file://check_solidsense.service \
   file://check_solidsense.sh \
   file://gpl-2.0.txt \
   file://restart.sh \
   git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=V1.1;destsuffix=SolidSense-V1;name=SolidSense-V1 \
"
SRCREV_SolidSense-V1 = "a31f0edd32d684403e924480f851e5380b6a59e2"
S-V1 = "${WORKDIR}/SolidSense-V1"

SYSTEMD_SERVICE_${PN} = "check_solidsense.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

inherit systemd

do_install () {
    install -d ${D}/opt/scripts
    install -d ${D}/opt/SolidSense/bin
    install -m 0755 ${WORKDIR}/check_solidsense.sh ${D}/opt/scripts/check_solidsense
    install -m 0755 ${WORKDIR}/restart.sh ${D}/opt/scripts/restart
    ln -s /opt/scripts/restart ${D}/opt/SolidSense/bin/restart

    install -m 0755 ${S-V1}/ublox/flash_ublox.sh ${D}/opt/scripts/flash_ublox

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
    /opt/scripts/flash_ublox \
    /opt/scripts/restart \
    /opt/SolidSense/bin/restart \
"

COMPATIBLE_MACHINE = "solidsense|n6gq|n6gsdl"
