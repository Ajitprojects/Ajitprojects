# AnimeStudio

A Hindi animation project built around Remotion compositions, storyboard data, and audio timing. The saved source includes character components, Hindi subtitles and scripts for local text-to-speech experiments.

## How the speaker's mouth follows the dialogue

The timing helper converts a video frame into seconds of audio by subtracting the start-frame offset and dividing by the frame rate. It checks whether that moment is inside a speech segment, then looks up the mouth shape and amplitude. A separate speaker check keeps other characters' mouths closed during the line.

For example, at 30 fps with the audio starting on frame 15, frame 45 corresponds to one second into the audio. This offset lets the animator leave a pause before dialogue begins.

## Keeping the end of a line intact

The validator requires enough frames for the audio start offset plus the speech duration rounded up to whole frames. A scene containing 2.1 seconds of audio at 30 fps with a 15-frame lead-in therefore needs at least 78 frames. A shorter scene raises a timing error before the clip can cut off a line.

This source walkthrough is based on `src/utils/audioTiming.ts`. The full renderer and media assets are outside this portfolio snapshot.
