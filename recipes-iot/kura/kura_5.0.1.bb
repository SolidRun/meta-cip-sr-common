SUMMARY = "Kura"
DESCRIPTION = "Kura"
LICENSE = "EPL-1.0"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=0a41ba798cc1e1772a98a4888f1d8709 \
"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=master;destsuffix=SolidSense-V1;name=SolidSense-V1 \
    git://github.com/SolidRun/kura.git;branch=solidsense-5.0.1;destsuffix=kura-${PV};name=kura \
    file://kura.service \
    file://polkit.kura \
    file://sudoers.kurad \
    file://0001-add-n8-compact.patch \
"
SRCREV_SolidSense-V1 = "e01f8420fd3717a7e7ee3719969b4e268e41797e"
SRCREV_kura = "6120d4a66fbf98119d82a7b7e87f9795ced881c6"
S-V1 = "${WORKDIR}/SolidSense-V1"
S-KURA = "${WORKDIR}/kura-${PV}"
KURA_VERSION = "${PV}"
KURA_VERSION_PATH = "/opt/eclipse/kura_${KURA_VERSION}_solid_sense"
KURA_PATH = "/opt/eclipse/kura"
KURA_PROFILE = "${@bb.utils.contains('MACHINE', 'imx8mnc', 'n8-compact', 'raspberry-pi-2', d)}"

SYSTEMD_SERVICE_${PN} = "kura.service firewall.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

DEPENDS = " \
    maven-native \
    openssl \
    openjdk-11-native \
"
RDEPENDS_${PN} = " \
    bash \
    ca-certificates-java \
    openjdk-11 \
    openssl \
    python3 \
"

JAVA_HOME="${WORKDIR}/recipe-sysroot-native/usr/lib/jvm/openjdk-11-native"

inherit systemd useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = " \
    --no-create-home --system --password '' --shell /sbin/nologin --user-group kura; \
    --system --password '' --groups dialout --user-group kurad; \
"
USERMOD_PARAM_${PN} = " \
    --lock kurad \
"

do_compile () {
    export JAVA_HOME="${JAVA_HOME}"

    # Kura
    cd ${S-KURA}
    ./build-all.sh

    # Custom plugins
    #cd ${SRC_SS}/Kura/LTE/org.eclipse.kura.linux.net
    #mvn -f pom.xml clean install ${MAVEN_PROPS}
    #cd ${SRC_SS}/Kura/LTE/org.eclipse.kura.net.admin
    #mvn -f pom.xml clean install ${MAVEN_PROPS}
}

