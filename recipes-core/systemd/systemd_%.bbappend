do_install_append () {
    # Create a directory on the data partition for journald logs
    install -d ${D}/data/.var/log/journal

    # Remove systemd init
    rm ${D}/init
    rm ${D}/sbin/init
}

FILES_${PN} += " \
    /data/.var/log/journal \
"
