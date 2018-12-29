package isa.projekat.Projekat.controller.airline;

import isa.projekat.Projekat.model.airline.Airline;
import isa.projekat.Projekat.service.airline.AirlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AirlineController {

    @Autowired
    private AirlineService airlineService;

    @RequestMapping(value = "api/airlines/findAll", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Airline> findAll(){
        return airlineService.findAll();
    }

}
