# Customizations to mender

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://mender-device-identity.sh \
"

do_install_append() {
    install -d ${D}${datadir}/mender/identity
    install -m 0755 ${WORKDIR}/mender-device-identity.sh ${D}${datadir}/mender/identity/mender-device-identity
}

FILES_${PN} += " \
    /usr/share/mender/identity/mender-device-identity \
"
