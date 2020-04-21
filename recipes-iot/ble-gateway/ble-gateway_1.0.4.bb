SUMMARY = "BLE Gateway"
DESCRIPTION = "BLE Gateway"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=b5d7f7156f7785ca2eb55d6cc1b4c118 \
"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-BLE.git;protocol=ssh;branch=master \
"
SRCREV = "dd555538d669c302bbfdb6d163c01d7ef1cd187d"
S = "${WORKDIR}/git"
PARALLEL_MAKE = ""

SYSTEMD_SERVICE_${PN} = "bleTransport.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

inherit systemd

DEPENDS = " \
   glib-2.0-native \
   pkgconfig \
"

RDEPENDS_${PN} = " \
    bluez5 \
    kura \
    mynewt-sr-nina-b1-blehci \
    python3 \
    python3-paho-mqtt \
    python3-pyyaml \
"

do_compile () {
    cd ${S}/helper

    # Create debug version
    oe_runmake "DEBUGGING=1"
    mv ${S}/helper/bluepy-helper ${S}/helper/bluepy-helper-dbg

    # Do a clean between builds
    make clean

    # Create normal version
    oe_runmake
}

do_install () {
    # install ble_gateway
    install -d ${D}/opt/SolidSense/ble_gateway
    cp -arP ${S}/MQTT-Transport-Client ${D}/opt/SolidSense/ble_gateway/
    cp -arP ${S}/BLE-Bluepy ${D}/opt/SolidSense/ble_gateway/
    cp -arP ${S}/bluepy ${D}/opt/SolidSense/ble_gateway/
    chown -R root:root ${D}/opt/SolidSense/ble_gateway

    # install initial config to backup location
    install -d ${D}/opt/SolidSense/.config/ble_gateway
    install -m 0644 ${S}/MQTT-Transport-Client/settings_example.cfg ${D}/opt/SolidSense/.config/ble_gateway/bleTransport.service.cfg

    # install systemd service file
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/Install/bleTransport.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/bleTransport.service

    # install bluepy-helper
    install -d ${D}/opt/SolidSense/bin
    install -m 0777 ${S}/helper/bluepy-helper ${D}/opt/SolidSense/bin/bluepy-helper

    # install bluepy-helper-dbg
    install -d ${D}/opt/SolidSense/bin
    install -m 0777 ${S}/helper/bluepy-helper-dbg ${D}/opt/SolidSense/bin/bluepy-helper-dbg

    # remove unnecessary installed items
    rm ${D}/opt/SolidSense/ble_gateway/MQTT-Transport-Client/payload-examples.json
    rm ${D}/opt/SolidSense/ble_gateway/MQTT-Transport-Client/settings_example.cfg
    rm ${D}/opt/SolidSense/ble_gateway/MQTT-Transport-Client/settings_example2.cfg
}

FILES_${PN} = " \
  /opt/SolidSense/.config/ble_gateway/bleTransport.service.cfg \
  /opt/SolidSense/bin/bluepy-helper \
  /opt/SolidSense/bin/bluepy-helper-dbg \
  /opt/SolidSense/ble_gateway/MQTT-Transport-Client \
  /opt/SolidSense/ble_gateway/MQTT-Transport-Client/.gitkeep \
  /opt/SolidSense/ble_gateway/MQTT-Transport-Client/mqtt_wrapper.py \
  /opt/SolidSense/ble_gateway/MQTT-Transport-Client/ble_mqtt_service.py \
  /opt/SolidSense/ble_gateway/MQTT-Transport-Client/utils \
  /opt/SolidSense/ble_gateway/MQTT-Transport-Client/utils/serialization_tools.py \
  /opt/SolidSense/ble_gateway/MQTT-Transport-Client/utils/__init__.py \
  /opt/SolidSense/ble_gateway/MQTT-Transport-Client/utils/log_tools.py \
  /opt/SolidSense/ble_gateway/MQTT-Transport-Client/utils/argument_tools.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/Readme \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/BLE-Test3.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/BLE-Test2.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/BLE_Client.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/BLE-Test4.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/Ruuvi.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/Documentation \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/BLE_Data.py \
  /opt/SolidSense/ble_gateway/bluepy/btle.py \
  /opt/SolidSense/ble_gateway/bluepy/uuids.json \
"
