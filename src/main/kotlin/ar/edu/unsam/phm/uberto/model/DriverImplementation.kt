package ar.edu.unsam.phm.uberto.model

import jakarta.persistence.Entity

@Entity
class BikeDriver(): Driver() {
    private val reference:Double = 500.0
    override fun plusBasePrice(time: Int, numberPassengers: Int): Double {
        return if(time < 30) reference else reference + 100.0
    }

    override fun toString() = "Motorbiker"
}

@Entity
class SimpleDriver(): Driver() {
    override fun plusBasePrice(time: Int, numberPassengers: Int): Double {
        return 1000.0
    }

    override fun toString() = "Simple Driver"
}

@Entity
class PremiumDriver(): Driver() {
    private val reference:Double = 1500.0

    override fun plusBasePrice(time: Int, numberPassengers: Int): Double {
        return if(numberPassengers > 1) reference else reference + 500.0
    }

    override fun toString() = "Premium Driver"

}