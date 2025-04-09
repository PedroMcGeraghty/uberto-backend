package ar.edu.unsam.phm.uberto.model

import ar.edu.unsam.phm.uberto.BalanceAmmountNotValidException
import ar.edu.unsam.phm.uberto.FriendAlreadyExistException
import ar.edu.unsam.phm.uberto.FriendNotExistException
import ar.edu.unsam.phm.uberto.InsufficientBalanceException
import ar.edu.unsam.phm.uberto.repository.AvaliableInstance
import exceptions.BusinessException

class Passenger(
    override var firstName: String = "",
    override var lastName: String = "",
    override var balance: Double = 0.0,
    override val trips: MutableList<Trip> = mutableListOf(),
    var cellphone: Int = 0,
    var age: Int = 0,
    override var id: Int = 0,
    override var img: String = "",
    val friends: MutableList<Passenger> = mutableListOf(),
    override var userId: Int = 0
) : User, AvaliableInstance {

    //var currentTrip:Trip? = null

    fun requestTrip(trip: Trip) {
        if (validateTrip(trip)) {
            throw InsufficientBalanceException()
        }
        this.addTrip(trip)
    }

    private fun validateTrip(trip: Trip): Boolean {
        return this.balance < trip.price()
    }

    private fun addTrip(trip: Trip) {
        this.payTrip(trip.price())
        this.trips.add(trip)
    }

    private fun payTrip(price: Double) {
        this.balance -= price
    }

    fun loadBalance(balance: Double) {
        if(balance <= 0){
            throw BalanceAmmountNotValidException()
        }
        this.balance += balance
    }

    override fun getScores(): List<TripScore> {
        return this.getScoredTrips().map { trip:Trip -> trip.score!! }
    }

    private fun getScoredTrips():List<Trip>{
        return this.trips.filter { it.score != null }
    }

    fun isFriendOf(passenger: Passenger) = this.friends.contains(passenger)

    fun addFriend(friend: Passenger) {
        if(this.isFriendOf(friend)){
            throw FriendAlreadyExistException()
        }
        friends.add(friend)
    }

    fun removeFriend(friend: Passenger) {
        if(!this.isFriendOf(friend)){
            throw FriendNotExistException()
        }
        friends.remove(friend)
    }

    fun pendingTrips() = trips.filter { trip:Trip ->trip.pendingTrip()}
    fun finishedTrips() = trips.filter { trip:Trip ->trip.finished()}

    fun scoreTrip(trip: Trip, message:String, scorePoints:Int){
        val score = TripScore(message=message, scorePoints = scorePoints)
        trip.addScore(score)
    }

}
