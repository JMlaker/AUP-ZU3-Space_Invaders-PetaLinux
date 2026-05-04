#
# This file is the space-invaders recipe.
#

SUMMARY = "Simple space-invaders application"
SECTION = "PETALINUX/apps"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "libevdev"
DEPENDS += "pkgconfig-native"
DEPENDS += "alsa-lib"

EXTRA_OEMAKE += "FB=1"

EXTRA_IMAGE_FEATURES:append = " dbg-pkgs"
DEBUG_BUILD = "1"

SRC_URI = "file://space-invaders.c \
           file://Makefile \
           file://fb_sim.c \
           file://hw_contract.h \
           file://sw/main.c \
           file://sw/shm_map.c \
           file://sw/shm_map.h \
           file://sw/gfx.c \
           file://sw/gfx.h \
           file://sw/petalinux.c \
           file://sw/petalinux.h \
           file://sw/sprite.c \
           file://sw/sprite.h \
           file://sw/font.c \
           file://sw/font.h \
           file://sw/game.c \
           file://sw/game.h \
           file://sw/boss.c \
           file://sw/boss.h \
           file://sw/game_helpers.c \
           file://sw/game_helpers.h \
           file://sw/game_player.c \
           file://sw/game_player.h \
           file://sw/music_alsa.c \
           file://sw/music_alsa.h \
           file://sw/color.h \
"

S = "${WORKDIR}"

do_compile() {
    oe_runmake
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 space-invaders ${D}${bindir}
}

FILES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES','sysvinit','${sysconfdir}/*', '', d)}"
