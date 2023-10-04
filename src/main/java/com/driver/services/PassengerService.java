package com.driver.services;


import com.driver.model.Passenger;
import com.driver.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PassengerService {

    @Autowired
    PassengerRepository passengerRepository;

    public Integer addPassenger(Passenger passenger){
        //Add the passenger Object in the passengerDb and return the passegnerId that has been returned
        Passenger newPassanger = new Passenger();
        newPassanger.setName(passenger.getName());
        newPassanger.setAge(passenger.getAge());
        newPassanger.setBookedTickets(new ArrayList<>());
        // save newPassenger
        Passenger savedPassenger = passengerRepository.save(newPassanger);
        return savedPassenger.getPassengerId();
    }

}
