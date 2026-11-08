package com.airport.airport_investment_intelligence_agent.service;

import com.airport.airport_investment_intelligence_agent.model.*;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.*;

@Service
public class AirportAnalysisService {

    private static final double LONG_HAUL_DISTANCE_THRESHOLD = 3000.0;

    public double calculateDepartureDelayRate(List<Flight> flights) {

        long operatedDepartures = countOperatedDepartures(flights);

        if (operatedDepartures == 0) {
            return 0.0;
        }

        long delayedDepartures = countDelayedDepartures(flights);

        return (double) delayedDepartures / operatedDepartures;
    }

    /**
     * Calculates the percentage of scheduled departures
     * that were cancelled.
     */
    public double calculateCancellationRate(List<Flight> flights) {

        long totalDepartures = countTotalDepartures(flights);

        if (totalDepartures == 0) {
            return 0.0;
        }

        long cancelledDepartures = countCancelledDepartures(flights);

        return (double) cancelledDepartures / totalDepartures;
    }

    /**
     * Returns only flights departing from the requested airport.
     */
    public List<Flight> getDeparturesForAirport(
            List<Flight> flights,
            String airportId) {

        if (flights == null || airportId == null || airportId.isBlank()) {
            return List.of();
        }

        return flights.stream()
                .filter(flight -> flight != null)
                .filter(flight -> airportId.equals(flight.originAirportId()))
                .toList();
    }

    /**
     * Analyzes departure data for a specific airport
     * and returns its main operational metrics.
     */
    public AirportMetrics analyzeAirport(
            List<Flight> flights,
            String airportId) {

        List<Flight> departures =
                getDeparturesForAirport(flights, airportId);

        long totalDepartures = countTotalDepartures(departures);
        long delayedDepartures = countDelayedDepartures(departures);
        long cancelledDepartures = countCancelledDepartures(departures);

        double delayRate = calculateDepartureDelayRate(departures);
        double cancellationRate = calculateCancellationRate(departures);

        return new AirportMetrics(
                airportId,
                totalDepartures,
                delayedDepartures,
                cancelledDepartures,
                delayRate,
                cancellationRate
        );
    }

    // ---------------- Private helper methods ----------------

    private long countTotalDepartures(List<Flight> flights) {

        if (flights == null) {
            return 0;
        }

        return flights.stream()
                .filter(flight -> flight != null)
                .count();
    }

    private long countOperatedDepartures(List<Flight> flights) {

        if (flights == null) {
            return 0;
        }

        return flights.stream()
                .filter(flight -> flight != null)
                .filter(flight -> !flight.cancelled())
                .count();
    }

    private long countDelayedDepartures(List<Flight> flights) {

        if (flights == null) {
            return 0;
        }

        return flights.stream()
                .filter(flight -> flight != null)
                .filter(flight -> !flight.cancelled())
                .filter(Flight::departureDelayed15)
                .count();
    }

    private long countCancelledDepartures(List<Flight> flights) {

        if (flights == null) {
            return 0;
        }

        return flights.stream()
                .filter(flight -> flight != null)
                .filter(Flight::cancelled)
                .count();
    }

    public double calculateLongHaulPercentage(List<Flight> flights) {

        if (flights == null || flights.isEmpty()) {
            return 0.0;
        }

        long flightsWithKnownDistance = flights.stream()
                .filter(flight -> flight != null)
                .filter(flight -> !flight.cancelled())
                .filter(flight -> flight.distance() != null)
                .count();

        if (flightsWithKnownDistance == 0) {
            return 0.0;
        }

        long longHaulFlights = flights.stream()
                .filter(flight -> flight != null)
                .filter(flight -> !flight.cancelled())
                .filter(flight -> flight.distance() != null)
                .filter(flight -> flight.distance() > LONG_HAUL_DISTANCE_THRESHOLD)
                .count();

        return (double) longHaulFlights / flightsWithKnownDistance * 100;
    }

    public double calculateLongHaulPercentageForAirport(
            List<Flight> flights,
            String airportId) {

        List<Flight> departures =
                getDeparturesForAirport(flights, airportId);

        return calculateLongHaulPercentage(departures);
    }