do_install () {
    # Install Kura from zip file
    install -d ${D}/opt/eclipse
    cd ${D}/opt/eclipse
    unzip ${S-KURA}/kura/distrib/target/kura_${KURA_VERSION}_${KURA_PROFILE}.zip
    mv kura_${KURA_VERSION}_${KURA_PROFILE} kura_${KURA_VERSION}_solid_sense
    ln -s ${KURA_VERSION_PATH} ${D}/opt/eclipse/kura_${KURA_VERSION}_${KURA_PROFILE}
    ln -s ${KURA_VERSION_PATH} ${D}/opt/eclipse/kura

    # Mimic Kura kura_install.sh script

    # Install kura unit file
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/kura.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/kura.service

    # Install recovery directory and data directory
    install -d ${D}${KURA_VERSION_PATH}/.data
    install -d ${D}${KURA_VERSION_PATH}/data

    # Install /etc/sysconfig directory for iptables configuation file
    install -d ${D}${sysconfdir}/sysconfig

    # Install manage_kura_users.h in recovery directory
    install -m 0700 ${D}${KURA_VERSION_PATH}/install/manage_kura_users.sh ${D}${KURA_VERSION_PATH}/.data/manage_kura_users.sh

    # setup kurad user
    install -d ${D}${sysconfdir}/sudoers.d
    install -m 0600 ${WORKDIR}/sudoers.kurad ${D}${sysconfdir}/sudoers.d/kurad
    install -d ${D}${datadir}/polkit-1/rules.d
    install -m 0600 ${WORKDIR}/polkit.kura ${D}${datadir}/polkit-1/rules.d/kura.rules

    # Install default networking file
    install -d ${D}${sysconfdir}/network
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/network.interfaces ${D}${sysconfdir}/network/interfaces
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/network.interfaces ${D}${KURA_VERSION_PATH}/.data/interfaces

    # Install network helper scripts
    install -d ${D}${sysconfdir}/network/if-up.d
    install -d ${D}${sysconfdir}/network/if-down.d
    install -m 0744 ${D}${KURA_VERSION_PATH}/install/ifup-local.debian ${D}${sysconfdir}/network/if-up.d/ifup-local
    install -m 0744 ${D}${KURA_VERSION_PATH}/install/ifdown-local ${D}${sysconfdir}/network/if-down.d/idown-local

    # Install the default configuration recovery script
    install -m 0755 ${D}${KURA_VERSION_PATH}/install/recover_default_config.init \
        ${D}${KURA_VERSION_PATH}/bin/.recoverDefaultConfig.sh

    # Install firewall rules
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/iptables.init ${D}${KURA_VERSION_PATH}/.data/iptables
    #install -m 0644 ${D}${KURA_VERSION_PATH}/install/ip6tables.init ${D}${KURA_VERSION_PATH}/.data/ip6tables
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/iptables.init ${D}${sysconfdir}/sysconfig/iptables
    #install -m 0644 ${D}${KURA_VERSION_PATH}/install/ip6tables.init ${D}${sysconfdir}/sysconfig/ip6tables
    install -m 0755 ${D}${KURA_VERSION_PATH}/install/firewall.init ${D}${KURA_VERSION_PATH}/bin/firewall
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/firewall.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        -e 's,KURA_DIR,${KURA_PATH},g' \
        ${D}${systemd_unitdir}/system/firewall.service

    # Install snapshot_0.xml to recovery directory
    install -m 0644 ${D}${KURA_VERSION_PATH}/user/snapshots/snapshot_0.xml ${D}${KURA_VERSION_PATH}/.data/snapshot_0.xml

    # Install snapshot_0.xml to template directory
    install -d ${D}/opt/SolidSense/template/kura/
    install -m 0644 ${D}${KURA_VERSION_PATH}/user/snapshots/snapshot_0.xml ${D}/opt/SolidSense/template/kura/snapshot_0.xml

    # Install networking configuration

    # dhcpd config for eth0
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/dhcpd-eth0.conf ${D}${sysconfdir}/dhcpd-eth0.conf
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/dhcpd-eth0.conf ${D}${KURA_VERSION_PATH}/.data/dhcpd-eth0.conf
    # dhcpd config for wlan0
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/dhcpd-wlan0.conf ${D}${sysconfdir}/dhcpd-wlan0.conf
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/dhcpd-wlan0.conf ${D}${KURA_VERSION_PATH}/.data/dhcpd-wlan0.conf
    # kuranet.conf
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/kuranet.conf ${D}${KURA_VERSION_PATH}/user/kuranet.conf
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/kuranet.conf ${D}${KURA_VERSION_PATH}/.data/kuranet.conf
    # named
    install -d ${D}${sysconfdir}/bind
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/named.conf ${D}${sysconfdir}/bind/named.conf
    install -d ${D}${localstatedir}/named
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/named.ca ${D}${localstatedir}/named/named.ca
    install -m 0644 ${D}${KURA_VERSION_PATH}/install/named.rfc1912.zones ${D}${sysconfdir}/named.rfc1912.zones
    ln -s /etc/bind/db.empty ${D}${localstatedir}/named/named.empty
    ln -s /etc/bind/db.local ${D}${localstatedir}/named/named.localhost
    ln -s /etc/bind/db.127 ${D}${localstatedir}/named/named.loopback

    # Install updated start_kura_background.sh
    install -d ${D}${KURA_VERSION_PATH}/bin
    install -m 0755 ${S-V1}/Kura/scripts/start_kura_background.sh ${D}${KURA_VERSION_PATH}/bin/start_kura_background.sh

    # Install shell script to assist with running cli command via Kura/Kapua
    install -d ${D}${base_bindir}
    install -m 0755 ${S-V1}/Kura/scripts/krc.sh ${D}${base_bindir}/krc

    # Install updated logging config
    install -d ${D}${KURA_VERSION_PATH}/user
    install -m 0644 ${S-V1}/Kura/user/log4j.xml ${D}${KURA_VERSION_PATH}/user/log4j.xml

    # Install the log configuration service
    install -d ${D}${KURA_VERSION_PATH}/packages
    install -m 0655 ${S-V1}/Kura/logs/com.solidsense.kura.LogConfigurationService/resources/dp/LogConfigurationService.dp \
        ${D}${KURA_VERSION_PATH}/packages/LogConfigurationService_1.0.0.dp

    chown -R kurad:kurad ${D}/opt/eclipse

    # Delete unneeded files
    rm ${D}/opt/eclipse/kura_${KURA_VERSION}_solid_sense/user/snapshots/snapshot_0.xml
}

