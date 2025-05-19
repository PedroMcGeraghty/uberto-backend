package ar.edu.unsam.phm.uberto.repository

import ar.edu.unsam.phm.uberto.model.TripScore
import org.springframework.data.repository.CrudRepository

interface TripScoreRepository: CrudRepository<TripScore, Long> {
}