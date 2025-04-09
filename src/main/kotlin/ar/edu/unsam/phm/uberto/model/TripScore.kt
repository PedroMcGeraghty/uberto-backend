package ar.edu.unsam.phm.uberto.model

import ar.edu.unsam.phm.uberto.repository.AvaliableInstance
import java.time.LocalDate

data class TripScore(
    var message:String = "",
    var scorePoints:Int = 0,
    var date:LocalDate = LocalDate.now(),
//    var passenger: Passenger = Passenger(),
    override var id: Int=0
) : AvaliableInstance{}
