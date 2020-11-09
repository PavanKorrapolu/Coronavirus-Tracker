package com.track.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.track.demo.models.LocationStats;
import com.track.demo.services.CoronavirusDataService;

@Controller
public class HomeController 
{
	@Autowired
	CoronavirusDataService coronavirusDataService;
	@GetMapping("/")
	public String home(Model model)
	{
		List<LocationStats> allStats = coronavirusDataService.getAllStats();
		int totalReportedCases = allStats.stream().mapToInt(stat->stat.getLatestTotal()).sum();
		int totalNewCases = allStats.stream().mapToInt(stat->stat.getDiffFromPrevDay()).sum();
		model.addAttribute("locationStats",allStats);
		model.addAttribute("totalReportedCases",totalReportedCases);
		model.addAttribute("totalNewCases",totalNewCases);
		return "home";	
	}
}
