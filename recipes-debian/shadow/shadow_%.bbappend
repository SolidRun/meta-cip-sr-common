# Remove conflicting nologin with utils-linux

do_install_append () {
    rm -f ${D}${base_sbindir}/nologin
    rm -f ${D}${sbindir}/nologin
}
