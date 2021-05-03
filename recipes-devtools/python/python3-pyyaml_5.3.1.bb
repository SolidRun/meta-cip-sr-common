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
LIC_FILES_CHKSUM = "file://LICENSE;md5=7bbd28caa69f81f5cd5f48647236663d"

SRCNAME = "PyYAML"
SRC_URI = "http://pyyaml.org/download/pyyaml/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "d3590b85917362e837298e733321962b"
SRC_URI[sha256sum] = "b8eac752c5e14d3eca0e6dd9199cd627518cb5ec06add0de9d32baeee6fe645d"

S = "${WORKDIR}/${SRCNAME}-${PV}"

DEFAULT_PREFERENCE = "-1"

inherit setuptools3

DEPENDS += "libyaml python3-cython-native"
