package com.example;

import com.example.api.ElpriserAPI;
import com.example.cli.ArgsParser;
import com.example.cli.CliPrinter;
import com.example.model.PricePoint;
import com.example.service.PriceService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ArgsParser.Args options;

        try {
            options = ArgsParser.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            ArgsParser.printHelp();
            return;
        }

        if (options.showHelp()) {
            ArgsParser.printHelp();
            return;
        }

        ElpriserAPI elpriserAPI = new ElpriserAPI();
        PriceService priceService = new PriceService(elpriserAPI);

        List<PricePoint> prices = priceService.getPricePoints(options.zone(), options.date());

        CliPrinter.printPrices(prices, options.zone().name(), options.date());
    }
}
