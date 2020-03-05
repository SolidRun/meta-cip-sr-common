SUMMARY = "Wirepas Mesh - IoT network with unprecedented scale, density, flexibility and reliability"
HOMEPAGE = "https://github.com/wirepas/backend-apis"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb6bb17b0d0cca188339074207e9f4d8"

SRC_URI = " \
    git://github.com/SolidRun/backend-apis;branch=maersk-dev \
"
SRCREV = "0aee936ef892538d3a0876db7726a2e4e918c80a"
S = "${WORKDIR}/git"

DEPENDS = " \
    python3-native \
    python3-grpcio-native \
    python3-grpcio-tools-native \
"
RDEPENDS_${PN} = " \
    python3 \
    python3-alabaster \
    python3-atomicwrites \
    python3-attrs \
    python3-babel \
    python3-certifi \
    python3-chardet \
    python3-coverage \
    python3-docutils \
    python3-grpcio (=1.23.0) \
    python3-grpcio-tools \
    python3-idna \
    python3-imagesize \
    python3-importlib-metadata \
    python3-jinja2 \
    python3-markupsafe \
    python3-more-itertools \
    python3-packaging \
    python3-pluggy \
    python3-pockets \
    python3-protobuf (=3.10.0) \
    python3-pygments \
    python3-py \
    python3-pyparsing \
    python3-pytest \
    python3-pytz \
    python3-requests \
    python3-snowballstemmer \
    python3-sphinx \
    python3-sphinxcontrib-applehelp \
    python3-sphinxcontrib-devhelp \
    python3-sphinxcontrib-jsmath \
    python3-sphinxcontrib-htmlhelp \
    python3-sphinxcontrib-napoleon \
    python3-sphinxcontrib-qthelp \
    python3-sphinxcontrib-serializinghtml \
    python3-urllib3 \
    python3-wcwidth \
"

WIREPAS_MESSAGING_INSTALL_ARGS = " \
    --root=${D} \
    --prefix=${prefix} \
    --install-data=${datadir} \
"

inherit python3native python3-dir setuptools3

do_configure_prepend () {
    cd ${S}/wrappers/python
}

do_compile () {
    cd ${S}/wrappers/python
    ./utils/pull_protos.sh
    ./utils/compile_protos.sh
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py build
}

do_install () {
    cd ${S}/wrappers/python
    ${STAGING_BINDIR_NATIVE}/python3-native/python3 setup.py install ${WIREPAS_MESSAGING_INSTALL_ARGS}
}

FILES_${PN} = " \
    /usr/share \
    /usr/share/wirepas_messaging-extras \
    /usr/share/wirepas_messaging-extras/package \
    /usr/share/wirepas_messaging-extras/package/setup.py \
    /usr/share/wirepas_messaging-extras/package/README.md \
    /usr/share/wirepas_messaging-extras/package/requirements.txt \
    /usr/share/wirepas_messaging-extras/package/LICENSE \
    /usr/lib \
    /usr/lib/python3.7 \
    /usr/lib/python3.7/site-packages \
    /usr/lib/python3.7/site-packages/wirepas_messaging \
    /usr/lib/python3.7/site-packages/wirepas_messaging-1.2.0-py3.7.egg-info \
    /usr/lib/python3.7/site-packages/wirepas_messaging/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/__main__.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/__about__.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wpe \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway \
    /usr/lib/python3.7/site-packages/wirepas_messaging/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_messaging/nanopb \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wpe/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wpe/public_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wpe/public_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wpe/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wpe/__pycache__/public_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wpe/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wpe/__pycache__/public_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/config_message_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/wp_global_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/generic_message_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/error_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/data_message_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/wp_global_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/data_message_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/error_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/config_message_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/otap_message_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/generic_message_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/otap_message_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/generic_message_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/otap_message_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/wp_global_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/data_message_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/error_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/otap_message_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/generic_message_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/config_message_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/error_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/wp_global_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/config_message_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/__pycache__/data_message_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/status.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/process_scratchpad.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/wirepas_exceptions.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/get_scratchpad_status.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/send_data.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/otap_helper.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/get_gw_info.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/set_config.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/event.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/config_helper.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/gateway_result_code.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/response.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/upload_scratchpad.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/get_configs.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/request.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/received_data.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/test_send_data.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/test_get_gw_info.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/test_upload_scratchpad.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/test_status.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/test_set_config.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/test_process_scratchpad.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/test_get_configs.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/default_value.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/test_get_scratchpad_status.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/test_received_data.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/test_get_gw_info.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/default_value.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/test_upload_scratchpad.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/test_get_configs.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/test_get_scratchpad_status.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/test_status.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/test_send_data.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/test_set_config.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/test_process_scratchpad.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/test/__pycache__/test_received_data.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/status.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/config_helper.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/process_scratchpad.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/request.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/response.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/received_data.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/wirepas_exceptions.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/get_scratchpad_status.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/send_data.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/set_config.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/upload_scratchpad.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/get_configs.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/get_gw_info.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/otap_helper.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/event.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/gateway/api/__pycache__/gateway_result_code.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/__pycache__/__about__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/__pycache__/__main__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/nanopb/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/nanopb/nanopb_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/nanopb/nanopb_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/nanopb/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_messaging/nanopb/__pycache__/nanopb_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/nanopb/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/nanopb/__pycache__/nanopb_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/positioning_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/remote_api_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/internal_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/positioning_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/message_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/commons_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/message_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/internal_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/remote_api_pb2_grpc.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/commons_pb2.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/authenticationmessages.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/messagesbase.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/nodemessages.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__init__.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/floorplanmessages.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/realtimesituationmessages.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/applicationconfigurationmessages.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/networkmessages.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/areamessages.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/buildingmessages.py \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__ \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__/buildingmessages.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__/areamessages.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__/realtimesituationmessages.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__/authenticationmessages.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__/floorplanmessages.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__/applicationconfigurationmessages.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__/networkmessages.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__/nodemessages.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/ws_api/__pycache__/messagesbase.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/message_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/positioning_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/commons_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/message_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/internal_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/commons_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/remote_api_pb2_grpc.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/positioning_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/internal_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/__init__.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging/wnt/__pycache__/remote_api_pb2.cpython-37.pyc \
    /usr/lib/python3.7/site-packages/wirepas_messaging-1.2.0-py3.7.egg-info/SOURCES.txt \
    /usr/lib/python3.7/site-packages/wirepas_messaging-1.2.0-py3.7.egg-info/requires.txt \
    /usr/lib/python3.7/site-packages/wirepas_messaging-1.2.0-py3.7.egg-info/PKG-INFO \
    /usr/lib/python3.7/site-packages/wirepas_messaging-1.2.0-py3.7.egg-info/dependency_links.txt \
    /usr/lib/python3.7/site-packages/wirepas_messaging-1.2.0-py3.7.egg-info/top_level.txt \
"
