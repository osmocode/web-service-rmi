package web.service.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rmi.bike.interfaces.bike.BikeListService;
import rmi.bike.models.BikeState;
import rmi.customer.interfaces.CustomerListService;
import web.service.rest.providers.BikeListProvider;
import web.service.rest.providers.BikeProvider;
import web.service.rest.session.Authenticated;

import javax.validation.Valid;
import java.rmi.RemoteException;
import java.util.UUID;


@RestController
public class BikeController {

    @Autowired
    BikeListService service;

    @Autowired
    CustomerListService authService;

    @GetMapping("/api/v1/bike")
    public BikeListProvider getBike() throws RemoteException {
        return new BikeListProvider(service.getAll());
    }

    @GetMapping("api/v1/bike/{id}")
    public BikeProvider getBikeById(@PathVariable("id") String uuid) throws RemoteException {
        var bike = service.getBikeByUUID(uuid);
        if (bike == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bike not found");
        }
        return new BikeProvider(UUID.fromString(uuid), bike);
    }

    @Authenticated
    @PostMapping("/api/v1/bike")
    public BikeProvider postBike(@RequestHeader(value = "X-Auth-Token") String token, @Valid @RequestBody BikeProvider bike) throws RemoteException {
        var user = authService.isLogged(UUID.fromString(token));
        var entry = service.add(bike.label, bike.desc, user, BikeState.valueOf(bike.state));
        if(entry == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bike not created");
        }
        var response =  entry.entrySet().stream().findFirst();
        if(response.isEmpty()){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "bike creation error");
        }
        return new BikeProvider(response.get().getKey(), response.get().getValue());
    }

}
