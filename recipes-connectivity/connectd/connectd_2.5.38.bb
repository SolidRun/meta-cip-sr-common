DESCRIPTION = "remote.it"
LICENSE = "CLOSED"

SRC_URI = " \
    https://github.com/remoteit/installer/releases/download/v${PV}/connectd_${PV}_arm-linaro-pi.tar;subdir=connectd-${PV} \
"
SRC_URI[sha256sum] = "0b6e3bb9babf35f1580de0b32ba27a13e5187dfd5a66c6694e2e4713c49c0532"

INSANE_SKIP_${PN} = "ldflags"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

inherit bin_package

COMPATIBLE_MACHINE = "solidsense|n6gq|n6gsdl|in6gq|in6gsdl"
