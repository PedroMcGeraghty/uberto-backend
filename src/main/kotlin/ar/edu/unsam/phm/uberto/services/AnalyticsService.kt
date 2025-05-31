package ar.edu.unsam.phm.uberto.services

import ar.edu.unsam.phm.uberto.dto.ClickDTO
import ar.edu.unsam.phm.uberto.repository.AnalyticsRepository
import org.springframework.stereotype.Service

@Service
class AnalyticsService(val analyticsRepository: AnalyticsRepository) {

    fun logClick(click: ClickDTO) {
        analyticsRepository.save(click.fromDTO())
    }
}