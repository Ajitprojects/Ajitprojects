# Evidence to prompt

A small browser tool for drafting an investigation prompt from case notes. Open [index.html](index.html), choose GitHub’s **Download raw file** button, and open the downloaded file in your browser.

1. Choose **Load fictional example**, then **Build investigation prompt**.
2. Read the prompt. It asks an AI assistant for a timeline, hypotheses with supporting evidence, and a short update.
3. Copy it into your preferred assistant. Compare each claim in the response with the notes. The browser tool constructs the prompt locally. Model inference happens in the assistant you choose.
4. Edit a timestamp. The earlier prompt clears, so the next copy uses the revised notes after you rebuild it.

## A claim you can check

The sample records a 5xx alert at **10:03 UTC**, some gateway timeouts at **10:15**, and a return to normal error rates at **10:28**. A summary saying “the gateway caused the outage and every payment failed” adds two unsupported claims. A grounded update would say:

> The payment API error rate returned to normal at 10:28 UTC. Some requests showed gateway timeouts. Customer impact and root cause are still unconfirmed.

This worked example illustrates the human review step. It is an illustrative answer written for the lab.
