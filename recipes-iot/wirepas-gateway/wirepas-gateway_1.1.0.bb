SUMMARY = "Wirepas Mesh - IoT network with unprecedented scale, density, flexibility and reliability"
HOMEPAGE = "https://github.com/wirepas/gateway"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=b8b4f77337154d1f64ebe68dcd93fab6"

SRC_URI = " \
    file://LICENSE \
    file://com.wirepas.sink.conf \
    file://configure_node.py \
    file://grpc.tar.gz \
    file://sinkService \
    file://wirepasMicro.service \
    file://wirepasSink1.service \
    file://wirepasSink2.service \
    file://wirepasSinkConfig.service \
    file://wirepasTransport1.service \
    file://wirepasTransport2.service \
    file://wirepas_gateway-1.1.0-cp34-cp34m-linux_armv7l.whl \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

DEPENDS = " \
    python3-native \
    python3-pip-native \
"
RDEPENDS_${PN} = " \
    openocd \
    python3 \
    python3-grpcio \
    python3-grpcio-tools \
    python3-paho-mqtt \
    python3-pydbus \
    python3-pyyaml \
    systemd \
    wirepas-firmware \
    wirepas-messaging \
"

SYSTEMD_SERVICE_${PN} = "wirepasSink1.service wirepasSink2.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

inherit python3native python3-dir setuptools3 systemd

do_configure () {
}

do_compile () {
}

do_install () {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    ${STAGING_BINDIR_NATIVE}/pip3 install --disable-pip-version-check -v --no-deps \
        -t ${D}/${PYTHON_SITEPACKAGES_DIR} --no-cache-dir ${WORKDIR}/wirepas_gateway-1.1.0-cp34-cp34m-linux_armv7l.whl

    ln -s /usr/lib/python3.7/site-packages/dbusCExtension.cpython-34m.so ${D}/usr/lib/python3.7/site-packages/dbusCExtension.so

    install -d ${D}${bindir}
    ln -s ${D}/usr/lib/python3.7/site-packages/bin/wm-dbus-print ${D}${bindir}/wm-dbus-print
    ln -s ${D}/usr/lib/python3.7/site-packages/bin/wm-gw ${D}${bindir}/wm-gw

    sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' ${D}${bindir}/wm-gw
    sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' ${D}${bindir}/wm-dbus-print
}

do_install_append () {
    # Install the require grpc
    install -d ${D}/data/solidsense/grpc
    cp -a ${WORKDIR}/grpc/* ${D}/data/solidsense/grpc
    chown -R root:root ${D}/data/solidsense/grpc

    install -d ${D}/data/solidsense/wirepas
    install -m 0755 ${WORKDIR}/sinkService ${D}/data/solidsense/wirepas/sinkService
    install -m 0644 ${WORKDIR}/configure_node.py ${D}/data/solidsense/wirepas/configure_node.py
    install -d ${D}${sysconfdir}/dbus-1/system.d
    install -m 0644 ${WORKDIR}/com.wirepas.sink.conf ${D}${sysconfdir}/dbus-1/system.d/com.wirepas.sink.conf

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/wirepasMicro.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/wirepasSink1.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/wirepasSink2.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/wirepasSinkConfig.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/wirepasTransport1.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/wirepasTransport2.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/wirepasMicro.service
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/wirepasSink1.service
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/wirepasSink2.service
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/wirepasSinkConfig.service
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/wirepasTransport1.service
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/wirepasTransport2.service
}

FILES_${PN} = " \
    /etc \
    /data/solidsense/grpc \
    /data/solidsense/grpc/grpc_service.proto \
    /data/solidsense/grpc/grpc_service_pb2.py \
    /data/solidsense/grpc/argument_tools.py \
    /data/solidsense/grpc/grpc_service_pb2_grpc.py \
    /data/solidsense/grpc/client_demo.py \
    /data/solidsense/grpc/grpc_service.py \
    /data/solidsense/wirepas/sinkService \
    /data/solidsense/wirepas/configure_node.py \
    /lib/systemd/system/wirepasSinkConfig.service \
    /lib/systemd/system/wirepasTransport2.service \
    /lib/systemd/system/wirepasTransport1.service \
    /lib/systemd/system/wirepasMicro.service \
    /etc/dbus-1 \
    /etc/dbus-1/system.d \
    /etc/dbus-1/system.d/com.wirepas.sink.conf \
    /usr/bin/wm-dbus-print \
    /usr/bin/wm-gw \
    /usr/lib/python3.7/site-packages/dbusCExtension.cpython-34m.so \
    /usr/lib/python3.7/site-packages/wirepas_gateway \
    /usr/lib/python3.7/site-packages/bin \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.1.0.dist-info \
    /usr/lib/python3.7/site-packages/wirepas_gateway-extras \
    /usr/lib/python3.7/site-packages/wirepas_gateway/transport_service.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__main__.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus_print_client.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/utils \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol \
    /usr/lib/python3.7/site-packages/wirepas_gateway/wirepas_certs \
    /usr/lib/python3.7/site-packages/wirepas_gateway/utils/serialization_tools.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/utils/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/utils/log_tools.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/utils/argument_tools.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/utils/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_gateway/utils/__pycache__/serialization_tools.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/utils/__pycache__/argument_tools.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/utils/__pycache__/log_tools.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/utils/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/return_code.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/sink_manager.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/dbus_client.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/c-extension \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/c-extension/dbus_c.c \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/__pycache__/return_code.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/__pycache__/sink_manager.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/dbus/__pycache__/dbus_client.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__pycache__/dbus_print_client.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__pycache__/__main__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__pycache__/transport_service.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/topic_helper.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/__pycache__/topic_helper.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/wirepas_certs/extwirepas.pem \
    /usr/lib/python3.7/site-packages/bin/wm-gw \
    /usr/lib/python3.7/site-packages/bin/wm-dbus-print \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.1.0.dist-info/entry_points.txt \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.1.0.dist-info/metadata.json \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.1.0.dist-info/RECORD \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.1.0.dist-info/WHEEL \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.1.0.dist-info/INSTALLER \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.1.0.dist-info/DESCRIPTION.rst \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.1.0.dist-info/top_level.txt \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.1.0.dist-info/METADATA \
    /usr/lib/python3.7/site-packages/wirepas_gateway-extras/package \
    /usr/lib/python3.7/site-packages/wirepas_gateway-extras/package/LICENSE.txt \
    /usr/lib/python3.7/site-packages/wirepas_gateway-extras/package/setup.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway-extras/package/README.rst \
    /usr/lib/python3.7/site-packages/wirepas_gateway-extras/package/extwirepas.pem \
    /usr/lib/python3.7/site-packages/wirepas_gateway-extras/package/requirements.txt \
    /usr/lib/python3.7/site-packages/wirepas_gateway-extras/package/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_gateway-extras/package/__pycache__/setup.cpython-37.pyc \
"
FILES_${PN}-dev = " \
    /usr/lib/python3.7/site-packages/dbusCExtension.so \
"
