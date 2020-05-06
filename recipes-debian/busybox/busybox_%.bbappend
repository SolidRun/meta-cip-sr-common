FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://fragment.cfg;subdir=busybox-1.30.1 \
"

SYSTEMD_SERVICE_${PN}-syslog = ""

do_install_prepend () {
    sed -i "s/\/sbin\/init//" busybox.links.*
}

do_install_append () {
    install -d ${D}${sysconfdir}/cron/crontabs
    install -d ${D}/mnt/usb1
}

FILES_${PN} += " \
    /etc/cron/crontabs \
    /mnt/usb1 \
"
