# Copyright (C) 2016-2017, TOSHIBA Corp., Daniel Sangorrin <daniel.sangorrin@toshiba.co.jp>
# SPDX-License-Identifier:	MIT

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += " \
    file://default.config \
    file://0001-update-dts-for-solidsense.patch \
    file://0002-add-leds-on-the-Ublox-addon-on-card.patch \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI_ALLOWED = " \
    git://git.kernel.org/pub/scm/linux/kernel/git/cip \
    git://github.com/SolidRun/linux-stable \
"
