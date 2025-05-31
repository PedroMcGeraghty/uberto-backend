package ar.edu.unsam.phm.uberto.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import org.springframework.data.annotation.Id

@Document(collection = "analytics")
class Analytics (
    val driver : String,
    val passenger : String
) {

    @Id
    var id: ObjectId? = null

    val timestamp: LocalDateTime = LocalDateTime.now()




}