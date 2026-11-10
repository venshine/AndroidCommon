# Release parameters (release-seal)

The generic steps live in the global `release-seal` skill. This file only records the parameters and exceptions for this project.

## Version

- Semantic versioning `X.Y.Z`: bump Z for bug fixes only, bump Y for new APIs, bump X for incompatible API changes.
- **The version is bumped at release time.** Do not bump to the next version after sealing (skip the skill's "bump version" step).
- Update all of these in the release commit (`grep -rn "<old version>" common/build.gradle sample/build.gradle README.md README-CN.md` should hit every one):
  - `version` under `publishing` in `common/build.gradle`
  - `versionName` in `sample/build.gradle` (leave `versionCode` unchanged). The sample's version badge and "copy dependency" coordinate read from it, so nothing else in the sample needs editing
  - The dependency coordinates (Groovy and Kotlin) in `README.md` and `README-CN.md`

## Pre-release checks

```bash
./gradlew :common:testDebugUnitTest :common:assembleRelease :common:lintDebug :sample:assembleDebug :sample:testDebugUnitTest :sample:lintDebug
```

CI (`.github/workflows/ci.yml`, branch `master`) only runs `:common:testDebugUnitTest :common:assembleRelease :sample:assembleDebug`; run the rest locally.
Robolectric is pinned to SDK 35 (`src/test/resources/robolectric.properties` in both modules). Code paths for other API levels and real-device behavior are not covered by the tests; re-check the affected scenarios on a device before releasing library changes.

## Tag

- The tag name is the bare version, without a `v` prefix: `2.0.0`
- Annotated tag with the message `AndroidCommon X.Y.Z`, created on the release commit and pushed to `origin`
- JitPack builds from the tag (`jitpack.yml` selects JDK 17); the coordinate is `com.github.venshine:AndroidCommon:X.Y.Z`. After pushing, check `https://jitpack.io/api/builds/com.github.venshine/AndroidCommon/X.Y.Z`

## GitHub Release

- Title is the version number. Notes are written in English with sections such as "Added / Changed / Removed / Installation"; 2.0.0 must point to the migration table in the README
- Attachment: the sample APK, named `AndroidCommon-sample-X.Y.Z.apk`
  - Build: `./gradlew :sample:assembleDebug`; output at `sample/build/outputs/apk/debug/sample-debug.apk`, copy it to the name above
  - Use the same signing certificate as previous sample APKs so users can install over an older version; check the certificate before releasing from another machine

## Afterwards

- There is no product doc or ROADMAP; the GitHub Release is the source of truth for release notes, so no other docs need syncing
