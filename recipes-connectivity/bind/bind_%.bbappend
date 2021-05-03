FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://rndc.conf \
    file://named.service \
"

SYSTEMD_SERVICE_${PN} = "named.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"
INITSCRIPT_PARAMS = "remove"

do_install_append () {
    rm -f ${D}${sysconfdir}/bind/rndc.conf
    install -d ${D}${sysconfdir}/bind
    install -m 0644 ${WORKDIR}/rndc.conf ${D}${sysconfdir}/bind/rndc.conf

    install -d ${D}/var/named
    chown 991:988 ${D}/var/named

    # remove named.conf
    rm ${D}/${sysconfdir}/bind/named.conf
}
