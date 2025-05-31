package ar.edu.unsam.phm.uberto.model

import ar.edu.unsam.phm.uberto.BusinessException
import ar.edu.unsam.phm.uberto.ScoredTripException
import ar.edu.unsam.phm.uberto.TripNotFinishedException
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.jvm.Transient

@Entity
class Trip(
){

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var duration: Int = 0

    @Column
    var numberPassengers: Int = 0

    @Column
    lateinit var date: LocalDateTime

    @Column(length = 40)
    var origin: String = ""

    @Column(length = 40)
    var destination: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", referencedColumnName = "id")
    var client: Passenger = Passenger()

    @Transient
    var driver: Driver = SimpleDriver()

    @Column(name = "driverMongoId")
    lateinit var driverId: String

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL],  orphanRemoval = true )
    @JoinColumn(name = "tripscore_id", referencedColumnName = "id")
    var score: TripScore? = null

    @Column(name= "end_date")
    var finishedDateTime: LocalDateTime = LocalDateTime.now()

    @Column(name = "price")
    var price: Double = 0.0


    fun addScore(newScore: TripScore){
        if(!this.finished()) throw TripNotFinishedException()
        if(this.scored()) throw ScoredTripException()
        this.score = newScore
    }

    fun deleteScore(passenger: Passenger){
        validateDeleteScore(passenger)
        score = null
    }

    private fun validateDeleteScore(passenger: Passenger) {
        if (!canDeleteScore(passenger.id!!)) { throw BusinessException("User has no ratings to delete") }
        if (!scored()){ throw BusinessException("The trip has no score") }
    }

    fun scored():Boolean = (this.score != null)

    fun canDeleteScore(userId: Long): Boolean {
        return userId == client.id
    }

    @PrePersist
    @PreUpdate
    fun calculatePrePersit() {
        finishedDateTime = finalizationDate()
        price = this.driver.fee(duration, numberPassengers)
    }

    fun finalizationDate() : LocalDateTime = date.plus(duration.toLong(), ChronoUnit.MINUTES)
    fun finished() : Boolean  {
        return finalizationDate() < LocalDateTime.now()
    }

    fun onlyTimeToStr(date: LocalDateTime): String {
        val time = date.toLocalTime()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return time.format(formatter).toString()
    }

}