    public List<AirportComparison> compareAirports(
            List<Flight> flights,
            String firstAirportId,
            String secondAirportId) {

        AirportMetrics first = analyzeAirport(flights, firstAirportId);
        AirportMetrics second = analyzeAirport(flights, secondAirportId);

        return List.of(
                new AirportComparison(
                        first.airportId(),
                        first.totalDepartures(),
                        first.departureDelayRate() * 100,
                        first.departureCancellationRate() * 100
                ),
                new AirportComparison(
                        second.airportId(),
                        second.totalDepartures(),
                        second.departureDelayRate() * 100,
                        second.departureCancellationRate() * 100
                )
        );
    }

    public List<AirportComparison> rankAirports(
            List<Flight> flights,
            List<String> airportIds) {

        if (flights == null || airportIds == null) {
            return List.of();
        }

        return airportIds.stream()
                .map(airportId -> analyzeAirport(flights, airportId))
                .map(metrics -> new AirportComparison(
                        metrics.airportId(),
                        metrics.totalDepartures(),
                        metrics.departureDelayRate() * 100,
                        metrics.departureCancellationRate() * 100
                ))
                .sorted(Comparator.comparingDouble(AirportComparison::departureDelayRate).thenComparingDouble(AirportComparison::departureCancellationRate))
                .toList();
    }

    public AirportDemandMetrics analyzeAirportDemand(
            List<T100SegmentData> records,
            String airportCode,
            int year,
            int month) {

        List<T100SegmentData> airportRecords = records.stream()
                .filter(record -> record.origin() != null
                        && record.origin().equalsIgnoreCase(airportCode))
                .filter(record -> record.year() == year)
                .filter(record -> record.month() == month)
                .toList();

        double totalPassengers = airportRecords.stream()
                .mapToDouble(T100SegmentData::passengers)
                .sum();

        double totalSeats = airportRecords.stream()
                .mapToDouble(T100SegmentData::seats)
                .sum();

        double totalDepartures = airportRecords.stream()
                .mapToDouble(T100SegmentData::departuresPerformed)
                .sum();

        double loadFactor = totalSeats > 0
                ? (totalPassengers / totalSeats) * 100
                : 0.0;

        return new AirportDemandMetrics(
                airportCode,
                totalPassengers,
                totalSeats,
                totalDepartures,
                loadFactor
        );
    }


    public List<InvestmentPotentialScore> calculateInvestmentScores(
            List<T100SegmentData> data,
            List<String> airportCodes,
            int year,
            int month) {

        Map<String, AirportDemandMetrics> metricsByAirport = new HashMap<>();

        for (String airportCode : airportCodes) {
            AirportDemandMetrics metrics =
                    analyzeAirportDemand(data, airportCode, year, month);

            metricsByAirport.put(airportCode, metrics);
        }

        double maxPassengers = metricsByAirport.values().stream()
                .mapToDouble(AirportDemandMetrics::totalPassengers)
                .max().orElse(0);

        double maxFlights = metricsByAirport.values().stream()
                .mapToDouble(AirportDemandMetrics::totalDeparturesPerformed)
                .max().orElse(0);

        List<InvestmentPotentialScore> results = new ArrayList<>();

        for (Map.Entry<String, AirportDemandMetrics> entry :
                metricsByAirport.entrySet()) {

            String airportCode = entry.getKey();
            AirportDemandMetrics metrics = entry.getValue();

            double passengerScore = maxPassengers == 0 ? 0 :
                    metrics.totalPassengers() / maxPassengers * 100;

            double flightScore = maxFlights == 0 ? 0 :
                    metrics.totalDeparturesPerformed() / maxFlights * 100;

            double loadFactorScore = Math.min(
                    100, Math.max(0, metrics.loadFactor()));

            double totalScore =
                    passengerScore * 0.40 +
                            flightScore * 0.30 +
                            loadFactorScore * 0.30;

            results.add(new InvestmentPotentialScore(
                    airportCode,
                    passengerScore,
                    flightScore,
                    loadFactorScore,
                    totalScore
            ));
        }

        return results.stream()
                .sorted(Comparator.comparingDouble(
                        InvestmentPotentialScore::totalScore).reversed())
                .toList();
    }

}


