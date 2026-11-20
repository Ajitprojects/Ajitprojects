# Local AI & Search Lab

I wanted to answer two questions on my own Windows PC. Could I run a chat model locally? Could I also give it access to current information through a search service I controlled?

I tested Qwen3 8B with Ollama and observed GPU offload on my Radeon RX 9060 XT. I also queried SearXNG from the command line and got a JSON response. Connecting those two pieces through the chat interface is the next experiment.

## Workbench

Windows 11 Pro · Ryzen 7 9700X · Radeon RX 9060 XT with 16 GB VRAM · 16 GB system RAM

| Component | What I checked |
| --- | --- |
| Ollama and Qwen3 8B | Ran the model locally and observed GPU offload during testing. |
| Open WebUI | Started the browser interface for the lab. |
| SearXNG | Ran the search service in a container, configured response formats and safe search, and tested its JSON endpoint independently. |

## What the search test established

A JSON response tells me the search service can answer a direct request. To establish that a chat answer uses search, I also need to see the interface issue the query and check the answer against a returned source.

For the next run, I’ll ask a question about a recent software release, capture the query received by SearXNG, and compare the answer with the linked release notes. That gives me three places to investigate if the result looks wrong: the query, the retrieved page, and the model’s summary.

## Open questions

- After a Windows restart, do the services come back with their settings and model data intact?
- How do memory use and response time change as the conversation gets longer?

This case study records observations from my workstation. A captured model response, startup checks and performance measurements are still pending.
