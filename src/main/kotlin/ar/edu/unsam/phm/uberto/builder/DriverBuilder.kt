package ar.edu.unsam.phm.uberto.builder

import ar.edu.unsam.phm.uberto.model.Driver
import ar.edu.unsam.phm.uberto.model.UserAuthCredentials

class DriverBuilder(val newDriver: Driver) {

    fun userId(id: Long): DriverBuilder = apply {
        if (newDriver.credentials == null) newDriver.credentials = UserAuthCredentials()
        newDriver.credentialsId = id

    }


    fun firstName(name: String): DriverBuilder = apply {
        newDriver.firstName = name
    }

    fun lastName(name: String): DriverBuilder = apply {
        newDriver.lastName = name
    }

    fun balance(moneyBalance: Double): DriverBuilder = apply {
        newDriver.balance = moneyBalance
    }

    fun brand(brand: String): DriverBuilder = apply {
        newDriver.brand = brand
    }

    fun serial(serial: String): DriverBuilder = apply {
        newDriver.serial = serial
    }

    fun model(model: Int): DriverBuilder = apply {
        newDriver.model = model
    }

    fun img(img: String): DriverBuilder = apply {
        newDriver.img = img
    }

    fun basePrice(price: Double): DriverBuilder = apply {
        newDriver.basePrice = price
    }

    fun build(): Driver = newDriver

}