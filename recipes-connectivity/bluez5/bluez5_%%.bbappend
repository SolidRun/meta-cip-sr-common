# Add main.conf configuration file

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://main.conf \
    file://0001-use-the-deprecated-name-for-SIOCGSTAMP.patch \
"

do_install_append () {
    install -d ${D}${sysconfdir}/bluetooth
    install -m 0644 ${WORKDIR}/main.conf ${D}${sysconfdir}/bluetooth/main.conf
}

FILES_${PN} += " \
    /etc/bluetooth/main.conf \
"
