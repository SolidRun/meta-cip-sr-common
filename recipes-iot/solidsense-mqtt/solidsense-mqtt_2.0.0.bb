SUMMARY = "Solidsense MQTT"
DESCRIPTION = "Solidsense MQTT"
LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=c7cc8aa73fb5717f8291fcec5ce9ed6c \
"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-MQTT.git;protocol=ssh;branch=master \
"
SRCREV = "891d3d7a791463095c624be0e34c13b5e4291582"
S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = "solidsense-mqtt.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

inherit systemd

RDEPENDS_${PN} = "\
"

do_install () {
    install -d ${D}/opt/SolidSense/mqtt
    cp -arP ${S}/* ${D}/opt/SolidSense/mqtt
    chown -R root:root ${D}/opt/SolidSense/mqtt

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/install/solidsense_mqtt.service ${D}${systemd_unitdir}/system/solidsense-mqtt.service
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/solidsense-mqtt.service
}

FILES_${PN} = " \
  /opt \
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
