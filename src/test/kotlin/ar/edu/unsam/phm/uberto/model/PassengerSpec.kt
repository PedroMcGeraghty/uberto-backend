package ar.edu.unsam.phm.uberto.model

import ar.edu.unsam.phm.uberto.*
import ar.edu.unsam.phm.uberto.builder.PassengerBuilder
import ar.edu.unsam.phm.uberto.builder.TripBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class PassengerSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    val passenger = PassengerBuilder().build()

    describe(name = "Given a passsenger") {

        describe(name="Balance") {
            it(name = "Initial balance of 0.0") {
                passenger.balance shouldBeExactly 0.0
            }

            it(name="Adding balance"){
                val balance:Double = 5000.0
                passenger.loadBalance(balance)
                passenger.balance shouldBeExactly balance
            }

            describe(name="Invalid balance"){
                it(name="Adds 0.0 balance"){
                    val balance:Double = 0.0
                    shouldThrow<BalanceAmmountNotValidException> {
                        passenger.loadBalance(balance)
                    }
                }

                it(name="Adds negative balance"){
                    val balance:Double = -0.5
                    shouldThrow<BalanceAmmountNotValidException> {
                        passenger.loadBalance(balance)
                    }
                }
            }
        }

        describe(name="Friends") {
            val friend:Passenger = PassengerBuilder().build()
            describe(name="Adding friends"){
                it(name="Initially not friends, added succesfully"){
                    passenger.isFriendOf(friend) shouldBe false
                    passenger.addFriend(friend)
                    passenger.isFriendOf(friend) shouldBe true
                }

                it(name="Already friends, cannot be added"){
                    passenger.addFriend(friend)
                    passenger.isFriendOf(friend) shouldBe true
                    shouldThrow<FriendAlreadyExistException> {
                        passenger.addFriend(friend)
                    }
                }
            }

            describe(name="Deleting friends"){
                it(name="Succesfully deleted"){
                    passenger.addFriend(friend)
                    passenger.isFriendOf(friend) shouldBe true
                    passenger.removeFriend(friend)
                    passenger.isFriendOf(friend) shouldBe false
                }

                it(name="Cannot deleted a non-friend"){
                    passenger.isFriendOf(friend) shouldBe false
                    shouldThrow<FriendNotExistException> {
                        passenger.removeFriend(friend)
                    }
                }
            }
        }

        describe(name="Can book trips") {
            val driver:SimpleDriver = SimpleDriver()
            driver.basePrice = 10.0
            val trip:Trip = TripBuilder().duration(10).passenger(passenger).driver(driver).build()
            it(name="Succesfully booked. Expends balance"){
                val balance:Double = 1000000.0
                passenger.loadBalance(balance)
                passenger.requestTrip(trip)
                passenger.balance shouldBeExactly (balance - trip.price())
            }

            it(name="Insufficient balance"){
                shouldThrow<InsufficientBalanceException> {
                    passenger.requestTrip(trip)
                }
            }

        }

        describe(name="Can score trips") {
            val driver:SimpleDriver = SimpleDriver()
            driver.basePrice = 10.0
            val balance:Double = 1000000.0
            passenger.loadBalance(balance)
            it(name="Can score if trip is finished"){
                val yesterday: LocalDateTime = LocalDateTime.now().minus(1, ChronoUnit.DAYS)
                val trip:Trip = TripBuilder().duration(10).passenger(passenger).driver(driver).setDate(yesterday.toString()).build()
                passenger.requestTrip(trip)
                trip.finished() shouldBe true
                passenger.scoreTrip(trip, message = "Score message", scorePoints = 5)
            }

            it(name="Trip not finished, cannot score"){
                val tomorrow: LocalDateTime = LocalDateTime.now().plus(1, ChronoUnit.DAYS)
                val trip:Trip = TripBuilder().duration(10).passenger(passenger).driver(driver).setDate(tomorrow.toString()).build()
                passenger.requestTrip(trip)
                trip.finished() shouldBe false
                shouldThrow<TripNotFinishedException> {
                    passenger.scoreTrip(trip, message = "Score message", scorePoints = 5)
                }
            }

        }

    }
})
