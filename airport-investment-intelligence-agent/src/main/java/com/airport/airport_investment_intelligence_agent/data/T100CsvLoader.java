package com.airport.airport_investment_intelligence_agent.data;

import com.airport.airport_investment_intelligence_agent.model.T100SegmentData;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class T100CsvLoader {

    public List<T100SegmentData> loadData() throws IOException {

        List<T100SegmentData> records = new ArrayList<>();

        InputStream inputStream = getClass()
                .getResourceAsStream("/data/T_T100D_SEGMENT_ALL_CARRIER.csv");

        if (inputStream == null) {
            throw new IOException("T-100 CSV file not found");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {

                String[] columns = line.split(",", -1);

                if (columns.length < 8) {
                    continue;
                }

                T100SegmentData record = new T100SegmentData(
                        Integer.parseInt(columns[6].trim()),
                        Integer.parseInt(columns[7].trim()),
                        columns[5].trim(),
                        parseDouble(columns[0]),
                        parseDouble(columns[1]),
                        parseDouble(columns[2]),
                        parseDouble(columns[3]),
                        parseDouble(columns[4])
                );

                records.add(record);
            }
        }

        return records;
    }

    private double parseDouble(String value) {
        return value == null || value.isBlank()
                ? 0.0
                : Double.parseDouble(value.trim());
    }
}