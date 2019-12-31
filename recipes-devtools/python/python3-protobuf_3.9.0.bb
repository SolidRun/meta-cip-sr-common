DESCRIPTION = "Protocol Buffers"
HOMEPAGE = "https://developers.google.com/protocol-buffers/"
SECTION = "devel/python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=19e8f490f9526b1de84f8d949cfcfd4e"

inherit pypi

SRC_URI[md5sum] = "4ec7b2d49a5d1460591e520b54153c87"
SRC_URI[sha256sum] = "b3452bbda12b1cbe2187d416779de07b2ab4c497d83a050e43c344778763721d"

# http://errors.yoctoproject.org/Errors/Details/184715/
# Can't find required file: ../src/google/protobuf/descriptor.proto
CLEANBROKEN = "1"

UPSTREAM_CHECK_REGEX = "protobuf/(?P<pver>\d+(\.\d+)+)/"

RDEPENDS_${PN} += " \
    python3-datetime \
    python3-json \
    python3-logging \
    python3-netclient \
    python3-numbers \
    python3-pkgutil \
    python3-six \
    python3-unittest \
"

inherit setuptools3

DEPENDS += "protobuf (=3.9.0)"
DISTUTILS_BUILD_ARGS += "--cpp_implementation"
DISTUTILS_INSTALL_ARGS += "--cpp_implementation"

do_compile_prepend_class-native () {
    export KOKORO_BUILD_NUMBER="1"
}

# For usage in other recipies when compiling protobuf files (e.g. by grpcio-tools)
BBCLASSEXTEND = "native nativesdk"
