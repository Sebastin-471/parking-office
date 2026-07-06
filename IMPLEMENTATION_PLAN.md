# Plan de Implementación - Parking Office

## Análisis de Hallazgos

### Pendientes identificados en README.md
1. **Tests Unitarios** ⚠️ Parcial — Solo existe `ParkingServiceTest.java` con un stub (`assertTrue(true)`). Sin cobertura real de servicios, repositorios ni controllers.
2. **Impresión Directa** 🟢 Baja — Los PDFs se guardan en disco pero no se imprimen automáticamente.
3. **Gráficos JFreeChart** 🟢 Baja — Dependencia en `pom.xml` pero sin uso en ningún archivo `.java`.
4. **CSS de Estilos** — README marcado como pendiente, pero `styles.css` ya existe (109 líneas).

### Hallazgos adicionales no reflejados en README.md
1. **Violación de convención de logging** — AGENTS.md prohíbe `e.printStackTrace()` y `System.out/err.println()`. Se detectaron **11 infracciones** en 6 archivos:
   - `GenericRepository.java` (3x `e.printStackTrace()`)
   - `AuthService.java` (1x `System.out.println`, 1x `e.printStackTrace()`)
   - `DashboardController.java` (1x `System.err.println`, 1x `e.printStackTrace()`)
   - `EspacioConfigRepository.java` (2x `e.printStackTrace()`)
   - `ConfigLoader.java` (1x `System.out.println`, 1x `e.printStackTrace()`)
2. **Constructor injection pendiente** — `ParkingService` y `AuthService` crean sus repositorios con `new` dentro del constructor. El test stub menciona "In Sprint 2 we will refactor ParkingService to use constructor injection."
3. **README.md desactualizado** — Marca `styles.css` y `application.properties` como inexistentes; ambos archivos existen.

---

## Priorización

| ID | Tarea | Prioridad | Esfuerzo estimado | Justificación |
|---|---|---|---|---|
| T1 | Refactor: constructor injection en servicios | 🔴 Alta | Media | Bloquea testing real; mencionado en test stub |
| T2 | Reemplazar `printStackTrace()` por SLF4J logger | 🔴 Alta | Baja | Violación directa de convención AGENTS.md |
| T3 | Implementar tests unitarios reales (JUnit 5 + Mockito) | 🟡 Media | Alta | Cobertura actual nula; desbloqueada por T1 |
| T4 | Implementar tests de integración (Testcontainers) | 🟡 Media | Alta | Validación end-to-end contra PostgreSQL |
| T5 | Implementar impresión directa de PDFs | 🟢 Baja | Media | Feature pendiente declarada en README |
| T6 | Implementar gráficos JFreeChart en reportes | 🟢 Baja | Media | Feature pendiente declarada en README |
| T7 | Actualizar README.md (estado y pendientes) | 🟢 Baja | Baja | Documentación desactualizada |

---

## Roadmap Paso a Paso

### Fase 1 — Code Quality & Testability (Bloqueadores)

**Objetivo:** Eliminar infracciones de convención y desbloquear testing real.

#### Paso 1.1 — Refactor constructor injection en `ParkingService`
- Modificar constructor para recibir `MovimientoRepository`, `VehiculoRepository`, `TarifaRepository`, `EspacioConfigRepository` como parámetros.
- Mantener constructor sin argumentos como delegado para compatibilidad con controllers existentes.
- Actualizar controllers que instancian `ParkingService` para inyectar repositorios.

#### Paso 1.2 — Refactor constructor injection en `AuthService`
- Modificar constructor para recibir `UsuarioRepository` como parámetro.
- Mantener constructor sin argumentos como delegado.

#### Paso 1.3 — Reemplazar `printStackTrace()` / `System.out/err` por SLF4J
- Crear/verificar `logback.xml` en `src/main/resources/`.
- Sustituir en cada archivo infractor:
  - `System.out.println(...)` → `logger.info(...)`
  - `System.err.println(...)` → `logger.error(...)`
  - `e.printStackTrace()` → `logger.error("...", e)`
