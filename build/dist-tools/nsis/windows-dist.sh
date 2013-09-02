#!/bin/sh


TARGET=build/tmp/dist-windows

make_dist() {
    ARCH=$1
    VEGA=build/stage/I.VegaBuild/VegaBuild-win32.win32.$ARCH.zip
    rm -rf $TARGET
    mkdir -p $TARGET
    /usr/bin/unzip -d $TARGET $VEGA
    cp build/dist-tools/nsis/win-$ARCH.ini $TARGET/vega/Vega.ini
    cp build/dist-tools/nsis/epl.txt $TARGET
    cp build/dist-tools/nsis/vega.nsi $TARGET
    /usr/local/bin/makensis $TARGET/vega.nsi
}

mkdir build/dist

make_dist x86
mv $TARGET/VegaSetup.exe build/dist/VegaSetup32.exe

make_dist x86_64
mv $TARGET/VegaSetup.exe build/dist/VegaSetup64.exe


