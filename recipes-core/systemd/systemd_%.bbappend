do_install_append () {
    # Create a directory on the data partition for journald logs
    install -d ${D}/data/.var/log/journal

    # Remove systemd init
    rm ${D}/sbin/init

    # Disable network naming
    ln -sf /dev/null ${D}/etc/systemd/network/99-default.link
}

FILES_${PN} += " \
    /data/.var/log/journal \
"
