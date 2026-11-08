package com.airport.airport_investment_intelligence_agent.model;

import java.time.LocalDate;

public record Flight(
        LocalDate flightDate,
        String originAirportId,
        String origin,
        String originState,
        String destinationAirportId,
        String destination,
        boolean departureDelayed15,
        boolean cancelled,
        boolean diverted,
        Double distance
) {
}