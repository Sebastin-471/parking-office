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

## 🧩 Tecnologías

### Frontend (interfaz de escritorio)

| Categoría                     | Tecnología            |
| ----------------------------- | --------------------- |
| Framework UI                  | JavaFX (SDK 21)       |
| Diseño de vistas              | Scene Builder (Gluon) |
| Lenguaje de marcado de vistas | FXML                  |
| Estilos                       | CSS (JavaFX CSS)      |

### Backend (lógica de negocio y datos)

| Categoría                            | Tecnología             |
| ------------------------------------ | ---------------------- |
| Lenguaje                             | Java (JDK 21 LTS)      |
| ORM                                  | Hibernate / JPA        |
| Driver de base de datos              | PostgreSQL JDBC Driver |
| Seguridad (hash de contraseñas)      | jBCrypt                |
| Generación de PDF (tickets/reportes) | Apache PDFBox / iText  |
| Gráficos de reportes                 | JFreeChart             |
| Logs                                 | SLF4J + Logback        |
| Gestor de dependencias               | Maven                  |

### Base de datos

| Categoría              | Detalle                                                                                      |
| ---------------------- | -------------------------------------------------------------------------------------------- |
| Motor                  | PostgreSQL 16+                                                                               |
| Cliente administrativo | pgAdmin                                                                                      |
| Entidades principales  | `roles`, `usuarios`, `tipos_vehiculo`, `vehiculos`, `tarifas`, `espacios`, `movimientos`     |
| Relación núcleo        | `movimientos` conecta vehículo, espacio, usuario y tarifa en cada registro de entrada/salida |

### Entorno de desarrollo

- IDE: NetBeans (con soporte nativo para Maven y JavaFX)
- Control de versiones: Git + GitHub

---

## 📂 Estructura del proyecto (propuesta)

```
parking-office/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/parkingoffice/
│   │   │       ├── model/          # Entidades JPA
│   │   │       ├── repository/     # Acceso a datos
│   │   │       ├── service/        # Lógica de negocio (cálculo de tarifas, etc.)
│   │   │       ├── controller/     # Controladores de vistas JavaFX
│   │   │       └── App.java        # Punto de entrada
│   │   └── resources/
│   │       ├── fxml/               # Vistas
│   │       ├── css/                # Estilos
│   │       └── application.properties
├── database/
│   └── parking_system_schema.sql   # Script de creación de la BD
├── pom.xml
└── README.md
```
