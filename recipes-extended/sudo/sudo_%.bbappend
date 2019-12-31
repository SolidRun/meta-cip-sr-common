# Allow those in group wheel to execute any command

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://sudo \
"

do_install_append () {
    rm ${D}${sysconfdir}/sudoers.dist
    install -m 0440 ${WORKDIR}/sudo ${D}${sysconfdir}/sudoers.d/sudo
}

FILES_${PN} += " \
    /etc/sudoers.d/sudo \
"