FILES_${PN} = " \
  /opt \
  /bin \
  /usr \
  /etc \
  /var \
  /opt/eclipse \
  /opt/SolidSense \
  /opt/eclipse/kura \
  /opt/eclipse/kura_5.0.0_raspberry-pi-2 \
  /opt/eclipse/kura_5.0.0_n8-compact \
  /opt/eclipse/kura_5.0.0_solid_sense \
  /opt/eclipse/kura_5.0.0_solid_sense/notice.html \
  /opt/eclipse/kura_5.0.0_solid_sense/data \
  /opt/eclipse/kura_5.0.0_solid_sense/bin \
  /opt/eclipse/kura_5.0.0_solid_sense/log4j \
  /opt/eclipse/kura_5.0.0_solid_sense/framework \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins \
  /opt/eclipse/kura_5.0.0_solid_sense/console \
  /opt/eclipse/kura_5.0.0_solid_sense/install \
  /opt/eclipse/kura_5.0.0_solid_sense/.data \
  /opt/eclipse/kura_5.0.0_solid_sense/packages \
  /opt/eclipse/kura_5.0.0_solid_sense/user \
  /opt/eclipse/kura_5.0.0_solid_sense/bin/start_kura_debug.sh \
  /opt/eclipse/kura_5.0.0_solid_sense/bin/start_kura.sh \
  /opt/eclipse/kura_5.0.0_solid_sense/bin/start_kura_background.sh \
  /opt/eclipse/kura_5.0.0_solid_sense/bin/.recoverDefaultConfig.sh \
  /opt/eclipse/kura_5.0.0_solid_sense/bin/firewall \
  /opt/eclipse/kura_5.0.0_solid_sense/log4j/log4j.xml \
  /opt/eclipse/kura_5.0.0_solid_sense/framework/config.ini \
  /opt/eclipse/kura_5.0.0_solid_sense/framework/kura.properties \
  /opt/eclipse/kura_5.0.0_solid_sense/framework/jdk.dio.properties \
  /opt/eclipse/kura_5.0.0_solid_sense/framework/RELEASE_NOTES.txt \
  /opt/eclipse/kura_5.0.0_solid_sense/framework/jdk.dio.policy \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.keystore_1.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.qpid.jms.client_0.45.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.jetty.continuation_9.4.41.v20210516.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.comm_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.wire.provider_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.useradmin.store_1.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.localization_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-expression_4.3.20.RELEASE_1.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.coordinator_1.3.800.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-context_4.3.20.RELEASE_1.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.felix.useradmin_1.0.4.k1.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.configuration_2.0.300.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.wire.camel_1.0.200.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.osgi_3.16.0.v20200828-0759.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/minimal-json_0.9.5.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.soda.dk.comm_1.2.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.commons.beanutils_1.9.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.eclipsesource.jaxrs.provider.security_2.2.0.201602281253.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/javax.servlet.jsp_2.2.0.v201112011158.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.log4j2-api-config_1.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.http.jetty_3.7.400.v20200123-1333.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.camel.cloud.factory_1.1.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.weaving.caching_1.1.400.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.codec-http_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.soda.dk.comm.armv6hf_1.2.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.usb4java_1.2.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/bluez-dbus-osgi_0.1.4.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.log.stream_1.0.300.v20200828-1034.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.core.jobs_3.10.800.v20200421-0950.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.deployment_1.1.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.core.runtime_3.19.0.v20200724-1004.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.camel.camel-amqp_2.25.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/bcpkix_1.65.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.json.marshaller.unmarshaller.provider_1.0.300.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.jetty.security_9.4.41.v20210516.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.app_1.5.0.v20200717-0620.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.jetty.server_9.4.41.v20210516.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.console_1.4.200.v20200828-1034.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-jms_4.3.20.RELEASE_1.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/jakarta.annotation-api_1.3.5.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.commons.exec_1.3.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.http.server.manager_1.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.codeminders.hidapi.aarch64_1.1.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.bluetooth_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.commons.io_2.4.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/jakarta.activation-api_1.2.2.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.felix.scr_2.1.16.v20200110-1820.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.commons.net_3.1.0.v201205071737.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.logging.log4j.slf4j-impl_2.8.2.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.sysv.provider_1.0.200.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.eclipsesource.jaxrs.publisher_5.3.1.201602281253.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-core_4.3.20.RELEASE_1.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.camel_1.3.200.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.transport-native-unix-common_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/usb4java-javax_1.2.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.soda.dk.comm.x86_64_1.2.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.util_1.1.300.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.google.protobuf_3.8.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.codeminders.hidapi.x86_64_1.1.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/jdk.dio.armv6hf_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.soda.dk.comm.aarch64_1.2.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.device_1.1.0.v20200810-0747.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.core.variables_3.4.800.v20200120-1101.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.tigris.mtoolkit.iagent.rpc_3.0.0.20110411-0918.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.felix.gogo.shell_1.1.0.v20180713-1646.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.preferences_3.8.0.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.camel.sun.misc_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.xml.marshaller.unmarshaller.provider_1.0.300.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/jcl.over.slf4j_1.7.25.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.cloudconnection.raw.mqtt.provider_1.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.misc.cloudcat_1.0.400.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.codec_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.cloud_1.1.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.driver.helper.provider_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.position_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/jdk.dio.x86_64_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.jetty.http_9.4.41.v20210516.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.handler_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.ble.eddystone.provider_1.0.400.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.camel.camel-core-osgi_2.25.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.broker.artemis.core_1.1.200.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.asset.provider_2.0.300.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.tamper.detection_1.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.clock_1.1.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/jakarta.xml.bind-api_2.3.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.jsp.jasper.registry_1.1.400.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.felix.deploymentadmin_0.9.5.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.gwt.user_1.1.200.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.logging.log4j.core_2.8.2.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/jdk.dio.aarch64_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.commons.csv_1.4.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.qpid.proton-j_0.33.2.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.registry_3.9.0.v20200625-1425.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/jakarta.xml.ws-api_2.3.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.transport_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.codeminders.hidapi_1.1.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.util_1.1.200.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.weaving.caching.j9_1.1.400.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.bidi_1.3.0.v20200612-1624.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.glassfish.hk2.osgi-resource-locator_1.0.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.commons.collections_3.2.2.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.useradmin_1.2.0.v20200807-1148.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.usb.armv6hf_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.activemq.artemis_2.6.100.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.felix.gogo.command_1.0.2.v20170914-1324.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.hamcrest.core_1.3.0.v201303031735.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.cm_1.4.400.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.wire.h2db.component.provider_2.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.network.threat.manager_1.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.supplement_1.10.0.v20200612-0806.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.buffer_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.activemq.artemis-native_2.6.4.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.web2_2.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.transport-native-kqueue_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.inventory_1.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.common_3.13.0.v20200828-1034.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.geronimo.specs.geronimo-jta_1.1_spec_1.1.1.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/jdk.dio_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/jakarta.xml.soap-api_1.4.2.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.transport-native-epoll_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.camel.camel-jms_2.25.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.camel.camel-core_2.25.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.concurrent_1.1.500.v20200106-1437.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.eclipsesource.jaxrs.jersey-min_2.22.2.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-tx_4.3.20.RELEASE_1.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.servicemix.bundles.spring-beans_4.3.20.RELEASE_1.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.geronimo.specs.geronimo-json_1.0_spec_1.0.0.alpha-1.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.jetty.util_9.4.41.v20210516.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.io_1.1.100.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.transforms.hook_1.2.500.v20190714-1852.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.camel.xml_1.1.200.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.eclipsesource.jaxrs.provider.gson_2.3.0.201602281253.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.gpio_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.http.registry_1.2.0.v20200614-1851.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.system_1.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.ble.provider_1.0.400.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.geronimo.specs.geronimo-jms_2.0_spec_1.0.0.alpha-2.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.metatype_1.5.300.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.osgi.util_3.5.300.v20190708-1141.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.broker.artemis.xml_1.0.400.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/bcprov_1.65.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.rest.provider_1.1.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/javax.el_2.2.0.v201303151357.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.localization.resources_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.api_2.2.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.camel.camel-script_2.25.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.common_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.hook.file.move.provider_1.0.400.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.rest.asset.provider_1.0.400.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.logging.log4j.api_2.8.2.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.jetty.customizer_1.0.200.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.net_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.wire.component.provider_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.commons.fileupload_1.3.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.h2database_1.4.199.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.jetty.servlet_9.4.41.v20210516.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.weaving.hook_1.2.700.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.launcher_1.5.800.v20200727-1323.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.cloud.factory_1.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.camel.camel-stream_2.25.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.region_1.5.0.v20200807-1629.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.status_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/osgi.annotation_6.0.1.201503162037.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.asset.helper.provider_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.knowhowlab.osgi.monitoradmin_1.0.2.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.certificates_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.ds_1.6.200.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.google.guava_25.0.0.jre.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.watchdog_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.core.expressions_3.7.0.v20200720-1126.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.cloudconnection.eclipseiot.mqtt.provider_1.0.200.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.codec-mqtt_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.asset.cloudlet.provider_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.usb_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.event_1.5.500.v20200616-0800.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.core.contenttype_3.7.800.v20200724-0804.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.frameworkadmin_2.1.400.v20191002-0702.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.jetty.util.ajax_9.4.41.v20210516.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.broker.artemis.simple.mqtt_1.0.400.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.ble.ibeacon.provider_1.0.400.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/io.netty.resolver_4.1.34.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.felix.gogo.runtime_1.1.0.v20180713-1646.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.transforms.xslt_1.1.100.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.deployment.agent_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.core.crypto_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/slf4j.api_1.7.25.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.wire.component.join.provider_1.0.300.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.debian.provider_1.0.200.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.command_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.wire.component.conditional.provider_1.0.300.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.wireadmin_1.0.800.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.jetty.io_9.4.41.v20210516.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.codeminders.hidapi.armv6hf_1.1.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.wire.helper.provider_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.http.servlet_1.6.600.v20200707-1543.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.net.admin_1.0.600.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.commons.lang3_3.4.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.felix.dependencymanager_3.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.apache.activemq.artemis-mqtt-protocol_2.6.4.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.sun.xml.bind.jaxb-osgi_2.3.3.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.jboss.logging.jboss-logging_3.3.2.Final.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.console.jaas.fragment_1.0.300.v20200111-0718.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.frameworkadmin.equinox_1.1.400.v20200319-1546.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.osgi.services_3.9.0.v20200511-1725.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.sun.misc_1.0.500.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.equinox.jsp.jasper_1.1.500.v20200422-1833.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/org.eclipse.kura.linux.net_2.0.0.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/javax.servlet_3.1.0.v201410161800.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/plugins/com.google.gson_2.7.0.v20170129-0911.jar \
  /opt/eclipse/kura_5.0.0_solid_sense/console/skin \
  /opt/eclipse/kura_5.0.0_solid_sense/console/skin/favicon-16x16.png \
  /opt/eclipse/kura_5.0.0_solid_sense/console/skin/favicon-32x32.png \
  /opt/eclipse/kura_5.0.0_solid_sense/console/skin/favicon-96x96.png \
  /opt/eclipse/kura_5.0.0_solid_sense/console/skin/favicon.ico \
  /opt/eclipse/kura_5.0.0_solid_sense/install/usr.sbin.named \
  /opt/eclipse/kura_5.0.0_solid_sense/install/named.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/install/iptables.init \
  /opt/eclipse/kura_5.0.0_solid_sense/install/named.ca \
  /opt/eclipse/kura_5.0.0_solid_sense/install/kuranet.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/install/kura.service \
  /opt/eclipse/kura_5.0.0_solid_sense/install/kura.logrotate \
  /opt/eclipse/kura_5.0.0_solid_sense/install/kura.init.yocto \
  /opt/eclipse/kura_5.0.0_solid_sense/install/ifup-local \
  /opt/eclipse/kura_5.0.0_solid_sense/install/hostapd.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/install/sysctl.kura.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/install/kura_install.sh \
  /opt/eclipse/kura_5.0.0_solid_sense/install/ifup-local.raspbian \
  /opt/eclipse/kura_5.0.0_solid_sense/install/dhcpd-eth0.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/install/dhcpd-wlan0.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/install/network.interfaces \
  /opt/eclipse/kura_5.0.0_solid_sense/install/monitrc.raspbian \
  /opt/eclipse/kura_5.0.0_solid_sense/install/logrotate.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/install/kura.init.raspbian \
  /opt/eclipse/kura_5.0.0_solid_sense/install/ifup-local.debian \
  /opt/eclipse/kura_5.0.0_solid_sense/install/recover_default_config.init \
  /opt/eclipse/kura_5.0.0_solid_sense/install/manage_kura_users.sh \
  /opt/eclipse/kura_5.0.0_solid_sense/install/patch_sysctl.sh \
  /opt/eclipse/kura_5.0.0_solid_sense/install/firewall.init \
  /opt/eclipse/kura_5.0.0_solid_sense/install/ifdown-local \
  /opt/eclipse/kura_5.0.0_solid_sense/install/monit.init.raspbian \
  /opt/eclipse/kura_5.0.0_solid_sense/install/named.rfc1912.zones \
  /opt/eclipse/kura_5.0.0_solid_sense/install/firewall.service \
  /opt/eclipse/kura_5.0.0_solid_sense/.data/snapshot_0.xml \
  /opt/eclipse/kura_5.0.0_solid_sense/.data/kuranet.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/.data/dhcpd-eth0.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/.data/dhcpd-wlan0.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/.data/iptables \
  /opt/eclipse/kura_5.0.0_solid_sense/.data/manage_kura_users.sh \
  /opt/eclipse/kura_5.0.0_solid_sense/.data/interfaces \
  /opt/eclipse/kura_5.0.0_solid_sense/packages/LogConfigurationService_1.0.0.dp \
  /opt/eclipse/kura_5.0.0_solid_sense/user/kuranet.conf \
  /opt/eclipse/kura_5.0.0_solid_sense/user/kura_custom.properties \
  /opt/eclipse/kura_5.0.0_solid_sense/user/log4j.xml \
  /opt/eclipse/kura_5.0.0_solid_sense/user/snapshots \
  /opt/eclipse/kura_5.0.0_solid_sense/user/security \
  /opt/eclipse/kura_5.0.0_solid_sense/user/security/cacerts.ks \
  /opt/SolidSense/template \
  /opt/SolidSense/template/kura \
  /opt/SolidSense/template/kura/snapshot_0.xml \
  /bin/krc \
  /usr/share \
  /usr/share/polkit-1 \
  /usr/share/polkit-1/rules.d \
  /usr/share/polkit-1/rules.d/kura.rules \
  /etc/dhcpd-eth0.conf \
  /etc/dhcpd-wlan0.conf \
  /etc/named.rfc1912.zones \
  /etc/sysconfig \
  /etc/bind \
  /etc/network \
  /etc/sudoers.d \
  /etc/sysconfig/iptables \
  /etc/bind/named.conf \
  /etc/network/interfaces \
  /etc/network/if-up.d \
  /etc/network/if-down.d \
  /etc/network/if-up.d/ifup-local \
  /etc/network/if-down.d/idown-local \
  /etc/sudoers.d/kurad \
  /var/named \
  /var/named/named.ca \
  /var/named/named.localhost \
  /var/named/named.empty \
  /var/named/named.loopback \
"
