package ar.edu.unsam.phm.uberto.model

interface User {
    var firstName:String
    var lastName:String
    var balance:Double
    val trips:MutableList<Trip>
    val img:String
    fun getScores():List<TripScore>
}