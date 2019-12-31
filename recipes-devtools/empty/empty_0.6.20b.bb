SUMMARY = "empty"
DESCRIPTION = "run processes and applications under pseudo-terminal (PTY) sessions and replace TCL/Expect with a simple shell-tool"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://COPYRIGHT;md5=92bdef9f92d08cb685e9ebdd4766a874 \
"

SRC_URI = " \
    https://downloads.sourceforge.net/project/empty/empty/empty-0.6.20b/empty-0.6.20b.tgz \
"
SRC_URI[md5sum] = "d576754795ab4eb1c528ca2a98b5d545"
SRC_URI[sha256sum] = "7e6636e400856984c4405ce7bd0843aaa3329fa3efd20c58df8400a9eaa35f09"

do_configure () {
}

do_compile () {
    oe_runmake 'CC=${CC}' "CFLAGS=${CFLAGS} -Wall -W"
}

do_install () {
    install -d ${D}${bindir}
    install -m 0777 ${S}/empty ${D}${bindir}/empty
}

FILES_${PN} = " \
    ${bindir}/empty \
"
