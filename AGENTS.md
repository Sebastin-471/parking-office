# AGENTS.md - Guía para Agentes de IA

## Comandos de Build y Test

```bash
# Compilar
mvn clean compile

# Ejecutar aplicación
mvn javafx:run

# Ejecutar tests
mvn test

# Build completo
mvn clean package
```

## Estructura del Proyecto

- **Entry Point**: `src/main/java/com/parkingoffice/Launcher.java` → `App.java`
- **Modelos**: `model/` - Entidades JPA con anotaciones `@Entity`
- **Repositories**: `repository/` - Extienden `GenericRepository`
- **Services**: `service/` - Lógica de negocio
- **Controllers**: `controller/` - Manejan eventos de UI JavaFX
- **Core**: `core/` - Utilidades (SessionManager singleton, ConfigLoader)

## Patrones de Código

1. **Singleton**: `SessionManager.getInstance()` para estado de sesión
2. **Repository Pattern**: Cada entidad tiene su repository
3. **MVC**: Controllers separados de lógica de negocio
4. **RBAC**: Verificar `SessionManager.getInstance().isAdmin()` antes de operaciones admin

## Excepciones Personalizadas

- `ParkingException` - Base
- `VehicleAlreadyParkedException`
- `VehicleNotFoundException`
- `NoActiveRateException`
- `ParkingFullException` (nueva)

## Archivos Clave

- `pom.xml` - Dependencias y plugins JavaFX
- `hibernate.cfg.xml` - Mapeo de entidades
- `application.properties` - Configuración DB (crear si no existe)
- `database/parking_system_schema.sql` - Schema de BD

## Notas de Desarrollo

- No usar `e.printStackTrace()` - usar logger SLF4J
- Validar datos antes de persistir
- Los packages FXML usan `stylesheets="@../css/styles.css"`
- Formato de placas: `^[A-Z0-9]{3}-?[A-Z0-9]{3,4}$`