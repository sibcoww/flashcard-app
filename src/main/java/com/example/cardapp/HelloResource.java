package com.example.cardapp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {

    @GET
    @Produces("text/plain; charset=UTF-8")
    public String sayHello() {
        return "Привет с сервера Java EE!";
    }
}
