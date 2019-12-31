# Update package groups to remove unavailable packages
#
# Copyright (C) 2016-2018, TOSHIBA Corp., Daniel Sangorrin <daniel.sangorrin@toshiba.co.jp>
# SPDX-License-Identifier:	MIT

RDEPENDS_${PN}_remove += " \
libdw-dev \
libelf-dev \
libncurses5-dev \
libncursesw5-dev \
"
