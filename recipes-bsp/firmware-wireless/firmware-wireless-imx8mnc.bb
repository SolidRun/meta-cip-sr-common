SUMMARY = "Wireless Firmware required by SolidRun MX8 based SOMs"
DESCRIPTION = "Provides firmware required by wireless hardware on \
SolidRun i.MX8 based SOMs."
LICENSE = "CLOSED"

SRC_URI = "git://github.com/SolidRun/imx8mp_build.git;protocol=https;branch=imx8mn"
SRCREV = "f25e0e73e08a3e540212c52455d7959ee0058f36"

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
}

FILES_${PN} = " \
    ${base_libdir}/firmware/brcm/* \
    ${sysconfdir}/firmware/* \
"

COMPATIBLE_MACHINE = "imx8mnc"
