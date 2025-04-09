package ar.edu.unsam.phm.uberto.builder

import ar.edu.unsam.phm.uberto.model.Driver

class DriverBuilder(val newDriver: Driver) {

    fun userId(id: Int): DriverBuilder = apply {
        newDriver.userId = id
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

    fun build(): Driver = newDriver

}