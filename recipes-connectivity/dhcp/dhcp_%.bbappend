# Create dhcpd.conf and enable dhcpd service

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "\
    file://dhcpd-eth0.conf \
    file://dhcpd-eth0.service \
    file://dhcpd-wlan0.conf \
    file://dhcpd-wlan0.service \
"

SYSTEMD_SERVICE_${PN}-server = "dhcpd-eth0.service dhcpd-wlan0.service"
SYSTEMD_AUTO_ENABLE_${PN}-server = "disable"

do_install_append () {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/dhcpd-eth0.conf ${D}${sysconfdir}/dhcpd-eth0.conf
    install -m 0644 ${WORKDIR}/dhcpd-wlan0.conf ${D}${sysconfdir}/dhcpd-wlan0.conf
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/dhcpd-eth0.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/dhcpd-eth0.service
    install -m 0644 ${WORKDIR}/dhcpd-wlan0.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/dhcpd-wlan0.service

    # Remove unwanted dhcp service files
    rm -f ${D}${systemd_unitdir}/system/dhcpd.service
    rm -f ${D}${systemd_unitdir}/system/dhcpd6.service
}

FILES_${PN}-server += " \
    /etc/dhcpd-eth0.conf \
    /etc/dhcpd-wlan0.conf \
"
