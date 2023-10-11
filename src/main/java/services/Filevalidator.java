package services;

import interfaces.ValidationStrategy;
import lombok.extern.log4j.Log4j2;

import javax.enterprise.context.RequestScoped;

@Log4j2
@RequestScoped
public class Filevalidator {
    private ValidationStrategy strategy;

    public void setStrategy(ValidationStrategy strategy) {
        this.strategy = strategy;
    }

    public  boolean validate(byte[] image){
        return strategy.validateImage(image);
    }
}