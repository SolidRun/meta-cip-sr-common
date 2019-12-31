SUMMARY = "Modem GPS"
DESCRIPTION = "Modem GPS"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://../gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = " \
    file://modem-gps-${PV}.tar.gz \
    file://modem_gps.service \
    file://gpl-2.0.txt \
"

SYSTEMD_SERVICE_${PN} = "modem_gps.service"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

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
    cp -arP ${WORKDIR}/modem_gps-${PV}/* ${D}/opt/SolidSense/modem_gps
    chown -R root:root ${D}/opt/SolidSense/modem_gps

    install -d ${D}/data/.opt/log
    ln -s /data/.opt/log ${D}/opt/log

    install -d ${D}/data/solidsense/modem_gps

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/modem_gps.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/modem_gps.service
}

FILES_${PN} = " \
    /data \
    /opt \
    /data/solidsense \
    /data/.opt \
    /data/solidsense/modem_gps \
    /data/.opt/log \
    /opt/log \
    /opt/SolidSense \
    /opt/SolidSense/modem_gps \
    /opt/SolidSense/modem_gps/GPS_Service.proto \
    /opt/SolidSense/modem_gps/Modem_GPS_Parameters.py \
    /opt/SolidSense/modem_gps/GPS_Service_pb2_grpc.py \
    /opt/SolidSense/modem_gps/GPS_Service_pb2.py \
    /opt/SolidSense/modem_gps/Modem_Service_Client.py \
    /opt/SolidSense/modem_gps/GPS_Reader.py \
    /opt/SolidSense/modem_gps/Modem_Service.py \
    /opt/SolidSense/modem_gps/Modem_GPS_Service.py \
    /opt/SolidSense/modem_gps/QuectelAT_Service.py \
    /opt/SolidSense/modem_gps/GPS_Service_Client.py \
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
"
