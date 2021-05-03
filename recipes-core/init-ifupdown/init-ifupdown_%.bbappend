FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

do_install_append () {
    rm ${D}${sysconfdir}/network/interfaces
}
