package ar.edu.unsam.phm.uberto.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
class TripScore  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(length = 255)
    var message:String = ""

    @Column
    var scorePoints:Int = 0

    @Column
    var date:LocalDate = LocalDate.now()
}
