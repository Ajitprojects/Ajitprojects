# CleanYourTab

I wanted a home screen for my Xiaomi Pad 7 with an iPadOS-inspired layout and Nothing-style widgets. I also wanted to build it from Windows and transfer it over Wi-Fi. I chose the design, paired the tablet, ran the setup script and tested the result. AI helped write the Java launcher and PowerShell scripts and prepare a second design from my feedback.

## What happened on the tablet

On Android 16 with HyperOS 3, wireless ADB detected the tablet. The v1 one-click build reported `BUILD SUCCESSFUL in 22s`. The next step, installing the APK through ADB, returned `INSTALL_FAILED_USER_RESTRICTED`. The script transferred the APK to the tablet for manual installation.

I opened v1 on the tablet. Its screen still felt like a regular app, and pressing Home took me back to Xiaomi Home. That test exposed two separate issues: the visual design and which application handled Home.

| Version | Evidence |
| --- | --- |
| One-click v1 | Build and transfer recorded in the setup output. Opening the app and the Home-button result were tested on the tablet. |
| Wireless upgrade v2 | A redesigned version was prepared after the v1 test. Device installation, appearance and Home handling still need verification. |

## Repeat the Home-button check

After installing the launcher on a test tablet, open it, launch another app, then press Home. Record the launcher version, OS version and which screen appears. Repeat after a restart to check whether the choice persists. The first v1 test returned to Xiaomi Home. The restart check is a proposed follow-up.
