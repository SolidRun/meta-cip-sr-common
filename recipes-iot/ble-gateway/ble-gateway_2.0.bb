SUMMARY = "BLE Gateway"
DESCRIPTION = "BLE Gateway"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=b5d7f7156f7785ca2eb55d6cc1b4c118 \
"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-BLE.git;protocol=ssh;branch=V2.0 \
"
SRCREV = "51ceac0852af2b6a8e5213426c0e9241d54437d5"
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
    cp -arP ${S}/BLE-Bluepy ${D}/opt/SolidSense/ble_gateway/
    cp -arP ${S}/bluepy ${D}/opt/SolidSense/ble_gateway/
    chown -R root:root ${D}/opt/SolidSense/ble_gateway

    # install initial config to backup location
    install -d ${D}/opt/SolidSense/.config/ble_gateway

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
}

FILES_${PN} = " \
  /opt/SolidSense/ble_gateway \
  /opt/SolidSense/.config \
  /opt/SolidSense/ble_gateway/BLE-Bluepy \
  /opt/SolidSense/ble_gateway/bluepy \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/Readme \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/BLE-Test3.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/BLE-Test2.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/BLE_Client.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/BLE-Test4.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/Ruuvi.py \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/Documentation \
  /opt/SolidSense/ble_gateway/BLE-Bluepy/BLE_Data.py \
  /opt/SolidSense/ble_gateway/bluepy/uuids.json \
  /opt/SolidSense/ble_gateway/bluepy/btle.py \
  /opt/SolidSense/.config/ble_gateway \
  /opt/SolidSense/bin/bluepy-helper-dbg \
  /opt/SolidSense/bin/bluepy-helper \
"
