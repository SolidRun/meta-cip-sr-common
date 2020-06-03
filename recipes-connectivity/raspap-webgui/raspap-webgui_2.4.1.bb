SUMMARY = "RaspAP WiFi Configuration Portal"
DESCRIPTION = "Simple AP setup & WiFi management for Debian-based devices https://raspap.com"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=4fe869ee987a340198fb0d54c55c47f1 \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
   git://github.com/billz/raspap-webgui.git;protocol=https;branch=master;destsuffix=raspap;name=raspap \
   file://networking.defaults \
   file://hostapd.ini \
"
SRCREV_raspap = "7cc196f4f5284255ed8173e928cb5f6793fb82e5"
S = "${WORKDIR}/raspap"

RDEPENDS_${PN} = "bash dhcp-server dnsmasq hostapd php php-cli php-cgi php-fpm lighttpd sudo"

SYSTEMD_SERVICE_${PN} = "raspapd.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

inherit systemd

do_install () {
    # install the www files
    install -d ${D}${localstatedir}/www/html
    cp -a ${S}/* ${D}${localstatedir}/www/html
    rm -rf ${D}${localstatedir}/www/html/installers
    chown -R 33:33 ${D}${localstatedir}/www

    # install sudoers.d file
    install -d ${D}${sysconfdir}/sudoers.d
    install -m 0600 ${S}/installers/raspap.sudoers ${D}${sysconfdir}/sudoers.d/090_raspap

    # create RaspAP configuration directories
    install -d ${D}${sysconfdir}/raspap/backups
    install -d ${D}${sysconfdir}/raspap/networking
    install -d ${D}${sysconfdir}/raspap/lighttpd
    install -d ${D}${sysconfdir}/raspap/hostapd
    chown -R 33:33 ${D}${sysconfdir}/raspap

    # install default networking setting
    install -m 0644 ${WORKDIR}/networking.defaults ${D}${sysconfdir}/raspap/networking/defaults
    chown 33:33 ${D}${sysconfdir}/raspap/networking/defaults

    # install default hostapd.ini
    install -m 0644 ${WORKDIR}/hostapd.ini ${D}${sysconfdir}/raspap/hostapd.ini
    chown 33:33 ${D}${sysconfdir}/raspap/hostapd.ini

    # install hostapd logging and service control scripts
    install -m 0750 ${S}/installers/disablelog.sh ${D}${sysconfdir}/raspap/hostapd
    install -m 0750 ${S}/installers/enablelog.sh ${D}${sysconfdir}/raspap/hostapd
    install -m 0750 ${S}/installers/servicestart.sh ${D}${sysconfdir}/raspap/hostapd
    chown -R 0:33 ${D}${sysconfdir}/raspap/hostapd/*.sh

    # install the lighttpd control scripts
    install -m 0750 ${S}/installers/configport.sh ${D}${sysconfdir}/raspap/lighttpd
    chown -R 0:33 ${D}${sysconfdir}/raspap/lighttpd/*.sh

    # install service
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/installers/raspapd.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/raspapd.service

    # create missing /etc/hostapd
    install -d ${D}${sysconfdir}/hostapd
}
