package ar.edu.unsam.phm.uberto.bootstrap

import ar.edu.unsam.phm.uberto.builder.DriverBuilder
import ar.edu.unsam.phm.uberto.builder.PassengerBuilder
import ar.edu.unsam.phm.uberto.builder.TripBuilder
import ar.edu.unsam.phm.uberto.dto.toTripDriverDTO
import ar.edu.unsam.phm.uberto.dto.toTripScoreDTOMongo
import ar.edu.unsam.phm.uberto.factory.AuthFactory
import ar.edu.unsam.phm.uberto.factory.TestFactory
import ar.edu.unsam.phm.uberto.model.*
import ar.edu.unsam.phm.uberto.repository.*
import ar.edu.unsam.phm.uberto.security.TokenJwtUtil
import ar.edu.unsam.phm.uberto.services.AuthService
import ar.edu.unsam.phm.uberto.services.DriverService
import ar.edu.unsam.phm.uberto.services.PassengerService
import ar.edu.unsam.phm.uberto.services.TripService
import com.fasterxml.jackson.core.util.DefaultIndenter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class Bootstrap(
    @Autowired val passengerRepo: PassengerRepository,
    @Autowired val tripRepo: TripsRepository,
    @Autowired val tripService: TripService,
    @Autowired val authRepo: AuthRepository,
    @Autowired val tripScoreRepo: TripScoreRepository,
    @Autowired val passwordEncoder: PasswordEncoder,
    @Autowired val authService: AuthService,
    @Autowired val jwtUtil: TokenJwtUtil,
    @Autowired val driverService: DriverService,
    @Autowired val passengerService: PassengerService,
    @Autowired val mongoRepoDriver: MongoDriverRepository,
    @Autowired val analyticsRepository: AnalyticsRepository

) : CommandLineRunner {

    val factory = TestFactory(authService, passengerService, driverService ,jwtUtil)
    override fun run(vararg args: String?) {

        createAccounts()
        createPassengers()
        createDrivers()
        createTrips()
//        createTripScore()
    }

    private fun createAccounts() {
        val authFactory: AuthFactory = AuthFactory()

        val account01 = authFactory.createAccount(username = "adrian", password = passwordEncoder.encode("adrian"), role = Role.PASSENGER)
        val account02 = authFactory.createAccount(username = "diego", password = passwordEncoder.encode("diegoo"), role = Role.PASSENGER)
        val account03 = authFactory.createAccount(username = "matias", password = passwordEncoder.encode("matias"), role = Role.PASSENGER)
        val account04 = authFactory.createAccount(username = "pedro", password = passwordEncoder.encode("pedroo"), role = Role.PASSENGER)
        val account05 = authFactory.createAccount(username = "valen", password = passwordEncoder.encode("valenn"), role = Role.PASSENGER)
        val account06 = authFactory.createAccount(username = "premium", password = passwordEncoder.encode("premium"), role = Role.DRIVER)
        val account07 = authFactory.createAccount(username = "simple", password = passwordEncoder.encode("simple"), role = Role.DRIVER)
        val account08 = authFactory.createAccount(username = "biker", password = passwordEncoder.encode("biker"), role = Role.DRIVER)
        val account09 = authFactory.createAccount(username = "mandarina", password = passwordEncoder.encode("mandarina"), role = Role.DRIVER)

        val accounts = listOf(account01, account02, account03, account04, account05, account06, account07, account08, account09)

        authRepo.saveAll(accounts)
    }

    private fun createPassengers() {
        val passengerList = mutableListOf<Passenger>()

        val users = authRepo.findByRole(Role.PASSENGER)
        val names = listOf<String>("Adrian", "Diego", "Matias", "Pedro", "Valentin")
        val lastNames = listOf<String>("Perez", "Lentz", "Diaz", "Geragthy", "Pugliese")
        val ages = listOf<LocalDate>(
            LocalDate.now(),
            LocalDate.of(1990, 1, 1),
            LocalDate.of(1889, 12, 31),
            LocalDate.of(1995, 10, 11),
            LocalDate.of(1999, 11, 15)
        )
        val imgenes = listOf<String>("https://res.cloudinary.com/dumcjdzxo/image/upload/adrian_cdouit.jpg",
            "https://res.cloudinary.com/dumcjdzxo/image/upload/diego_uyhcwb.jpg",
            "https://res.cloudinary.com/dumcjdzxo/image/upload/matias_tclwsz.jpg",
            "https://res.cloudinary.com/dumcjdzxo/image/upload/perdo1_jfmu6o.jpg",
            "https://res.cloudinary.com/dumcjdzxo/image/upload/valen_ilptyh.jpg")

        val balances = listOf<Double>(1000000.0, 1000000.0, 1000000.0, 1000000.0, 1.0)
        val phones = listOf<Int>(1568568792, 1235598763, 1556876259, 1235468975, 1554876255)

        users.forEachIndexed { index: Int, user: UserAuthCredentials ->
            val passenger = PassengerBuilder()
                .userId(user.id!!)
                .firstName(names[index])
                .lastName(lastNames[index])
                .birthDate(ages[index])
                .img(imgenes[index])
                .cellphone(phones[index])
                .balance(balances[index])
                .build()

            passengerList.add(passenger)

        }
        passengerRepo.saveAll(passengerList)
    }
    private fun createDrivers() {
        if(mongoRepoDriver.count() != 0.toLong()){
            mongoRepoDriver.deleteAll()
        }
        val driverList = mutableListOf<Driver>()
        val users = authRepo.findByRole(Role.DRIVER)
        val names = listOf<String>("Dominic", "Franco", "Nicky", "Chano")
        val lastNames = listOf<String>("Toretto", "Colapinto", "Lauda", "Charpentier")
        val balances = listOf<Double>(200.0, 5000.0, 10000.0, 500.0)
        val driverType = listOf(PremiumDriver(), SimpleDriver(), BikeDriver(), SimpleDriver())
        val brand = listOf("Fiat Uno", "Fiat Uno", "Gilera", "Falcon")
        val serial = listOf("FTG 879", "DEV 666", "AAA 123", "GIL 123")
        val model = listOf(2013, 1999, 2003, 1998)
        val img = listOf("https://res.cloudinary.com/dumcjdzxo/image/upload/toreto_wx2me4.jpg",
            "https://res.cloudinary.com/dumcjdzxo/image/upload/colapinto_bihvlt.jpg",
            "https://res.cloudinary.com/dumcjdzxo/image/upload/laudo_hmkucz.jpg",
            "https://s.yimg.com/ny/api/res/1.2/pDQ.2O97G7kjKifAaDEIRg--/YXBwaWQ9aGlnaGxhbmRlcjt3PTY0MDtoPTM2MQ--/https://media.zenfs.com/en/homerun/feed_manager_auto_publish_494/8346eca0608b3a3c423caf07cef0486a")
        val baseP = listOf(1000.0, 700.0, 4500.0,150.0)

        users.forEachIndexed { index: Int, user: UserAuthCredentials ->
            val driver = DriverBuilder(driverType[index])
                .firstName(names[index])
                .lastName(lastNames[index])
                .userId(user.id!!)
                .balance(balances[index])
                .brand(brand[index])
                .basePrice(baseP[index])
                .serial(serial[index])
                .model(model[index])
                .img(img[index])

                .build()
            driverList.add(driver)
        }
        mongoRepoDriver.saveAll(driverList)
    }

    private fun createTrips() {
        var passengers: List<Passenger> = passengerRepo.findAll().toList()
        val passengersAmmounts: List<Int> = listOf(
            2, 1, 3, 4, 1, 2, 3, 4, 1, 1,
            3, 2, 4, 1, 2, 3, 4, 1, 3, 2,
            4, 1, 2, 3, 4
        )

        val durations: List<Int> = listOf(
            10, 25, 40, 15, 20, 25, 10, 40, 15, 20,
            10, 40, 25, 15, 20, 10, 25, 40, 15, 20,
            10, 25, 40, 15, 20
        )

        val origin: List<String> = listOf(
            "Av. Corrientes 3500",
            "Av. Santa Fe 1800",
            "Av. 9 de Julio 500",
            "Av. Rivadavia 6000",
            "Av. Belgrano 1500",
            "Calle Defensa 800",
            "Av. Libertador 5000",
            "Av. Pueyrredón 1200",
            "Av. Córdoba 3500",
            "Calle Suipacha 700",
            "Av. Callao 900",
            "Calle Bolivar 400",
            "Av. de Mayo 1400",
            "Av. San Juan 2500",
            "Av. Las Heras 2200",
            "Av. Juan B. Justo 4500",
            "Calle Esmeralda 300",
            "Av. Independencia 2200",
            "Calle Moreno 800",
            "Av. Medrano 900",
            "Calle Tacuarí 600",
            "Av. Ángel Gallardo 1000",
            "Calle Ayacucho 1500",
            "Av. Directorio 4300",
            "Calle Junín 900"
        )

        val destination: List<String> = listOf(
            "Av. Cabildo 2500",
            "Av. Scalabrini Ortiz 1800",
            "Av. Juan de Garay 1200",
            "Av. Montes de Oca 900",
            "Av. San Martín 2900",
            "Calle Lavalle 700",
            "Av. Alem 1200",
            "Av. Acoyte 900",
            "Calle Florida 500",
            "Av. Juramento 2200",
            "Av. Congreso 3000",
            "Av. Entre Ríos 1200",
            "Av. Pedro Goyena 800",
            "Av. Boedo 1200",
            "Av. Triunvirato 3600",
            "Calle Carlos Pellegrini 1100",
            "Av. Gaona 2500",
            "Calle Rodríguez Peña 700",
            "Calle Perú 400",
            "Av. Pavón 2100",
            "Av. Sáenz 1100",
            "Calle Yerbal 1900",
            "Av. Almirante Brown 1300",
            "Av. Vélez Sarsfield 800",
            "Av. Olazábal 3100"
        )


        val drivers: List<Driver> = mongoRepoDriver.findAll().toList()

        //8 viajes de cada tipo. Premium, simple, biker. 24 viajes
        //Cada usuario tiene que tener minimo 2 viajes.
        val toretto = drivers.first { it.lastName == "Toretto" }
        val colapinto = drivers.first { it.lastName == "Colapinto" }
        val lauda = drivers.first { it.lastName == "Lauda" }
        val chano = drivers.first{it.lastName == "Charpentier"}

        val adrian = passengers.first { it.firstName == "Adrian" }
        val matias = passengers.first { it.firstName == "Matias" }
        val diego = passengers.first { it.firstName == "Diego" }
        val pedro = passengers.first { it.firstName == "Pedro" }
        val valentin = passengers.first { it.firstName == "Valentin" }

        val pastDates = listOf(
            LocalDateTime.now().minusDays(1).plusHours(3).plusMinutes(10).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().minusDays(2).plusHours(5).plusMinutes(15).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().minusDays(1).plusHours(7).plusMinutes(5).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().minusDays(2).plusHours(2).plusMinutes(20).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().minusDays(1).plusHours(4).plusMinutes(45).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().minusDays(2).plusHours(1).plusMinutes(35).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().minusDays(1).plusHours(6).plusMinutes(50).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().minusDays(2).plusHours(5).plusMinutes(40).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().minusDays(1).plusHours(7).plusMinutes(50).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().minusDays(1).plusHours(3).plusMinutes(10).truncatedTo(ChronoUnit.SECONDS),
        )

        val futureDates = listOf(
            LocalDateTime.now().plusDays(1).withHour(10).withMinute(30).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(3).withHour(9).withMinute(15).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(4).withHour(16).withMinute(45).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(5).withHour(11).withMinute(20).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(6).withHour(13).withMinute(10).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(7).withHour(15).withMinute(50).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(10).withHour(12).withMinute(5).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(12).withHour(18).withMinute(25).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(14).withHour(10).withMinute(0).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(16).withHour(9).withMinute(45).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(20).withHour(17).withMinute(30).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(25).withHour(11).withMinute(15).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(30).withHour(14).withMinute(40).truncatedTo(ChronoUnit.SECONDS),
            LocalDateTime.now().plusDays(35).withHour(13).withMinute(55).truncatedTo(ChronoUnit.SECONDS))



//        trips realizados
        val tripAdrian01 = TripBuilder().passenger(adrian).driver(toretto)
            .setDate(pastDates[0].toString())
            .duration(durations[0]).origin(destination[0]).destination(origin[0])
            .passengerAmmount(passengersAmmounts[0]).build()
        val tripAdrian02 = TripBuilder().passenger(adrian).driver(lauda)
            .setDate(pastDates[1].toString())
            .duration(durations[1]).origin(destination[1]).destination(origin[1])
            .passengerAmmount(passengersAmmounts[1]).build()
        val tripMatias01 = TripBuilder().passenger(matias).driver(lauda)
            .setDate(pastDates[2].toString())
            .duration(durations[5]).origin(destination[5]).destination(origin[5])
            .passengerAmmount(passengersAmmounts[5]).build()

        val tripMatias02 = TripBuilder().passenger(matias).driver(colapinto)
            .setDate(pastDates[3].toString())
            .duration(durations[6]).origin(destination[6]).destination(origin[6])
            .passengerAmmount(passengersAmmounts[6]).build()


        val tripDiego01 = TripBuilder().passenger(diego).driver(colapinto)
            .setDate(pastDates[4].toString())
            .duration(durations[10]).origin(destination[10]).destination(origin[10])
            .passengerAmmount(passengersAmmounts[10]).build()
        val tripDiego02 = TripBuilder().passenger(diego).driver(toretto)
            .setDate(pastDates[5].toString())
            .duration(durations[11]).origin(destination[11]).destination(origin[11])
            .passengerAmmount(passengersAmmounts[11]).build()

        val tripPedro01 = TripBuilder().passenger(pedro).driver(colapinto)
            .setDate(pastDates[6].toString())
            .duration(durations[15]).origin(destination[15]).destination(origin[15])
            .passengerAmmount(passengersAmmounts[15]).build()
        val tripPedro02 = TripBuilder().passenger(pedro).driver(colapinto)
            .setDate(pastDates[7].toString())
            .duration(durations[16]).origin(destination[16]).destination(origin[16])
            .passengerAmmount(passengersAmmounts[16]).build()

        val tripValentin01 = TripBuilder().passenger(valentin).driver(lauda)
            .setDate(pastDates[8].toString())
            .duration(durations[20]).origin(destination[20]).destination(origin[20])
            .passengerAmmount(passengersAmmounts[20]).build()
        val tripValentin02 = TripBuilder().passenger(valentin).driver(toretto)
            .setDate(pastDates[9].toString())
            .duration(durations[21]).origin(destination[21]).destination(origin[21])
            .passengerAmmount(passengersAmmounts[21]).build()

        //trips pendientes

        val tripAdrian03 = TripBuilder().passenger(adrian).driver(colapinto)
            .setDate(futureDates[0].toString())
            .duration(durations[2]).origin(destination[2]).destination(origin[2])
            .passengerAmmount(passengersAmmounts[2]).build()
        val tripAdrian04 = TripBuilder().passenger(adrian).driver(lauda)
            .setDate(futureDates[1].toString())
            .duration(durations[3]).origin(destination[3]).destination(origin[3])
            .passengerAmmount(passengersAmmounts[3]).build()
        val tripAdrian05 = TripBuilder().passenger(adrian).driver(lauda)
            .setDate(futureDates[2].toString())
            .duration(durations[4]).origin(destination[4]).destination(origin[4])
            .passengerAmmount(passengersAmmounts[4]).build()


        val tripMatias03 = TripBuilder().passenger(matias).driver(toretto)
            .setDate(futureDates[3].toString())
            .duration(durations[7]).origin(destination[7]).destination(origin[7])
            .passengerAmmount(passengersAmmounts[7]).build()
        val tripMatias04 = TripBuilder().passenger(matias).driver(toretto)
            .setDate(futureDates[4].toString())
            .duration(durations[8]).origin(destination[8]).destination(origin[8])
            .passengerAmmount(passengersAmmounts[8]).build()
        val tripMatias05 = TripBuilder().passenger(matias).driver(toretto)
            .setDate(futureDates[5].toString())
            .duration(durations[9]).origin(destination[9]).destination(origin[9])
            .passengerAmmount(passengersAmmounts[9]).build()


        val tripDiego03 = TripBuilder().passenger(diego).driver(lauda)
            .setDate(futureDates[6].toString())
            .duration(durations[12]).origin(destination[12]).destination(origin[12])
            .passengerAmmount(passengersAmmounts[12]).build()
        val tripDiego04 = TripBuilder().passenger(diego).driver(lauda)
            .setDate(futureDates[7].toString())
            .duration(durations[13]).origin(destination[13]).destination(origin[13])
            .passengerAmmount(passengersAmmounts[13]).build()
        val tripDiego05 = TripBuilder().passenger(diego).driver(lauda)
            .setDate(futureDates[8].toString())
            .duration(durations[14]).origin(destination[14]).destination(origin[14])
            .passengerAmmount(passengersAmmounts[14]).build()


        val tripPedro03 = TripBuilder().passenger(pedro).driver(colapinto)
            .setDate(futureDates[9].toString())
            .duration(durations[17]).origin(destination[17]).destination(origin[17])
            .passengerAmmount(passengersAmmounts[17]).build()
        val tripPedro04 = TripBuilder().passenger(pedro).driver(colapinto)
            .setDate(futureDates[10].toString())
            .duration(durations[18]).origin(destination[18]).destination(origin[18])
            .passengerAmmount(passengersAmmounts[18]).build()
        val tripPedro05 = TripBuilder().passenger(pedro).driver(colapinto)
            .setDate(futureDates[11].toString())
            .duration(durations[19]).origin(destination[19]).destination(origin[19])
            .passengerAmmount(passengersAmmounts[19]).build()


        val tripValentin03 = TripBuilder().passenger(valentin).driver(toretto)
            .setDate(futureDates[12].toString())
            .duration(durations[22]).origin(destination[22]).destination(origin[22])
            .passengerAmmount(passengersAmmounts[22]).build()
        val tripValentin04 = TripBuilder().passenger(valentin).driver(toretto)
            .setDate(futureDates[13].toString())
            .duration(durations[23]).origin(destination[23]).destination(origin[23])
            .passengerAmmount(passengersAmmounts[23]).build()
        val tripValentin05 = TripBuilder().passenger(valentin).driver(toretto)
            .setDate(futureDates[14].toString())
            .duration(durations[24]).origin(destination[24]).destination(origin[24])
            .passengerAmmount(passengersAmmounts[24]).build()

        var allTrips = tripRepo.saveAll(listOf(
            tripAdrian01, tripAdrian02, tripAdrian03, tripAdrian04, tripAdrian05,
            tripMatias01, tripMatias02, tripMatias03, tripMatias04, tripMatias05,
            tripDiego01, tripDiego02, tripDiego03, tripDiego04, tripDiego05,
            tripPedro01, tripPedro02, tripPedro03, tripPedro04, tripPedro05,
            tripValentin01, tripValentin02, tripValentin03, tripValentin04, tripValentin05
        ))

        allTrips.filter{ it.finished() }.forEach {
            it.score = TripScore().apply {
                message = "Este esta copado"
                scorePoints = 5
            }
        }
        tripRepo.saveAll(allTrips)
        allTrips = tripRepo.findAll()

//        }
        allTrips.filter{ it.driverId == colapinto.id }.forEach {
            if(it.score != null) colapinto.tripsScoreDTO.add(it.toTripScoreDTOMongo())
            colapinto.tripsDTO.add(it.toTripDriverDTO())
        }

        allTrips.filter{ it.driverId == lauda.id }.forEach {
            if(it.score != null) lauda.tripsScoreDTO.add(it.toTripScoreDTOMongo())
            lauda.tripsDTO.add(it.toTripDriverDTO())
        }

        allTrips.filter{ it.driverId == toretto.id }.forEach {
            if(it.score != null) toretto.tripsScoreDTO.add(it.toTripScoreDTOMongo())
            toretto.tripsDTO.add(it.toTripDriverDTO())
        }



        //Para la query 5
        val finishedDate = LocalDateTime.now().minusDays(4).plusHours(3).plusMinutes(10).truncatedTo(ChronoUnit.SECONDS)
        val finishedDate2 = LocalDateTime.now().minusDays(4).plusHours(3).plusMinutes(10).truncatedTo(ChronoUnit.SECONDS)

        val tripValentinMock = TripBuilder().passenger(valentin).driver(chano)
            .setDate(finishedDate.toString())
            .duration(durations[21]).origin(destination[21]).destination(origin[21])
            .passengerAmmount(passengersAmmounts[21]).build()
        val tripValentinMock2 = TripBuilder().passenger(valentin).driver(chano)
            .setDate(finishedDate2.toString())
            .duration(durations[21]).origin(destination[21]).destination(origin[21])
            .passengerAmmount(passengersAmmounts[21]).build()
        val trip1 = tripRepo.save(tripValentinMock)
        val trip2 = tripRepo.save(tripValentinMock2)
        chano.tripsDTO.add(trip1.toTripDriverDTO())
        chano.tripsDTO.add(trip2.toTripDriverDTO())
        //Para la query 5
        mongoRepoDriver.saveAll(drivers)

    }

    private fun createTripScore(listaTrip : MutableList<Trip>) : List<Trip> {
        listaTrip.filter { it ->  it.finished() }.forEach { factory.createTripScore(it) }
        return tripRepo.saveAll(listaTrip).toList()

    }

}