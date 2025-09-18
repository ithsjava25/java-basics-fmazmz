package com.example.service;

import com.example.api.ElpriserAPI;
import com.example.model.PricePoint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceRetrievalService {
  private final ElpriserAPI elpriserAPI;

  public PriceRetrievalService(ElpriserAPI elpriserAPI) {
    this.elpriserAPI = elpriserAPI;
  }

  public List<PricePoint> getPricePoints(ElpriserAPI.Prisklass zone, LocalDate date) {
    List<PricePoint> pricePoints = new ArrayList<>();

    // Try today and tomorrow instead of checking if its 1PM, fallback on today if API data is empty for tomorrow.
    for (LocalDate d : List.of(date, date.plusDays(1))) {
      List<ElpriserAPI.Elpris> raw = elpriserAPI.getPriser(d, zone);

      // Convert API DTO's into internal DTO's as extra data fields are not required.
      raw.forEach(r -> {
        PricePoint point = new PricePoint(r.timeStart().toLocalDateTime(), r.sekPerKWh());
        if (!pricePoints.contains(point)) {
          pricePoints.add(point);
        }
      });
    }

    // Return Elpris objects as internal.
    return pricePoints;
  }
}
