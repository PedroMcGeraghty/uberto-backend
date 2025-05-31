package ar.edu.unsam.phm.uberto.repository

import ar.edu.unsam.phm.uberto.model.Analytics
import org.springframework.data.mongodb.repository.MongoRepository

interface AnalyticsRepository : MongoRepository<Analytics, String> {
}