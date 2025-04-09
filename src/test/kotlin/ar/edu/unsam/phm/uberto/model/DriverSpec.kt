package ar.edu.unsam.phm.uberto.model

import ar.edu.unsam.phm.uberto.DriverNotAvaliableException
import ar.edu.unsam.phm.uberto.builder.PassengerBuilder
import ar.edu.unsam.phm.uberto.builder.TripBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class DriverSpec: DescribeSpec( {
    isolationMode = IsolationMode.InstancePerTest
    val client = PassengerBuilder().build()

    describe("Dado un chofer simple") {
        val simpleDriver:SimpleDriver = SimpleDriver()
        simpleDriver.basePrice = 10.0
        it("Cobra su precio base + 1500 * duracion del viaje") {
            val trip = TripBuilder().driver(simpleDriver).passenger(client).duration(10).build()
            simpleDriver.fee(trip.duration, trip.numberPassengers) shouldBeExactly (simpleDriver.basePrice + 1000.0 * trip.duration)
        }
    }

    describe("Dado un chofer premium") {
        val premiumDriver:PremiumDriver = PremiumDriver()
        premiumDriver.basePrice = 10.0
        describe("Precio del viaje segun la cantidad de pasajeros") {
            it("Con 1 pasajero cobra: precio base + 2000 * duracion del viaje") {
                val trip = TripBuilder().driver(premiumDriver).passenger(client).passengerAmmount(1).build()
                premiumDriver.fee(trip.duration, trip.numberPassengers) shouldBeExactly (premiumDriver.basePrice + 2000.0 * trip.duration)
            }
            it("Varios pasajeros") {
                val trip = TripBuilder().driver(premiumDriver).passenger(client).passengerAmmount(2).build()

                premiumDriver.fee(trip.duration, trip.numberPassengers) shouldBeExactly (premiumDriver.basePrice + 1500.0 * trip.duration)
            }
        }
    }

    describe("Dado un chofer de moto") {
        val bikeDriver:BikeDriver = BikeDriver()
        bikeDriver.basePrice = 10.0
        describe("Precio del viaje segun la duracion del viaje") {
            it("Duracion menor a 30 minutos: precio base + 500 * duracion del viaje") {
                val trip = TripBuilder().driver(bikeDriver).passenger(client).duration(29).build()
                bikeDriver.fee(trip.duration, trip.numberPassengers) shouldBeExactly (bikeDriver.basePrice + 500.0 * trip.duration)
            }
            it("DUracion mayor o igual a 30 minutos: precio base + 600 * duracion del viaje") {
                val trip = TripBuilder().driver(bikeDriver).passenger(client).duration(30).build()
                bikeDriver.fee(trip.duration, trip.numberPassengers) shouldBeExactly (bikeDriver.basePrice + 600.0 * trip.duration)
            }
        }
    }

    describe("Dado un chofer cualquiera") {
        val driver:SimpleDriver = SimpleDriver()
        driver.basePrice = 10.0
        it("Monto inicial de 0") {
            driver.balance shouldBeExactly 0.0
        }

        it("Realiza un viaje y aumenta su monto recaudado") {
            val tomorrow = LocalDateTime.now().plus(1, ChronoUnit.DAYS).toString()
            val trip = TripBuilder().driver(driver).passenger(client).setDate(tomorrow).duration(10).build()
            driver.balance shouldBeExactly 0.0
            driver.responseTrip(trip, trip.duration)
            driver.balance shouldBeExactly driver.fee(trip.duration, trip.numberPassengers)
        }

        describe("Driver solo acepta un viaje dentro de una franja horaria"){
            val tomorrow = LocalDateTime.now().plus(1, ChronoUnit.DAYS).toString()
            val trip = TripBuilder().driver(driver).passenger(client).setDate(tomorrow).duration(10).build()
            it("Si esta disponible, acepta el viaje, y lo guarda en su coleccion") {
                driver.responseTrip(trip, trip.duration)
                driver.trips shouldContain trip
            }
            it("Si NO esta disponible, no lo puede aceptar") {
                driver.trips.isEmpty() shouldBe true
                driver.responseTrip(trip, trip.duration)
                val tripDateConflict = TripBuilder().driver(driver).passenger(client).setDate(tomorrow).duration(10).build()
                shouldThrow<DriverNotAvaliableException> {
                    driver.responseTrip(tripDateConflict, tripDateConflict.duration)
                }
            }
        }

        describe("Su calificacion es el promedio de calificaciones de los viajes realiszados"){
            val yesterday = LocalDateTime.now().minus(1, ChronoUnit.DAYS).toString()
            val trip = TripBuilder().driver(driver).passenger(client).setDate(yesterday).duration(10).build()
            it("Si NO tiene viajes, su score es 0") {
                driver.scoreAVG() shouldBeExactly 0.0
            }
            it("Si NO tiene calificaciones, su score es 0 ") {
                driver.trips.add(trip) //Se hardcodea un viaje ya finalizado
                trip.score shouldBe null
                driver.scoreAVG() shouldBeExactly 0.0
            }

            describe(name="Varias calificaciones") {
                val trip2 = TripBuilder().driver(driver).passenger(client).setDate(yesterday).duration(10).build()
                val trip3 = TripBuilder().driver(driver).passenger(client).setDate(yesterday).duration(10).build()
                driver.trips.addAll(listOf(trip, trip2, trip3)) //Se hardcodea un viaje ya finalizado
                client.scoreTrip(trip=trip, message="Maso", scorePoints = 2)
                client.scoreTrip(trip=trip2, message="Maso", scorePoints = 2)
                client.scoreTrip(trip=trip3, message="Buenaso", scorePoints = 5)
                it("3 calificaciones, con 2,2 y 5 puntos. Score promedio 3") {
                    driver.scoreAVG() shouldBeExactly (2.0+2.0+5.0)/3
                }
                it("Las calificaciones del chofer son las recibidas en los viajes realizados") {
                    driver.getScores() shouldContainAll listOf(trip.score, trip2.score, trip3.score)
                }
            }

        }
    }

})

