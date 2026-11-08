package com.airport.airport_investment_intelligence_agent.service;

import com.airport.airport_investment_intelligence_agent.data.FlightCsvLoader;
import com.airport.airport_investment_intelligence_agent.data.T100CsvLoader;
import com.airport.airport_investment_intelligence_agent.model.Flight;
import com.airport.airport_investment_intelligence_agent.model.T100SegmentData;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AirportDataService {

    private final FlightCsvLoader flightCsvLoader;
    private final T100CsvLoader t100CsvLoader;

    private List<Flight> flights = List.of();
    private List<T100SegmentData> t100Data = List.of();

    public AirportDataService(
            FlightCsvLoader flightCsvLoader,
            T100CsvLoader t100CsvLoader) {

        this.flightCsvLoader = flightCsvLoader;
        this.t100CsvLoader = t100CsvLoader;
    }

    @PostConstruct
    public void loadData() throws IOException {

        flights = flightCsvLoader.loadFlights();
        t100Data = t100CsvLoader.loadData();

        System.out.println(
                "Successfully loaded " + flights.size() + " on-time records"
        );

        System.out.println(
                "Successfully loaded " + t100Data.size() + " T-100 records"
        );
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public List<T100SegmentData> getT100Data() {
        return t100Data;
    }
}