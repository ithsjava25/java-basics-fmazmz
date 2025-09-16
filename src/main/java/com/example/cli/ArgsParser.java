package com.example.cli;

import com.example.api.ElpriserAPI;

import java.time.LocalDate;

public class ArgsParser {

  // Object to hold args variables.
  public record Args(ElpriserAPI.Prisklass zone, LocalDate date, boolean sorted, int chargingHours, boolean showHelp) {}

  public static Args parse(String[] args) {
    ElpriserAPI.Prisklass zone = null;
    LocalDate date = LocalDate.now();
    boolean sorted = false;
    int chargingHours = 0;
    boolean showHelp = false;

    // Switch case to catch input args and re-assign them to the final Args object.
    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "--zone" -> zone = ElpriserAPI.Prisklass.valueOf(args[++i].toUpperCase());
        case "--date" -> date = LocalDate.parse(args[++i]);
        case "--sorted" -> sorted = true;
        case "--charging" -> chargingHours = Integer.parseInt(args[++i].replace("h", ""));
        case "--help" -> showHelp = true;
      }
    }

    // Enforce zone as it is a required field in the request.
    if (zone == null && !showHelp) {
      throw new IllegalArgumentException("Zone (--zone SE1|SE2|SE3|SE4) is required");
    }

    return new Args(zone, date, sorted, chargingHours, showHelp);
  }


  public static void printHelp() {
    System.out.println("""
            Usage: java -cp target/classes com.example.Main --zone SE1|SE2|SE3|SE4 [options]

            Options:
              --date YYYY-MM-DD     Date to fetch prices (default is today)
              --sorted              Sort prices by cost descending
              --charging 2h|4h|8h   Find optimal charging window
              --help                Show this help
            """);
  }
}
