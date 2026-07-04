# parking-office

Repositorio: [github.com/Sebastin-471/parking-office](https://github.com/Sebastin-471/parking-office)

Sistema de escritorio para el control de entrada y salida de vehículos en un estacionamiento, con cálculo automático de tarifas, control de espacios disponibles en tiempo real, gestión de usuarios por rol y generación de reportes y comprobantes.

---

## 🎯 Objetivo

Desarrollar una aplicación de escritorio en Java que permita:

- Registrar el ingreso y salida de vehículos mediante placa (identificación manual).
- Calcular automáticamente el monto a pagar según el tiempo de permanencia (tarifa por hora/minuto).
- Controlar en tiempo real la disponibilidad de espacios del estacionamiento.
- Diferenciar el acceso al sistema según el rol del usuario (administrador / operador de caseta).
- Generar tickets/comprobantes imprimibles y reportes de ingresos y ocupación.

---

## 📊 Estado del Proyecto

| Componente | Estado | Comentarios |
|------------|--------|-------------|
| Autenticación/Login | ✅ Completo | jBCrypt, RBAC implementado |
| Registro Entradas/Salidas | ✅ Completo | Cálculo de tarifas funcionando |
| Gestión de Tarifas | ✅ Completo | Con historial de versiones |
| Gestión de Usuarios | ✅ Completo | CRUD completo, validaciones |
| Gestión Tipos Vehículo | ✅ Completo | CRUD completo |
| Historial Movimientos | ✅ Completo | Filtros por fecha/placa |
| Reportes PDF | ✅ Completo | Tickets y cierre de caja |
| Control de Espacios | ✅ Implementado | Capacidad simple (total vehículos) |
| Tests Unitarios | ⚠️ Parcial | Solo test stub, sin cobertura real |

---

## 🧩 Tecnologías

### Frontend (interfaz de escritorio)

| Categoría                     | Tecnología            |
| ----------------------------- | --------------------- |
| Framework UI                  | JavaFX (SDK 21)       |
| Diseño de vistas              | Scene Builder (Gluon) |
| Lenguaje de marcado de vistas | FXML                  |
| Estilos                       | CSS (JavaFX CSS)      |

### Backend (lógica de negocio y datos)

| Categoría                            | Tecnología                |
| ------------------------------------ | ------------------------- |
| Lenguaje                             | Java (JDK 21 LTS)         |
| ORM                                  | Hibernate / JPA            |
| Driver de base de datos              | PostgreSQL JDBC Driver    |
| Seguridad (hash de contraseñas)      | jBCrypt                   |
| Generación de PDF (tickets/reportes) | Apache PDFBox             |
| Gráficos de reportes               | JFreeChart (no usado)     |
| Logs                                 | SLF4J + Logback           |
| Gestor de dependencias               | Maven                     |

### Base de datos

| Categoría              | Detalle                                                                                     |
| ---------------------- | ------------------------------------------------------------------------------------------- |
| Motor                  | PostgreSQL 16+                                                                                |
| Cliente administrativo | pgAdmin                                                                                      |
| Entidades implementadas| `roles`, `usuarios`, `tipos_vehiculo`, `vehiculos`, `tarifas`, `movimientos`, `espacio_config` |
| Relación núcleo        | `movimientos` conecta vehículo, usuario y tarifa en cada registro de entrada/salida |

### Entorno de desarrollo

- IDE: NetBeans (con soporte nativo para Maven y JavaFX)
- Control de versiones: Git + GitHub

---

## 📂 Estructura del proyecto

```
parking-office/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/parkingoffice/
│   │   │       ├── model/          # Entidades JPA
│   │   │       │   ├── Usuario.java
│   │   │       │   ├── Rol.java
│   │   │       │   ├── Vehiculo.java
│   │   │       │   ├── TipoVehiculo.java
│   │   │       │   ├── Tarifa.java
│   │   │       │   ├── Movimiento.java
│   │   │       │   └── EspacioConfig.java
│   │   │       ├── repository/     # GenericRepository + repositorios
│   │   │       │   ├── GenericRepository.java
│   │   │       │   ├── UsuarioRepository.java
│   │   │       │   ├── RolRepository.java
│   │   │       │   ├── VehiculoRepository.java
│   │   │       │   ├── TipoVehiculoRepository.java
│   │   │       │   ├── TarifaRepository.java
│   │   │       │   ├── MovimientoRepository.java
│   │   │       │   └── EspacioConfigRepository.java
│   │   │       ├── service/        # AuthService, ParkingService, ReportService
│   │   │       ├── controller/     # LoginController, DashboardController, etc.
│   │   │       ├── core/           # SessionManager, ConfigLoader
│   │   │       └── exception/      # VehicleNotFoundException, NoActiveRateException, VehicleAlreadyParkedException, ParkingFullException
│   │   └── resources/
│   │       ├── fxml/               # Vistas FXML
│   │       │   ├── login.fxml
│   │       │   ├── dashboard.fxml
│   │       │   ├── tarifas.fxml
│   │       │   ├── usuarios.fxml
│   │       │   ├── tiposVehiculo.fxml
│   │       │   ├── historial.fxml
│   │       │   ├── reportes.fxml
│   │       │   └── espacios.fxml
│   │       ├── application.properties
│   │       └── css/styles.css
├── database/
│   └── parking_system_schema.sql
├── pom.xml
└── README.md
```

---

## 🔒 Seguridad Implementada

1. **Autenticación**
   - Hash de contraseñas con jBCrypt (bcrypt)
   - Credenciales admin externalizadas en `application.properties`
   - Soporte para variables de entorno

2. **Control de Acceso (RBAC)**
   - Dos roles: `ADMINISTRADOR` y `OPERADOR`
   - Defensa en profundidad: UI, Controllers y Services validan roles

3. **Validación de Entrada**
   - Login: username alfanumérico, password ≥6 caracteres
   - Placas: formato `ABC-123` o `ABC123`
   - Tarifas: montos positivos obligatorios
   - Fechas: rango lógico validado

---

## 🚗 Control de Espacios (Capacidad Simple)

El sistema ahora incluye control de capacidad del estacionamiento:

- **Configuración**: Admin define capacidad máxima (ej: 20 vehículos)
- **Visualización**: En dashboard se muestra "Espacios disponibles: X / Y"
- **Validación**: Al intentar entrada, se rechaza si está lleno
- **Protección**: No se permite reducir capacidad por debajo de vehículos ocupados

### Flujo

1. Admin abre "Gestión de Espacios" desde menú Administración
2. Ingresa capacidad total y guarda
3. El sistema valida que no sea menor a vehículos actuales
4. Los operadores ven disponibilidad pero no pueden modificarla

---

## ⚠️ Pendientes

| Prioridad | Funcionalidad |
|-----------|--------------|
| 🟡 Media | CSS de Estilos | `styles.css` no existe físicamente |
| 🟡 Media | Tests Unitarios | Implementar cobertura real con Testcontainers |
| 🟢 Baja | Impresión Directa | Los tickets PDF se guardan pero no se imprimen automáticamente |
| 🟢 Baja | Gráficos JFreeChart | La dependencia está pero no se usa |

---

## 🚀 Instalación y Ejecución

### Requisitos

- JDK 21
- PostgreSQL 16+
- Maven 3.9+

### Pasos

1. Crear base de datos:
```sql
CREATE DATABASE parking_office;
```

2. Ejecutar schema:
```bash
psql -U postgres -d parking_office -f database/parking_system_schema.sql
```

3. Editar `src/main/resources/application.properties` con credenciales:
```properties
db.url=jdbc:postgresql://localhost:5432/parking_office
db.user=postgres
db.password=tu_password
```

4. Compilar y ejecutar:
```bash
mvn clean compile
mvn javafx:run
```

---

## 📝 Licencia

Este proyecto es privado y de uso interno.