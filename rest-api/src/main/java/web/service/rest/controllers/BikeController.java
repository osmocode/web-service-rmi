package web.service.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rmi.bike.interfaces.bike.BikeListService;
import web.service.rest.providers.BikeProvider;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BikeController {

    @Autowired
    BikeListService service;


    @GetMapping("/bike")
    public Map<String, BikeProvider> getBike() throws RemoteException {
        var lst = service.getBikesList();
        var map = new HashMap<String, BikeProvider>();
        for (int i = 0; i < lst.size(); i ++) {
            map.put(String.valueOf(i), new BikeProvider(String.valueOf(i), lst.get(i)));
        }
        return map;
    }

}
