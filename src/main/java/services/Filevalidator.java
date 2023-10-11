package services;

import interfaces.ValidationStrategy;
import lombok.extern.log4j.Log4j2;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Response;


@RequestScoped
public class Filevalidator {
    private ValidationStrategy strategy;

    public void setStrategy(ValidationStrategy strategy) {
        this.strategy = strategy;
    }

    public  boolean validate(byte[] image){
        return strategy.validateImage(image);
    }

    public Response checkFileValidity(byte[] image){
        return strategy.checkFileValidity(image);
    }



}