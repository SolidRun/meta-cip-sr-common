SUMMARY = "BLE Gateway"
DESCRIPTION = "BLE Gateway"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=b5d7f7156f7785ca2eb55d6cc1b4c118 \
"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-BLE.git;protocol=ssh;branch=master \
"
SRCREV = "3fdd0cdd61d9f9cf4359be3407372702f46f2d67"
S = "${WORKDIR}/git"
PARALLEL_MAKE = ""

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
