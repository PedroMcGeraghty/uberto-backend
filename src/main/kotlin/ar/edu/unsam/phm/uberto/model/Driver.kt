package ar.edu.unsam.phm.uberto.model

import ar.edu.unsam.phm.uberto.DriverNotAvaliableException
import ar.edu.unsam.phm.uberto.dto.DriverDTO
import ar.edu.unsam.phm.uberto.model.UserAuthCredentials
import exceptions.BusinessException
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Driver():User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    var credentials: UserAuthCredentials? = null

    @Column(length = 50)
    override lateinit var firstName: String

    @Column(length = 50)
    override lateinit var lastName: String

    @Column
    override var balance: Double = 0.0


    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    override var trips: MutableList<Trip> = mutableListOf()

    @Column(length = 255)
    override lateinit var img: String

    @Column
    var model:Int = 0

    @Column(length = 255)
    lateinit var brand:String

    @Column(length = 255)
    lateinit var serial:String

    @Column
    var basePrice:Double = 0.0

    override fun getScores(): List<TripScore> {
        return this.getScoredTrips().map{ it.score!! }
    }

    abstract fun plusBasePrice(time: Int, numberPassengers: Int):Double


    private fun getScoredTrips():List<Trip>{
        return this.trips.filter { it.score != null }
    }


    fun avaliable(tripDate: LocalDateTime, tripDuration: Int):Boolean{
        if(tripDate.isBefore(LocalDateTime.now())){
            throw BusinessException("The date must be later than the current date")
        }
        val finishedDate =  tripDate.plus(tripDuration.toLong(), ChronoUnit.MINUTES)
        return !this.tripOverlapping(tripDate, finishedDate) || trips.isEmpty()
    }

    fun tripOverlapping(tripStart: LocalDateTime, tripEnd: LocalDateTime):Boolean{
        return this.pendingTrips().any{ pending:Trip ->
            tripStart < pending.finalizationDate() && tripEnd > pending.date
        }
    }


    fun scoreAVG():Double{
        val avg = getScoredTrips().map { it.score!!.scorePoints }.average()
        return if(avg.isNaN()) 0.0 else avg
    }

    fun fee(time: Int, numberPassenger: Int):Double{
        return this.basePrice + this.plusBasePrice(time, numberPassenger) * time
    }

    fun addTrip(trip:Trip) {
        this.trips.add(trip)
        acceditTrip(trip.price())
    }

    fun acceditTrip(price: Double){
        this.balance += price
    }

    fun pendingTrips() = trips.filter { trip:Trip ->trip.pendingTrip()}
    fun finishedTrips() = trips.filter { trip:Trip ->trip.finished()}

    fun responseTrip(newTrip: Trip, time: Int) {
        if(!avaliable(newTrip.date, time)){
            throw DriverNotAvaliableException()
        }
        addTrip(newTrip)
    }

    fun update(dto: DriverDTO): Driver {
        this.firstName = dto.firstName
        this.lastName = dto.lastName
        this.serial = dto.serial
        this.brand = dto.brand
        this.model = dto.model
        this.basePrice = dto.price
        return this
    }

}