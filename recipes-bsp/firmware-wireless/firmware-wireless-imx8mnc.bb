SUMMARY = "Wireless Firmware required by SolidRun MX8 based SOMs"
DESCRIPTION = "Provides firmware required by wireless hardware on \
SolidRun i.MX8 based SOMs."
LICENSE = "CLOSED"

SRC_URI = " \
    git://github.com/SolidRun/imx8mp_build.git;protocol=https;branch=imx8mn \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=master;destsuffix=SolidSense-V1;name=SolidSense-V1 \
"
SRCREV = "f25e0e73e08a3e540212c52455d7959ee0058f36"
SRCREV_SolidSense-V1 = "e01f8420fd3717a7e7ee3719969b4e268e41797e"
S-V1 = "${WORKDIR}/SolidSense-V1"

SYSTEMD_SERVICE_${PN} = "ble1.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

inherit systemd

S = "${WORKDIR}/git"
FIRMWARE_LIB_SRC = "${S}/patches/overlay/usr/lib/firmware/brcm"
FIRMWARE_ETC_SRC = "${S}/patches/overlay/etc/firmware"

do_install () {
    install -d ${D}${base_libdir}/firmware/brcm 
    install -m 0644 ${FIRMWARE_LIB_SRC}/brcmfmac43455-sdio.bin ${D}${base_libdir}/firmware/brcm/brcmfmac43455-sdio.bin
    install -m 0644 ${FIRMWARE_LIB_SRC}/brcmfmac43455-sdio.clm_blob ${D}${base_libdir}/firmware/brcm/brcmfmac43455-sdio.clm_blob
    install -m 0644 ${FIRMWARE_LIB_SRC}/brcmfmac43455-sdio.txt ${D}${base_libdir}/firmware/brcm/brcmfmac43455-sdio.txt

    install -d ${D}${sysconfdir}/firmware
    install -d ${D}${sysconfdir}/firmware/murata-master
    install -m 0644 ${FIRMWARE_ETC_SRC}/BCM4345C0.1MW.hcd ${D}${sysconfdir}/firmware/BCM4345C0.1MW.hcd
    install -m 0644 ${FIRMWARE_ETC_SRC}/BCM4345C0_003.001.025.0144.0266.1MW.hcd ${D}${sysconfdir}/firmware/BCM4345C0_003.001.025.0144.0266.1MW.hcd
    install -m 0644 ${FIRMWARE_ETC_SRC}/CYW4345C0.1MW.hcd ${D}${sysconfdir}/firmware/CYW4345C0.1MW.hcd
    install -m 0644 ${FIRMWARE_ETC_SRC}/murata-master/_BCM4345C0.1MW.hcd ${D}${sysconfdir}/firmware/murata-master/_BCM4345C0.1MW.hcd

    # install systemd service file
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S-V1}/BLE/systemd/ble1.service.imx8mnc ${D}${systemd_unitdir}/system/ble1.service
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/ble1.service
}

FILES_${PN} = " \
    ${base_libdir}/firmware/brcm/* \
    ${sysconfdir}/firmware/* \
"

COMPATIBLE_MACHINE = "imx8mnc"
