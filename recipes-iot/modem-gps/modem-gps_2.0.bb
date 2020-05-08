SUMMARY = "Modem GPS"
DESCRIPTION = "Modem GPS"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=b5d7f7156f7785ca2eb55d6cc1b4c118 \
"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-Modem_GPS_Service.git;protocol=ssh;branch=V2.0 \
"
SRCREV = "4de111fc464c9dcc7a13f62e0f1b1ff78fc3bc2d"
S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = "modem_gps.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

inherit systemd

RDEPENDS_${PN} = "\
    python3 \
    python3-protobuf \
    python3-pyserial \
    python3-grpcio \
    python3-grpcio-tools \
"

do_install () {
    install -d ${D}/opt/SolidSense/modem_gps
    cp -arP ${S}/* ${D}/opt/SolidSense/modem_gps
    chown -R root:root ${D}/opt/SolidSense/modem_gps

    # Install modem_status
    install -d ${D}${bindir}
    install -m 0755 ${S}/modem_status ${D}${bindir}/modem_status
    sed -i -e 's,/bin/bash,/bin/sh,g' ${D}${bindir}/modem_status

    # Intsall modem_gps_check
    install -d ${D}/opt/scripts
    install -m 0755 ${S}/modem_gps_check.sh ${D}/opt/scripts/modem_gps_check

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/modem_gps.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/modem_gps.service

    # remove unecessary installed items
    rm -f ${D}/opt/SolidSense/modem_gps/install.sh
    rm -f ${D}/opt/SolidSense/modem_gps/modem_gps.service
    rm -f ${D}/opt/SolidSense/modem_gps/modem_status
    rm -f ${D}/opt/SolidSense/modem_gps/setup.py
    rm -f ${D}/opt/SolidSense/modem_gps/modem_gps_check.sh
}

FILES_${PN} = " \
  /opt \
  /usr \
  /opt/scripts \
  /opt/SolidSense \
  /opt/scripts/modem_gps_check \
  /opt/SolidSense/modem_gps \
  /opt/SolidSense/modem_gps/GPS_Service.proto \
  /opt/SolidSense/modem_gps/Modem_GPS_Parameters.py \
  /opt/SolidSense/modem_gps/Factory_Reset.py \
  /opt/SolidSense/modem_gps/Test_Modem.py \
  /opt/SolidSense/modem_gps/GPS_Service_pb2_grpc.py \
  /opt/SolidSense/modem_gps/GPS_Service_pb2.py \
  /opt/SolidSense/modem_gps/SMS_Test.py \
  /opt/SolidSense/modem_gps/Modem_Service_Client.py \
  /opt/SolidSense/modem_gps/Reg_test.py \
  /opt/SolidSense/modem_gps/GPS_Test.py \
  /opt/SolidSense/modem_gps/README.md \
  /opt/SolidSense/modem_gps/GPS_Reader.py \
  /opt/SolidSense/modem_gps/SMS_Client.py \
  /opt/SolidSense/modem_gps/ATcmd.py \
  /opt/SolidSense/modem_gps/Modem_Service.py \
  /opt/SolidSense/modem_gps/Trace_Modem.py \
  /opt/SolidSense/modem_gps/Modem_GPS_Service.py \
  /opt/SolidSense/modem_gps/QuectelAT_Service.py \
  /opt/SolidSense/modem_gps/gen_proto.sh \
  /opt/SolidSense/modem_gps/GPS_Service_Client.py \
  /opt/SolidSense/modem_gps/LICENSE \
  /opt/SolidSense/modem_gps/pynmea2 \
  /opt/SolidSense/modem_gps/pynmea2/seatalk_utils.py \
  /opt/SolidSense/modem_gps/pynmea2/__init__.py \
  /opt/SolidSense/modem_gps/pynmea2/nmea_utils.py \
  /opt/SolidSense/modem_gps/pynmea2/_version.py \
  /opt/SolidSense/modem_gps/pynmea2/nmea.py \
  /opt/SolidSense/modem_gps/pynmea2/stream.py \
  /opt/SolidSense/modem_gps/pynmea2/nmea_file.py \
  /opt/SolidSense/modem_gps/pynmea2/types \
  /opt/SolidSense/modem_gps/pynmea2/types/talker.py \
  /opt/SolidSense/modem_gps/pynmea2/types/__init__.py \
  /opt/SolidSense/modem_gps/pynmea2/types/proprietary \
  /opt/SolidSense/modem_gps/pynmea2/types/proprietary/ubx.py \
  /opt/SolidSense/modem_gps/pynmea2/types/proprietary/__init__.py \
  /opt/SolidSense/modem_gps/pynmea2/types/proprietary/tnl.py \
  /opt/SolidSense/modem_gps/pynmea2/types/proprietary/grm.py \
  /opt/SolidSense/modem_gps/pynmea2/types/proprietary/sxn.py \
  /opt/SolidSense/modem_gps/pynmea2/types/proprietary/rdi.py \
  /opt/SolidSense/modem_gps/pynmea2/types/proprietary/ash.py \
  /opt/SolidSense/modem_gps/pynmea2/types/proprietary/srf.py \
  /usr/bin \
  /usr/bin/modem_status \
"
