# Aplicación Backend en Kotlin

Este proyecto es una aplicación backend desarrollada en Kotlin, diseñada para gestionar eventos, usuarios y recomendaciones dentro de un entorno colaborativo. Forma parte del desarrollo del grupo 3 para la materia Programación con Tipado Estático (2025), UNSAM.

## 🚀 Características Principales

- ✅ Backend desarrollado con Kotlin.
- ✅ Arquitectura modular con separación clara entre controladores, servicios y modelos.
- ✅ Uso de DTOs para la transferencia de datos.
- ✅ Control de acceso basado en tipos de usuario.
- ✅ Soporte para recomendaciones y puntuaciones entre usuarios.

## 📦 Requisitos Previos

Antes de ejecutar la aplicación, asegurate de tener instalado:

- [JDK 17+](https://adoptium.net/)
- [Gradle](https://gradle.org/) (opcional si usás el wrapper `./gradlew`)
- IDE recomendado: IntelliJ IDEA

## ⚙️ Instalación y Ejecución

1. **Clonar el repositorio**:

   ```bash
   git clone https://github.com/phm-unsam/backend-2025-grupo3.git

2. **Navegar al directorio del proyecto**:

3. **Construir el proyecto**:
  
   ```bash
   ./gradlew build
   ```
4. **Ejecutar la aplicación**:
   ```bash
   ./gradlew run
   ```
## La aplicación quedará corriendo en http://localhost:8080

🧪 Testing
Podés correr los tests del proyecto con:

   ```bash
   ./gradlew test
   ```

📬 Endpoints principales
Algunos de los endpoints disponibles:

GET /TripScore?userId={id}: Obtener puntuaciones para un usuario.

POST /Trip: Crear un nuevo viaje.

DELETE /TripScore?userId={id}&tripScoreId={id}: Eliminar una recomendación.

GET /Driver/{id}/trips: Obtener viajes asociados a un conductor.

(Ver detalles completos en los controladores del proyecto)

🤝 Contribuciones
¡Las contribuciones son bienvenidas! Para colaborar:

Forkeá este repositorio.

Creá una rama con tu mejora o fix.

Hacé commit de los cambios.

Abrí un Pull Request.
