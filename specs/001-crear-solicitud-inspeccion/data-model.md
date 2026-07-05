# Modelo de Datos: Crear solicitud de inspección vinculada a frente y actividad

**Fecha**: 2026-06-27
**Plan**: [plan.md](plan.md)

## Entidades de Dominio

### SolicitudInspeccion

Representa la petición formal de que una actividad de construcción sea inspeccionada y
liberada por el inspector de calidad.

| Atributo | Tipo | Reglas de Validación |
|----------|------|----------------------|
| `id` | UUID | Generado por el sistema; inmutable |
| `proyectoId` | UUID | Obligatorio; debe referenciar un Proyecto existente |
| `frenteId` | UUID | Obligatorio; debe referenciar un Frente existente y pertenecer al proyecto |
| `actividadId` | UUID | Obligatorio; debe referenciar una Actividad existente y pertenecer al frente |
| `estado` | EstadoSolicitud | Siempre 'PENDIENTE' al crear; transitions controladas por el dominio |
| `fechaCreacion` | LocalDateTime | Generada por el sistema al crear; inmutable |
| `inspectorId` | UUID | Obligatorio; identidad del inspector que crea la solicitud |
| `forzarDuplicado` | boolean | Solo usado en el momento de creación; no persiste |

**Invariantes de dominio**:
- `proyectoId`, `frenteId`, `actividadId` e `inspectorId` son obligatorios y no pueden ser nulos.
- Solo puede existir una SolicitudInspeccion con estado `PENDIENTE` para la misma terna
  (proyectoId, frenteId, actividadId) a menos que el inspector haya confirmado explícitamente
  la creación del duplicado (`forzarDuplicado = true`).
- `fechaCreacion` es inmutable una vez establecida.

**Transiciones de estado**:
```
PENDIENTE → APROBADA  (fuera del alcance de esta historia)
PENDIENTE → RECHAZADA (fuera del alcance de esta historia)
```

---

### EstadoSolicitud (enumeración)

| Valor | Descripción |
|-------|-------------|
| `PENDIENTE` | Estado inicial; la actividad está lista según producción pero aún no inspeccionada |
| `APROBADA` | La inspección fue realizada y la actividad fue liberada (fuera del alcance de v1) |
| `RECHAZADA` | La inspección detectó problemas; la actividad no fue liberada (fuera del alcance de v1) |

---

### Proyecto

Unidad organizativa de nivel superior. Agrupa frentes y actividades.

| Atributo | Tipo | Reglas de Validación |
|----------|------|----------------------|
| `id` | UUID | Generado por el sistema |
| `nombre` | String | Obligatorio; máximo 200 caracteres |
| `descripcion` | String | Opcional |

**Nota**: Los proyectos son datos de catálogo precargados. Esta historia no incluye CRUD
de proyectos.

---

### Frente

Sector o zona de trabajo dentro de un proyecto. Cada residente supervisa uno o más frentes.

| Atributo | Tipo | Reglas de Validación |
|----------|------|----------------------|
| `id` | UUID | Generado por el sistema |
| `proyectoId` | UUID | Obligatorio; referencia al proyecto contenedor |
| `nombre` | String | Obligatorio; máximo 200 caracteres |
| `residenteId` | UUID | Obligatorio; identidad del residente responsable |

---

### Actividad

Tarea constructiva concreta dentro de un frente que puede ser inspeccionada y liberada.

| Atributo | Tipo | Reglas de Validación |
|----------|------|----------------------|
| `id` | UUID | Generado por el sistema |
| `frenteId` | UUID | Obligatorio; referencia al frente contenedor |
| `nombre` | String | Obligatorio; máximo 200 caracteres |
| `descripcion` | String | Opcional |

---

## Relaciones

```
Proyecto 1 ──────< Frente 1 ──────< Actividad
                     │
                     └──────────── residenteId (referencia externa)

SolicitudInspeccion >────── Proyecto (proyectoId)
SolicitudInspeccion >────── Frente   (frenteId)
SolicitudInspeccion >────── Actividad (actividadId)
SolicitudInspeccion >────── Inspector (inspectorId — referencia externa)
```

## Esquema de Persistencia (referencia para la capa de infraestructura)

Las entidades JPA viven en `infrastructure.persistence.entity` y NO son las mismas que las
entidades de dominio. Los mappers en `infrastructure.persistence.mapper` convierten entre ambas.

```sql
-- Tablas de catálogo (datos precargados)
proyectos     (id UUID PK, nombre VARCHAR(200), descripcion TEXT)
frentes       (id UUID PK, proyecto_id UUID FK→proyectos, nombre VARCHAR(200), residente_id UUID)
actividades   (id UUID PK, frente_id UUID FK→frentes, nombre VARCHAR(200), descripcion TEXT)

-- Tabla principal
solicitudes_inspeccion (
  id              UUID PRIMARY KEY,
  proyecto_id     UUID NOT NULL,
  frente_id       UUID NOT NULL,
  actividad_id    UUID NOT NULL,
  estado          VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
  fecha_creacion  TIMESTAMP NOT NULL,
  inspector_id    UUID NOT NULL
)

-- Índice de rendimiento para la consulta de duplicados (no UNIQUE — decisión v1)
-- Nota: la unicidad se garantiza solo a nivel de aplicación en v1 (check-then-act en
-- CrearSolicitudInspeccionUseCase). La baja concurrencia esperada (obra individual) hace
-- aceptable esta estrategia. En v2 se evaluará una restricción UNIQUE parcial para
-- proteger la condición de carrera bajo carga concurrente real.
INDEX idx_solicitud_duplicado ON solicitudes_inspeccion
  (proyecto_id, frente_id, actividad_id, estado)
  WHERE estado = 'PENDIENTE'
```
