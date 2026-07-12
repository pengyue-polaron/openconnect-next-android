# F-Droid Packaging Notes

This document tracks what is needed to submit OpenConnect Next to the official
F-Droid repository.

## Current Status

- Not submitted to F-Droid yet.
- Upstream metadata is being added under `fastlane/metadata/android/en-US`.
- GitHub releases are tagged, but `versionName` still needs to be aligned with
  the public release tag before a real F-Droid merge request.
- The Android application ID is still `app.openconnect`.

## Important Finding

F-Droid already has an active OpenConnect package at
`net.openconnect_vpn.android`, and older F-Droid metadata/history exists for
`app.openconnect`.

Because this project is a fork, the recommended submission path is:

1. Keep the user-facing name distinct: **OpenConnect Next**.
2. Change the Android application ID before submission.
3. Update affected authorities, permissions, remote API strings, and migration
   notes.

Recommended future application ID:

```text
io.github.pengyuepolaron.openconnectnext
```

This was not changed in the README/documentation pass because changing the
application ID also changes install/update behavior and requires testing the
VPN service, file provider authority, remote API permission, and Android
backup/migration behavior.

## Official Submission Path

1. Confirm the repo is public and contains the full buildable source.
2. Confirm all dependencies and build tools are FOSS-compatible.
3. Keep license files in the root repo.
4. Add upstream metadata:

   ```text
   fastlane/metadata/android/en-US/title.txt
   fastlane/metadata/android/en-US/short_description.txt
   fastlane/metadata/android/en-US/full_description.txt
   fastlane/metadata/android/en-US/changelogs/<versionCode>.txt
   fastlane/metadata/android/en-US/images/icon.png
   fastlane/metadata/android/en-US/images/phoneScreenshots/1.png
   ```

5. Create a release tag that matches the Android `versionName`.
6. Fork `fdroiddata`.
7. Add a metadata file named after the final application ID.
8. Open a merge request to `fdroiddata`.
9. Respond to maintainer review and build failures.

## Metadata Draft

This is a starting point for the eventual `fdroiddata` metadata file after the
application ID migration.

```yaml
Categories:
  - Connectivity
License: GPL-2.0-or-later
AuthorName: pengyue-polaron
SourceCode: https://github.com/pengyue-polaron/openconnect-next-android
IssueTracker: https://github.com/pengyue-polaron/openconnect-next-android/issues
Changelog: https://github.com/pengyue-polaron/openconnect-next-android/releases

RepoType: git
Repo: https://github.com/pengyue-polaron/openconnect-next-android.git

Builds:
  - versionName: 1.11.4
    versionCode: 1119
    commit: v1.11.4
    submodules: true
    gradle:
      - yes

AutoUpdateMode: Version
UpdateCheckMode: Tags
CurrentVersion: 1.11.4
CurrentVersionCode: 1119
```

Before submitting this metadata, update `versionName`, `versionCode`, `commit`,
`CurrentVersion`, and `CurrentVersionCode` to the final post-application-ID
release.

## Pre-Submission Checklist

- [ ] Change application ID from `app.openconnect`.
- [ ] Update app label, About page, README, and fastlane title consistently.
- [ ] Decide whether GitHub releases should remain debug-signed or switch to
      a reproducible release signing process.
- [ ] Align Gradle `versionName` with the public release tag.
- [ ] Increment `versionCode`.
- [ ] Build from a clean clone with submodules.
- [ ] Run `./gradlew assembleDebug testDebugUnitTest`.
- [ ] Test profile creation, connection prompt, password visibility, log tab,
      and dark mode.
- [ ] Prepare a fresh release tag for F-Droid.
