#!/usr/bin/env bash
# Go Mastery - builds ONE Android APK containing the whole course and the
# complete official Go toolchain (compiles and runs Go offline on the phone).
#
# GitHub runs this automatically (see .github/workflows/build.yml).
# It needs these four files in the repository root:
#   App.kt   Ui.kt   course.json   build.sh
set -euo pipefail
SRC="$(cd "$(dirname "$0")" && pwd)"
cd "$SRC"
for f in App.kt Ui.kt course.json; do
  if [ ! -f "$f" ]; then
    echo "::error::Missing $f. Upload App.kt, Ui.kt, course.json and build.sh to the top level of the repository."
    exit 1
  fi
done

P="$SRC/project"
rm -rf "$P"
mkdir -p "$P/app/src/main/java/com/gomastery/app/ui" "$P/app/src/main/assets" \
         "$P/app/src/main/res/values" "$P/app/src/main/res/drawable" "$P/scripts"
cp App.kt  "$P/app/src/main/java/com/gomastery/app/App.kt"
cp Ui.kt   "$P/app/src/main/java/com/gomastery/app/ui/Ui.kt"
cp course.json "$P/app/src/main/assets/course.json"

# ---------------------------------------------------------------- Gradle
cat > "$P/settings.gradle.kts" <<'GM_EOF'
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "GoMastery"
include(":app")
GM_EOF
cat > "$P/build.gradle.kts" <<'GM_EOF'
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}
GM_EOF
cat > "$P/gradle.properties" <<'GM_EOF'
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
GM_EOF
cat > "$P/app/build.gradle.kts" <<'GM_EOF'
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.gomastery.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gomastery.app"
        minSdk = 26
        // targetSdk 28 on purpose: Android only lets apps that target 28 or lower
        // execute programs they write to their own storage. Running a real Go
        // compiler (and the programs it builds) needs exactly that. Termux does
        // the same. The app still installs and runs on current Android versions.
        targetSdk = 28
        versionCode = (System.getenv("VERSION_CODE") ?: "1").toInt()
        versionName = System.getenv("VERSION_NAME") ?: "3.0"
        ndk { abiFilters += listOf("arm64-v8a") }
    }

    signingConfigs {
        // A fixed key, so every new build installs as an update over the last one.
        create("release") {
            storeFile = file("release.jks")
            storePassword = "gomastery"
            keyAlias = "gomastery"
            keyPassword = "gomastery"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    androidResources {
        // The Go bundle zips are already compressed; storing them avoids a second
        // compression pass and lets the app stream them straight out of the APK.
        noCompress += listOf("zip")
    }

    lint {
        disable += listOf("ExpiredTargetSdkVersion", "OldTargetApi")
        checkReleaseBuilds = false
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
}
GM_EOF

# ---------------------------------------------------------------- Android resources
cat > "$P/app/src/main/AndroidManifest.xml" <<'GM_EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Not used to go online: Go programs (net/http, httptest, TCP lessons)
         open sockets on 127.0.0.1, and Android requires this permission even
         for local sockets. -->
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:roundIcon="@drawable/ic_launcher"
        android:label="Go Mastery"
        android:theme="@style/Theme.GoMastery"
        android:extractNativeLibs="true"
        tools:targetApi="28">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize"
            android:configChanges="orientation|screenSize|screenLayout|keyboard|keyboardHidden|smallestScreenSize|uiMode">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
GM_EOF
cat > "$P/app/src/main/res/values/themes.xml" <<'GM_EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.GoMastery" parent="android:Theme.Material.NoActionBar">
        <item name="android:windowBackground">#111827</item>
        <item name="android:statusBarColor">#111827</item>
        <item name="android:navigationBarColor">#111827</item>
    </style>
</resources>
GM_EOF
cat > "$P/app/src/main/res/drawable/ic_launcher.xml" <<'GM_EOF'
<?xml version="1.0" encoding="utf-8"?>
<!-- A simple gopher-blue "Go" tile: no copyrighted mascot. -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp"
    android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#111827" android:pathData="M0,0h108v108h-108z" />
    <path android:fillColor="#00ADD8"
        android:pathData="M22,20h64a12,12 0,0 1,12 12v44a12,12 0,0 1,-12 12h-64a12,12 0,0 1,-12 -12v-44a12,12 0,0 1,12 -12z" />
    <path android:fillColor="#111827"
        android:pathData="M30,40h22v7h-15v14h8v-6h-5v-6h12v19h-22z" />
    <path android:fillColor="#111827"
        android:pathData="M58,40h20v28h-20zM65,47v14h6v-14z" />
</vector>
GM_EOF

# signing key (fixed, so updates install over the previous version)
base64 -d > "$P/app/release.jks" <<'GM_EOF'
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
dsYMyJVUUqhijjQyAgInEA==
GM_EOF

# ---------------------------------------------------------------- Go toolchain bundle
cat > "$P/scripts/course_std_packages.py" <<'GM_EOF'
"""Print every standard-library import path used by the course's lessons."""
import json
import re
import sys

data = json.load(open(sys.argv[1], encoding="utf-8"))
pkgs = set()
for lesson in data["lessons"]:
    for src in [lesson["code"]] + list(lesson["files"].values()):
        for block in re.findall(r"import\s*\((.*?)\)", src, re.S):
            pkgs.update(re.findall(r'"([^"]+)"', block))
        pkgs.update(re.findall(r'^import\s+(?:\w+\s+)?"([^"]+)"', src, re.M))
pkgs = {p for p in pkgs if p != "C" and "." not in p.split("/")[0]}
print(" ".join(sorted(pkgs)))
GM_EOF
cat > "$P/scripts/build_go_bundle.sh" <<'GM_BUNDLE_EOF'
#!/usr/bin/env bash
# Builds the offline Go toolchain that ships inside the APK.
#
#   goroot.zip   the COMPLETE official Go distribution (every source file,
#                test, doc and tool), with every program in bin/ and pkg/tool/
#                rebuilt for Android arm64 phones.
#   gocache.zip  a pre-built Go build cache (android/arm64) for the standard
#                library packages the course uses, so the first Run on the
#                phone only has to compile the learner's own code.
#   go-bundle.txt  version + file counts, read by the app.
#
# Usage: GO_VERSION=go1.26.0 scripts/build_go_bundle.sh   (blank = latest)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
WORK="$ROOT/gobuild"
ASSETS="$ROOT/app/src/main/assets"
rm -rf "$WORK" && mkdir -p "$WORK" "$ASSETS"

V="${GO_VERSION:-}"
if [ -z "$V" ]; then
  V="$(curl -fsSL 'https://go.dev/VERSION?m=text' | head -1)"
fi
case "$V" in go*) ;; *) V="go$V" ;; esac
echo "== Go version: $V"

