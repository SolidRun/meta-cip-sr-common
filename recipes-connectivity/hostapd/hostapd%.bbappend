# Create soft link for hostapd.conf
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
   file://hostapd-wlan0.conf \
   file://hostapd-wlan0.service \
"

SYSTEMD_SERVICE_${PN} = "hostapd-wlan0.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"
INITSCRIPT_PARAMS = "remove"

do_install_append () {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/hostapd-wlan0.conf ${D}${sysconfdir}/hostapd-wlan0.conf

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/hostapd-wlan0.service ${D}${systemd_unitdir}/system/
    sed -i -e 's,@SBINDIR@,${sbindir},g' -e 's,@SYSCONFDIR@,${sysconfdir},g' ${D}${systemd_unitdir}/system/hostapd-wlan0.service

    # Remove hostapd.service
    rm -f ${D}${systemd_unitdir}/system/hostapd.service
    rm -f ${D}${sysconfdir}/init.d/hostapd
}
