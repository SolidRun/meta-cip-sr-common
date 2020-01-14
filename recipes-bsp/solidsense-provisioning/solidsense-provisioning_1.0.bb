SUMMARY = "Solidsense provisioning"
DESCRIPTION = "Solidsense provisioning"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://../gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=V0.95;destsuffix=SolidSense-V1;name=SolidSense-V1 \
    file://gpl-2.0.txt \
"

SRCREV_SolidSense-V1 = "37ca2156a9e48e767db20b8fd75f950c7f06c702"
S-V1 = "${WORKDIR}/SolidSense-V1"

do_install () {
    # Install factory configuration files
    install -d ${D}/opt/SolidSense/config
    install -m 0644 ${S-V1}/config/SolidSense-HW-configurations.yml ${D}/opt/SolidSense/config/SolidSense-HW-configurations.yml
    install -m 0644 ${S-V1}/config/SolidSense-conf-base.yml ${D}/opt/SolidSense/config/SolidSense-conf-base.yml

    # Install template directory
    cp -a ${S-V1}/template ${D}/opt/SolidSense/template
    chown -R root:root ${D}/opt/SolidSense/template

    # Install provisioning
    cp -a ${S-V1}/provisioning ${D}/opt/SolidSense/provisioning
    chown -R root:root ${D}/opt/SolidSense/provisioning
}

FILES_${PN} = " \
    /opt \
    /opt/SolidSense \
    /opt/SolidSense/template \
    /opt/SolidSense/config \
    /opt/SolidSense/provisioning \
    /opt/SolidSense/template/hostapd.conf.tmpl \
    /opt/SolidSense/template/kura \
    /opt/SolidSense/template/ppp \
    /opt/SolidSense/template/kura/snapshot_0.xml \
    /opt/SolidSense/template/kura/kura_custom.properties.tmpl \
    /opt/SolidSense/template/kura/snapshot_0-ppp-good.xml \
    /opt/SolidSense/template/kura/snapshot_0-1.xml \
    /opt/SolidSense/template/kura/snapshot_0-full.xml \
    /opt/SolidSense/template/kura/snapshot_0-base-good.xml \
    /opt/SolidSense/template/kura/snapshot_0-old.xml \
    /opt/SolidSense/template/kura/snapshot_0-reference.xml \
    /opt/SolidSense/template/ppp/disconnect.tmpl \
    /opt/SolidSense/template/ppp/chat.tmpl \
    /opt/SolidSense/template/ppp/disconnect_EC25_2-1.2 \
    /opt/SolidSense/template/ppp/EC25_2-1.2 \
    /opt/SolidSense/template/ppp/peer.tmpl \
    /opt/SolidSense/template/ppp/chat_EC25_2-1.2 \
    /opt/SolidSense/config/SolidSense-HW-configurations.yml \
    /opt/SolidSense/config/SolidSense-conf-base.yml \
    /opt/SolidSense/provisioning/cpconfig \
    /opt/SolidSense/provisioning/rsn0 \
    /opt/SolidSense/provisioning/ModemPppService.py \
    /opt/SolidSense/provisioning/SnapshotXML.py \
    /opt/SolidSense/provisioning/SolidSenseService.py \
    /opt/SolidSense/provisioning/SolidSenseProvisionning.py \
    /opt/SolidSense/provisioning/ElementTreeLoc.py \
    /opt/SolidSense/provisioning/provisioning_utils.py \
"

COMPATIBLE_MACHINE = "solidsense|n6gq|n6gsdl"
