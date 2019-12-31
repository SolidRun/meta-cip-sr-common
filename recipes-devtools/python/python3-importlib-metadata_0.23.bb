DESCRIPTION = "Read metadata from Python packages"
HOMEPAGE = "http://importlib-metadata.readthedocs.io/"
SECTION = "devel/python"
LICENSE = "Apache-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e88ae122f3925d8bde8319060f2ddb8e"

PYPI_PACKAGE = "importlib_metadata"

SRC_URI[md5sum] = "80d677d744995336c9c22d21a85ddeb8"
SRC_URI[sha256sum] = "aa18d7378b00b40847790e7c27e11673d7fed219354109d0e7b9e5b25dc3ad26"

inherit setuptools3 pypi

DEPENDS = " \
    python3-setuptools \
    python3-setuptools-scm \
    python3-setuptools-native \
    python3-setuptools-scm-native \
"
