# DIGAE Spring Boot Backend

Este directorio contiene la futura estructura para la API de Spring Boot que servirá al sistema DIGAE.

## Conexión a Supabase (PostgreSQL)
Para configurar la conexión a la base de datos, en un futuro deberemos crear el archivo `src/main/resources/application.properties` con las siguientes credenciales de Supabase:

```properties
spring.datasource.url=jdbc:postgresql://<SUPABASE_HOST>:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=<PASSWORD>
spring.jpa.hibernate.ddl-auto=update
```
