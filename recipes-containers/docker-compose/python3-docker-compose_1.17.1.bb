SUMMARY = "Multi-container orchestration for Docker"
HOMEPAGE = "https://www.docker.com/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=435b266b3899aa8a959f17d41c56def8"

SRC_URI += "file://0001-update-runtime-depends-for-setup.py.patch"

inherit pypi setuptools3

SRC_URI[md5sum] = "fc5f5df49f26ad22dac74f7029a5420a"
SRC_URI[sha256sum] = "e29469dbd78457c82ddad674c0fa0b7afed065b2c73858d332dd6d20d92a8541"

RDEPENDS_${PN} = "\
  ${PYTHON_PN}-cached-property \
  ${PYTHON_PN}-certifi \
  ${PYTHON_PN}-chardet \
  ${PYTHON_PN}-colorama \
  ${PYTHON_PN}-docker \
  ${PYTHON_PN}-docker-pycreds \
  ${PYTHON_PN}-dockerpty \
  ${PYTHON_PN}-docopt \
  ${PYTHON_PN}-idna \
  ${PYTHON_PN}-jsonschema \
  ${PYTHON_PN}-pyyaml \
  ${PYTHON_PN}-requests \
  ${PYTHON_PN}-six \
  ${PYTHON_PN}-terminal \
  ${PYTHON_PN}-texttable \
  ${PYTHON_PN}-urllib3 \
  ${PYTHON_PN}-websocket-client \
  "

