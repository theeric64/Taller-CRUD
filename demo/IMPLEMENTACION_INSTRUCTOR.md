# Implementación de la Entidad Instructor - AgendaSENA

## 📋 Resumen de Cambios

Se ha implementado exitosamente la entidad `Instructor` con todas las refactorizaciones necesarias para gestionar correctamente las reservas del sistema.

---

## 1️⃣ ENTIDAD INSTRUCTOR (Nueva)

**Archivo:** `src/main/resources/Model/Instructor.java`

```java
@Entity
@Table(name = "instructor")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Instructor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del instructor es obligatorio")
    @Column(nullable = false)
    private String nombre;

    private String email;
    private boolean activo = true;
}
```

**Campos:**
- `id` (Long): Identificador único, autogenerado
- `nombre` (String): Nombre del instructor (obligatorio)
- `email` (String): Email del instructor
- `activo` (boolean): Estado del instructor (default: true)

---

## 2️⃣ ENTIDAD RESERVA (Refactorizada)

**Archivo:** `src/main/resources/Model/Reserva.java`

**CAMBIOS PRINCIPALES:**
- ❌ Eliminado: `private String nombreInstructor;`
- ✅ Agregado: Relación `@ManyToOne` con `Instructor`

```java
@Entity
@Table(name = "reserva")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Reserva {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ambiente_id", nullable = false)
    private Ambiente ambiente;

    @ManyToOne(fetch = FetchType.EAGER)  // ← NUEVO
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private int numeroAprendices;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoReserva estado = EstadoReserva.ACTIVA;
}
```

**Cambio en BD:** Se agregará columna `instructor_id` como clave foránea.

---

## 3️⃣ DTOs ACTUALIZADOS

### ReservaRequest (Modificado)
**Archivo:** `src/main/resources/dto/ReservaRequest.java`

```java
@Data
public class ReservaRequest {
    @NotNull(message = "El ID del ambiente es obligatorio")
    private Long ambienteId;

    @NotNull(message = "El ID del instructor es obligatorio")
    private Long instructorId;  // ← CAMBIO: Era "nombreInstructor"

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @Min(value = 1, message = "Debe haber al menos 1 aprendiz")
    private int numeroAprendices;
}
```

### ReservaResponse (Nuevo)
**Archivo:** `src/main/resources/dto/ReservaResponse.java`

```java
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservaResponse {
    private Long id;
    private Long ambienteId;
    private String nombreAmbiente;
    private Long instructorId;          // ← NUEVO
    private String nombreInstructor;    // ← NUEVO
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private int numeroAprendices;
    private EstadoReserva estado;
}
```

### InstructorRequest (Nuevo)
**Archivo:** `src/main/resources/dto/InstructorRequest.java`

```java
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InstructorRequest {
    @NotBlank(message = "El nombre del instructor es obligatorio")
    private String nombre;

    @Email(message = "El email debe ser válido")
    private String email;

    private boolean activo = true;
}
```

### InstructorResponse (Nuevo)
**Archivo:** `src/main/resources/dto/InstructorResponse.java`

```java
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InstructorResponse {
    private Long id;
    private String nombre;
    private String email;
    private boolean activo;
}
```

---

## 4️⃣ REPOSITORIES

### InstructorRepository (Nuevo)
**Archivo:** `src/main/java/com/example/demo/repository/InstructorRepository.java`

```java
@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
```

### ReservaRepository (Nuevo/Mejorado)
**Archivo:** `src/main/java/com/example/demo/repository/ReservaRepository.java`

**Métodos Custom:**

1. **countReservasActivasPorInstructor** - Cuenta reservas con solapamiento de horarios
   ```java
   @Query("SELECT COUNT(r) FROM Reserva r WHERE r.instructor = :instructor " +
          "AND r.estado = 'ACTIVA' " +
          "AND r.fechaInicio < :fechaFin AND r.fechaFin > :fechaInicio")
   long countReservasActivasPorInstructor(
       @Param("instructor") Instructor instructor,
       @Param("fechaInicio") LocalDateTime fechaInicio,
       @Param("fechaFin") LocalDateTime fechaFin
   );
   ```

