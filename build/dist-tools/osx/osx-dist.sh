#!/bin/bash
TARGET=build/tmp/dist-osx

make_dist() {
	echo "enter make_dist()"
	ARCH=$1
	VEGA=build/stage/I.VegaBuild/VegaBuild-macosx.cocoa.$ARCH.zip
	rm -rf $TARGET
	mkdir -p $TARGET
	echo "Target is $TARGET"
	echo "Vega is $VEGA"
	/usr/bin/unzip -d $TARGET $VEGA
	LAUNCHER=$(ls $TARGET/vega/plugins/org.eclipse.equinox.launcher*.jar)
	LAUNCHER_LIB=$(ls -d $TARGET/vega/plugins/org.eclipse.equinox.launcher.cocoa*)
	cat > $TARGET/Vega.ini <<- EOF
	-startup
	../Resources/plugins/$(basename $LAUNCHER)
	--launcher.library
	../Resources/plugins/$(basename $LAUNCHER_LIB)
	$(cat $TARGET/vega/Vega.app/Contents/MacOS/Vega.ini)
	EOF
	RESOURCES=$TARGET/vega/Vega.app/Contents/Resources
	mv $TARGET/vega/plugins $RESOURCES
	mv $TARGET/vega/features $RESOURCES
	mv $TARGET/vega/configuration $RESOURCES
	mv $TARGET/vega/scripts $RESOURCES
	mv $TARGET/vega/xml $RESOURCES
	mv $TARGET/vega/templates $RESOURCES
	mv $TARGET/Vega.ini $TARGET/vega/Vega.app/Contents/MacOS/Vega.ini
}

make_dmg() {
	echo "enter make_dmg()"
	dd if=/dev/zero of=$TARGET/vega.dmg bs=1M count=40
	mkfs.hfsplus -v Vega $TARGET/vega.dmg
	/usr/local/bin/mount-dmg $TARGET/vega.dmg
	mv $TARGET/vega/Vega.app /mnt/dmg
	/usr/local/bin/mount-dmg -u
}
mkdir build/dist
make_dist x86
make_dmg
mv $TARGET/vega.dmg build/dist/Vega.dmg
make_dist x86_64
make_dmg
mv $TARGET/vega.dmg build/dist/Vega64.dmg
#rm -rf build/tmp



