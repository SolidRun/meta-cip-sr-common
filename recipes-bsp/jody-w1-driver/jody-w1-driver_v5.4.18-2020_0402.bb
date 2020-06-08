SUMMARY = "Cypress Linux WiFi Driver Release (FMAC) [2020-04-02] for Jody W1"
LICENSE = "GPLv2 & cypress-proprietary"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814 \
    file://firmware/LICENCE;md5=cbc5f665d04f741f1e006d2096236ba7 \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

inherit module

SRC_URI = " \
    https://community.cypress.com/servlet/JiveServlet/download/19375-1-53475/cypress-fmac-${PV}.zip;name=cypress \
    file://jody-w167.nvram \
"

SRC_URI[cypress.md5sum] = "7a44eb0174ae8582c2a8b528bedd4e3d"
SRC_URI[cypress.sha256sum] = "b12b0570f462c2f3c26dde98b10235a845a7109037def1e7e51af728bcc1a958"

CYPRESS_BACKPORTS = "${WORKDIR}/cypress-backports-${PV}_${PR}-module-src.tar.gz"
CYPRESS_FIRMWARE = "${WORKDIR}/cypress-firmware-${PV}_${PR}.tar.gz"
S_BACKPORTS = "${WORKDIR}/v5.4.18-backports"
S_FIRMWARE = "${WORKDIR}/firmware"

DEPENDS = "bison-native flex-native virtual/kernel"
RDEPENDS_${PN} = "rfkill"

MAKE_TARGETS = "modules"
MODULES_INSTALL_TARGET = "modules_install"
PACKAGES_DYNAMIC += "^${BPN}-kernel-module-.*"

KERNEL_MODULE_PACKAGE_PREFIX = "${BPN}-"

EXTRA_INCLUDE = " \
    -I${S}/drivers/net/wireless/broadcom/brcm80211/include \
"
EXTRA_OEMAKE = " \
    KLIB=${STAGING_KERNEL_DIR} \
    KLIB_BUILD=${STAGING_KERNEL_BUILDDIR} \
    CFLAGS="${CFLAGS} ${EXTRA_INCLUDE}" \
    LEX=flex \
    YACC=yacc \
"

PARALLEL_MAKE=""

do_unpack_append() {
    bb.build.exec_func('do_unpack_extra', d)
}

do_unpack_extra() {
    tar -zxf ${CYPRESS_BACKPORTS}
    tar -zxf ${CYPRESS_FIRMWARE}
    rmdir ${WORKDIR}/${PN}-${PV}
    mv ${S_BACKPORTS} ${WORKDIR}/${PN}-${PV}
    mv ${S_FIRMWARE} ${WORKDIR}/${PN}-${PV}/
}

do_configure_append () {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    CC="${BUILD_CC}" LD="${BUILD_LD}" AR="${BUILD_AR}" \
    oe_runmake -C ${S}/kconf O=${S}/kconf conf

    oe_runmake defconfig-brcmfmac
}

do_install_append () {
    # install the kernel modules
    oe_runmake modules_install INSTALL_MOD_PATH="${D}/lib"

    # install the nvram file
    install -d ${D}/lib/firmware/brcm
    install ${WORKDIR}/jody-w167.nvram ${D}/lib/firmware/brcm/brcmfmac4359-pcie.txt

    # install the firmware
    install ${S}/firmware/brcmfmac4359-pcie.bin ${D}/lib/firmware/brcm
    install ${S}/firmware/brcmfmac4359-pcie.clm_blob ${D}/lib/firmware/brcm
}

FILES_${PN} += " \
    /lib/firmware/brcm/brcmfmac4359-pcie.txt \
    /lib/firmware/brcm/brcmfmac4359-pcie.bin \
    /lib/firmware/brcm/brcmfmac4359-pcie.clm_blob \
"

RPROVIDES_${PN} += "jody-w1-driver-kernel-module-rfkill"
