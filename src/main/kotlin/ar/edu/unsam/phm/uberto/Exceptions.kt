package ar.edu.unsam.phm.uberto

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

class FailSaveEntity(msg: String) : RuntimeException(msg)

class BusinessException(msg: String) : RuntimeException(msg)

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

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BalanceAmmountNotValidException(msg: String = balanceAmmountNotValidMessage) : RuntimeException(msg)

const val insufficientBalanceMessage = "Insufficient balance."

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InsufficientBalanceException(msg: String = insufficientBalanceMessage) : RuntimeException(msg)

const val friendAlreadyExistMessage = "Already friend of passenger."

@ResponseStatus(HttpStatus.BAD_REQUEST)
class FriendAlreadyExistException(msg: String = friendAlreadyExistMessage) : RuntimeException(msg)

const val friendNotExistMessage = "Not friend of passenger"

@ResponseStatus(HttpStatus.NOT_FOUND)
class FriendNotExistException(msg: String = friendNotExistMessage) : RuntimeException(msg)

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundEntityException(msg: String) : RuntimeException(msg)

const val passengerNotFoundMessage = "Not passenger match for given ID"

@ResponseStatus(HttpStatus.NOT_FOUND)
class PassengerNotFoundException(msg: String = passengerNotFoundMessage) : RuntimeException(msg)

const val noFriendsFoundMessage = "No passenger matching the given name or lastname"

@ResponseStatus(HttpStatus.NOT_FOUND)
class NoFriendsFoundException(msg: String = noFriendsFoundMessage) : Exception(msg)

const val isEmptyMessage = "the message cannot be empty"

@ResponseStatus(HttpStatus.BAD_REQUEST)
class IsEmptyException(msg: String = isEmptyMessage) : RuntimeException(msg)

const val incorrectValues = "Incorrect values"

@ResponseStatus(HttpStatus.BAD_REQUEST)
class IncorrectValuesException(msg: String = incorrectValues) : RuntimeException(msg)