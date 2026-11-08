package com.airport.airport_investment_intelligence_agent.model;

public record AirportComparison(
        String airportId,
        long totalDepartures,
        double departureDelayRate,
        double departureCancellationRate
) {
}