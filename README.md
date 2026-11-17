# KAIROS

Plataforma colaborativa de descuentos y promociones construida con **Spring Boot 4**, **Spring MVC**, **Spring Data JPA**, **Thymeleaf**, **MySQL** y **CSS puro**.

## Requisitos

- Java 21+
- MySQL 8 en `localhost:3306`
- Maven Wrapper incluido (`mvnw.cmd` en Windows, `./mvnw` en Linux/macOS)


## Ejecutar

```powershell
.\mvnw spring-boot:run
```

## Cuentas de demostración

| Rol | Correo | Contraseña |
| --- | --- | --- |
| Administrador | `admin@kairos.dev` | `admin1234` |
| Usuario | `demo@kairos.dev` | `demo1234` |



## Funcionalidades

- **Feed público** con búsqueda por texto, filtro por categoría/subcategoría, ordenación por precio/descuento y tarjetas en estado `VENCIDA / AGOTADA`.
- **Registro, login y logout** mediante `HttpSession`.
- **Publicación de ofertas** con subida de imágenes, validación de precios y envío a moderación en estado `PENDIENTE`.
- **Panel de administración** para aprobar, rechazar, marcar vencidas, inhabilitar, reactivar y eliminar definitivamente.
- **Reportes comunitarios** con un único reporte por usuario y publicación; al alcanzar 10 reportes la oferta pasa automáticamente a `INACTIVA` y sale del feed.

## Estructura

```text
src/main/java/com/kairos/
├── config/         # Datos inciales t WebConfig
├── controlador/    # Auth, Feed, Publicacion y Admin
├── dto/            # Formularios de registro y publicación
├── enums/          # RolUsuario, EstadoPublicacion
├── excepciones/    # Excepciones de negocio
├── modelo/         # Usuario, Categoria, Subcategoria, Publicacion, Reporte
├── repositorio/    # Repositorios Spring Data JPA
├── servicio/       # Interfaces I...Servicio
└── servicio/impl/  # Implementaciones @Service
```