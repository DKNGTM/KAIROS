# KAIROS

Plataforma colaborativa de descuentos y promociones construida con **Spring Boot 4**, **Spring MVC**, **Spring Data JPA**, **Thymeleaf**, **MySQL** y **CSS puro**.

## Requisitos

- Java 21+
- MySQL 8 en `localhost:3306`
- Maven Wrapper incluido (`mvnw.cmd` en Windows, `./mvnw` en Linux/macOS)

## Configuración de base de datos

La conexión se define en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/kairos_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=${KAIROS_DB_PASSWORD:31521}
```

Si tu MySQL usa otra contraseña, define la variable de entorno `KAIROS_DB_PASSWORD` antes de arrancar:

```powershell
$env:KAIROS_DB_PASSWORD = "tu-password"
```

La base `kairos_db` y las tablas se crean automáticamente al iniciar.

## Ejecutar

```powershell
.\mvnw.cmd spring-boot:run
```

Abrir `http://localhost:8080/feed`.

## Cuentas de demostración

| Rol | Correo | Contraseña |
| --- | --- | --- |
| Administrador | `admin@kairos.dev` | `admin1234` |
| Usuario | `demo@kairos.dev` | `demo1234` |

También existen los usuarios `kernel_panic`, `nexus_scout` y `caffeine_architect` con la contraseña `demo1234`.

El catálogo de demostración se controla con `kairos.seed-demo=true` en `application.properties`. Las categorías, subcategorías y cuentas base se crean siempre que la base esté vacía.

## Funcionalidades

- **Feed público** con búsqueda por texto, filtro por categoría/subcategoría, ordenación por precio/descuento y tarjetas en estado `VENCIDA / AGOTADA`.
- **Registro, login y logout** mediante `HttpSession`, sin Spring Security.
- **Publicación de ofertas** con subida de imágenes, validación de precios y envío a moderación en estado `PENDIENTE` (RF001).
- **Panel de administración** para aprobar, rechazar, marcar vencidas (RF004), inhabilitar, reactivar y eliminar definitivamente (RF002).
- **Reportes comunitarios** con un único reporte por usuario y publicación; al alcanzar 10 reportes la oferta pasa automáticamente a `INACTIVA` y sale del feed (RF005).

## Estructura

```text
src/main/java/com/kairos/
├── config/         # WebConfig: sirve /uploads/**
├── controlador/    # Auth, Feed, Publicacion y Admin
├── dto/            # Formularios de registro y publicación
├── enums/          # RolUsuario, EstadoPublicacion
├── excepciones/    # Excepciones de negocio
├── modelo/         # Usuario, Categoria, Subcategoria, Publicacion, Reporte
├── repositorio/    # Repositorios Spring Data JPA
├── servicio/       # Interfaces I...Servicio
└── servicio/impl/  # Implementaciones @Service
```

Las vistas están en `src/main/resources/templates` y los estilos en `src/main/resources/static/css` (`main.css`, `feed.css`, `auth.css`, `admin.css`).
