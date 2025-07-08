package org.example.controllers;

import org.example.models.VrsAir;
import org.example.services.OpenSkyFetchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/Feed")
public class FeedController {

    private final OpenSkyFetchService fetchService;

    public FeedController(OpenSkyFetchService fetchService) {
        this.fetchService = fetchService;
    }

    /**
     * VRS будет делать GET http://<ваш-хост>:<порт>/Feed/AllAircraft.json
     */
    @GetMapping(value = "/AircraftList.json", produces = "application/json")
    public Collection<VrsAir> allAircraft() {
        return fetchService.getAllAircraft();
    }
}