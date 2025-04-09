package ar.edu.unsam.phm.uberto.repository

import ar.edu.unsam.phm.uberto.dto.FormTripDTO
import ar.edu.unsam.phm.uberto.model.*
import ar.edu.unsam.phm.uberto.services.auth.UserAuthCredentials
import exceptions.BusinessException
import org.springframework.stereotype.Component
import java.time.LocalDateTime


//Hace referencia a los objetos los cuales van a ser instancias en cada repositorio
//Esto es para no tener un repositorio de entidades no deseadas

//TODO Se necesita un metodo upsert??? la tiro no mas

interface AvaliableInstance {
    var id: Int

}

@Component
abstract class Repository<T : AvaliableInstance> {
    var instances: MutableSet<T> = mutableSetOf()

    fun create(objeto: T): Boolean {
        this.asignarID(objeto)
        return instances.add(objeto) //SI es Set devuelve true o false, si es list siempre devuelve true
    }

    fun delete(objeto: T) {
        exist(objeto.id)
        instances.removeIf { objeto.id == it.id }//
    }

    fun exist(id: Int) {
        if (!existeElemento(id)) {
            throw BusinessException("no se encuentra")
        }
    }

    private fun existeElemento(id: Int) = instances.any { it.id == id }

    fun update(objeto: T) {
        delete(objeto)
        instances.add(objeto)
    }

    fun getByID(objectID: Int): T {
        exist(objectID)
        return instances.first { it.id == objectID } //devuelve elemento o null
    }


    private fun asignarID(objeto: T) {
        val lastId = instances.maxOfOrNull { it.id }
        objeto.id = if(lastId != null) lastId + 1 else 1
    }

}

@Component
class DriverRepository(): Repository<Driver>() {

    fun searchByUserID(userID:Int): Driver?{
        return instances.find { it.userId == userID }
    }

    fun avaliable(date: LocalDateTime, time: Int): List<Driver> = instances.filter { it.avaliable(date, time) }

}
@Component
class PassengerRepository(): Repository<Passenger>() {

    fun searchByUserID(userID:Int): Passenger?{
        return instances.find { it.userId == userID}
    }

}

@Component
class TripsRepository(): Repository<Trip>() {

    fun searchByForm(form: FormTripDTO, driverId: Int): List<Trip>{
        val tripFromDriver = instances.filter { it.driver.userId == driverId }
        return tripFromDriver.filter{ trip ->
            (form.origin == trip.origin || form.origin == null || form.origin == "") &&
            (form.destination == trip.destination || form.destination == null || form.destination == "") &&
            (form.numberPassengers == trip.numberPassengers || form.numberPassengers == null) &&
            (form.name == trip.client.firstName || form.name == null || form.name == "")
        }
    }
}

@Component
class TripScoreRepository(): Repository<TripScore>() {
}

@Component
class AuthCredentialsRepository(): Repository<UserAuthCredentials>() {
}