package com.airport.airport_investment_intelligence_agent.service;

import com.airport.airport_investment_intelligence_agent.model.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AirportTools {

    private final AirportAnalysisService analysisService;
    private final AirportDataService dataService;

    public AirportTools(
            AirportAnalysisService analysisService,
            AirportDataService dataService) {

        this.analysisService = analysisService;
        this.dataService = dataService;
    }

    @Tool(
            name = "compareAirports",
            description = "Compare two airports using their three-letter IATA codes, such as LAX and SNA. Returns actual departures, delay rates and cancellation rates from the available flight data."
    )
    public List<AirportComparison> compareAirports(
            @ToolParam(description = "Three-letter IATA code of the first airport")
            String firstAirportCode,

            @ToolParam(description = "Three-letter IATA code of the second airport")
            String secondAirportCode) {

        String firstAirportId = findAirportId(firstAirportCode);
        String secondAirportId = findAirportId(secondAirportCode);

        return analysisService.compareAirports(
                dataService.getFlights(),
                firstAirportId,
                secondAirportId
        );
    }

    private String findAirportId(String airportCode) {

        return dataService.getFlights().stream()
                .filter(flight -> flight.origin() != null
                        && flight.origin().equalsIgnoreCase(airportCode))
                .map(Flight::originAirportId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Airport not found: " + airportCode));
    }

    @Tool(
            name = "getLongHaulPercentage",
            description = "Returns the long-haul departure percentage as a formatted percentage string, including the % sign. Do not convert or multiply this value."
    )
    public String getLongHaulPercentage(
            @ToolParam(description = "Three-letter IATA airport code, for example ANC")
            String airportCode) {

        String airportId = findAirportId(airportCode);

        double percentage =
                analysisService.calculateLongHaulPercentageForAirport(
                        dataService.getFlights(),
                        airportId
                );

        return String.format(
                java.util.Locale.US,
                "%.2f%%",
                percentage
        );
    }

    @Tool(
            name = "rankAirports",
            description = "Rank airports by operational performance. Returns the airport IATA code, BTS ID, total departures, delay rate and cancellation rate. Lower delay rate is the primary ranking criterion, followed by lower cancellation rate."
    )
    public List<AirportRankingResult> rankAirports(
            @ToolParam(description = "List of three-letter IATA airport codes")
            List<String> airportCodes) {

        List<String> airportIds = new ArrayList<>();

        for (String code : airportCodes) {
            airportIds.add(findAirportId(code));
        }

        List<AirportComparison> rankings =
                analysisService.rankAirports(
                        dataService.getFlights(),
                        airportIds
                );

        return rankings.stream()
                .map(result -> {
                    String airportCode = dataService.getFlights().stream()
                            .filter(flight -> flight.originAirportId()
                                    .equals(result.airportId()))
                            .map(Flight::origin)
                            .findFirst()
                            .orElse("UNKNOWN");

                    return new AirportRankingResult(
                            airportCode,
                            getAirportName(airportCode),
                            result.airportId(),
                            result.totalDepartures(),
                            result.departureDelayRate(),
                            result.departureCancellationRate()
                    );
                })
                .toList();
    }

    private String getAirportName(String airportCode) {

        return switch (airportCode.toUpperCase()) {
            case "BOS" -> "Boston Logan International Airport";
            case "BDL" -> "Bradley International Airport";
            case "PVD" -> "Rhode Island T. F. Green International Airport";
            case "LAX" -> "Los Angeles International Airport";
            case "SNA" -> "John Wayne Airport";
            case "SAN" -> "San Diego International Airport";
            case "ANC" -> "Ted Stevens Anchorage International Airport";
            case "SFO" -> "San Francisco International Airport";
            default -> "Unknown Airport";
        };
    }

    @Tool(
            name = "analyzeAirportDemand",
            description = """
        Returns airport demand metrics for a specific month.
        The result includes the airport name, passenger count,
        available seats, operated departures and load factor.
        Use this tool for airport demand and capacity utilization questions.
        Copy all returned values exactly. Do not invent airport names
        or recalculate percentages.
        """
    )
    public String analyzeAirportDemand(
            @ToolParam(description = "Three-letter IATA airport code")
            String airportCode,
            @ToolParam(description = "Year, for example 2025")
            int year,
            @ToolParam(description = "Month, from 1 to 12")
            int month) {

        AirportDemandMetrics metrics =
                analysisService.analyzeAirportDemand(
                        dataService.getT100Data(),
                        airportCode,
                        year,
                        month
                );

        String airportName = getAirportName(airportCode);

        return String.format(
                java.util.Locale.US,
                """
                Airport: %s (%s)
                Period: %02d/%d
                Total passengers: %.0f
                Total available seats: %.0f
                Total operated departures: %.0f
                Load factor: %.2f%%
                """,
                airportName,
                airportCode,
                month,
                year,
                metrics.totalPassengers(),
                metrics.totalSeats(),
                metrics.totalDeparturesPerformed(),
                metrics.loadFactor()
        );
    }




    @Tool(description = """
        Calculate and rank airports by preliminary investment potential.
        Required parameters:
        - airportCodes: list of IATA airport codes to compare.
        - year: data year.
        - month: data month (1-12).

        The deterministic score uses:
        passenger volume (40%),
        flight activity (30%),
        and load factor (30%).

        Use only airports and periods available in the dataset.
        This score is a preliminary investment indicator, not actual profitability.
        """)
    public List<InvestmentPotentialScore> rankInvestmentCandidates(
            List<String> airportCodes,
            int year,
            int month) {

        return analysisService.calculateInvestmentScores(
                dataService.getT100Data(),
                airportCodes,
                2025,
                1
        );
    }



}