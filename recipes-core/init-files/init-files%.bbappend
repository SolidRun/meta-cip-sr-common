# Fix checksum
# The LIC_FILES_CHKSUM does not match for
#   file:///home/eric/work/Solidrun/solidsense/cip-project/deby/poky/poky/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690
#   init-files: The new md5 checksum is b97a012949927931feb7793eee5ed924
#   file /etc/network/interfaces conflicts between attempted installs of init-files-1.0-r0.cortexa9t2hf_neon and init-ifupdown-1.0-r7.cortexa9t2hf_neon

LIC_FILES_CHKSUM = " \
    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://fstab.ss \
"

do_install_append () {
    # Remove clash
    rm -f ${D}${sysconfdir}/network/interfaces

    # Create necessary files for bind mounted /var/log
    install -d ${D}/data/.var/log
    install -d ${D}/data/.var/log/journal
    touch ${D}/data/.var/log/btmp
    touch ${D}/data/.var/log/lastlog
    touch ${D}/data/.var/log/wtmp

    # Copy over modified fstab
    install -m 644 ${WORKDIR}/fstab.ss ${D}${sysconfdir}/fstab

    # Create /var/volatile
    install -d ${D}/var/volatile
}

FILES_${PN} += " \
    /data/.var/log/btmp \
    /data/.var/log/journal \
    /data/.var/log/lastlog \
    /data/.var/log/wtmp \
"
