package ar.edu.unsam.phm.uberto.builder

import ar.edu.unsam.phm.uberto.model.Passenger

class PassengerBuilder(val newPassenger: Passenger = Passenger()) {

    fun userId(id: Int): PassengerBuilder = apply {
        newPassenger.userId = id
    }

    fun firstName(name: String): PassengerBuilder = apply {
        newPassenger.firstName = name
    }

    fun img(image: String): PassengerBuilder = apply {
        newPassenger.img = image
    }

    fun lastName(name: String): PassengerBuilder = apply {
        newPassenger.lastName = name
    }

    fun age(age: Int): PassengerBuilder = apply {
        newPassenger.age = age
    }

    fun cellphone(cellphoneNumber: Int): PassengerBuilder = apply {
        newPassenger.cellphone = cellphoneNumber
    }

    fun balance(moneyBalance: Double): PassengerBuilder = apply {
        newPassenger.balance = moneyBalance
    }

    fun build(): Passenger = newPassenger

}