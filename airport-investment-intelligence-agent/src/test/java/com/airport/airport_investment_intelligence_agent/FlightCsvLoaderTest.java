
package com.airport.airport_investment_intelligence_agent;

import com.airport.airport_investment_intelligence_agent.data.FlightCsvLoader;
import com.airport.airport_investment_intelligence_agent.model.Flight;
import com.airport.airport_investment_intelligence_agent.service.AirportAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class FlightCsvLoaderTest {

    @Autowired
    private FlightCsvLoader loader;
    @Autowired
    private AirportAnalysisService analysisService;

    @Test
    void shouldLoadFlightsFromCsv() throws Exception {

        List<Flight> flights = loader.loadFlights();

        long abeFlights = flights.stream()
                .filter(flight -> "10135".equals(flight.originAirportId()))
                .count();

        long abeDelayed = flights.stream()
                .filter(flight -> "10135".equals(flight.originAirportId()))
                .filter(Flight::departureDelayed15)
                .count();

        System.out.println("ABE departures: " + abeFlights);
        System.out.println("ABE delayed departures: " + abeDelayed);

        long abeCancelled = flights.stream()
                .filter(flight -> "10135".equals(flight.originAirportId()))
                .filter(Flight::cancelled)
                .count();

        System.out.println("ABE cancelled departures: " + abeCancelled);

        System.out.println("Number of flights loaded: " + flights.size());

        System.out.println("First flight: " + flights.get(0));

        assertFalse(flights.isEmpty());

        double abeLongHaulPercentage =
                analysisService.calculateLongHaulPercentageForAirport(
                        flights, "10135"
                );

        System.out.println(
                "ABE long-haul percentage: " + abeLongHaulPercentage
        );

        double sfoLongHaulPercentage =
                analysisService.calculateLongHaulPercentageForAirport(
                        flights, "14771"
                );

        System.out.println(
                "SFO long-haul percentage: " + sfoLongHaulPercentage
        );

        long sfoLongHaulFlights = flights.stream()
                .filter(flight -> "14771".equals(flight.originAirportId()))
                .filter(flight -> flight.distance() != null)
                .filter(flight -> flight.distance() > 3000)
                .filter(flight -> !flight.cancelled())
                .count();

        System.out.println(
                "SFO long-haul flights: " + sfoLongHaulFlights
        );

        double sfoMaxDistance = flights.stream()
                .filter(flight -> "14771".equals(flight.originAirportId()))
                .filter(flight -> !flight.cancelled())
                .filter(flight -> flight.distance() != null)
                .mapToDouble(Flight::distance)
                .max()
                .orElse(0);

        System.out.println("SFO max distance: " + sfoMaxDistance);

        double overallLongHaulPercentage =
                analysisService.calculateLongHaulPercentage(flights);

        System.out.println(
                "Overall long-haul percentage: " + overallLongHaulPercentage
        );

        double anchorageLongHaulPercentage =
                analysisService.calculateLongHaulPercentageForAirport(
                        flights, "10299"
                );

        System.out.println(
                "Anchorage long-haul percentage: "
                        + anchorageLongHaulPercentage + "%"
        );
    }


}
