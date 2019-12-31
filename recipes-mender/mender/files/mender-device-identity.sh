#!/bin/sh

set -ue

SCN=/sys/class/net
min=65535
arphrd_ether=1
ifdev=
solidsense_file="/etc/solidsense"

# find iface with lowest ifindex, skip non ARPHRD_ETHER types (lo, sit ...)
for dev in "${SCN}"/*; do
	if [ ! -f "${dev}/type" ]; then
		continue
	fi

	iftype=$(cat "${dev}"/type)
	if [ "${iftype}" -ne "${arphrd_ether}" ]; then
		continue
	fi

	idx=$(cat "${dev}"/ifindex)
	if [ "${idx}" -lt "${min}" ]; then
		min=${idx}
		ifdev=${dev}
	fi
done

if [ -z "${ifdev}" ]
then
	echo "no suitable interfaces found" >&2
	exit 1
else
	echo "using interface ${ifdev}" >&2
	# grab MAC address
	echo "mac=$(cat "${ifdev}"/address)"
fi

# shellcheck source=/dev/null
. ${solidsense_file}

if [ -n "${SERIAL}" ]
then
	echo "serial=${SERIAL}"
fi
