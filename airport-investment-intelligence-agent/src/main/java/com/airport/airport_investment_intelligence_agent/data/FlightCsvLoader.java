
package com.airport.airport_investment_intelligence_agent.data;

import com.airport.airport_investment_intelligence_agent.model.Flight;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

@Component
public class FlightCsvLoader {

    public List<Flight> loadFlights() throws IOException {

        List<Flight> flights = new ArrayList<>();

        InputStream inputStream = getClass()
                .getResourceAsStream("/data/T_ONTIME_REPORTING.csv");

        if (inputStream == null) {
            throw new IOException("Could not find T_ONTIME_REPORTING.csv");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream))) {

            String line;
            boolean header = true;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    "M/d/yyyy h:mm:ss a",
                    Locale.US
            );

            while ((line = reader.readLine()) != null) {

                if (header) {
                    header = false;
                    continue;
                }

                if (line.isBlank()) {
                    continue;
                }

                String[] columns = line.split(",", -1);


                Flight flight = new Flight(
                        LocalDateTime.parse(columns[4], formatter).toLocalDate(),
                        columns[5],
                        columns[6],
                        columns[7],
                        columns[8],
                        columns[9],
                        parseBoolean(columns[10]),
                        parseBoolean(columns[11]),
                        parseBoolean(columns[12]),
                        Double.parseDouble(columns[13])
                );

                flights.add(flight);
            }
        }

        return flights;
    }

    private boolean parseBoolean(String value) {
        return value != null && !value.isBlank()
                && Double.parseDouble(value.trim()) == 1.0;
    }
}
