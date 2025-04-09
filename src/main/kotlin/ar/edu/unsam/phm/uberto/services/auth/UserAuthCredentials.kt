package ar.edu.unsam.phm.uberto.services.auth

import ar.edu.unsam.phm.uberto.repository.AvaliableInstance

class UserAuthCredentials(
    var username:String,
    var password:String,
    var rol:String,
    override var id: Int = 0
):AvaliableInstance {}