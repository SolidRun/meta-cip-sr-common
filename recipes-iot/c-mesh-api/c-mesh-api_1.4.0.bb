SUMMARY = "Wirepas Mesh API"
HOMEPAGE = "https://github.com/wirepas/c-mesh-api"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=b8b4f77337154d1f64ebe68dcd93fab6"

SRC_URI = " \
    git://github.com/wirepas/c-mesh-api;protocol=https \
    file://0001-comment-out-hardcoded-values-for-CC-AS-LD-AR.patch \
"

SRCREV = "v1.4.0"
S = "${WORKDIR}/git/lib"

do_install () {
    install -d ${D}${base_libdir}
    install -m 0644 ${S}/build/mesh_api_lib.a ${D}${base_libdir}/libmesh_api.a
}
