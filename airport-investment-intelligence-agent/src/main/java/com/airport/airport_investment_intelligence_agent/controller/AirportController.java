
package com.airport.airport_investment_intelligence_agent.controller;

import com.airport.airport_investment_intelligence_agent.model.AirportComparison;
import com.airport.airport_investment_intelligence_agent.model.AirportDemandMetrics;
import com.airport.airport_investment_intelligence_agent.model.AirportMetrics;
import com.airport.airport_investment_intelligence_agent.model.InvestmentPotentialScore;
import com.airport.airport_investment_intelligence_agent.service.AirportAnalysisService;
import com.airport.airport_investment_intelligence_agent.service.AirportDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportAnalysisService analysisService;
    private final AirportDataService dataService;

    public AirportController(
            AirportAnalysisService analysisService,
            AirportDataService dataService) {

        this.analysisService = analysisService;
        this.dataService = dataService;
    }

    @GetMapping("/{airportId}/metrics")
    public AirportMetrics getAirportMetrics(
            @PathVariable String airportId) {

        return analysisService.analyzeAirport(
                dataService.getFlights(),
                airportId
        );
    }

    @GetMapping("/compare")
    public List<AirportComparison> compareAirports(
            @RequestParam String firstAirportId,
            @RequestParam String secondAirportId) {

        return analysisService.compareAirports(
                dataService.getFlights(),
                firstAirportId,
                secondAirportId
        );
    }

    @GetMapping("/{airportId}/long-haul")
    public double getLongHaulPercentage(
            @PathVariable String airportId) {

        return analysisService.calculateLongHaulPercentageForAirport(
                dataService.getFlights(),
                airportId
        );
    }

    @GetMapping("/rank")
    public List<AirportComparison> rankAirports(
            @RequestParam List<String> airportIds) {

        return analysisService.rankAirports(
                dataService.getFlights(),
                airportIds
        );
    }

    @GetMapping("/{airportCode}/demand")
    public AirportDemandMetrics getAirportDemand(
            @PathVariable String airportCode,
            @RequestParam(defaultValue = "2025") int year,
            @RequestParam(defaultValue = "1") int month) {

        return analysisService.analyzeAirportDemand(
                dataService.getT100Data(),
                airportCode,
                year,
                month
        );
    }


    @GetMapping("/investment-score")
    public List<InvestmentPotentialScore> getInvestmentScores(
            @RequestParam List<String> airportCodes,
            @RequestParam(defaultValue = "2025") int year,
            @RequestParam(defaultValue = "1") int month) {

        return analysisService.calculateInvestmentScores(
                dataService.getT100Data(),
                airportCodes,
                year,
                month
        );
    }

}
