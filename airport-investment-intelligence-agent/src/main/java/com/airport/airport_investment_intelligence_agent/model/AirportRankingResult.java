package com.airport.airport_investment_intelligence_agent.model;

public record AirportRankingResult(
        String airportCode,
        String airportName,
        String airportId,
        long totalDepartures,
        double departureDelayRate,
        double departureCancellationRate
) {
}