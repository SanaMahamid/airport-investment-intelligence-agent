package com.airport.airport_investment_intelligence_agent.model;

public record AirportScore(
        String airportCode,
        double score,
        String investmentRecommendation
) {
}
