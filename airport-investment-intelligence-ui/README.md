# Airport Investment Intelligence Agent -- Frontend

## Overview

React and Vite chat interface for the Airport Investment Intelligence
Agent. It communicates with the Spring Boot backend and supports text
chat, suggested questions, voice input, and text-to-speech for assistant
responses.

## Requirements

-   Node.js and npm
-   Backend running at `http://localhost:8081`

## Run locally

1.  Open a terminal in the frontend project directory.

2.  Install dependencies:

    ``` bash
    npm install
    ```

3.  Start the Vite development server:

    ``` bash
    npm run dev
    ```

4.  Open the local URL printed by Vite. The default is usually
    `http://localhost:5173`. If that port is occupied, Vite may select
    another port, such as `5174`.

## Backend connection

The Vite development server proxies `/api` requests to:

``` text
http://localhost:8081
```

Start the Spring Boot backend before sending chat messages.

## Conversation handling

The frontend creates a conversation ID and stores it in browser
`sessionStorage`. Requests from the same browser session reuse that ID,
allowing backend chat memory to associate follow-up questions with the
same conversation.

The ID is stored in the browser session, but backend chat memory is
in-memory. Conversation history is not persistent across backend
restarts.

## Voice features

-   Voice input uses the browser Speech Recognition API when supported.
-   Assistant responses can be read aloud using the browser Speech
    Synthesis API.
-   Browser support and microphone permissions may affect availability.
    Allow microphone access when prompted.

## Troubleshooting

-   **Chat requests fail:** Verify that the backend is running on port
    `8081`.
-   **The page does not open on port 5173:** Use the alternative local
    URL printed by Vite.
-   **Voice input does not work:** Check browser support and microphone
    permissions.
-   **No AI response:** Verify that Ollama is running and that the
    configured model is installed.
