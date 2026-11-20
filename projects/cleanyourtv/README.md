# CleanYourTV

A custom Android TV home screen written in Java. The app grid, dashboard and animated night scene share a screen intended for a TV remote.

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
