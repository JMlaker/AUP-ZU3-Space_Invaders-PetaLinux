# AUP-ZU3 Space-Invaders Petalinux Image
This repository contains what is necessary to build a PetaLinux image for our ECE554 Capstone project at UW Madison.

The PetaLinux image is built entirely within a podman container so it (should) work regardless of OS.

There is a custom Space-Invaders inspired rougelike video game built directly into the image, with the FPGA hardware acting as a 2D raster graphics engine to generate the game's frames.

For the main development repository, see [maggardcolin/ECE554_Capstone](https://github.com/maggardcolin/ECE554_Capstone/tree/pl-render).

## Prerequisites
You must download PetaLinux 2024.1 from [AMD's website](https://www.xilinx.com/support/download/index.html/content/xilinx/en/downloadNav/embedded-design-tools.html) and place it inside `./Container/container`.

This project was only tested using rootless [podman](https://podman.io/). For any other container program, please see the **Notices** section.

For faster flashing, you can install The YoctoProject's bmaptool from their [GitHub](https://github.com/yoctoproject/bmaptool) or through your package manager if it has it (e.g. bmaptool in AUR).

## Running PetaLinux
Note that this will take 30+ minutes on the first run and will use up 100% CPU at some stages.

To build the image, run one of the following:
```bash
# To run as a daemon
make

# To run in the foreground
env DEBUG=1 make
```

If run as a daemon process, run `make unshare` after it's completed to re-own all the files (`podman ps | grep petalinux` returns 1).\
Real-time outputs are cached and sent into `./projects/petalinux.log`

## Flashing the Image
After the podman container completes, you can flash an SD card (8-32 GB work best) with the following:
```bash
cd projects/space-invaders/images/linux/

# Using bmaptool (~10 seconds)
sudo bmaptool copy --bmap petalinux-sdimage.wic.bmap petalinux-sdimage.wic [SD block device]

# If you run into a checksum error, regenerate the bmap with
bmaptool create -o petalinux-sdimage.wic.bmap petalinux-sdimage.wic
# And repeat the command above

# If you do not have bmaptool, you can instead directly flash the image (takes ~300 seconds)
sudo dd if=petalinux-sdimage.wic of=[SD block device] bs=1M status=progress (optional flags: iflag=direct oflag=dsync)
```

To run the game:
1. Plug your AUP-ZU3 into your display with an active mDP cable\
1.1. The display must support 640x480 resolution for the game to run at full screen (most do)
2. Swap the boot switch to SD
3. Plug in the SD card
4. Power and turn on the AUP-ZU3\
4.1. Note that this can take a minute or two before signs of life on the display\
4.2. Feel free to connect to UART for a serial terminal and watch the boot messages
5. Plug in a keyboard (I found non-QMK and non-VIA keyboards to work more consistently)
6. Enter the username `petalinux`
7. Choose a password (I prefer `a`)
8. Run `sudo space-invaders`
9. Have fun :)

## Continued Development
The only files that exist continuously outside the container after it's been built are the game files.\
Thus, if you wish to make changes to the image on your own, run the following.
```bash
# Start the container with same runtime settings
podman start petalinux

# Exec into the container
podman exec -it petalinux bash
```
Inside the container, you must re-own all the files and change to a non-root user:
```bash
# INSIDE THE CONTAINER!!
chown -R petalinux:petalinux /projects /external
su petalinux
```

Additionally, feel free to steal the Containerfile for your own use.

## Notices
### podman Failed at compose
Are you running podman rootless? Check `systemctl --user status podman.socket`\
If this isn't wasn't the error, look into podman specific debugs.

### Alternate Container
I personally prefer podman over Docker or other container programs due to its true rootless mode.\
You can try using your favourite container program by overriding with `env CONTAINER=[container program] make`

### SELinux
SELinux should not cause any initial problems and was used in the initial development of the game.

There are some problems with adding and/or changing files after the container was already built. If you run into any, try rebuilding the container by first unsharing `make unshare`, then killing and removing the container before rerunning `make`.

### Visual Bugs?
Pixel artifacting and other visual bugs are common and expected. There are many reasons why they might be occuring, we didn't delve too deep into debugging them.

### Want to develop your own game?
As of right now, the colors that get mapped are hard coded into the hardware. You can view some of the hardware files in the repository at the beginning, though as of 
writing this, its missing some updates and the hardware not reproducible.\
Additionally, the files are all over the place with some things doing nothing and others actively hindering performance.

Regardless, if you want to continue, the most important files for the graphics are
- `./space-invaders/fb-sim.c` for the game loop and display logic
- `./space-invaders/sw/gfx.[ch]` for the graphics logic
- `./space-invaders/sw/petalinux.[ch]` for the UIO and DMA logic
- `./space-invaders/sw/color.h` for the colors

All graphical instructions must be made with `l_putpix`, as `l_putrect` does not work due to unfixed timing violations.

## Acknowledgement
This project makes use of [ikwzm/udmabuf](https://github.com/ikwzm/udmabuf) and ncurses' libtinfo5. See project-spec/meta-user/recipe-modules/u-dma-buf/files and container/libs respectively for copyright and licensing details.
