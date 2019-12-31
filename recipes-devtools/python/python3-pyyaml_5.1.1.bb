HOMEPAGE = "http://www.pyyaml.org"
SUMMARY = "Python support for YAML"
DESCRIPTION = "\
  YAML is a data serialization format designed for human readability \
  and interaction with scripting languages.  PyYAML is a YAML parser \
  and emitter for Python. \
  .       \
  PyYAML features a complete YAML 1.1 parser, Unicode support, pickle \
  support, capable extension API, and sensible error messages.  PyYAML \
  supports standard YAML tags and provides Python-specific tags that \
  allow to represent an arbitrary Python object. \
  .       \
  PyYAML is applicable for a broad range of tasks from complex \
  configuration files to object serialization and persistance. \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a76b4c69bfcf82313bbdc0393b04438a"

SRCNAME = "PyYAML"
SRC_URI = "http://pyyaml.org/download/pyyaml/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "7e5a8d073b4084742c1d80105423ee9f"
SRC_URI[sha256sum] = "b4bb4d3f5e232425e25dda21c070ce05168a786ac9eda43768ab7f3ac2770955"

S = "${WORKDIR}/${SRCNAME}-${PV}"

DEFAULT_PREFERENCE = "-1"

inherit setuptools3

DEPENDS += "libyaml python3-cython-native"
