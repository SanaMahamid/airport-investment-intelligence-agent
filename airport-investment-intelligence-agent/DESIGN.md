# Airport Investment Intelligence Agent

## Design and Architecture

### 1. Overview

The Airport Investment Intelligence Agent is a conversational
application that analyzes U.S. airport operational and passenger data
and provides preliminary indicators for airport investment research.

The solution combines deterministic calculations in a Java backend with
an AI agent that understands natural-language questions, selects
relevant backend tools, and explains their results. A React frontend
provides the chat experience.

### 2. Architecture

**Frontend -- React and Vite** - Provides the conversational user
interface. - Sends questions to the backend through HTTP requests. -
Stores a conversation ID in browser `sessionStorage` so follow-up
questions reuse the same ID during the browser session. - Supports voice
input and text-to-speech in supported browsers.

**Backend -- Java 21 and Spring Boot** - Loads BTS CSV datasets at
application startup. - Parses and stores records in memory. - Exposes
REST endpoints for airport analysis and AI chat. - Performs
deterministic calculations for operational metrics, passenger demand,
long-haul percentages, airport rankings, and investment scores.

**AI integration -- Spring AI and Ollama** - Spring AI connects the
application to a locally running Ollama model. - The model interprets
questions and invokes Java tools exposed through Spring AI tool
calling. - Java tools return calculated results to the model. - The
model explains results in natural language and uses conversation memory
for follow-up questions.

### 3. Data Sources and Scope

The application uses public datasets from the U.S. Bureau of
Transportation Statistics (BTS):

-   **On-Time Performance data:** departure counts, delays,
    cancellations, and flight distances.
-   **T-100 Segment data:** passenger counts, available seats, performed
    departures, and load factor.

The current implementation loads downloaded CSV snapshots from backend
resources. It does not retrieve live data from a BTS API at runtime.

The investment analysis tested in this project uses January 2025 T-100
data. The demonstrated operational analysis also uses January 2025
On-Time data. Results are limited to the periods and coverage
represented in the supplied CSV files.

T-100 results are aggregated segment data. They do not, by themselves,
establish the number of passengers unable to obtain a seat or the
physical capacity of an airport terminal.

### 4. Investment Scoring Methodology

The application includes a deterministic preliminary score to compare a
selected group of airports.

  ------------------------------------------------------------------------
  Metric                                      Weight Interpretation
  --------------------- ---------------------------- ---------------------
  Passenger volume                               40% Relative passenger
                                                     volume among selected
                                                     airports

  Flight activity                                30% Relative number of
                                                     performed departures

  Load factor                                    30% Passenger seat
                                                     utilization
                                                     percentage
  ------------------------------------------------------------------------

Passenger volume and performed departures are normalized against the
maximum value among airports in the comparison group:

`Passenger Score = Airport Passengers / Maximum Passengers in Group * 100`

`Flight Activity Score = Airport Performed Departures / Maximum Performed Departures in Group * 100`

Load factor:

`Load Factor = Total Passengers / Total Seats * 100`

Final score:

`Total Score = (Passenger Score * 0.40) + (Flight Activity Score * 0.30) + (Load Factor Score * 0.30)`

Results are sorted in descending order. Scores are relative to the
selected airports and period and should be interpreted only in that
context.

**Example from January 2025 test data:**

  -----------------------------------------------------------------------
  Airport          Passenger         Flight    Load Factor    Total Score
                       Score Activity Score          Score 
  ----------- -------------- -------------- -------------- --------------
  BOS                 100.00         100.00          73.29          91.99

  BDL                  20.86          20.16          72.52          36.15

  PVD                  12.51          13.43          71.47          30.47
  -----------------------------------------------------------------------

These scores indicate relative activity and seat utilization, not
guaranteed investment returns.

### 5. Operational Analysis

The application supports operational comparisons and rankings based on
BTS On-Time data. The operational ranking uses: 1. Lower departure delay
rate. 2. Lower cancellation rate as a secondary criterion.

These are operational performance indicators, not direct measurements of
airport congestion and not a substitute for the investment score.

Long-haul departures are defined in the implementation as flights with a
distance greater than 3,000 miles.

### 6. Where and How AI Is Used

AI is used for language understanding, tool selection, and
explanation---not as the source of numerical truth.

1.  The user submits a natural-language question in the React chat.
2.  The frontend sends the question and conversation ID to the Spring
    Boot AI endpoint.
3.  Spring AI sends the question and system instructions to the Ollama
    model.
4.  The model selects an appropriate Java tool, such as
    `compareAirports`, `getLongHaulPercentage`, `rankAirports`,
    `analyzeAirportDemand`, or `rankInvestmentCandidates`.
5.  The Java tool calculates results using the loaded dataset and
    returns them to the model.
6.  The model explains the returned results, including relevant scope
    and limitations.

The system prompt instructs the model to use the appropriate tool,
preserve Java's ranking order, avoid inventing metrics, and distinguish
operational performance from investment potential.

The frontend keeps a conversation ID in `sessionStorage`. The current
backend chat memory is in-memory and is not persistent across backend
restarts.

### 7. Key Design Trade-offs

**Deterministic calculations in Java** - Benefit: Numerical results are
reproducible and can be tested independently of the language model. -
Trade-off: New metrics or scoring changes require backend code changes.

**Local model through Ollama** - Benefit: The application can run
without a paid external model API or API key. - Trade-off: Response
quality and speed depend on the local model and available hardware.

**CSV snapshots instead of live API integration** - Benefit: Simplifies
data ingestion and makes the assignment runnable with a fixed dataset. -
Trade-off: Results are limited to downloaded data and are not real-time.

**In-memory data and chat memory** - Benefit: Simple setup and fast
access for a demonstration. - Trade-off: Data and conversation history
are not persisted across application restarts.

**Preliminary investment score** - Benefit: Provides a transparent and
explainable comparison using available data. - Trade-off: It does not
model construction costs, terminal capacity, gate availability, future
demand, revenue, or project financing.

### 8. Limitations and Future Improvements

Potential improvements include live aviation data integration,
historical trend analysis, terminal capacity and gate utilization data,
construction cost and revenue estimation, persistent chat history, and a
larger airport dataset.

The current score is an initial screening indicator for further
analysis, not a final investment recommendation.
