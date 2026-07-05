# Guía de Validación: Crear solicitud de inspección

**Fecha**: 2026-06-27
**Prerequisito**: [plan.md](plan.md) · [data-model.md](data-model.md) · [contracts/openapi.yaml](contracts/openapi.yaml)

Esta guía describe los escenarios de validación que deben ejecutarse para confirmar que la
funcionalidad funciona de extremo a extremo. No incluye código de implementación; ese detalle
pertenece a las tareas de implementación.

## Prerrequisitos

1. Java 25 instalado y configurado.
2. Gradle disponible (`./gradlew` o `gradle`).
3. El servicio arranca con `./gradlew bootRun` (H2 en memoria, datos de prueba cargados).
4. `curl` o una herramienta equivalente (Postman, HTTPie) disponible para llamadas manuales.

## Datos de Prueba (cargados al arrancar)

El servicio debe inicializar los siguientes datos de catálogo al arrancar en perfil de desarrollo:

```
Proyecto:  id=11111111-0000-0000-0000-000000000001  nombre="Proyecto Alfa"
Frente:    id=22222222-0000-0000-0000-000000000001  proyectoId=11111...  nombre="Frente Norte"  residenteId=...
Actividad: id=33333333-0000-0000-0000-000000000001  frenteId=22222...  nombre="Encofrado columnas"
Inspector: id=44444444-0000-0000-0000-000000000001  (referencia externa)
```

## Escenario 1 — Crear solicitud válida (HU-1, P1)

**Historia de usuario**: HU-1 — Registro de solicitud de inspección

### Pasos

```bash
# Arrancar el servicio
./gradlew bootRun

# Crear una solicitud válida
curl -s -X POST http://localhost:8080/solicitudes-inspeccion \
  -H "Content-Type: application/json" \
  -d '{
    "proyectoId":  "11111111-0000-0000-0000-000000000001",
    "frenteId":    "22222222-0000-0000-0000-000000000001",
    "actividadId": "33333333-0000-0000-0000-000000000001",
    "inspectorId": "44444444-0000-0000-0000-000000000001"
  }' | jq .
```

### Resultado Esperado

- Código HTTP: `201 Created`
- Cuerpo de respuesta incluye `id` (UUID), `estado: "PENDIENTE"`, `fechaCreacion` (timestamp actual)
- La solicitud persiste y puede recuperarse con `GET /solicitudes-inspeccion/{id}`

---

## Escenario 2 — Consultar solicitudes del frente sin recargar (HU-2, P2)

**Historia de usuario**: HU-2 — Consulta en tiempo real de solicitudes por frente

### Pasos

```bash
# Consultar solicitudes del frente del residente
curl -s "http://localhost:8080/solicitudes-inspeccion?frenteId=22222222-0000-0000-0000-000000000001" | jq .
```

### Resultado Esperado

- Código HTTP: `200 OK`
- La solicitud creada en el Escenario 1 aparece en el listado con `estado: "PENDIENTE"`
- Repetir la llamada después de crear una segunda solicitud confirma que el listado se actualiza
  (el cliente puede llamar periódicamente cada 5 segundos)

---

## Escenario 3 — Campos obligatorios vacíos (HU-3, P3)

**Historia de usuario**: HU-3 — Validación de datos obligatorios

### Pasos

```bash
# Intentar guardar sin frenteId
curl -s -X POST http://localhost:8080/solicitudes-inspeccion \
  -H "Content-Type: application/json" \
  -d '{
    "proyectoId":  "11111111-0000-0000-0000-000000000001",
    "actividadId": "33333333-0000-0000-0000-000000000001",
    "inspectorId": "44444444-0000-0000-0000-000000000001"
  }' | jq .
```

### Resultado Esperado

- Código HTTP: `400 Bad Request`
- El campo `errores` contiene exactamente un elemento con `campo: "frenteId"` y un mensaje
  descriptivo en español
- No se crea ninguna solicitud

---

## Escenario 4 — Detección de duplicado sin confirmación (HU-3, P3)

**Historia de usuario**: HU-3 — Control de duplicados

### Pasos

```bash
# (El Escenario 1 ya creó una solicitud pendiente para este punto)
# Intentar crear un duplicado sin confirmar
curl -s -X POST http://localhost:8080/solicitudes-inspeccion \
  -H "Content-Type: application/json" \
  -d '{
    "proyectoId":  "11111111-0000-0000-0000-000000000001",
    "frenteId":    "22222222-0000-0000-0000-000000000001",
    "actividadId": "33333333-0000-0000-0000-000000000001",
    "inspectorId": "44444444-0000-0000-0000-000000000001"
  }' | jq .
```

### Resultado Esperado

- Código HTTP: `409 Conflict`
- El cuerpo incluye `codigoError: "SOLICITUD_DUPLICADA"` y el campo `solicitudExistenteId`
  con el UUID de la solicitud activa existente
- No se crea una segunda solicitud

---

## Escenario 5 — Crear duplicado con confirmación explícita (HU-3, P3)

```bash
curl -s -X POST http://localhost:8080/solicitudes-inspeccion \
  -H "Content-Type: application/json" \
  -d '{
    "proyectoId":  "11111111-0000-0000-0000-000000000001",
    "frenteId":    "22222222-0000-0000-0000-000000000001",
    "actividadId": "33333333-0000-0000-0000-000000000001",
    "inspectorId": "44444444-0000-0000-0000-000000000001",
    "forzar": true
  }' | jq .
```

### Resultado Esperado

- Código HTTP: `201 Created`
- Se crea una segunda solicitud con un nuevo `id` y estado `PENDIENTE`
- El listado del frente ahora muestra dos solicitudes pendientes para el mismo punto

---

## Validación de Cobertura

```bash
# Ejecutar pruebas y generar reporte de cobertura
./gradlew test jacocoTestReport jacocoTestCoverageVerification

# Ver reporte HTML
# build/reports/jacoco/test/html/index.html
```

### Resultado Esperado

- Todas las pruebas pasan (verde)
- Cobertura global de líneas >= 80%
- Cobertura global de ramas >= 80%
- El build NO falla por umbrales de cobertura
