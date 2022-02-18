SUMMARY = "Solidsense MQTT"
DESCRIPTION = "Solidsense MQTT"
LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=c7cc8aa73fb5717f8291fcec5ce9ed6c \
"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-MQTT.git;protocol=ssh;branch=master \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=master;destsuffix=SolidSense-V1;name=SolidSense-V1 \
"
SRCREV = "69252b836b784ef303bd5f8648bdf0be4dddbeb2"
SRCREV_SolidSense-V1 = "e01f8420fd3717a7e7ee3719969b4e268e41797e"
S = "${WORKDIR}/git"
S-V1 = "${WORKDIR}/SolidSense-V1"
KURA_VERSION ?= "5.0.0"
KURA_PATH = "/opt/eclipse/kura_${KURA_VERSION}_solid_sense"

SYSTEMD_SERVICE_${PN} = "solidsense_mqtt.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

inherit systemd

RDEPENDS_${PN} = "\
"

do_install () {
    install -d ${D}/opt/SolidSense/mqtt
    cp -arP ${S}/* ${D}/opt/SolidSense/mqtt
    chown -R root:root ${D}/opt/SolidSense/mqtt

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/install/solidsense_mqtt.service ${D}${systemd_unitdir}/system/solidsense_mqtt.service
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/solidsense_mqtt.service

    # Install the SolidSense MQTT configuration service
    install -d ${D}/${KURA_PATH}/packages
    install -m 0644 ${S-V1}/Kura/plugins/SolidsenseMqttService.dp \
        ${D}${KURA_PATH}/packages/SolidsenseMqttService_1.0.0.dp
}

FILES_${PN} = " \
  /opt \
  /opt/eclipse \
  /opt/eclipse/kura_5.0.0_solid_sense \
  /opt/eclipse/kura_5.0.0_solid_sense/data \
  /opt/eclipse/kura_5.0.0_solid_sense/data/packages \
  /opt/eclipse/kura_5.0.0_solid_sense/data/packages/SolidsenseMqttService_1.0.0.dp \
  /opt/SolidSense \
  /opt/SolidSense/mqtt \
  /opt/SolidSense/mqtt/solidsense_mqtt_service.py \
  /opt/SolidSense/mqtt/payload-examples.json \
  /opt/SolidSense/mqtt/test.cfg \
  /opt/SolidSense/mqtt/OBD_Client.py \
  /opt/SolidSense/mqtt/settings_example.cfg \
  /opt/SolidSense/mqtt/README.md \
  /opt/SolidSense/mqtt/mqtt_wrapper.py \
  /opt/SolidSense/mqtt/mqtt_time.py \
  /opt/SolidSense/mqtt/Modem_GPS_Client.py \
  /opt/SolidSense/mqtt/LICENSE \
  /opt/SolidSense/mqtt/settings_example2.cfg \
  /opt/SolidSense/mqtt/utils \
  /opt/SolidSense/mqtt/install \
  /opt/SolidSense/mqtt/utils/serialization_tools.py \
  /opt/SolidSense/mqtt/utils/__init__.py \
  /opt/SolidSense/mqtt/utils/log_tools.py \
  /opt/SolidSense/mqtt/utils/argument_tools.py \
  /opt/SolidSense/mqtt/install/solidsense-mqtt.service \
"
