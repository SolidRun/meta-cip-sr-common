SUMMARY = "Solidsense Vehicle"
DESCRIPTION = "Solidsense Vehicle"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=8264535c0c4e9c6c335635c4026a8022 \
"

SRC_URI = " \
    git://git@github.com/SolidRun/SolidSense-Vehicle.git;protocol=ssh;branch=master \
    git://git@github.com/SolidRun/SolidSense-V1.git;protocol=ssh;branch=master;destsuffix=SolidSense-V1;name=SolidSense-V1 \
"
SRCREV = "0585c7b93199d31039192f4cdddeb7671557429f"
SRCREV_SolidSense-V1 = "e01f8420fd3717a7e7ee3719969b4e268e41797e"
S = "${WORKDIR}/git"
S-V1 = "${WORKDIR}/SolidSense-V1"

SYSTEMD_SERVICE_${PN} = "vehicle_obd.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

inherit systemd

RDEPENDS_${PN} = "\
"

do_install () {
    install -d ${D}/opt/SolidSense/vehicle
    cp -arP ${S}/* ${D}/opt/SolidSense/vehicle
    chown -R root:root ${D}/opt/SolidSense/vehicle

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/vehicle_obd.service ${D}${systemd_unitdir}/system/vehicle_obd.service
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${systemd_unitdir}/system/vehicle_obd.service
}

FILES_${PN} = " \
  /opt \
  /opt/SolidSense \
  /opt/SolidSense/vehicle \
  /opt/SolidSense/vehicle/tox.ini \
  /opt/SolidSense/vehicle/setup.py \
  /opt/SolidSense/vehicle/vehicle_obd.service \
  /opt/SolidSense/vehicle/simulator.py \
  /opt/SolidSense/vehicle/vehicle_obd_server.py \
  /opt/SolidSense/vehicle/README.md \
  /opt/SolidSense/vehicle/obd_cmd.json \
  /opt/SolidSense/vehicle/OBD_Service_pb2_grpc.py \
  /opt/SolidSense/vehicle/vehicle_service.py \
  /opt/SolidSense/vehicle/OBD_Service_pb2.py \
  /opt/SolidSense/vehicle/OBD_Service.proto \
  /opt/SolidSense/vehicle/std_obd_cmd.json \
  /opt/SolidSense/vehicle/mkdocs.yml \
  /opt/SolidSense/vehicle/LICENSE \
  /opt/SolidSense/vehicle/tests \
  /opt/SolidSense/vehicle/obd \
  /opt/SolidSense/vehicle/pint \
  /opt/SolidSense/vehicle/docs \
  /opt/SolidSense/vehicle/tests/test_protocol_can.py \
  /opt/SolidSense/vehicle/tests/test_protocol_legacy.py \
  /opt/SolidSense/vehicle/tests/test_uas.py \
  /opt/SolidSense/vehicle/tests/test_protocol.py \
  /opt/SolidSense/vehicle/tests/test_OBDCommand.py \
  /opt/SolidSense/vehicle/tests/README.md \
  /opt/SolidSense/vehicle/tests/test_commands.py \
  /opt/SolidSense/vehicle/tests/test_OBD.py \
  /opt/SolidSense/vehicle/tests/test_obdsim.py \
  /opt/SolidSense/vehicle/tests/conftest.py \
  /opt/SolidSense/vehicle/tests/test_decoders.py \
  /opt/SolidSense/vehicle/obd/utils.py \
  /opt/SolidSense/vehicle/obd/__version__.py \
  /opt/SolidSense/vehicle/obd/elm327.py \
  /opt/SolidSense/vehicle/obd/UnitsAndScaling.py \
  /opt/SolidSense/vehicle/obd/__init__.py \
  /opt/SolidSense/vehicle/obd/decoders.py \
  /opt/SolidSense/vehicle/obd/codes.py \
  /opt/SolidSense/vehicle/obd/README.md \
  /opt/SolidSense/vehicle/obd/OBDCommand.py \
  /opt/SolidSense/vehicle/obd/obd.py \
  /opt/SolidSense/vehicle/obd/commands.py \
  /opt/SolidSense/vehicle/obd/asynchronous.py \
  /opt/SolidSense/vehicle/obd/OBDResponse.py \
  /opt/SolidSense/vehicle/obd/protocols \
  /opt/SolidSense/vehicle/obd/protocols/protocol_unknown.py \
  /opt/SolidSense/vehicle/obd/protocols/protocol_legacy.py \
  /opt/SolidSense/vehicle/obd/protocols/__init__.py \
  /opt/SolidSense/vehicle/obd/protocols/README.md \
  /opt/SolidSense/vehicle/obd/protocols/protocol.py \
  /opt/SolidSense/vehicle/obd/protocols/protocol_can.py \
  /opt/SolidSense/vehicle/pint/systems.py \
  /opt/SolidSense/vehicle/pint/registry_helpers.py \
  /opt/SolidSense/vehicle/pint/util.py \
  /opt/SolidSense/vehicle/pint/numpy_func.py \
  /opt/SolidSense/vehicle/pint/registry.py \
  /opt/SolidSense/vehicle/pint/errors.py \
  /opt/SolidSense/vehicle/pint/__init__.py \
  /opt/SolidSense/vehicle/pint/unit.py \
  /opt/SolidSense/vehicle/pint/compat.py \
  /opt/SolidSense/vehicle/pint/matplotlib.py \
  /opt/SolidSense/vehicle/pint/definitions.py \
  /opt/SolidSense/vehicle/pint/constants_en.txt \
  /opt/SolidSense/vehicle/pint/converters.py \
  /opt/SolidSense/vehicle/pint/quantity.py \
  /opt/SolidSense/vehicle/pint/context.py \
  /opt/SolidSense/vehicle/pint/formatting.py \
  /opt/SolidSense/vehicle/pint/pint-convert \
  /opt/SolidSense/vehicle/pint/pint_eval.py \
  /opt/SolidSense/vehicle/pint/xtranslated.txt \
  /opt/SolidSense/vehicle/pint/babel_names.py \
  /opt/SolidSense/vehicle/pint/measurement.py \
  /opt/SolidSense/vehicle/pint/default_en.txt \
  /opt/SolidSense/vehicle/pint/testsuite \
  /opt/SolidSense/vehicle/pint/testsuite/test_babel.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_compat_upcast.py \
  /opt/SolidSense/vehicle/pint/testsuite/helpers.py \
  /opt/SolidSense/vehicle/pint/testsuite/parameterized.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_pitheorem.py \
  /opt/SolidSense/vehicle/pint/testsuite/__init__.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_errors.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_measurement.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_contexts.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_formatter.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_pint_eval.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_issues.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_converters.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_unit.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_compat_downcast.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_quantity.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_numpy_func.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_application_registry.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_matplotlib.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_numpy.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_definitions.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_infer_base_unit.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_systems.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_umath.py \
  /opt/SolidSense/vehicle/pint/testsuite/test_util.py \
  /opt/SolidSense/vehicle/pint/testsuite/baseline \
  /opt/SolidSense/vehicle/pint/testsuite/baseline/test_plot_with_set_units.png \
  /opt/SolidSense/vehicle/pint/testsuite/baseline/test_basic_plot.png \
  /opt/SolidSense/vehicle/docs/Command Tables.md \
  /opt/SolidSense/vehicle/docs/Custom Commands.md \
  /opt/SolidSense/vehicle/docs/Command Lookup.md \
  /opt/SolidSense/vehicle/docs/index.md \
  /opt/SolidSense/vehicle/docs/Async Connections.md \
  /opt/SolidSense/vehicle/docs/Responses.md \
  /opt/SolidSense/vehicle/docs/Connections.md \
  /opt/SolidSense/vehicle/docs/Debug.md \
  /opt/SolidSense/vehicle/docs/Troubleshooting.md \
  /opt/SolidSense/vehicle/docs/assets \
  /opt/SolidSense/vehicle/docs/assets/extra.js \
"
