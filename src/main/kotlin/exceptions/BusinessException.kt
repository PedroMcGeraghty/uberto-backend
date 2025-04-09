package exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class BusinessException(msg: String) : Exception(msg)

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(msg: String) : Exception(msg)