package ar.edu.unsam.phm.uberto

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

class BusinessException(msg: String) : Exception(msg)

const val invalidCredentialsMessage = "Incorrect Username or Password"
@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidCredentialsException(msg: String = invalidCredentialsMessage) : Exception(msg)

const val driverNotAvaliableMessage = "Driver not avaliable"
class DriverNotAvaliableException(msg: String = driverNotAvaliableMessage) : Exception(msg)

const val scoredTripMessage = "Trip already scored."
class ScoredTripException(msg: String = scoredTripMessage) : Exception(msg)

const val tripNotFinishedException = "Trip not finished."
class TripNotFinishedException(msg: String = tripNotFinishedException) : Exception(msg)

const val balanceAmmountNotValidMessage = "Balance must be positive."
class BalanceAmmountNotValidException(msg: String = balanceAmmountNotValidMessage) : Exception(msg)

const val insufficientBalanceMessage = "Insufficient balance."
class InsufficientBalanceException(msg: String = insufficientBalanceMessage) : Exception(msg)

const val friendAlreadyExistMessage = "Already friend of passenger."
class FriendAlreadyExistException(msg: String = friendAlreadyExistMessage) : Exception(msg)

const val friendNotExistMessage = "Not friend of passenger"
class FriendNotExistException(msg: String = friendNotExistMessage) : Exception(msg)

const val passengerNotFoundMessage = "Not passenger match for given ID"
@ResponseStatus(HttpStatus.NOT_FOUND)
class PassengerNotFoundException(msg: String = passengerNotFoundMessage) : Exception(msg)

const val noFriendsFoundMessage = "No passenger matching the given name or lastname"
@ResponseStatus(HttpStatus.NOT_FOUND)
class NoFriendsFoundException(msg: String = noFriendsFoundMessage) : Exception(msg)