curl -fsSL "https://go.dev/dl/$V.linux-amd64.tar.gz" -o "$WORK/go.tgz"
mkdir -p "$WORK/host" && tar -C "$WORK/host" -xzf "$WORK/go.tgz"
HOSTGO="$WORK/host/go"
export PATH="$HOSTGO/bin:$PATH" GOROOT="$HOSTGO" GOTOOLCHAIN=local GOFLAGS= GOTELEMETRY=off

# ---- 1. full GOROOT copy -------------------------------------------------
cp -a "$HOSTGO" "$WORK/goroot"
G="$WORK/goroot"
HOSTTOOLS="$G/pkg/tool/linux_amd64"
TOOLS="$(ls "$HOSTTOOLS")"
BINS="$(ls "$G/bin")"
rm -rf "$G/bin" "$HOSTTOOLS"
mkdir -p "$G/bin" "$G/pkg/tool/android_arm64"

# ---- 2. rebuild every tool for android/arm64 --------------------------------
# android/arm64 links internally, so no Android NDK / C compiler is needed.
build_cmd() { # name outdir
  ( cd "$HOSTGO/src" && CGO_ENABLED=0 GOOS=android GOARCH=arm64 \
      go build -trimpath -o "$2/$1" "./cmd/$1" )
}
for b in $BINS;  do echo "   bin/$b";  build_cmd "$b" "$G/bin"; done
for t in $TOOLS; do echo "   tool/$t"; build_cmd "$t" "$G/pkg/tool/android_arm64"; done
file "$G/bin/go" || true

# ---- 3. pre-built cache for the std packages the course imports ----------
PKGS="$(python3 "$ROOT/scripts/course_std_packages.py" "$ASSETS/course.json")"
CACHE="$WORK/gocache"
export GOCACHE="$CACHE" GOOS=android GOARCH=arm64 CGO_ENABLED=0
go build $PKGS testing/internal/testdeps || for p in $PKGS testing/internal/testdeps; do go build "$p" || echo "   (skipped $p)"; done
# warm the go test + go vet paths too
mkdir -p "$WORK/warm" && cd "$WORK/warm"
printf 'module warm\n\ngo %s\n' "$(echo "${V#go}" | cut -d. -f1,2)" > go.mod
printf 'package main\n\nimport "fmt"\n\nfunc main() { fmt.Println("hi") }\n' > main.go
printf 'package main\n\nimport "testing"\n\nfunc TestX(t *testing.T) {}\n' > main_test.go
go vet . && go test -c -o "$WORK/warm/warm.test" .
cd "$ROOT"
unset GOOS GOARCH CGO_ENABLED GOCACHE

# ---- 4. zip ---------------------------------------------------------------
( cd "$WORK" && rm -f goroot.zip && zip -q -r goroot.zip goroot ) || [ $? -eq 18 ]
( cd "$CACHE" && rm -f ../gocache.zip && zip -q -r ../gocache.zip . )
NG="$(cd "$WORK" && unzip -Z1 goroot.zip | wc -l)"
NC="$(cd "$WORK" && unzip -Z1 gocache.zip | wc -l)"
mv "$WORK/goroot.zip" "$WORK/gocache.zip" "$ASSETS/"
cat > "$ASSETS/go-bundle.txt" <<EOF
version=$V
goroot_entries=$NG
cache_entries=$NC
built=$(date -u +%Y-%m-%dT%H:%M:%SZ)
EOF
echo "$V" > "$WORK/version.txt"
ls -la "$ASSETS"
cat "$ASSETS/go-bundle.txt"
GM_BUNDLE_EOF

echo "== Building the Go toolchain for Android (full distribution)"
bash "$P/scripts/build_go_bundle.sh"

# ---------------------------------------------------------------- APK
echo "== Building the APK"
cd "$P"
export VERSION_CODE="${GITHUB_RUN_NUMBER:-1}"
export VERSION_NAME="3.0.${GITHUB_RUN_NUMBER:-1}"
gradle --no-daemon --stacktrace assembleRelease
mkdir -p "$SRC/out"
cp app/build/outputs/apk/release/app-release.apk "$SRC/out/GoMastery.apk"
ls -la "$SRC/out"
echo "== Done: out/GoMastery.apk"
