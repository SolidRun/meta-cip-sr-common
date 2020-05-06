# Fix file conflicts
# 
#   file /usr/lib/os-release conflicts between attempted installs of os-release-1.0-r0.noarch and base-files-10.3+deb10u2-r0.solidsense

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
   file://hosts \
   file://set-aliases.sh \
   file://set-prompt.sh \
   file://share/dot.profile \
"

do_install_append () {
    # Remove /usr/lib/os-release
    rm ${D}${libdir}/os-release
    rm ${D}${sysconfdir}/os-release

    # Install updated hotss
    install -m 0644 ${WORKDIR}/hosts ${D}${sysconfdir}/hosts

    # Install profile.d script to set prompt
    install -d ${D}${sysconfdir}/profile.d
    install -m 0774 ${WORKDIR}/set-prompt.sh ${D}${sysconfdir}/profile.d/set-prompt.sh

    # Install profile.d script to set usefull aliases
    install -m 0774 ${WORKDIR}/set-aliases.sh ${D}${sysconfdir}/profile.d/set-aliases.sh
}

FILES_${PN} += " \
    /etc/profile.d/set-prompt.sh \
"
