package ar.edu.unsam.phm.uberto.services

import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
object TravelTimeMockService {

    fun getTime(): Map<String, Int> {
        val time: Map<String, Int> = mapOf("time" to Random.nextInt(1, 90))
        return time
    }
}