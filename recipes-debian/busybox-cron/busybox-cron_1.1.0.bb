SUMMARY = "busybox crond.service"
DESCRIPTION = "Systemd unit file for busybox cron"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://../gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = " \
    file://crond.service \
    file://gpl-2.0.txt \
"

inherit systemd

SYSTEMD_SERVICE_${PN} = "crond.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

inherit systemd

do_install () {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/crond.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/crond.service

    install -d ${D}/var/spool/cron/crontabs
}

FILES_${PN} = " \
    /var/spool/cron/crontabs \
"