2. **countReservasActivasDelDia** - Cuenta reservas ACTIVAS en un día específico
   ```java
   @Query("SELECT COUNT(r) FROM Reserva r WHERE r.instructor = :instructor " +
          "AND r.estado = 'ACTIVA' AND DATE(r.fechaInicio) = DATE(:fecha)")
   long countReservasActivasDelDia(
       @Param("instructor") Instructor instructor,
       @Param("fecha") LocalDateTime fecha
   );
   ```

### AmbienteRepository (Nuevo)
**Archivo:** `src/main/java/com/example/demo/repository/AmbienteRepository.java`

```java
@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {
    Optional<Ambiente> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
```

---

## 5️⃣ SERVICIOS

### ReservaService (Nuevo/Mejorado)
**Archivo:** `src/main/java/com/example/demo/service/ReservaService.java`

#### Método: `crearReserva(ReservaRequest)`

**Validaciones ejecutadas:**

1. ✅ Fechas válidas (inicio < fin)
2. ✅ Instructor existe en BD
3. ✅ Instructor está activo
4. ✅ Ambiente existe en BD
5. ✅ Ambiente está activo
6. ✅ **REGLA DE NEGOCIO: El instructor NO tiene 3 reservas ACTIVAS ese día**

**Excepciones lanzadas:**

```java
// 404 Not Found
"Instructor con ID X no encontrado"

// 404 Not Found
"Ambiente con ID X no encontrado"

// 400 Bad Request
"El instructor X no está activo"
"El ambiente X no está activo"
"La fecha de inicio debe ser anterior a la fecha de fin"

// 409 Conflict (¡IMPORTANTE!)
"El instructor X ya tiene 3 reservas activas el YYYY-MM-DD. 
No puede tener más reservas el mismo día"
```

**Ejemplo de uso:**
```java
ReservaRequest req = new ReservaRequest();
req.setAmbienteId(1L);
req.setInstructorId(5L);  // ← Ahora es instructorId, no nombreInstructor
req.setFechaInicio(LocalDateTime.now());
req.setFechaFin(LocalDateTime.now().plusHours(2));
req.setNumeroAprendices(15);

ReservaResponse respuesta = reservaService.crearReserva(req);
```

#### Otros métodos:
- `obtenerReserva(Long id)` - GET por ID
- `obtenerTodasLasReservas()` - GET todas
- `actualizarEstadoReserva(Long id, EstadoReserva nuevoEstado)` - Cambiar estado
- `eliminarReserva(Long id)` - DELETE

### InstructorService (Nuevo)
**Archivo:** `src/main/java/com/example/demo/service/InstructorService.java`

#### Métodos:
- `crearInstructor(InstructorRequest)` - Crea nuevo instructor (valida nombre único)
- `obtenerInstructor(Long id)` - GET por ID
- `obtenerTodosLosInstructores()` - GET todos
- `actualizarInstructor(Long id, InstructorRequest)` - PUT (actualiza datos)
- `eliminarInstructor(Long id)` - DELETE

---

## 6️⃣ CONTROLADORES REST

### ReservaController (Nuevo)
**Archivo:** `src/main/java/com/example/demo/controller/ReservaController.java`

**Endpoints:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| **POST** | `/api/reservas` | Crear nueva reserva |
| **GET** | `/api/reservas/{id}` | Obtener por ID |
| **GET** | `/api/reservas` | Obtener todas |
| **DELETE** | `/api/reservas/{id}` | Eliminar |

**Respuestas HTTP:**
- `201 Created` - Reserva creada exitosamente
- `404 Not Found` - Instructor o Ambiente no existen
- `409 Conflict` - Instructor ya tiene 3 reservas activas ese día ⚠️
- `400 Bad Request` - Datos inválidos o instructor/ambiente inactivo

### InstructorController (Nuevo)
**Archivo:** `src/main/java/com/example/demo/controller/InstructorController.java`

