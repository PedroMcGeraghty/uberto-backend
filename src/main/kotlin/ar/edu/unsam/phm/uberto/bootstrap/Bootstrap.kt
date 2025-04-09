package ar.edu.unsam.phm.uberto.bootstrap

import ar.edu.unsam.phm.uberto.builder.DriverBuilder
import ar.edu.unsam.phm.uberto.builder.PassengerBuilder
import ar.edu.unsam.phm.uberto.builder.TripBuilder
import ar.edu.unsam.phm.uberto.builder.TripScoreBuilder
import ar.edu.unsam.phm.uberto.model.*
import ar.edu.unsam.phm.uberto.repository.*
import ar.edu.unsam.phm.uberto.services.auth.UserAuthCredentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

@Component
class Bootstrap(
    @Autowired val passengerRepo: PassengerRepository,
    @Autowired val driverRepo: DriverRepository,
    @Autowired val tripRepo: TripsRepository,
    @Autowired val accountsRepo: AuthCredentialsRepository,
    @Autowired val tripScoreRepo: TripScoreRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        createAccounts()
        createPassengers()
        createDrivers()
        createTrips()
//        createTripScore()
    }

    private fun createAccounts(){
        val account01 = UserAuthCredentials(username="adrian", password="adrian", rol="passenger")
        val account02 = UserAuthCredentials(username="diego", password="diegoo", rol="passenger")
        val account03 = UserAuthCredentials(username="matias", password="matias", rol="passenger")
        val account04 = UserAuthCredentials(username="pedro", password="pedroo", rol="passenger")
        val account05 = UserAuthCredentials(username="valen", password="valenn", rol="passenger")
        val account06 = UserAuthCredentials(username="premium", password="premium", rol="driver")
        val account07 = UserAuthCredentials(username="simple", password="simple", rol="driver")
        val account08 = UserAuthCredentials(username="biker", password="biker", rol="driver")

        val accounts = listOf(account01, account02, account03, account04, account05,account06, account07, account08)

        accounts.forEach { account:UserAuthCredentials ->
            accountsRepo.create(account)
        }
    }
    private fun createPassengers() {
        val users = accountsRepo.instances.filter { it.rol == "passenger" }
        val names = listOf<String>("Adrian", "Diego", "Matias", "Pedro", "Valentin")
        val lastNames = listOf<String>("Perez", "Lentz", "Diaz", "Geragthy", "Pugliese")
        val ages = listOf<Int>(1,2,3,4,5)
        val imgenes = listOf<String>(
            "https://1.bp.blogspot.com/-mX40EP3h9w0/XuEMJ7e7TdI/AAAAAAAAAGw/ABJg-o2m1JEC2-UA22ouBtLBXdSkR8ZoQCLcBGAsYHQ/s1600/013d6285-1a23-41ae-8564-a954e04e60d9.jpg",
            "https://img.freepik.com/fotos-premium/avatar-digital-fisioterapeuta-inteligencia-artificial-generativa_934475-9204.jpg",
            "https://img.freepik.com/fotos-premium/persona-avatar-plana-fondo-rojo-personaje-dibujos-animados_1036693-6803.jpg",
            "https://img.freepik.com/fotos-premium/hombre-joven-sonriente-adam-avatar-3d-personas-vectoriales-ilustracion-personajes-estilo-minimalista-dibujos-animados_1029476-294679.jpg",
            "https://ar.images.search.yahoo.com/search/images;_ylt=AwrFNzpEaORnzvkjOTCt9Qt.?p=avatar+persona+html+img&fr=mcafee&imgf=face&fr2=p%3As%2Cv%3Ai#id=236&iurl=https%3A%2F%2Fimages.pexels.com%2Fphotos%2F220453%2Fpexels-photo-220453.jpeg%3Fcs%3Dsrgb%26dl%3Dpexels-pixabay-220453.jpg%26fm%3Djpg&action=click")
        val balances = listOf<Double>(1000000.0, 1000000.0, 1000000.0, 1000000.0, 1000000.0)
        val phones = listOf<Int>(1568568792,1235598763,1556876259,1235468975,1554876255)

        users.forEachIndexed { index:Int, user:UserAuthCredentials ->
            val passenger = PassengerBuilder()
                .userId(user.id)
                .firstName(names[index])
                .lastName(lastNames[index])
                .age(ages[index])
                .img(imgenes[index])
                .cellphone(phones[index])
                .balance(balances[index])
                .build()

            passengerRepo.create(passenger)
        }

    }

    private fun createDrivers() {
        val users = accountsRepo.instances.filter { it.rol == "driver" }
        val names = listOf<String>("Dominic", "Franco", "Nicky")
        val lastNames = listOf<String>("Toretto", "Colapinto", "Lauda")
        val balances = listOf<Double>(200.0, 5000.0, 10000.0)
        val driverType = listOf(PremiumDriver(), SimpleDriver(), BikeDriver())
        val brand = listOf("Fiat Uno", "Fiat Uno", "Gilera")
        val serial = listOf("FTG 879", "DEV 666", "AAA 123")
        val model = listOf(2013,1999, 2003)
        val img = listOf(
            "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcSxIABKumHIEfVtFkRWCdlA9qmyHCZyxV6-N7m_c1Xc4MOXv8s61ssMabL5Ny5mdcBpBYG21zMUqikXJ-6K0xK5n8jm58thk8-9MXSGA0w",
            "https://imgs.search.brave.com/govkyiYhkWlQIXB_rGkHY0bbnntpI5wjyJDdmJ3Oxfc/rs:fit:500:0:0:0/g:ce/aHR0cHM6Ly93d3cu/bGFuYWNpb24uY29t/LmFyL3Jlc2l6ZXIv/djIvc2UtdmlyYWxp/em8tdW5hLWZvdG8t/ZGUtZnJhbmNvLWNv/bGFwaW50by1jdWFu/ZG8tUzVIMkpKNEdM/RkVRUEZaTE5NVEE2/QU9NSzQuSlBHP2F1/dGg9ZmJjOWJiMjU3/NDY4ZTdmNDllNDU1/NzBlMTYzYzNmNDIz/ZTUwODZlOTA4ZGFh/ZDBhYzRkNWQ5ZDNl/N2E5ODgxOCZ3aWR0/aD00MjAmaGVpZ2h0/PTI4MCZxdWFsaXR5/PTcwJnNtYXJ0PXRy/dWU",
            "https://imgs.search.brave.com/ccyEUVl1Jj6vw63RrXeCqblIt0xyl5WfdLyQXdIH8jk/rs:fit:500:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5nZXR0eWltYWdl/cy5jb20vaWQvMTA4/OTI5OTY3Mi9lcy9m/b3RvL2JyYW5kcy1o/YXRjaC1lbmdsYW5k/LWF1c3RyaWFuLWYx/LXJhY2luZy1kcml2/ZXItbmlraS1sYXVk/YS1hdC1icmFuZHMt/aGF0Y2gtb24tanVs/eS0wMS0xOTc0LWlu/LmpwZz9zPTYxMng2/MTImdz0wJms9MjAm/Yz1GZ29lUEQ0ZFJE/c3YycFlQVVdDalpy/S0FfaTJjRlgzQmNV/UTN6eThEN2FJPQ"
        )

        users.forEachIndexed { index:Int, user:UserAuthCredentials ->
            val driver = DriverBuilder(driverType[index])
                .userId(user.id)
                .firstName(names[index])
                .lastName(lastNames[index])
                .balance(balances[index])
                .brand(brand[index])
                .serial(serial[index])
                .model(model[index])
                .img(img[index])
                .build()

            driverRepo.create(driver)
        }


    }

    private fun createTrips(){
        var passengers:List<Passenger> = passengerRepo.instances.toList()
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


        val drivers:List<Driver> = driverRepo.instances.toList()

        //8 viajes de cada tipo. Premium, simple, biker. 24 viajes
        //Cada usuario tiene que tener minimo 2 viajes.
        val toretto = driverRepo.instances.first{it.lastName == "Toretto"}
        val colapinto = driverRepo.instances.first{it.lastName == "Colapinto"}
        val lauda = driverRepo.instances.first{it.lastName == "Lauda"}

        val adrian = passengerRepo.instances.first{it.firstName == "Adrian"}
        val matias = passengerRepo.instances.first{it.firstName == "Matias"}
        val diego = passengerRepo.instances.first{it.firstName == "Diego"}
        val pedro = passengerRepo.instances.first{it.firstName == "Pedro"}
        val valentin = passengerRepo.instances.first{it.firstName == "Valentin"}

        val lastMonth = LocalDate.now().minus(1, ChronoUnit.MONTHS)
        val pastWeek = LocalDate.now().minus(1, ChronoUnit.WEEKS)
        val yesterday3 = LocalDate.now().minus(3, ChronoUnit.DAYS)
        val yesterday2 = LocalDate.now().minus(2, ChronoUnit.DAYS)
        val yesterday1 = LocalDate.now().minus(1, ChronoUnit.DAYS)
        val today = LocalDate.now()
        val tomorrow1 = LocalDate.now().plus(1, ChronoUnit.DAYS)
        val tomorrow2 = LocalDate.now().plus(1, ChronoUnit.DAYS)
        val tomorrow3 = LocalDate.now().plus(1, ChronoUnit.DAYS)
        val nextWeek = LocalDate.now().plus(1, ChronoUnit.WEEKS)
        val nextMonth = LocalDate.now().plus(1, ChronoUnit.MONTHS)

        val dawn = LocalTime.of(6,0, 0)
        val morning = LocalTime.of(9,0, 0)
        val noon = LocalTime.of(12,0, 0)
        val afternoon = LocalTime.of(15,0, 0)
        val sunset = LocalTime.of(17,0, 0)
        val dusk = LocalTime.of(19,0, 0)
        val night = LocalTime.of(21,0, 0)
        val midnight = LocalTime.of(0,0, 0)


        val tripAdrian01 = TripBuilder().passenger(adrian).driver(toretto)
            .setDate(LocalDateTime.of(lastMonth, morning).toString())
            .duration(durations[0]).origin(destination[0]).destination(origin[0]).passengerAmmount(passengersAmmounts[0]).build()
        val tripAdrian02 = TripBuilder().passenger(adrian).driver(toretto)
            .setDate(LocalDateTime.of(pastWeek, morning).toString())
            .duration(durations[1]).origin(destination[1]).destination(origin[1]).passengerAmmount(passengersAmmounts[1]).build()
        val tripAdrian03 = TripBuilder().passenger(adrian).driver(colapinto)
            .setDate(LocalDateTime.of(today, morning).toString())
            .duration(durations[2]).origin(destination[2]).destination(origin[2]).passengerAmmount(passengersAmmounts[2]).build()
        val tripAdrian04 = TripBuilder().passenger(adrian).driver(lauda)
            .setDate(LocalDateTime.of(tomorrow1, afternoon).toString())
            .duration(durations[3]).origin(destination[3]).destination(origin[3]).passengerAmmount(passengersAmmounts[3]).build()
        val tripAdrian05 = TripBuilder().passenger(adrian).driver(lauda)
            .setDate(LocalDateTime.of(tomorrow1, night).toString())
            .duration(durations[4]).origin(destination[4]).destination(origin[4]).passengerAmmount(passengersAmmounts[4]).build()

        val tripMatias01 = TripBuilder().passenger(matias).driver(toretto)
            .setDate(LocalDateTime.of(pastWeek, morning).toString())
            .duration(durations[5]).origin(destination[5]).destination(origin[5]).passengerAmmount(passengersAmmounts[5]).build()
        val tripMatias02 = TripBuilder().passenger(matias).driver(toretto)
            .setDate(LocalDateTime.of(today, morning).toString())
            .duration(durations[6]).origin(destination[6]).destination(origin[6]).passengerAmmount(passengersAmmounts[6]).build()
        val tripMatias03 = TripBuilder().passenger(matias).driver(toretto)
            .setDate(LocalDateTime.of(today, afternoon).toString())
            .duration(durations[7]).origin(destination[7]).destination(origin[7]).passengerAmmount(passengersAmmounts[7]).build()
        val tripMatias04 = TripBuilder().passenger(matias).driver(toretto)
            .setDate(LocalDateTime.of(today, sunset).toString())
            .duration(durations[8]).origin(destination[8]).destination(origin[8]).passengerAmmount(passengersAmmounts[8]).build()
        val tripMatias05 = TripBuilder().passenger(matias).driver(toretto)
            .setDate(LocalDateTime.of(today, night).toString())
            .duration(durations[9]).origin(destination[9]).destination(origin[9]).passengerAmmount(passengersAmmounts[9]).build()

        val tripDiego01 = TripBuilder().passenger(diego).driver(lauda)
            .setDate(LocalDateTime.of(lastMonth, afternoon).toString())
            .duration(durations[10]).origin(destination[10]).destination(origin[10]).passengerAmmount(passengersAmmounts[10]).build()
        val tripDiego02 = TripBuilder().passenger(diego).driver(lauda)
            .setDate(LocalDateTime.of(lastMonth, afternoon).toString())
            .duration(durations[11]).origin(destination[11]).destination(origin[11]).passengerAmmount(passengersAmmounts[11]).build()
        val tripDiego03 = TripBuilder().passenger(diego).driver(lauda)
            .setDate(LocalDateTime.of(lastMonth, afternoon).toString())
            .duration(durations[12]).origin(destination[12]).destination(origin[12]).passengerAmmount(passengersAmmounts[12]).build()
        val tripDiego04 = TripBuilder().passenger(diego).driver(lauda)
            .setDate(LocalDateTime.of(lastMonth, afternoon).toString())
            .duration(durations[13]).origin(destination[13]).destination(origin[13]).passengerAmmount(passengersAmmounts[13]).build()
        val tripDiego05 = TripBuilder().passenger(diego).driver(lauda)
            .setDate(LocalDateTime.of(tomorrow1, afternoon).toString())
            .duration(durations[14]).origin(destination[14]).destination(origin[14]).passengerAmmount(passengersAmmounts[14]).build()

        val tripPedro01 = TripBuilder().passenger(pedro).driver(colapinto)
            .setDate(LocalDateTime.of(lastMonth,dawn).toString())
            .duration(durations[15]).origin(destination[15]).destination(origin[15]).passengerAmmount(passengersAmmounts[15]).build()
        val tripPedro02 = TripBuilder().passenger(pedro).driver(colapinto)
            .setDate(LocalDateTime.of(lastMonth, morning).toString())
            .duration(durations[16]).origin(destination[16]).destination(origin[16]).passengerAmmount(passengersAmmounts[16]).build()
        val tripPedro03 = TripBuilder().passenger(pedro).driver(colapinto)
            .setDate(LocalDateTime.of(lastMonth, noon).toString())
            .duration(durations[17]).origin(destination[17]).destination(origin[17]).passengerAmmount(passengersAmmounts[17]).build()
        val tripPedro04 = TripBuilder().passenger(pedro).driver(colapinto)
            .setDate(LocalDateTime.of(lastMonth, afternoon).toString())
            .duration(durations[18]).origin(destination[18]).destination(origin[18]).passengerAmmount(passengersAmmounts[18]).build()
        val tripPedro05 = TripBuilder().passenger(pedro).driver(colapinto)
            .setDate(LocalDateTime.of(lastMonth, sunset).toString())
            .duration(durations[19]).origin(destination[19]).destination(origin[19]).passengerAmmount(passengersAmmounts[19]).build()

        val tripValentin01 = TripBuilder().passenger(valentin).driver(toretto)
            .setDate(LocalDateTime.of(tomorrow1, morning).toString())
            .duration(durations[20]).origin(destination[20]).destination(origin[20]).passengerAmmount(passengersAmmounts[20]).build()
        val tripValentin02 = TripBuilder().passenger(valentin).driver(toretto)
            .setDate(LocalDateTime.of(tomorrow1, sunset).toString())
            .duration(durations[21]).origin(destination[21]).destination(origin[21]).passengerAmmount(passengersAmmounts[21]).build()
        val tripValentin03 = TripBuilder().passenger(valentin).driver(toretto)
            .setDate(LocalDateTime.of(tomorrow1, night).toString())
            .duration(durations[22]).origin(destination[22]).destination(origin[22]).passengerAmmount(passengersAmmounts[22]).build()
        val tripValentin04 = TripBuilder().passenger(valentin).driver(toretto)
            .setDate(LocalDateTime.of(tomorrow2, morning).toString())
            .duration(durations[23]).origin(destination[23]).destination(origin[23]).passengerAmmount(passengersAmmounts[23]).build()
        val tripValentin05 = TripBuilder().passenger(valentin).driver(toretto)
            .setDate(LocalDateTime.of(tomorrow3, morning).toString())
            .duration(durations[24]).origin(destination[24]).destination(origin[24]).passengerAmmount(passengersAmmounts[24]).build()

        val allTrips:List<Trip> = listOf(
            tripAdrian01, tripAdrian02, tripAdrian03, tripAdrian04, tripAdrian05,
            tripMatias01, tripMatias02, tripMatias03, tripMatias04, tripMatias05,
            tripDiego01, tripDiego02, tripDiego03, tripDiego04, tripDiego05,
            tripPedro01, tripPedro02, tripPedro03, tripPedro04, tripPedro05,
            tripValentin01, tripValentin02, tripValentin03, tripValentin04, tripValentin05
        )

        allTrips.forEach {
            tripRepo.create(it)
            it.driver.addTrip(it)
            it.client.trips.add(it)
        }

    }


    private fun createTripScore(){
        var passengers:List<Passenger> = passengerRepo.instances.toList()
        val scoresPoints:List<Int> = listOf(3, 2, 1, 3, 4)
        val scoresMessages:List<String> = listOf("Excelente", "Bueno", "Malo", "Malisimo", "Nefasto")
        passengers.forEachIndexed { index:Int, passenger:Passenger ->
            val score = TripScoreBuilder()
                .score(scoresPoints[index])
                .date(LocalDate.now())
                .message(scoresMessages[index])
                .build()

            tripScoreRepo.create(score)
            passenger.trips[0].score = score
        }
    }
}