
package com.airport.airport_investment_intelligence_agent;

import com.airport.airport_investment_intelligence_agent.model.AirportMetrics;
import com.airport.airport_investment_intelligence_agent.model.Flight;
import com.airport.airport_investment_intelligence_agent.service.AirportAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AirportAnalysisServiceTest {

    private AirportAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new AirportAnalysisService();
    }

    private Flight createFlight(
            String airportId,
            boolean cancelled,
            Double distance) {

        return new Flight(
                LocalDate.now(),
                airportId,
                "AAA",
                "CA",
                "BBB",
                "BBB",
                false,
                cancelled,
                false,
                distance
        );
    }

    @Test
    void shouldCalculateDepartureDelayRate() {

        List<Flight> flights = List.of(
                new Flight(null, "101", "JFK", "NY",
                        "102", "LAX", true, false, false, 1000.0),

                new Flight(null, "101", "JFK", "NY",
                        "103", "ORD", false, false, false, 1000.0),

                new Flight(null, "101", "JFK", "NY",
                        "104", "MIA", true, false, false, 1000.0),

                new Flight(null, "101", "JFK", "NY",
                        "105", "BOS", false, true, false, 1000.0)
        );

        double result = service.calculateDepartureDelayRate(flights);

        assertEquals(2.0 / 3.0, result, 0.0001);
    }

    @Test
    void shouldCalculateCancellationRate() {

        List<Flight> flights = List.of(
                new Flight(null, "101", "JFK", "NY",
                        "102", "LAX", true, false, false, 1000.0),

                new Flight(null, "101", "JFK", "NY",
                        "103", "ORD", false, false, false, 1000.0),

                new Flight(null, "101", "JFK", "NY",
                        "104", "MIA", true, false, false, 1000.0),

                new Flight(null, "101", "JFK", "NY",
                        "105", "BOS", false, true, false, 1000.0)
        );

        double result = service.calculateCancellationRate(flights);

        assertEquals(0.25, result, 0.0001);
    }

    @Test
    void shouldReturnOnlyDeparturesForRequestedAirport() {

        List<Flight> flights = List.of(
                new Flight(null, "101", "JFK", "NY",
                        "102", "LAX", true, false, false, 1000.0),

                new Flight(null, "102", "LAX", "CA",
                        "101", "JFK", false, false, false, 1000.0),

                new Flight(null, "101", "JFK", "NY",
                        "103", "ORD", true, false, false, 1000.0)
        );

        List<Flight> result =
                service.getDeparturesForAirport(flights, "101");

        assertEquals(2, result.size());

        assertEquals(
                true,
                result.stream().allMatch(
                        flight -> flight.originAirportId().equals("101")
                )
        );
    }

    @Test
    void shouldAnalyzeAirportCorrectly() {

        List<Flight> flights = List.of(
                new Flight(null, "101", "JFK", "NY",
                        "102", "LAX", true, false, false, 1000.0),

                new Flight(null, "101", "JFK", "NY",
                        "103", "ORD", false, false, false, 1000.0),

                new Flight(null, "101", "JFK", "NY",
                        "104", "MIA", false, true, false, 1000.0),

                new Flight(null, "102", "LAX", "CA",
                        "101", "JFK", true, false, false, 1000.0)
        );

        AirportMetrics result = service.analyzeAirport(flights, "101");

        assertEquals(3, result.totalDepartures());
        assertEquals(1, result.delayDepartures());
        assertEquals(1, result.cancelDepartures());
        assertEquals(0.5, result.departureDelayRate());
        assertEquals(1.0 / 3.0, result.departureCancellationRate());
    }

    @Test
    void shouldCalculateLongHaulPercentage() {

        List<Flight> flights = List.of(
                createFlight("101", false, 3500.0),
                createFlight("101", false, 2500.0),
                createFlight("101", false, 4000.0),
                createFlight("101", true, 5000.0)
        );

        double result = service.calculateLongHaulPercentage(flights);

        assertEquals(66.6666666667, result, 0.001);
    }

    @Test
    void shouldCalculateLongHaulPercentageForSpecificAirport() {

        List<Flight> flights = List.of(
                createFlight("101", false, 3500.0),
                createFlight("101", false, 2500.0),
                createFlight("102", false, 4000.0),
                createFlight("101", true, 5000.0)
        );

        double result =
                service.calculateLongHaulPercentageForAirport(flights, "101");

        assertEquals(50.0, result, 0.001);
    }
}
