FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=V0.911;destsuffix=SolidSense-V1;name=SolidSense-V1 \
    file://iptables-flush \
    file://iptables.service \
    file://ipv4-forward.conf \
    file://ip6tables.service \
    file://ipv6-forward.conf \
"
SRCREV_SolidSense-V1 = "25052bbc277a0b690fec8c94512c0c005b9ac1aa"
S-V1 = "${WORKDIR}/SolidSense-V1"

SYSTEMD_SERVICE_${PN} += "iptables.service ip6tables.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

inherit systemd

do_install_append () {
    install -d ${D}/usr/lib/systemd/scripts
    install -m 0755 ${WORKDIR}/iptables-flush ${D}/usr/lib/systemd/scripts

    install -d ${D}${sysconfdir}/sysconfig
    install -m 0644 ${S-V1}/Kura/data/iptables ${D}${sysconfdir}/sysconfig/iptables
    install -m 0644 ${S-V1}/Kura/data/ip6tables ${D}${sysconfdir}/sysconfig/ip6tables

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/iptables.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/iptables.service
    install -m 0644 ${WORKDIR}/ip6tables.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/ip6tables.service

    install -d ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${WORKDIR}/ipv4-forward.conf ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${WORKDIR}/ipv6-forward.conf ${D}${sysconfdir}/sysctl.d
}

FILES_${PN} += " \
    /etc/sysconfig/iptables \
    /etc/sysconfig/ip6tables \
    /etc/sysctl.d/ipv4-forward.conf \
    /etc/sysctl.d/ipv6-forward.conf \
    /usr/lib/systemd/scripts/iptables-flush \
"
