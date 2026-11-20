# CleanYourTV Home

A custom Android TV home screen written in Java. The app grid, dashboard and animated night scene share a screen intended for a TV remote.

## What I tried on the TV

I wanted recommendations and a clock at the top of the home screen, then room for the app grid once someone started scrolling with the remote. I chose the Anime Night theme and requested the MUST WATCH row. AI helped with Java implementation and revisions. On my realmeTechLife REA75P running Android 14, I reported that the launcher ran and the upper dashboard moved out of view when browsing the lower rows. The test record leaves default Home-button behaviour open.

The scroll behaviour was already present in the tested launcher. The later patch below aimed to reduce repeated work during scrolling. Installation of that patch and its effect on frame times are unverified.

## One scroll event, two visual changes

In the saved performance patch, the app grid reads its vertical offset once for two updates. The dashboard switches state after a 24 dp threshold, while the background follows the offset continuously. At the top of the list, the offset resets to zero.

Excerpt from `MainActivity.java`:

```java
final float dashboardHideThresholdPx = 24f * density;
final Runnable syncLowerGridScene = () -> {
    int scrollOffset = grid.canScrollVertically(-1) ? grid.computeVerticalScrollOffset() : 0;
    setLowerAppRowsActive(scrollOffset > dashboardHideThresholdPx);
    animeNight.setScrollAmount(scrollOffset);
};
```

This portfolio page documents the saved implementation. Device frame-time measurements and a distributable launcher build are outside this snapshot.

## Recovered project files

The original launcher archive contains the Gradle project and Java activity. The scroll-cleanup archive contains PowerShell scripts for applying and installing the change. The separate performance patch includes revised Java sources for the activity and animated background. The public name refers to the same launcher originally tested as CleanYourTV.
