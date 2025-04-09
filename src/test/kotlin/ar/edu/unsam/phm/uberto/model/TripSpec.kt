package ar.edu.unsam.phm.uberto.model
//
import ar.edu.unsam.phm.uberto.BalanceAmmountNotValidException
import ar.edu.unsam.phm.uberto.ScoredTripException
import ar.edu.unsam.phm.uberto.TripNotFinishedException

import ar.edu.unsam.phm.uberto.builder.TripBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


class TripSpec: DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    var trip: Trip = TripBuilder().build()
    describe(name="Given a trip"){

        it(name="Start date") {
            val date:LocalDateTime = LocalDateTime.now()
            var trip: Trip = TripBuilder().setDate(date.toString()).build()
            trip.date shouldBeEqual date
        }

        it(name="Finalization date is start date plus duration") {
            val date:LocalDateTime = LocalDateTime.now()
            val duration:Int = 10
            trip.duration = duration
            trip.date = date
            trip.finalizationDate() shouldBeEqual date.plus(duration.toLong(), ChronoUnit.MINUTES)
        }

        describe(name="State") {
            it(name="Finished if finalization date is before now-instant"){
                val today:LocalDateTime = LocalDateTime.now()
                val yesterday:LocalDateTime = today.minus(1, ChronoUnit.DAYS)
                trip.date = yesterday
                trip.date shouldBeBefore today
                trip.finished() shouldBeEqual true
            }

            it(name="Not Finished if finalization date is after now-instant"){
                val today:LocalDateTime = LocalDateTime.now()
                val tomorrow:LocalDateTime = today.plus(1, ChronoUnit.DAYS)
                trip.date = tomorrow
                trip.date shouldBeAfter today
                trip.finished() shouldBeEqual false
            }
        }

        describe(name="Score") {
            it(name="No score by default"){
                trip.score shouldBe null
                trip.scored() shouldBe false
            }

            it(name="Can be scored if is finished"){
                val today:LocalDateTime = LocalDateTime.now()
                val yesterday:LocalDateTime = today.minus(1, ChronoUnit.DAYS)
                trip.date = yesterday
                trip.finished() shouldBeEqual true
                val score:TripScore = TripScore(message = "Score message", scorePoints = 5)
                trip.scored() shouldBeEqual false
                trip.addScore(score)
                trip.scored() shouldBeEqual true
            }

            it(name="Cannot be scored if is not finished"){
                val today:LocalDateTime = LocalDateTime.now()
                val tomorrow:LocalDateTime = today.plus(1, ChronoUnit.DAYS)
                trip.date = tomorrow
                trip.finished() shouldBeEqual false
                val score:TripScore = TripScore(message = "Score message", scorePoints = 5)
                shouldThrow<TripNotFinishedException> {
                    trip.addScore(score)
                }
            }

            it(name="Cannot be scored if is already scored"){
                val today:LocalDateTime = LocalDateTime.now()
                val yesterday:LocalDateTime = today.minus(1, ChronoUnit.DAYS)
                trip.date = yesterday
                trip.finished() shouldBeEqual true
                val score:TripScore = TripScore(message = "Score message", scorePoints = 5)
                trip.addScore(score)
                shouldThrow<ScoredTripException> {
                    trip.addScore(score)
                }
            }

            it(name="Score can be deleted"){
                val today:LocalDateTime = LocalDateTime.now()
                val yesterday:LocalDateTime = today.minus(1, ChronoUnit.DAYS)
                trip.date = yesterday
                trip.finished() shouldBeEqual true
                val score:TripScore = TripScore(message = "Score message", scorePoints = 5)

                trip.scored() shouldBeEqual false
                trip.addScore(score)
                trip.scored() shouldBeEqual true
                trip.deleteScore()
                trip.scored() shouldBeEqual false
            }
        }

    }
})