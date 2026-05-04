.ONESHELL:
.SILENT:
SHELL = bash

CONTAINER ?= $(shell command -v podman)
DEBUG ?= 0

ifeq ($(CONTAINER),)

all:
	echo "This is only tested using podman. If you use another container program, please run with 'env CONTAINER=[container]'"

else

all:
	if [ ! -f Container/container/petalinux*.run ]; then
		echo
		echo "Could not find petalinux installer. Please download the 2024.1 from"
		echo "https://www.xilinx.com/support/download/index.html/content/xilinx/en/downloadNav/embedded-design-tools.html"
		echo "and move it into ./Container/container"
		echo
		exit 1
	fi
	echo "Running petalinux... this takes around 30 minutes to complete the first time."
	echo "Container runs in daemon mode by default. Look at ./projects/petalinux.log for log updates"
	echo "Run with 'env DEBUG=1' to see output in real time."
	echo "If running in daemon mode, run 'make unshare' after completion to release all the files from the container."
	read -p "Press any key to continue..." __DUMMY_VAR__
	$(MAKE) run

unshare:
	$(CONTAINER) unshare chown -R 0:0 *

ifeq ($(DEBUG),0)

run:
	cd Container
	$(CONTAINER) compose up -d --build

else

run:
	cd Container
	$(CONTAINER) compose up --build
	cd ..
	$(MAKE) unshare

endif

endif

clean:
	-podman unshare chown -R 0:0 *
	-rm -rf projects
