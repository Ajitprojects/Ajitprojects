# Hi, I’m Ajit

![Animated workbench showing local AI, Android and creative tools](assets/workbench.svg)

I build software for the devices I use. Two questions behind my projects are why my tablet returns to Xiaomi Home when I press Home, and how to keep a line of Hindi dialogue from getting cut short in an animation.

## Selected projects

| Project | What I tried and what I learned |
| --- | --- |
| [CleanYourTab Home](projects/cleanyourtab) | I built and opened a launcher on my Xiaomi Pad 7. Pressing Home still returned me to Xiaomi Home. The case study includes the device test and the [original](projects/cleanyourtab/v1) and [experimental](projects/cleanyourtab/v2) Android sources. |
| [Local AI & Search Lab](projects/local-ai-search) | I ran Qwen3 8B with Ollama on my Windows PC and queried SearXNG separately. My next test is to trace a chat answer back to a search result. |
| [AnimeStudio](projects/animestudio) | A walkthrough of how Hindi dialogue drives a character’s mouth shapes. A worked example shows why a scene with 2.1 seconds of speech needs 78 frames when it opens with a 15-frame pause at 30 fps. |

## More to explore

- **[CleanYourTV Home](projects/cleanyourtv)** — I tested scrolling through apps with a TV remote. The case study includes the Java code that coordinates the dashboard and animated background.
- **[Evidence to Prompt](labs/evidence-to-prompt)** — Try the fictional case, build a prompt, and check an AI-written incident summary against the timestamps. The worked example shows how a timeout can become an unsupported claim about the cause.
- **[Incident Operations Dashboard](https://github.com/Ajitprojects/incident-operations-dashboard)** — Filter four fictional incidents and see why an open incident counts toward average detection time while its recovery time is still unknown.
- **[PCPriceTracker](https://github.com/Ajitprojects/PCPriceTracker)** — Follow a PC component listing from a Node.js collection script through Supabase to a React Native price-history chart.

I use AI to help prototype and troubleshoot. In the tablet project, for example, I chose the layout and tested it on hardware. AI helped write the Java launcher and PowerShell scripts, then revise the design after that first test.
