HOMEPAGE = "https://github.com/wirepas/backend-apis/tree/master/gateway_to_backend"
SUMMARY = "Wirepas Mesh Messaging"
DESCRIPTION = "This Python wheel contains the generated code to interact with Wirepas Mesh Network through the Gateway to Backend API. It offers an easy way to use the API in Python without the need to build the protobuf files yourself."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8509a13bf1b271295c62eac453132517"

PYPI_PACKAGE = "wirepas_mesh_messaging"

SRC_URI[md5sum] = "4b25381cadbfd472fb2205fe28ca556b"
SRC_URI[sha256sum] = "22c1d76cb8d9cda9e4b83954ad7cee5efae378ed7285198862cb1a002d28f279"

inherit setuptools3 pypi

do_configure_prepend() {
    touch ${S}/requirements.txt
}
