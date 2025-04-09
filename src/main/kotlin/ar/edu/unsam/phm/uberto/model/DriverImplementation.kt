package ar.edu.unsam.phm.uberto.model

class BikeDriver(override var id: Int = 0): Driver() {
    private val reference:Double = 500.0
    override fun plusBasePrice(time: Int, numberPassengers: Int): Double {
        return if(time < 30) reference else reference + 100.0
    }

    override fun toString() = "Motorbiker"
}

class SimpleDriver(override var id: Int = 0): Driver() {
    override fun plusBasePrice(time: Int, numberPassengers: Int): Double {
        return 1000.0
    }

    override fun toString() = "Simple Driver"
}

class PremiumDriver(override var id: Int = 0): Driver() {
    private val reference:Double = 1500.0

    override fun plusBasePrice(time: Int, numberPassengers: Int): Double {
        return if(numberPassengers > 1) reference else reference + 500.0
    }

    override fun toString() = "Premium Driver"

}