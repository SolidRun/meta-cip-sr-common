SUMMARY = "Kura"
DESCRIPTION = "Kura"
LICENSE = "epl-v10"
LIC_FILES_CHKSUM = " \
    file://opt/eclipse/kura/epl-v10.html;md5=d0fc088e4e5216422c217d39853d8601 \
"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=V0.911;destsuffix=SolidSense-V1;name=SolidSense-V1 \
    git://git@github.com/SolidRun/SolidSense-BLE.git;protocol=ssh;branch=V1.0.3;destsuffix=SolidSense-BLE;name=SolidSense-BLE \
    git://git@github.com/SolidRun/SolidSense-kura-wirepas.git;protocol=ssh;branch=master;destsuffix=SolidSense-kura-wp;name=SolidSense-kura-wp \
    file://kura-${PV}.tar.gz \
    file://kura.service \
"
SRCREV_SolidSense-V1 = "25052bbc277a0b690fec8c94512c0c005b9ac1aa"
SRCREV_SolidSense-BLE = "5841bdc83078e00028dda1d1c52ff4b0979b1e38"
SRCREV_SolidSense-kura-wp = "69ae491521c4adb7e3967128af7f0f355495d5f9"
S-V1 = "${WORKDIR}/SolidSense-V1"
S-BLE = "${WORKDIR}/SolidSense-BLE"
S-kura-wp = "${WORKDIR}/SolidSense-kura-wp"
KURA_PATH = "/opt/eclipse/kura_4.0.0_solid_sense/"

SYSTEMD_SERVICE_${PN} = "kura.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

INSANE_SKIP_${PN} = "ldflags"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

DEPENDS = " \
    libffi-native \
    zlib \
"
RDEPENDS_${PN} = " \
    openjdk-8 \
    python \
"

inherit systemd

