SUMMARY = "HTTP/2-based RPC framework"
HOMEPAGE = "https://grpc.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

SRC_URI += " file://0001-change-cc-to-gcc.patch"
SRC_URI[md5sum] = "3b18a6353ca2570127130b31ab5eecb7"
SRC_URI[sha256sum] = "8805d486c6128cc0fcc8ecf16c4095d99a8693a541ef851429ab334e028a4a97"

inherit pypi setuptools3
