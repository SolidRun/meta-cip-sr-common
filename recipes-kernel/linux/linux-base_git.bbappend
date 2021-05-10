# Copyright (C) 2016-2017, TOSHIBA Corp., Daniel Sangorrin <daniel.sangorrin@toshiba.co.jp>
# SPDX-License-Identifier:	MIT

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += " \
    file://001-default.config \
    file://002-rtc.config \
    file://0001-update-dts-for-solidsense.patch \
    file://0003-add-am1805-rtc-driver.patch \
    file://0004-add-ltc4162_l_charger.patch \
    file://0005-power-supply-ltc4162_l-constify-static-struct-attribute_group.patch \
    file://0006-dt_bindings-power-supply-add-ltc4162_l_charger.patch \
    file://0007-fb-mxc-move-hdmi_enable_overflow_interrupts.patch \
    file://rtc-am1805.c \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

SRC_URI_ALLOWED = " \
    git://git.kernel.org/pub/scm/linux/kernel/git/cip \
    git://github.com/SolidRun/linux-stable \
"

S = "${WORKDIR}/git"

do_compile_prepend () {
    cp ${WORKDIR}/rtc-am1805.c ${S}/drivers/rtc/rtc-am1805.c

    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'wl18xx-calibrator', '', '', d)}" ]; then
        sed -i 's/# CONFIG_NL80211_TESTMODE is not set/CONFIG_NL80211_TESTMODE=y/' ${WORKDIR}/build/.config
        oe_runmake KCONFIG_CONFIG=${WORKDIR}/build/.config oldnoconfig
    fi
}