do_install () {
    # Install kura
    cp -arP ${WORKDIR}/kura-${PV}/* ${D}

    # Install kura unit file
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/kura.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/kura.service

    # Install updated start_kura_background.sh
    install -d ${D}${KURA_PATH}/bin
    install -m 0755 ${S-V1}/Kura/scripts/start_kura_background.sh ${D}${KURA_PATH}/bin/start_kura_background.sh

    # Install firewall rules
    install -d ${D}${KURA_PATH}/.data
    install -m 0644 ${S-V1}/Kura/data/iptables ${D}${KURA_PATH}/.data/iptables
    install -m 0644 ${S-V1}/Kura/data/ip6tables ${D}${KURA_PATH}/.data/ip6tables

    # Install shell script to assist with running cli command via Kura/Kapua
    install -d ${D}${base_bindir}
    install -m 0755 ${S-V1}/Kura/scripts/krc.sh ${D}${base_bindir}/krc

    # Install updated logging config
    install -d ${D}${KURA_PATH}/user
    install -m 0644 ${S-V1}/Kura/user/log4j.xml ${D}${KURA_PATH}/user/log4j.xml

    # Install SolidSense configuration scripts/data
    rm -rf ${D}/opt/SolidSense/kura/config/*
    cp -arP ${S-V1}/Kura/Config/* ${D}/opt/SolidSense/kura/config/

    # Create initial empty dpa.properties
    rm -f ${D}${KURA_PATH}/data/dpa.properties
    touch ${D}${KURA_PATH}/data/dpa.properties

    # Install the wirepas Kura dp
    cp -a ${S-kura-wp}/com.solidsense.kura.WirepasConfigurationService/resources/dp/WirepasConfigurationService.dp \
        ${D}${KURA_PATH}/data/packages
    echo "WirePasConfigurationService=file\:/opt/eclipse/kura/data/packages/WirepasConfigurationService.dp" >> \
        ${D}${KURA_PATH}/data/dpa.properties

    # Install the ble-gateway Kura dp
    #    TODO: create a conditional to only install this if the ble-gateway recipe is selected
    cp -a ${S-BLE}/Install/BLEConfigurationService.dp ${D}${KURA_PATH}/data/packages
    echo "BLEConfigurationService=file\:/opt/eclipse/kura/data/packages/BLEConfigurationService.dp" >> \
        ${D}${KURA_PATH}/data/dpa.properties

    chown -R root:root ${D}/opt
}

FILES_${PN} = " \
  /bin/krc \
  /opt/eclipse/kura \
  /opt/eclipse/kura_4.0.0_solid_sense \
  /opt/eclipse/kura_4.0.0_solid_sense/notice.html \
  /opt/eclipse/kura_4.0.0_solid_sense/epl-v10.html \
  /opt/eclipse/kura_4.0.0_solid_sense/data \
  /opt/eclipse/kura_4.0.0_solid_sense/bin \
  /opt/eclipse/kura_4.0.0_solid_sense/log \
  /opt/eclipse/kura_4.0.0_solid_sense/framework \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins \
  /opt/eclipse/kura_4.0.0_solid_sense/.data \
  /opt/eclipse/kura_4.0.0_solid_sense/user \
  /opt/eclipse/kura_4.0.0_solid_sense/data/store.save \
  /opt/eclipse/kura_4.0.0_solid_sense/data/dpa.properties \
  /opt/eclipse/kura_4.0.0_solid_sense/data/persistance \
  /opt/eclipse/kura_4.0.0_solid_sense/data/packages \
  /opt/eclipse/kura_4.0.0_solid_sense/data/persistance/verification \
  /opt/eclipse/kura_4.0.0_solid_sense/data/packages/org.eclipse.kura.driver.eddystone-1.0.0.dp \
  /opt/eclipse/kura_4.0.0_solid_sense/data/packages/WirepasConfigurationService.dp \
  /opt/eclipse/kura_4.0.0_solid_sense/bin/start_kura_debug.sh \
  /opt/eclipse/kura_4.0.0_solid_sense/bin/start_kura.sh \
  /opt/eclipse/kura_4.0.0_solid_sense/bin/start_kura_background.sh \
  /opt/eclipse/kura_4.0.0_solid_sense/bin/.recoverDefaultConfig.sh \
  /opt/eclipse/kura_4.0.0_solid_sense/log/kura_install_20181213180931.log \
  /opt/eclipse/kura_4.0.0_solid_sense/framework/config.ini \
  /opt/eclipse/kura_4.0.0_solid_sense/framework/kura.properties \
  /opt/eclipse/kura_4.0.0_solid_sense/framework/jdk.dio.properties \
  /opt/eclipse/kura_4.0.0_solid_sense/framework/RELEASE_NOTES.txt \
  /opt/eclipse/kura_4.0.0_solid_sense/framework/jdk.dio.policy \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.ble.eddystone.provider_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.wire.helper.provider_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-expression_4.3.14.RELEASE_1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.core.contenttype_3.6.0.v20170207-1037.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.console.jaas.fragment_1.0.0.v20130327-1442.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.rest.provider_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.core.crypto_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.camel.camel-stream_2.21.1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.web2_2.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.app_1.3.400.v20150715-1528.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.jetty.servlet_9.4.12.v20180830.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.camel.camel-core_2.21.1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.core_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.ble.ibeacon.provider_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/tinyb_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.soda.dk.comm.x86_64_1.2.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.activemq.artemis-mqtt-protocol_2.5.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.eclipsesource.jaxrs.provider.security_2.2.0.201602281253.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/javax.servlet.jsp_2.2.0.v201112011158.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.hook.file.move.provider_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.log4j2-api-config_1.0.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-tx_4.3.14.RELEASE_1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.deployment.agent_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/jdk.dio.aarch64_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.http.jetty_3.4.0.v20170503-2025.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.osgi.services_3.6.0.v20170228-1906.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.soda.dk.comm.armv6hf_1.2.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.google.guava_19.0.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.camel.cloud.factory_1.1.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.wire.camel_1.0.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.jetty.io_9.4.12.v20180830.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.wire.component.join.provider_1.0.100.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.util_1.0.500.v20130404-1337.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.transforms.xslt_1.0.300.v20130327-1442.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.camel_1.3.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.usb_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.coordinator_1.3.300.v20170512-2111.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.core.runtime_3.13.0.v20170207-1030.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.sysv.provider_1.0.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.commons.exec_1.3.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.jetty.http_9.4.12.v20180830.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.core.cloud_1.1.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.registry_3.7.0.v20170222-1344.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.commons.io_2.4.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.localization.resources_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.activemq.artemis_2.5.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.cloudconnection.eclipseiot.mqtt.provider_1.0.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.felix.gogo.runtime_0.10.0.v201209301036.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.commons.net_3.1.0.v201205071737.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.preferences_3.7.0.v20170126-2132.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.jetty.continuation_9.4.12.v20180830.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.io_1.1.100.v20150430-1834.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.logging.log4j.slf4j-impl_2.8.2.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.jetty.util_9.4.12.v20180830.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.eclipsesource.jaxrs.publisher_5.3.1.201602281253.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.frameworkadmin_2.0.300.v20160504-1450.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.codeminders.hidapi.armv6hf_1.1.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.asset.provider_2.0.100.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.qpid.proton-j_0.26.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.felix.gogo.shell_0.10.0.v201212101605.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.tigris.mtoolkit.iagent.rpc_3.0.0.20110411-0918.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.broker.artemis.core_1.1.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.felix.gogo.command_0.10.0.v201209301215.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.soda.dk.comm_1.2.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/jcl.over.slf4j_1.7.25.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.core.net_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.wire.component.provider_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.weaving.hook_1.2.0.v20160929-1449.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.core.variables_3.4.0.v20170113-2056.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.jsp.jasper_1.0.500.v20150119-1358.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.asset.cloudlet.provider_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.driver.helper.provider_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.camel.camel-script_2.21.1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.commons.beanutils_1.9.2.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.common_3.9.0.v20170207-1454.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.jsp.jasper.registry_1.0.300.v20130327-1442.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.felix.deploymentadmin_0.9.5.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.logging.log4j.core_2.8.2.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.api_2.0.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.activemq.artemis-native_2.5.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.commons.csv_1.4.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.usb4java_1.2.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.camel.xml_1.1.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.h2_1.4.192.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.osgi_3.12.50.v20170928-1321.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.frameworkadmin.equinox_1.0.800.v20170524-1345.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.transport-native-epoll_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.useradmin_1.1.400.v20130327-1442.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.jetty.customizer_1.0.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.commons.collections_3.2.2.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/tinyb.armv6hf_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-beans_4.3.14.RELEASE_1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.camel.camel-jms_2.21.1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.bidi_1.1.0.v20160728-1031.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.event_1.4.0.v20170105-1446.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.hamcrest.core_1.3.0.v201303031735.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/minimal-json_0.9.4.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.gpio_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.json.marshaller.unmarshaller.provider_1.0.100.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.handler_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.codec-http_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.commons.fileupload_1.3.2.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/osgi.cmpn_4.3.0.201111022214.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.usb.armv6hf_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.geronimo.specs.geronimo-jta_1.1_spec_1.1.1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/usb4java-javax_1.2.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.broker.artemis.simple.mqtt_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.camel.sun.misc_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.transport-native-kqueue_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.xml.marshaller.unmarshaller.provider_1.0.100.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.net.admin_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.region_1.4.0.v20170117-1425.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.asset.helper.provider_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.buffer_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.eclipsesource.jaxrs.jersey-min_2.22.2.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.ip_1.1.400.v20161007-1324.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.net_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.common_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.clock_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.misc.cloudcat_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.cm_1.2.0.v20170105-1446.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.geronimo.specs.geronimo-json_1.0_spec_1.0.0.alpha-1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.jboss.logging.jboss-logging_3.3.1.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.core.comm_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.concurrent_1.1.0.v20130327-1442.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.eclipsesource.jaxrs.provider.gson_2.3.0.201602281253.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.camel.camel-amqp_2.21.1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.device_1.0.400.v20130327-1442.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.felix.scr_2.0.10.v20170501-2007.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.weaving.caching.j9_1.1.0.v20160929-1449.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.geronimo.specs.geronimo-jms_2.0_spec_1.0.0.alpha-2.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.command_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.codec_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.codeminders.hidapi.aarch64_1.1.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.jetty.security_9.4.12.v20180830.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.debian.provider_1.0.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/javax.el_2.2.0.v201303151357.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.codeminders.hidapi.x86_64_1.1.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.logging.log4j.api_2.8.2.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.codeminders.hidapi_1.1.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.util_1.1.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.bluetooth_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.core.certificates_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.soda.dk.comm.aarch64_1.2.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-core_4.3.14.RELEASE_1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.core.jobs_3.9.2.v20171030-1027.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/jdk.dio.armv6hf_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/jdk.dio_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.supplement_1.7.0.v20170329-1416.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.broker.artemis.xml_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/osgi.annotation_6.0.1.201503162037.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.knowhowlab.osgi.monitoradmin_1.0.2.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.rest.asset.provider_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.launcher_1.4.0.v20161219-1356.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-jms_4.3.14.RELEASE_1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.position_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.camel.camel-core-osgi_2.21.1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.linux.watchdog_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/jdk.dio.x86_64_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.codec-mqtt_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.core.status_1.0.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-context_4.3.14.RELEASE_1.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.ds_1.5.0.v20170307-1429.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.http.servlet_1.4.0.v20170524-1452.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.wire.h2db.component.provider_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.wire.provider_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.core.deployment_1.1.400.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/slf4j.api_1.7.25.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.core.configuration_2.0.100.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.sun.misc_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.ble.provider_1.0.200.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.weaving.caching_1.1.0.v20161007-1415.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.transforms.hook_1.2.0.v20170105-1446.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.http.registry_1.1.400.v20150715-1528.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.gwt.user_1.1.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.console_1.1.300.v20170512-2111.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.wireadmin_1.0.800.v20180827-1235.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.jetty.server_9.4.12.v20180830.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.core.expressions_3.6.0.v20170207-1037.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.osgi.util_3.4.0.v20170111-1608.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.wire.component.conditional.provider_1.0.100.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.resolver_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.kura.localization_1.0.300.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.commons.lang3_3.4.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.felix.dependencymanager_3.0.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.eclipse.equinox.metatype_1.4.300.v20170105-1446.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/javax.servlet_3.1.0.v201410161800.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/org.apache.qpid.jms.client_0.31.0.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/com.google.gson_2.7.0.v20170129-0911.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.transport-native-unix-common_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/plugins/io.netty.transport_4.1.22.Final.jar \
  /opt/eclipse/kura_4.0.0_solid_sense/.data/snapshot_0.xml \
  /opt/eclipse/kura_4.0.0_solid_sense/.data/kuranet.conf \
  /opt/eclipse/kura_4.0.0_solid_sense/.data/dhcpd-eth0.conf \
  /opt/eclipse/kura_4.0.0_solid_sense/.data/dhcpd-wlan0.conf \
  /opt/eclipse/kura_4.0.0_solid_sense/.data/iptables \
  /opt/eclipse/kura_4.0.0_solid_sense/.data/ip6tables \
  /opt/eclipse/kura_4.0.0_solid_sense/.data/hostapd-wlan0.conf \
  /opt/eclipse/kura_4.0.0_solid_sense/.data/interfaces \
  /opt/eclipse/kura_4.0.0_solid_sense/user/kuranet.conf \
  /opt/eclipse/kura_4.0.0_solid_sense/user/kura_custom.properties \
  /opt/eclipse/kura_4.0.0_solid_sense/user/.kura_custom.properties.swp \
  /opt/eclipse/kura_4.0.0_solid_sense/user/log4j.xml \
  /opt/eclipse/kura_4.0.0_solid_sense/user/snapshots \
  /opt/eclipse/kura_4.0.0_solid_sense/user/security \
  /opt/eclipse/kura_4.0.0_solid_sense/user/security/cacerts.ks \
  /opt/SolidSense/utilities \
  /opt/SolidSense/kura \
  /opt/SolidSense/utilities/kura_config.sh \
  /opt/SolidSense/utilities/solid_sense_install \
  /opt/SolidSense/utilities/repair \
  /opt/SolidSense/utilities/init \
  /opt/SolidSense/utilities/qmm \
  /opt/SolidSense/utilities/repair/repair_wifi \
  /opt/SolidSense/utilities/repair/hostapd-wlan0.conf.tmpl \
  /opt/SolidSense/utilities/repair/repair_wifi.zip \
  /opt/SolidSense/utilities/init/airsense-config \
  /opt/SolidSense/utilities/init/initscript \
  /opt/SolidSense/utilities/qmm/qmm_launcher \
  /opt/SolidSense/utilities/qmm/QMM-README \
  /opt/SolidSense/utilities/qmm/qmm \
  /opt/SolidSense/utilities/qmm/.operatorsDB \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0 \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/libgcc_s.so.1 \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/libz.so.1 \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/_codecs_jp.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/termios.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/resource.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/libbz2.so.1.0 \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/_ctypes.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/libssl.so.1.0.0 \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/_codecs_tw.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/_codecs_iso2022.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/_multibytecodec.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/libffi.so.6 \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/_codecs_kr.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/_hashlib.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/libcrypto.so.1.0.0 \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/bz2.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/QuectelATmain-V1.0 \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/readline.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/_codecs_hk.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/libtinfo.so.5 \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/_codecs_cn.arm-linux-gnueabihf.so \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/libreadline.so.6 \
  /opt/SolidSense/utilities/qmm/QuectelATmain-V1.0/_json.arm-linux-gnueabihf.so \
  /opt/SolidSense/kura/snapshots \
  /opt/SolidSense/kura/plugins \
  /opt/SolidSense/kura/config \
  /opt/SolidSense/kura/snapshots/snapshot_BS181300100_AMAGGI_190107.xml \
  /opt/SolidSense/kura/snapshots/@eaDir \
  /opt/SolidSense/kura/snapshots/@eaDir/snapshot_BS181300100_AMAGGI_190107.xml@SynoEAStream \
  /opt/SolidSense/kura/plugins/org.eclipse.kura.net.admin_1.0.400.jar \
  /opt/SolidSense/kura/plugins/org.eclipse.kura.linux.net_1.0.400.jar \
  /opt/SolidSense/kura/config/snapshot_0.xml \
  /opt/SolidSense/kura/config/gen_kura_properties.py \
  /opt/SolidSense/kura/config/kura_custom.properties.tmpl \
  /opt/SolidSense/kura/config/kura.properties \
  /opt/SolidSense/kura/config/solid_kura_config \
  /opt/SolidSense/kura/config/kuranet.conf.tmpl \
  /opt/SolidSense/kura/config/snapshot_1544970882041.xml \
  /opt/SolidSense/kura/config/kura_custom \
  /opt/SolidSense/kura/config/snapshot0-elements \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-data.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-position.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-firewall.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-mqtt.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-net.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-mqtt-ref.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-clock.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-net-ppp.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-watchdog.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-reference.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-header.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-H2Db.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-ssl.xml \
  /opt/SolidSense/kura/config/snapshot0-elements/snapshot_0-cloud.xml \
"
