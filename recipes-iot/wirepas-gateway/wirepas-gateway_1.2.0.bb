SUMMARY = "Wirepas Mesh - IoT network with unprecedented scale, density, flexibility and reliability"
HOMEPAGE = "https://github.com/wirepas/gateway"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb6bb17b0d0cca188339074207e9f4d8"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    git://github.com/wirepas/gateway;rev=v1.2.0 \
    git://github.com/wirepas/c-mesh-api;rev=v1.2.0;destsuffix=git/sink_service/c-mesh-api \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=V0.911;destsuffix=SolidSense-V1;name=SolidSense-V1 \
"

SRCREV_SolidSense-V1 = "25052bbc277a0b690fec8c94512c0c005b9ac1aa"
S = "${WORKDIR}/git"
S-V1 = "${WORKDIR}/SolidSense-V1"

DEPENDS = " \
    python3-native \
    systemd \
"
RDEPENDS_${PN} = " \
    python3 \
    python3-paho-mqtt \
    python3-pydbus \
    python3-pyyaml (=5.1.1)\
    systemd \
    wirepas-firmware \
    wirepas-firmware-check \
    wirepas-messaging \
"

SINK_SERVICE_CFLAGS = " \
    -I${S}/sink_service/c-mesh-api/lib/api \
    -I${S}/sink_service/c-mesh-api/lib/wpc/include \
    -I${S}/sink_service/c-mesh-api/lib/platform \
"

SECURITY_STACK_PROTECTOR = ""

EXTRA_OEMAKE = " \
    'CC=${CC}' \
    'CFLAGS=${CFLAGS} ${SINK_SERVICE_CFLAGS}' \
    'LFLAGS=${LDFLAGS}' \
"

WIREPAS_GATEWAY_INSTALL_ARGS = " \
    --root=${D} \
    --prefix=${prefix} \
    --install-data=${datadir} \
"

SYSTEMD_SERVICE_${PN} = "wirepasSink1.service wirepasSink2.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

inherit python3native python3-dir setuptools3 systemd

do_configure_prepend () {
    cd ${S}/python_transport
}

do_compile () {
    cd ${S}/python_transport
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py build

    cd ${S}/sink_service
    oe_runmake
}

do_install () {
    # Install wirepas-gateway
    cd ${S}/python_transport
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py install ${WIREPAS_GATEWAY_INSTALL_ARGS}

    # Make sure we use /usr/bin/env python
    sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' ${D}${bindir}/wm-gw
    sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' ${D}${bindir}/wm-dbus-print

    # Install the require grpc
    install -d ${D}/opt/SolidSense/wirepas/grpc
    cp -a ${S-V1}/wirepas/grpc/* ${D}/opt/SolidSense/wirepas/grpc
    chown -R root:root ${D}/opt/SolidSense/wirepas/grpc

    # Install the configure_node.py
    install -d ${D}/opt/SolidSense/wirepas
    install -m 0644 ${S-V1}/wirepas/scripts/configure_node.py ${D}/opt/SolidSense/wirepas/configure_node.py

    # Install the dbus config
    install -d ${D}${sysconfdir}/dbus-1/system.d
    install -m 0644 ${S-V1}/wirepas/dbus/com.wirepas.sink.conf ${D}${sysconfdir}/dbus-1/system.d/com.wirepas.sink.conf

    # Install systemd unit files
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S-V1}/wirepas/systemd/wirepasMicro.service ${D}${systemd_unitdir}/system
    install -m 0644 ${S-V1}/wirepas/systemd/wirepasSink1.service ${D}${systemd_unitdir}/system
    install -m 0644 ${S-V1}/wirepas/systemd/wirepasSink2.service ${D}${systemd_unitdir}/system
    install -m 0644 ${S-V1}/wirepas/systemd/wirepasSinkConfig.service ${D}${systemd_unitdir}/system
    install -m 0644 ${S-V1}/wirepas/systemd/wirepasTransport1.service ${D}${systemd_unitdir}/system
    install -m 0644 ${S-V1}/wirepas/systemd/wirepasTransport2.service ${D}${systemd_unitdir}/system
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

    # Install the sinkService
    install -d ${D}/opt/SolidSense/bin
    install -m 0755 ${S}/sink_service/build/sinkService ${D}/opt/SolidSense/bin/sinkService
}

FILES_${PN} = " \
    /usr/bin/wm-gw \
    /usr/bin/wm-dbus-print \
    /usr/lib/python3.7/site-packages/dbusCExtension.cpython-37m-arm-linux-gnueabihf.so \
    /usr/lib/python3.7/site-packages/wirepas_gateway \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.2.0-py3.7.egg-info \
    /usr/lib/python3.7/site-packages/wirepas_gateway/transport_service.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__main__.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__about__.py \
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
    /usr/lib/python3.7/site-packages/wirepas_gateway/__pycache__/__about__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__pycache__/__main__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/__pycache__/transport_service.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/topic_helper.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/mqtt_wrapper.py \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/__pycache__/mqtt_wrapper.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/__pycache__/topic_helper.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/protocol/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_gateway/wirepas_certs/extwirepas.pem \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.2.0-py3.7.egg-info/entry_points.txt \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.2.0-py3.7.egg-info/SOURCES.txt \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.2.0-py3.7.egg-info/requires.txt \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.2.0-py3.7.egg-info/PKG-INFO \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.2.0-py3.7.egg-info/dependency_links.txt \
    /usr/lib/python3.7/site-packages/wirepas_gateway-1.2.0-py3.7.egg-info/top_level.txt \
    /usr/share/wirepas_gateway-extras \
    /usr/share/wirepas_gateway-extras/package \
    /usr/share/wirepas_gateway-extras/package/setup.py \
    /usr/share/wirepas_gateway-extras/package/README.md \
    /usr/share/wirepas_gateway-extras/package/extwirepas.pem \
    /usr/share/wirepas_gateway-extras/package/requirements.txt \
    /usr/share/wirepas_gateway-extras/package/LICENSE \
    /opt/SolidSense/wirepas/grpc/grpc_service.proto \
    /opt/SolidSense/wirepas/grpc/grpc_service_pb2.py \
    /opt/SolidSense/wirepas/grpc/argument_tools.py \
    /opt/SolidSense/wirepas/grpc/grpc_service_pb2_grpc.py \
    /opt/SolidSense/wirepas/grpc/client_demo.py \
    /opt/SolidSense/wirepas/grpc/grpc_service.py \
    /opt/SolidSense/bin/sinkService \
    /opt/SolidSense/wirepas/configure_node.py \
    /etc/dbus-1/system.d/com.wirepas.sink.conf \
    /lib/systemd/system/wirepasSinkConfig.service \
    /lib/systemd/system/wirepasTransport2.service \
    /lib/systemd/system/wirepasTransport1.service \
    /lib/systemd/system/wirepasMicro.service \
"
