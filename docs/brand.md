# OConnect Brand Guide

This guide keeps the app, repository, store metadata, screenshots, and release
copy aligned around one recognizable product identity.

## Core Identity

- **Product name:** OConnect
- **Capitalization:** uppercase `O` and `C`; never `Oconnect`, `OC`, or
  `OpenConnect Next` for the current product.
- **Descriptor:** Android client for OpenConnect-compatible SSL VPN gateways.
- **Tagline:** OpenConnect VPN, refined for Android.
- **One-sentence positioning:** OConnect is a focused, open-source Android
  client that makes OpenConnect-compatible VPN profiles easier to configure,
  understand, and use.

`OpenConnect` remains the correct term for the protocol and native VPN engine.
`OConnect` is the app and product name.

## Brand Character

OConnect should feel calm, capable, and direct. It is infrastructure software,
so clarity and trust matter more than exaggerated claims.

- Lead with what the user can do.
- Prefer short, concrete sentences.
- Explain technical constraints without hiding them.
- Use “VPN gateway” for the server supplied by an organization.
- Avoid vague security superlatives such as “unbreakable,” “military-grade,”
  or “completely anonymous.”

## Visual System

The visual system follows the app's Material interface and monochrome
globe-and-shield mark.

| Role | Color | Use |
| --- | --- | --- |
| Canvas | `#090C12` | Hero art and dark presentation backgrounds |
| Surface | `#171D26` | Cards, panels, and supporting containers |
| Border | `#313A47` | Quiet separation without high contrast |
| Primary | `#3487F6` | Actions, links, focus, and small accents |
| Primary text | `#F7F9FC` | Titles and essential information |
| Secondary text | `#B7BFCB` | Descriptions, metadata, and supporting copy |

Use the blue accent sparingly. The icon and wordmark remain monochrome so they
stay legible at launcher, notification, and documentation sizes.

## Asset Locations

- Repository hero: `docs/assets/oconnect-hero.svg`
- Master icon previews: `outputs/icons/oconnect-final-preview/`
- Public product screenshots: `screenshots/readme/`
- Store icon and screenshots: `fastlane/metadata/android/en-US/images/`
- Android launcher artwork: `app/src/main/res/drawable*` and
  `app/src/main/res/mipmap-*`

Do not use the legacy cable-ring OpenConnect artwork for OConnect-facing
materials.

## Screenshot Standard

Public screenshots should be captured from the current APK, not copied from an
older design audit.

- Capture at `1080 × 2340` on the Android 16 emulator used by this repository.
- Use dark mode for the primary set.
- Fix the status bar to `09:41`, full battery, and a clean Wi-Fi state.
- Use English for the primary set and Simplified Chinese only when showing
  localization.
- Use `vpn.example.com` and `Example VPN`; never expose a real organization,
  gateway, account, token, or log.
- Keep one screen and one user task per image. QA comparisons belong under
  `screenshots/verification/`, not in the README or store listing.
- Refresh the set after any material navigation, visual, naming, or version
  change.

The current public sequence is: empty home, add profile, dashboard, profile
editor, settings, About, and Simplified Chinese home.

## Writing Examples

Preferred:

> Add your organization's VPN gateway to get started.

> OConnect supports OpenConnect-compatible SSL VPN gateways.

Avoid:

> The ultimate VPN for everything.

> Guaranteed secure and anonymous connectivity.

## Release Consistency Checklist

Before publishing a release:

1. Confirm `OConnect` appears consistently in the app label, About page,
   README, store title, and release title.
2. Confirm the application ID and version are accurate in the APK.
3. Refresh screenshots when the visible product or version changes.
4. Keep protocol references as `OpenConnect` and product references as
   `OConnect`.
5. State application-ID changes or other upgrade-breaking behavior prominently.
