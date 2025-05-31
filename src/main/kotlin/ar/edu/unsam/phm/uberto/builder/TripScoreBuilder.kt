package ar.edu.unsam.phm.uberto.builder

import ar.edu.unsam.phm.uberto.model.Passenger
import ar.edu.unsam.phm.uberto.model.TripScore
import java.time.LocalDate


class TripScoreBuilder(val newScore: TripScore = TripScore()) {

    fun message(str: String): TripScoreBuilder = apply {
        newScore.message = str
    }

    fun score(score: Int): TripScoreBuilder = apply {
        newScore.scorePoints = score
    }

    fun date(date: LocalDate): TripScoreBuilder = apply {
        newScore.date = date
    }
    fun build(): TripScore = newScore

}