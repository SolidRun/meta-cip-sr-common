#!/bin/bash
VERSION="2.0"
DATE="22/08/2019"

#-----------------------------------------------------------------------------
# Config variables
#-----------------------------------------------------------------------------
CURRENTPATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"


#-----------------------------------------------------------------------------
# Helper functions
#-----------------------------------------------------------------------------
print_help()
{
echo "Usage: $BASH_SOURCE <sink> <firwmare file>"
echo ""
}


#-----------------------------------------------------------------------------
# Command line option processing
#-----------------------------------------------------------------------------
SINK=""
case "$1" in
    "1" )
		SINK="nordic1"
	;;
    "2" )
		SINK="nordic2"
	;;

    * )
	print_help
	exit 1
	;;
esac

#-----------------------------------------------------------------------------
# Reset Memory protection bit
#-----------------------------------------------------------------------------
LOG_FILE="/tmp/open.log"
sub_pid=$( openocd -f  $CURRENTPATH/$SINK.cfg -c init > $LOG_FILE 2>&1 & echo $! )
sleep 1
res=`cat $LOG_FILE | grep "telnet"`
if [[ `echo $res | wc -c`<2 ]]; then
	echo "Chip is busy! Please power cycle and run again. Exiting..."
	exit 1
fi
res=`( echo "targets"; sleep 0.1 )| telnet localhost 4444`
if [[ `echo $res |grep "halted"| wc -c`>1 ]]; then
	echo "Chip is ready for flashing. Continuing..."
	echo "shutdown"| telnet localhost 4444 >/dev/null 2>&1
elif [[ `echo $res |grep "running"| wc -c`>1 ]]; then
	echo "Chip is running, halting..."
	echo "halt"| telnet localhost 4444 >/dev/null 2>&1
	echo "shutdown"| telnet localhost 4444 >/dev/null 2>&1
elif [[ `echo $res | grep "unknown" | wc -c`>1 && `cat $LOG_FILE | grep "MEM-AP" | wc -c`>1 ]]; then
	echo "Chip is locked! Preparing for flashing..."
	echo "nrf52.dap apreg 1 0x04 0x00"| telnet localhost 4444 >/dev/null 2>&1
	echo "nrf52.dap apreg 1 0x04 0x01"| telnet localhost 4444 >/dev/null 2>&1
	# Turn on ERASEALL bit
	sleep 1
	echo "nrf52.dap apreg 1 0x00 0x01"| telnet localhost 4444 >/dev/null 2>&1
	echo "nrf52.dap apreg 1 0x00 0x00"| telnet localhost 4444 >/dev/null 2>&1
	# Chip has gone through reset state
	echo "nrf52.dap apreg 1 0x04 0x00"| telnet localhost 4444 >/dev/null 2>&1
	# Turn off ERASEALL bit
	echo "reset halt"| telnet localhost 4444 >/dev/null 2>&1
	# Undergo reset and halt chip
	echo "shutdown"| telnet localhost 4444 >/dev/null 2>&1
#sudo kill $sub_pid >/dev/null 2>&1
	echo "MPU reset!"
else
	echo "Chip in unknown state, canceling."
	echo "shutdown"| telnet localhost 4444 >/dev/null 2>&1
	exit 1
fi
sleep 1
rm $LOG_FILE
#-----------------------------------------------------------------------------
# Call OpenOcd
#-----------------------------------------------------------------------------

echo "## Wirepas module flash: $SINK"
echo ""
openocd -f $CURRENTPATH/$SINK.cfg -c init -c "reset init" -c halt -c "nrf5 mass_erase" -c "program $2" -c reset -c exit
