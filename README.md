# 🚘 Uberto

**Uberto** es una aplicación de transporte urbano desarrollada como proyecto full-stack, orientada a resolver problemas reales de escalabilidad, almacenamiento heterogéneo y performance. A lo largo del desarrollo, se tomó una decisión clave: adoptar una arquitectura poliglota, eligiendo diferentes motores de persistencia para diferentes partes del dominio, según sus necesidades específicas.

## 🧠 Motivación detrás de la base de datos poliglota

No todas las entidades del sistema tienen las mismas características ni necesidades. Se analizaron los patrones de uso y acceso para cada conjunto de datos, y se adoptaron motores especializados:

---

## 🧠 Tecnologías principales

### 📱 Frontend

* React (SPA - mobile first).
* **Link al repositorio Frontend:** [https://github.com/PedroMcGeraghty/uberto-frontend](https://github.com/PedroMcGeraghty/uberto-frontend)

### ⚙️ Backend

* Kotlin + Spring Boot
* Spring Security + JWT (autenticación)
* Control de acceso basado en roles: pasajero o chofer

### 📃 Persistencia

* **PostgreSQL**: persistencia relacional para viajes, calificaciones y pasajeros
* **MongoDB**: persistencia documental para choferes y logs de clics
* **Redis**: almacenamiento clave-valor para búsquedas recientes cacheadas
* **Neo4j**: persistencia en grafos para relaciones sociales entre pasajeros

### 🐳 DevOps

* Docker y Docker Compose (para bases de datos y entorno local)

---

## 📌 Dominio de la aplicación

| Entidad principal | Descripción                                                                                      |
| ----------------- | ------------------------------------------------------------------------------------------------ |
| **Pasajero**      | Usuario que puede buscar choferes, contratar viajes, calificar, cargar saldo y gestionar amigos. |
| **Chofer**        | Persona que ofrece servicios de transporte. Tiene viajes asignados y recibe calificaciones.      |
| **Viaje**         | Representa un trayecto reservado por un pasajero con un chofer, en un horario determinado.       |
| **Calificación**  | Opinión del pasajero sobre un viaje realizado, con puntaje y comentario.                         |

---

## 🗭 Funcionalidades clave

### Para pasajeros

* Buscar choferes disponibles (por dirección y horario)
* Contratar viajes (de forma unilateral)
* Ver historial de viajes (pendientes y realizados)
* Calificar viajes completados
* Cargar saldo
* Gestionar red de amigos (agregar, eliminar, buscar)
* Recibir sugerencias de nuevos amigos (relaciones indirectas con choferes compartidos)

### Para choferes

* Ver viajes pendientes (filtrados)
* Ver viajes realizados y total recaudado
* Consultar calificaciones recibidas

---

## 📦 Arquitectura de persistencia poliglota

Cada motor de base de datos fue elegido en función de las necesidades del dominio y tipo de consulta esperada:

| Dominio                      | Motor elegido | Justificación técnica                                                                                                                                          | Tipo               |
| ---------------------------- | ------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------ |
| **Pasajeros y Viajes**       | PostgreSQL    | Datos estructurados, con relaciones fuertes y operaciones transaccionales ACID.                                                                                | Relacional         |
| **Choferes**                 | MongoDB       | Necesidad de flexibilidad en el esquema (distintos tipos de chofer), almacenamiento de documentos enriquecidos (tripsDTO, scores), y escalabilidad horizontal. | Documental         |
| **Caché / datos temporales** | Redis         | Usado para guardar sesiones JWT o datos de acceso rápido.                                                                                                      | clave-valor        |
| **Relaciones complejas**     | Neo4j         | Se usa para analizar grafos como recomendaciones de choferes, conexiones entre usuarios, rutas frecuentes, etc. (potencial uso futuro o en módulos avanzados). | orientada a grafos |

---

## 🔐 Seguridad: Spring Boot + JWT

El backend de Uberto implementa un sistema de autenticación **stateless** utilizando **Spring Security** y **JSON Web Tokens (JWT)**. Esto permite un control seguro y eficiente del acceso a los recursos de la API, con autorización basada en roles: `PASSENGER` y `DRIVER`.

### 🔑 Flujo de autenticación

1. El usuario se autentica mediante `/login` (punto final público).
2. Si las credenciales son válidas, se genera un token JWT firmado con HMAC-512.
3. El token se envía al cliente, quien lo incluirá en el header `Authorization` en cada request.
4. Un filtro (`JwtTokenValidator`) valida el token en cada request entrante:

   * Extrae el username, el ID del usuario y su rol.
   * Refresca el token si está por expirar (menos de 5 minutos restantes).
   * Establece el contexto de seguridad con los roles adecuados.

### 🔐 Componentes principales

#### 🔁 `JwtTokenValidator`

Filtro personalizado (`OncePerRequestFilter`) que:

* Extrae y valida el token JWT del encabezado `Authorization`.
* Establece el contexto de seguridad (`SecurityContextHolder`) para que Spring sepa quién está autenticado.
* Refresca el token si está por expirar y lo devuelve en el header `refresh-token`.

#### 🧱 `TokenJwtUtil`

Componente de utilidad para:

* Firmar y generar tokens con `userID`, `username` y `rol`.
* Validar tokens y extraer claims.
* Verificar cuándo debe refrescarse un token (cuando restan menos de 5 minutos).
* Extraer `userID` del token para controladores.

#### 🛡️ `ApplicationSecurityConfiguration`

Clase de configuración central que:

* Deshabilita CSRF y CORS (se maneja manualmente).
* Define endpoints públicos (`/login`, Swagger) y privados (controlados por rol).
* Configura las reglas de acceso usando `hasRole()` o `hasAnyRole()`.
* Registra el filtro JWT antes del filtro de autenticación básico.
* Define el `AuthenticationProvider` y el codificador de contraseñas (Argon2).

### 🛠️ Estructura JWT generada

El JWT contiene los siguientes claims:

```json
{
  "sub": "username",
  "rol": ["ROLE_PASSENGER" | "ROLE_DRIVER"],
  "userID": "idInterno",
  "iat": "...",
  "exp": "...",
  "iss": "UbertoBackend"
}
```

### 🔐 Autorización por roles

Los endpoints protegidos exigen el rol correspondiente, por ejemplo:

| Endpoint             | Método          | Rol requerido      |
| -------------------- | --------------- | ------------------ |
| `/trip/create`       | POST            | PASSENGER          |
| `/trip/driver`       | GET             | DRIVER             |
| `/passenger/friends` | GET/POST/DELETE | PASSENGER          |
| `/driver/available`  | GET             | PASSENGER          |
| `/tripScore/driver`  | GET             | DRIVER o PASSENGER |

> Los roles se definen como `ROLE_PASSENGER` y `ROLE_DRIVER` en los claims JWT y se traducen a `SimpleGrantedAuthority` en Spring.

---

## 📊 Consultas y analítica

### MongoDB

* Chofer más clickeado
* Choferes tipo moto
* Choferes con más de 4 estrellas
* Choferes con base entre \$1.000 y \$5.000
* Choferes sin viajes pendientes

### Redis

* Cache de búsquedas recientes (TTL: 5 horas)

### Neo4j

* Amigos de amigos que viajaron con un mismo chofer
* Pasajeros con más de 4 viajes
* Choferes sugeridos (por red social)
* Choferes con más de 4 viajes
* Usuario con un viaje asociado a un chofer con más de 3 pendientes

---

## 🧺 Modelado de viajes y cálculo de tarifas

El sistema calcula el precio de los viajes con base en:

* **Precio base del chofer**
* **Tipo de chofer** (simple, ejecutivo, moto)
* **Tiempo del viaje** (generado aleatoriamente entre 1 y 90 minutos)
* **Comisión de Uberto** (5%)

Ejemplo:

* Chofer tipo moto:

  * Si el viaje dura ≤ 30 minutos → \$500/minuto
  * Si el viaje dura > 30 minutos → \$600/minuto

---

## 🐳 Setup local

```bash
# Clonar el proyecto
git clone https://github.com/tu_usuario/uberto.git
cd uberto

# Iniciar servicios con Docker
docker-compose up -d

# Levantar backend
./gradlew bootRun

# Levantar frontend
cd frontend
npm install
npm start
```
