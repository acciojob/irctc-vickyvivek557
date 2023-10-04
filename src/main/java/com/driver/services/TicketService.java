package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db

        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        int trainId = bookTicketEntryDto.getTrainId();
        Optional<Train> trainOptional = trainRepository.findById(trainId);
        if(!trainOptional.isPresent()){
            throw new Exception("Train not found");
        }
        Train train = trainOptional.get();
        List<Ticket> tickets = train.getBookedTickets();
        int seatsNeeded = bookTicketEntryDto.getNoOfSeats();
        int totalSeatsInTrain = train.getNoOfSeats();
        int availableSeatsInTrain = totalSeatsInTrain - tickets.size();
        // In case the there are insufficient tickets throw new Exception("Less tickets are available");
        if(availableSeatsInTrain < seatsNeeded){
            throw new Exception("Less tickets are available");
        }
        //In case the train doesn't pass through the requested stations throw new Exception("Invalid stations");
        Station fromStation = bookTicketEntryDto.getFromStation();
        Station toStation = bookTicketEntryDto.getToStation();
        String[] trainRoute = train.getRoute().split(",");
        int left = 0, right = trainRoute.length-1;
        boolean fromStationFound = false, toStationFound = false;
        while(left < right){
            if(trainRoute[left].equals(fromStation.toString())){
                fromStationFound = true;
                break;
            }
            left++;
        }
        while(left < right){
            if(trainRoute[right].equals(toStation.toString())){
                toStationFound = true;
                break;
            }
            right--;
        }
        if(!fromStationFound || !toStationFound){
            throw new Exception("Invalid stations");
        }
        //otherwise book the ticket, calculate the price and other details. Save the information in corresponding DB Tables
        List<Integer> passengerIds = bookTicketEntryDto.getPassengerIds();
        Ticket ticket = new Ticket();
        ticket.setFromStation(fromStation);
        ticket.setToStation(toStation);
        //Fare System : Check problem statement
        int totalFairPerTicket = Math.abs(right - left) * 300;
        int totalFair = totalFairPerTicket * seatsNeeded;
        ticket.setTotalFare(totalFair);
        ticket.setTrain(train);
        Ticket savedTicket = ticketRepository.save(ticket);

        //Save the bookedTickets in the train Object
        train.getBookedTickets().add(savedTicket);

        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
        Optional<Passenger> passengerOptional = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId());
        Passenger bookingPassenger = passengerOptional.get();
        bookingPassenger.getBookedTickets().add(savedTicket);
        savedTicket.getPassengersList().add(bookingPassenger);
        for(Integer id : passengerIds){
            Optional<Passenger> passenger = passengerRepository.findById(id);
            passenger.get().getBookedTickets().add(savedTicket);
            savedTicket.getPassengersList().add(passenger.get());

        }
        ticketRepository.save(savedTicket);
        trainRepository.save(train);
        passengerRepository.save(bookingPassenger);
        //And the end return the ticketId that has come from db
        return savedTicket.getTicketId();

    }
}
