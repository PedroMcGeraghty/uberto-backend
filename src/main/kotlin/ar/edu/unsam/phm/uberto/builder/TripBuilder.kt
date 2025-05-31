package ar.edu.unsam.phm.uberto.builder

import ar.edu.unsam.phm.uberto.model.Driver
import ar.edu.unsam.phm.uberto.model.Passenger
import ar.edu.unsam.phm.uberto.model.Trip
import java.time.LocalDateTime


class TripBuilder(val newTrip: Trip = Trip()) {

    fun driver(driver: Driver): TripBuilder = apply {
        newTrip.driver = driver
        newTrip.driverId = driver.id!!
    }


    fun passenger(client: Passenger): TripBuilder = apply {
        newTrip.client = client
    }

    fun origin(origin: String): TripBuilder = apply {
        newTrip.origin = origin
    }

    fun duration(duration: Int): TripBuilder = apply {
        newTrip.duration = duration
    }

    fun destination(destination: String): TripBuilder = apply {
        newTrip.destination = destination
    }

    fun passengerAmmount(ammount: Int): TripBuilder = apply {
        newTrip.numberPassengers = ammount
    }

    fun setDate(date: String): TripBuilder = apply {
        newTrip.date = LocalDateTime.parse(date)
    }


    fun build(): Trip = newTrip

}