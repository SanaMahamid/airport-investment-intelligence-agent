# Airport Investment Intelligence Agent -- Backend

## Overview

Java 21 and Spring Boot backend for the Airport Investment Intelligence
Agent. It loads BTS aviation CSV datasets, calculates airport metrics,
exposes REST endpoints, and provides AI chat functionality through
Spring AI and Ollama.

## Requirements

-   Java 21
-   Maven 3.9 or later
-   Ollama installed and running locally
-   The Ollama model `qwen2.5:3b` downloaded
-   BTS CSV files under `src/main/resources/data/`

## Data files

The current CSV loaders expect: -
`src/main/resources/data/T_ONTIME_REPORTING.csv` -
`src/main/resources/data/T_T100D_SEGMENT_ALL_CARRIER.csv`

The application uses downloaded CSV snapshots, not a live BTS API.

## Run locally

1.  Start Ollama.

2.  Download the model if needed:

    ``` bash
    ollama pull qwen2.5:3b
    ```

3.  Verify Ollama is available at `http://localhost:11434`.

4.  Open a terminal in the backend project directory.

5.  Ensure Java 21 is active. Example for Windows PowerShell:

    ``` powershell
    $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.13.9-hotspot"
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
    ```

6.  Start the backend:

    ``` bash
    mvn spring-boot:run
    ```

The current project configuration uses port `8081`.

## Useful endpoints

Examples from the current implementation:

``` text
GET http://localhost:8081/api/airports/BOS/metrics
GET http://localhost:8081/api/airports/compare?firstAirportId=10721&secondAirportId=10529
GET http://localhost:8081/api/airports/ANC/long-haul
GET http://localhost:8081/api/airports/rank?airportIds=10721,10529,14307
GET http://localhost:8081/api/airports/SFO/demand?year=2025&month=1
GET http://localhost:8081/api/airports/investment-score?airportCodes=BOS,BDL,PVD&year=2025&month=1
```

AI chat endpoint:

``` text
GET http://localhost:8081/api/ai/ask?question=Which%20airports%20in%20New%20England%20are%20strong%20candidates%20for%20terminal%20expansion%3F&conversationId=demo-session
```

## AI configuration

The backend uses Spring AI with Ollama. The current model is
`qwen2.5:3b` and the Ollama base URL is `http://localhost:11434`. Ensure
the configured model is installed locally.

## Investment scoring

The preliminary score uses passenger volume (40%), performed departures
(30%), and load factor (30%). Scores are relative to the airports
supplied for comparison and the selected period. The currently tested
investment dataset is January 2025.

## Limitations

-   The backend loads local BTS CSV snapshots; it does not call a live
    BTS API.
-   Results are limited to the dates and scope represented in the CSV
    files.
-   The investment score is a preliminary comparison, not a financial
    profitability forecast.
-   Chat memory is in-memory and is lost when the backend restarts.
