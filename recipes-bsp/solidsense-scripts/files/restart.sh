#!/bin/sh
 
# Globals
PROG_NAME="$(basename "${0}")"
SLEEP="5"
OVERLAY_DIR="/media/rfs/rw/upperdir"
           
# functions
          
usage () {                              
        echo "${PROG_NAME}:"      
	echo "    -h|--help			:display this help"
        echo "    -s|--sleep                    :default is <5>"
        echo "    -w|--wipe                     :whether to erase overlay"           
	exit 1
}

wipe_overlay () {
	if [ ! -d "${OVERLAY_DIR}" ]; then
		echo "Overlay directory <${OVERLAY_DIR}> does not exist!"
		exit 1
	fi

	rm -rf "${OVERLAY_DIR}"
	mkdir -p "${OVERLAY_DIR}"
}

do_restart () {
	echo "Rebooting $(hostname) in ${SLEEP} seconds"
	(sleep "${SLEEP}" && reboot) &
}

# main

options=$(getopt -l "help,sleep:,wipe" -o "hs:w" -- "${@}")
eval set -- "${options}"

while true
do
	case "${1}" in
		-h|--help )
			usage
			;;
		-s|--sleep )
			shift
			SLEEP="${1}"
			;;
		-w|--wipe )
			WIPE="1"
			;;
		\? )
			usage
			;;
		: )
			echo "Invalid option: ${OPTARG} requires an argument" 1>&2
			;;
		-- )
			shift
			break
			;;
		* )
			usage
			;;
	esac
	shift
done

if [ "${WIPE}" = "1" ]; then
	wipe_overlay
	do_restart
else
	do_restart
fi
