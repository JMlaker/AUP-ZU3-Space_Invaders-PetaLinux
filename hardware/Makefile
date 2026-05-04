# This Makefile is intended to me used to automatically build the petalinux image.
# Trying to run this outside of the petalinux container will likely do nothing.

all:
	petalinux-config --get-hw-description=../ --silentconfig
	petalinux-build
	petalinux-package boot --u-boot --fpga --force
	petalinux-package wic --i images/linux --bootfiles "BOOT.BIN image.ub system.dtb boot.scr" --rootfs-file images/linux/rootfs.tar.gz
	bmaptool create images/linux/petalinux-sdimage.wic > images/linux/petalinux-sdimage.wic.bmap
	echo "Petalinux finished..."
