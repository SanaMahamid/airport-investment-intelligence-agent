package com.airport.airport_investment_intelligence_agent.model;

public record T100SegmentData(
        int year,
        int month,
        String origin,
        double departuresScheduled,
        double departuresPerformed,
        double seats,
        double passengers,
        double distance
) {
}