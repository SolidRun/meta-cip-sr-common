# Customize cronie recipe

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://crond.sysconfig \
"

do_install_append () {
    # create soft link for vi
    ln -sf /bin/vi ${D}/usr/bin/vi

    # install custom /etc/sysconfig/crond
    install -d ${D}${sysconfdir}/sysconfig
    install -m 0600 ${WORKDIR}/crond.sysconfig ${D}${sysconfdir}/sysconfig/crond
}