- Archivos a modificar: `GenericRepository.java`, `AuthService.java`, `DashboardController.java`, `EspacioConfigRepository.java`, `ConfigLoader.java`.

---

### Fase 2 — Testing (Cobertura Real)

**Objetivo:** Alcanzar cobertura significativa de código crítico.

#### Paso 2.1 — Tests unitarios de servicios
- Eliminar el stub de `ParkingServiceTest.java`.
- Implementar tests para `ParkingService`:
  - `registrarEntrada` success / vehículo ya estacionado / parking lleno / sin tarifa activa.
  - `registrarSalida` success / movimiento ya finalizado.
  - `calcularMonto` (unitario de lógica privada o via reflection).
- Implementar `AuthServiceTest`:
  - `login` correcto / incorrecto / usuario inactivo.
  - `hashPassword` genera hash válido.
  - `createDefaultAdminIfNotExists` crea admin solo si no existe.

#### Paso 2.2 — Tests de integración con Testcontainers
- Configurar contenedor PostgreSQL en test.
- Tests de repositorios:
  - `UsuarioRepository` — findByUsername, save, findAll.
  - `MovimientoRepository` — findActivos, findFinalizadosPorFecha, findActivoByVehiculo.
  - `TarifaRepository` — findActivaByTipoVehiculo.
  - `EspacioConfigRepository` — findActivo, save.
- Seeds de datos de prueba en `src/test/resources/`.

#### Paso 2.3 — Tests de controllers (si aplica)
- Validar flujos básicos de `LoginController`, `DashboardController`, `ReportesController`.
- Mock de `SessionManager` para probar RBAC.

---

### Fase 3 — Features Pendientes

**Objetivo:** Completar funcionalidades declaradas en README.

#### Paso 3.1 — Impresión directa de PDFs
- Extender `ReportService` con método `imprimirDocumento(String pdfPath)` usando `PrinterJob` de JavaFX.
- Modificar `DashboardController` para llamar a impresión automáticamente después de generar ticket de entrada/salida.
- Modificar `ReportesController.exportarPDF()` para ofrecer opción de impresión.
- Manejo de excepciones de impresión (sin impresora configurada, cancelación por usuario).

#### Paso 3.2 — Implementar gráficos JFreeChart
- Crear `ChartService` nuevo o extender `ReportService`.
- Gráfico 1: Ocupación de espacios en el tiempo (línea/timeline).
- Gráfico 2: Recaudación diaria/semanal (barras).
- Gráfico 3: Distribución por tipo de vehículo (pie).
- Integrar en `ReportesController` o vista de reportes.

---

### Fase 4 — Documentación

**Objetivo:** Sincronizar README con estado real del proyecto.

#### Paso 4.1 — Actualizar tabla de estado en README.md
- Marcar `styles.css` como completo.
- Marcar `application.properties` como completo.
- Actualizar Tests a "En progreso" (tras Fase 2) o "Completo" (tras Fase 2 finalizada).
- Actualizar Control de Espacios a "Completo" si aplica.
- Agregar fila para "Code Quality / Logging SLF4J".

---

## Criterios de Aceptación por Fase

| Fase | Criterio de aceptación |
|---|---|
| Fase 1 | `mvn clean compile` sin errores. 0 infracciones de `printStackTrace`/`System.out/err` en main. Constructor injection en servicios. |
| Fase 2 | `mvn test` pasa. Cobertura mínima 70% en `service/` y `repository/`. |
| Fase 3 | PDFs se imprimen automáticamente. Gráficos visibles en vista de reportes. |
| Fase 4 | README.md refleja estado real del proyecto. |

## Dependencias entre tareas

```
T1 (constructor injection)
 └──► T2 (logging SLF4J)
      └──► T3 (tests unitarios)
           └──► T4 (tests integración)

T5 (impresión) — independiente
T6 (gráficos) — independiente
T7 (README) — al final
```
