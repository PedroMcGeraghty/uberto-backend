package ar.edu.unsam.phm.uberto.dto

import ar.edu.unsam.phm.uberto.model.Analytics

class ClickDTO(val passenger: String, val driver: String) {
    fun fromDTO() : Analytics = Analytics(driver, passenger)
}