**Endpoints:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| **POST** | `/api/instructores` | Crear nuevo instructor |
| **GET** | `/api/instructores/{id}` | Obtener por ID |
| **GET** | `/api/instructores` | Obtener todos |
| **PUT** | `/api/instructores/{id}` | Actualizar |
| **DELETE** | `/api/instructores/{id}` | Eliminar |

---

## 📝 EJEMPLOS DE USO (cURL)

### 1. Crear un Instructor

```bash
curl -X POST http://localhost:8080/api/instructores \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan@sena.edu.co",
    "activo": true
  }'
```

**Respuesta (201 Created):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@sena.edu.co",
  "activo": true
}
```

---

### 2. Crear una Reserva

```bash
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "ambienteId": 1,
    "instructorId": 1,
    "fechaInicio": "2026-06-25T08:00:00",
    "fechaFin": "2026-06-25T10:00:00",
    "numeroAprendices": 20
  }'
```

**Respuesta (201 Created):**
```json
{
  "id": 1,
  "ambienteId": 1,
  "nombreAmbiente": "Laboratorio A",
  "instructorId": 1,
  "nombreInstructor": "Juan Pérez",
  "fechaInicio": "2026-06-25T08:00:00",
  "fechaFin": "2026-06-25T10:00:00",
  "numeroAprendices": 20,
  "estado": "ACTIVA"
}
```

---

### 3. Intento de Crear 4ª Reserva (Violation de Regla de Negocio)

Si el instructor Juan Pérez ya tiene 3 reservas ACTIVAS el mismo día:

```bash
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "ambienteId": 2,
    "instructorId": 1,
    "fechaInicio": "2026-06-25T14:00:00",
    "fechaFin": "2026-06-25T16:00:00",
    "numeroAprendices": 15
  }'
```

**Respuesta (409 Conflict):**
```json
{
  "timestamp": "2026-06-22T10:30:45",
  "status": 409,
  "error": "Conflict",
  "message": "El instructor Juan Pérez ya tiene 3 reservas activas el 2026-06-25. No puede tener más reservas el mismo día",
  "path": "/api/reservas"
}
```

---

### 4. Obtener Todas las Reservas

```bash
curl -X GET http://localhost:8080/api/reservas
```

---

## 🔧 CONFIGURACIONES NECESARIAS EN application.properties

Asegúrate de tener estas configuraciones para que JPA genere automáticamente las tablas:

```properties
# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
# O para MySQL:
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Base de datos (H2 por defecto)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

---

## ⚙️ DEPENDENCIAS REQUERIDAS (en pom.xml)

```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Jakarta Validation -->
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Base de datos (H2 o MySQL) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 📋 CHECKLIST DE IMPLEMENTACIÓN

- ✅ Entidad `Instructor` creada con campos id, nombre, email, activo
- ✅ Entidad `Reserva` refactorizada con relación @ManyToOne a Instructor
- ✅ DTOs actualizados (ReservaRequest ahora usa instructorId, ReservaResponse con datos de instructor)
- ✅ InstructorRepository con métodos findByNombre y existsByNombre
- ✅ ReservaRepository con métodos custom para contar reservas activas
- ✅ AmbienteRepository creado
- ✅ ReservaService con validaciones completas
- ✅ InstructorService con CRUD completo
- ✅ ReservaController con endpoints REST
- ✅ InstructorController con endpoints REST
- ✅ Regla de negocio implementada: **Un instructor NO puede tener más de 3 reservas ACTIVAS el mismo día**
- ✅ HTTP 409 (Conflict) retornado cuando se viola la regla de negocio

---

## 🚀 PRÓXIMOS PASOS (Opcional)

1. Crear AmbienteService y AmbienteController
2. Agregar auditoría (createdAt, updatedAt)
3. Implementar soft delete (activo = false en lugar de DELETE)
4. Agregar paginación en los GET
5. Implementar filtros avanzados
6. Crear tests unitarios para ReservaService
7. Crear tests de integración para los endpoints

---

**Versión:** 1.0  
**Fecha:** 22 de Junio de 2026  
**Estado:** ✅ Implementación Completada
