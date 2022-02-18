SUMMARY = "Solidsense provisioning"
DESCRIPTION = "Solidsense provisioning"
LICENSE = "EPL-2.0"
LIC_FILES_CHKSUM = " \
    file://../SolidSense-V1/LICENSE;md5=c7cc8aa73fb5717f8291fcec5ce9ed6c \
"
SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=master;destsuffix=SolidSense-V1;name=SolidSense-V1 \
"

SRCREV_SolidSense-V1 = "e01f8420fd3717a7e7ee3719969b4e268e41797e"
S-V1 = "${WORKDIR}/SolidSense-V1"

do_install () {
    # Install factory configuration files
    install -d ${D}/opt/SolidSense/config
    install -m 0644 ${S-V1}/config/SolidSense-HW-configurations.yml ${D}/opt/SolidSense/config/SolidSense-HW-configurations.yml
    install -m 0644 ${S-V1}/config/SolidSense-conf-base.yml ${D}/opt/SolidSense/config/SolidSense-conf-base.yml

    # Install template directory
    cp -a ${S-V1}/template ${D}/opt/SolidSense/template
    chown -R root:root ${D}/opt/SolidSense/template

    # Install common directory
    cp -a ${S-V1}/common ${D}/opt/SolidSense/common
    chown -R root:root ${D}/opt/SolidSense/common

    # Install provisioning
    cp -a ${S-V1}/provisioning ${D}/opt/SolidSense/provisioning
    chown -R root:root ${D}/opt/SolidSense/provisioning

    # Remove snapshot_0.xml as this is now in the kura recipe
    rm -f ${D}/opt/SolidSense/template/kura/snapshot_0.xml
}

FILES_${PN} = " \
  /opt \
  /opt/SolidSense \
  /opt/SolidSense/template \
  /opt/SolidSense/config \
  /opt/SolidSense/provisioning \
  /opt/SolidSense/common \
  /opt/SolidSense/template/hostapd.conf.tmpl \
  /opt/SolidSense/template/kura \
  /opt/SolidSense/template/ppp \
  /opt/SolidSense/template/kura/snapshot_0.xml \
  /opt/SolidSense/template/kura/kura_custom.properties.tmpl \
  /opt/SolidSense/template/kura/snapshot_0-full.xml \
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
  /opt/SolidSense/common/solidsense_led.py \
  /opt/SolidSense/common/solidsense_parameters.py \
"

COMPATIBLE_MACHINE = "solidsense|n6gq|n6gsdl|in6gq|in6gsdl|imx8mnc"
