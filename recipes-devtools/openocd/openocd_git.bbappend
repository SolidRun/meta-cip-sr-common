SRC_URI = " \
    git://repo.or.cz/openocd.git;protocol=http;name=openocd \
    git://repo.or.cz/r/git2cl.git;protocol=http;destsuffix=tools/git2cl;name=git2cl \
    git://repo.or.cz/r/jimtcl.git;protocol=http;destsuffix=git/jimtcl;name=jimtcl \
    git://repo.or.cz/r/libjaylink.git;protocol=http;destsuffix=git/src/jtag/drivers/libjaylink;name=libjaylink \
"

SRCREV_FORMAT = "openocd"
SRCREV_openocd = "ded67990255cc1e63c77832ffd6e6bef9120873d"
SRCREV_git2cl = "8373c9f74993e218a08819cbcdbab3f3564bbeba"
SRCREV_jimtcl = "dc4ba7770d580800634f90b67a24e077b4a26d98"
SRCREV_libjaylink = "cfccbc9d6763733f1d14dff3c2dc5b75aaef136b"

# Enable bitbanging for NXP IMX processors
EXTRA_OECONF += "--enable-imx_gpio"
