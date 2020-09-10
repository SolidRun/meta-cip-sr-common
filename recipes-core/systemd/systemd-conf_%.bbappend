FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://journald.conf \
    file://system.conf \
"

do_install_append () {
    install -d ${D}${sysconfdir}/systemd
    rm -f ${D}${sysconfdir}/systemd/journald.conf
    install -m 0644 ${WORKDIR}/journald.conf ${D}${sysconfdir}/systemd/journald.conf

    rm -f ${D}${sysconfdir}/systemd/system.conf
    install -m 0644 ${WORKDIR}/system.conf ${D}${sysconfdir}/systemd/system.conf
}
