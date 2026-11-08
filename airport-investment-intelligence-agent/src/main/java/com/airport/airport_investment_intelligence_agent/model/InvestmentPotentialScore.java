package com.airport.airport_investment_intelligence_agent.model;

public record InvestmentPotentialScore(
        String airportCode,
        double passengerVolumeScore,
        double flightActivityScore,
        double loadFactorScore,
        double totalScore
) {
}