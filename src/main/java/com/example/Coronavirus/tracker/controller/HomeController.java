package com.example.Coronavirus.tracker.controller;


import com.example.Coronavirus.tracker.model.LocationStats;
import com.example.Coronavirus.tracker.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    DataService dataService;

    @GetMapping("/")
    public String home(Model model) {

        List<LocationStats> locationStats = dataService.getLocationStats();

        int totalCases = locationStats.stream().mapToInt(LocationStats::getLatestTotalCases).sum();
        int newCases = locationStats.stream().mapToInt(LocationStats::getDiffFromPrevDay).sum();

        model.addAttribute("locationStats", locationStats);
        model.addAttribute("totalReportedCases", totalCases);
        model.addAttribute("newCases", newCases);

        return "home";
    }
}
