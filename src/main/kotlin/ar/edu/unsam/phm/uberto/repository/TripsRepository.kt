package ar.edu.unsam.phm.uberto.repository

import ar.edu.unsam.phm.uberto.dto.DriverAvailableDto
import ar.edu.unsam.phm.uberto.model.Driver
import ar.edu.unsam.phm.uberto.model.Passenger
import ar.edu.unsam.phm.uberto.model.Trip
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

import javax.print.attribute.standard.Destination

interface TripsRepository : CrudRepository<Trip, Long> {

    @EntityGraph(attributePaths = ["client", "driver", "score"])
    fun findByClient(client: Passenger): List<Trip>

    @EntityGraph(attributePaths = ["client", "driver", "score"])
    fun findByDriver(driver: Driver): List<Trip>

    @Query(
        """
        SELECT t FROM Trip t 
        WHERE ( t.driver.id = :driverId)
        AND ( t.origin = :origin)
        AND ( t.destination = :destination)
        AND ( t.numberPassengers = :numberPassengers)
        AND (
        LOWER(t.client.firstName) LIKE LOWER(CONCAT('%', :name, '%')) 
        OR LOWER(t.client.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
    ) 
    """
    )
    @EntityGraph(attributePaths = ["driver", "client"])
    fun searchByForm(
        origin: String,
        destination: String,
        numberPassengers: Int,
        name: String,
        driverId: Long
    ): List<Trip>

    @Query("""
SELECT
    new ar.edu.unsam.phm.uberto.dto.DriverAvailableDto(
        d,
        COALESCE(AVG(ts.scorePoints), 0)
    )
FROM Driver d
LEFT JOIN Trip t ON t.driver.id = d.id
LEFT JOIN t.score ts
WHERE d.id NOT IN (
    SELECT t2.driver.id
    FROM Trip t2
    WHERE t2.date < :endDate AND t2.finishedDateTime > :date
)
GROUP BY d.id
""")
    fun getAvailable(
        @Param("date") date: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<DriverAvailableDto>

}

