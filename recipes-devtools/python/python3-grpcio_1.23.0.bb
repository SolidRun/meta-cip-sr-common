SUMMARY = "HTTP/2-based RPC framework"
HOMEPAGE = "https://grpc.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

SRC_URI += " file://0001-change-cc-to-gcc.patch"
SRC_URI[md5sum] = "a45a90cc6ddc1194266048a9e4a0ca28"
SRC_URI[sha256sum] = "3cd3d99a8b5568d0d186f9520c16121a0f2a4bcad8e2b9884b76fb88a85a7774"

inherit pypi setuptools3
