SRC_URI = " \
    git://repo.or.cz/openocd.git;protocol=http;name=openocd \
    git://repo.or.cz/r/git2cl.git;protocol=http;destsuffix=tools/git2cl;name=git2cl \
    git://repo.or.cz/r/jimtcl.git;protocol=http;destsuffix=git/jimtcl;name=jimtcl \
    git://repo.or.cz/r/libjaylink.git;protocol=http;destsuffix=git/src/jtag/drivers/libjaylink;name=libjaylink \
"

SRCREV_FORMAT = "openocd"
SRCREV_openocd = "ee56c502607760deb1b44b4ab06b1cb3a59029fe"
SRCREV_git2cl = "8373c9f74993e218a08819cbcdbab3f3564bbeba"
SRCREV_jimtcl = "b9b2408283b5f7a9dd8edda8e7e946d8ec882879"
SRCREV_libjaylink = "cfccbc9d6763733f1d14dff3c2dc5b75aaef136b"

# Enable bitbanging for NXP IMX processors
EXTRA_OECONF += "--enable-imx_gpio"
