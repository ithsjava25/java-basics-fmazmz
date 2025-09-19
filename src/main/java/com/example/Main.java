package com.example;

import com.example.api.ElpriserAPI;
import com.example.cli.ArgsParser;
import com.example.cli.CliPrinter;
import com.example.model.PricePoint;
import com.example.service.PriceCalculatorService;
import com.example.service.PriceRetrievalService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ArgsParser.Args options;

        try {
            options = ArgsParser.parse(args);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            ArgsParser.printHelp();
            return;
        }

        if (options.showHelp()) {
            ArgsParser.printHelp();
            return;
        }

        ElpriserAPI elpriserAPI = new ElpriserAPI();
        PriceRetrievalService priceRetrievalService = new PriceRetrievalService(elpriserAPI);

        List<PricePoint> prices = priceRetrievalService.getPricePoints(options.zone(), options.date());
        if (prices.isEmpty()) {
            System.out.println("No data available for zone " + options.zone() + " and date " + options.date());
            return;
        }
        CliPrinter.printPrices(prices, options.zone().name(), options.date(), options.sorted());

        PriceCalculatorService priceCalculatorService = new PriceCalculatorService();

        double mean = priceCalculatorService.calculateMean(prices);
        PricePoint cheapest = priceCalculatorService.findCheapestHour(prices);
        PricePoint expensive = priceCalculatorService.findMostExpensiveHour(prices);

        CliPrinter.printCheapestAndMostExpensiveHours(cheapest, expensive);
        CliPrinter.printMeanPrice(mean);

        if (options.chargingHours() > 0) {
            List<PricePoint> window = priceCalculatorService.findOptimalChargingWindow(prices, options.chargingHours());
            CliPrinter.printChargingWindow(window, options.chargingHours());
        }

    }
}
