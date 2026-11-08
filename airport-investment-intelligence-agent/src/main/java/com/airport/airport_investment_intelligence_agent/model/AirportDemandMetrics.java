package com.airport.airport_investment_intelligence_agent.model;

public record AirportDemandMetrics(
        String airportCode,
        double totalPassengers,
        double totalSeats,
        double totalDeparturesPerformed,
        double loadFactor
) {
}