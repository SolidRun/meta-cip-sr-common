SUMMARY = "Mynewt Nimble BLE for Nina B1"
DESCRIPTION = "Mynewt Nimble BLE for Nina B1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = " \
    file://../LICENSE;md5=e28461b897ea18bf761beed4a8c5de49 \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    file://LICENSE \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=master;destsuffix=SolidSense-V1;name=SolidSense-V1 \
"

SRCREV_SolidSense-V1 = "e01f8420fd3717a7e7ee3719969b4e268e41797e"
S-V1 = "${WORKDIR}/SolidSense-V1"

RDEPENDS_${PN} = " \
     bluez5 \
"

SYSTEMD_SERVICE_${PN} = "ble2.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

inherit systemd

do_install () {
    # Install flashing script
    install -d ${D}/opt/scripts
    install -d ${D}/opt/SolidSense/bin
    install -m 0755 ${S-V1}/BLE/scripts/flash_ble.sh ${D}/opt/scripts/flash_ble
    ln -s /opt/scripts/flash_ble ${D}/opt/SolidSense/bin/flash_ble

    # install systemd service file
    install -d ${D}${systemd_unitdir}/system
    if [ -z "${@bb.utils.contains('MACHINE', 'imx8mnc', 'imx8mnc', '', d)}" ]; then
        install -m 0644 ${S-V1}/BLE/systemd/ble1.service ${D}${systemd_unitdir}/system
        sed -i -e 's,@SBINDIR@,${sbindir},g' \
            -e 's,@SYSCONFDIR@,${sysconfdir},g' \
            ${D}${systemd_unitdir}/system/ble1.service
    fi

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S-V1}/BLE/systemd/ble2.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/ble2.service

    # For imx8mnc, change the serial port for Sink1 from ttymcx1 to ttymxc3
    if [ -n "${@bb.utils.contains('MACHINE', 'imx8mnc', 'imx8mnc', '', d)}" ]; then
        sed -i -e 's,ttymxc1,ttymxc3,g' \
            ${D}${systemd_unitdir}/system/ble2.service
    fi
}

FILES_${PN} = " \
    /lib/systemd/system/ble1.service \
    /opt/scripts/flash_ble \
    /opt/SolidSense/bin/flash_ble \
"
