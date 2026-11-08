package com.airport.airport_investment_intelligence_agent.model;

public record AirportMetrics(
        String airportId,
        long totalDepartures,
        long delayDepartures,
        long cancelDepartures,
        double departureDelayRate,
        double departureCancellationRate
) {
}
