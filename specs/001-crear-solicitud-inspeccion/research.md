# Investigación: Crear solicitud de inspección vinculada a frente y actividad

**Fecha**: 2026-06-27
**Plan**: [plan.md](plan.md)

## Decisiones Técnicas

### 1. Estrategia de actualización en tiempo real para la vista del residente

**Decisión**: Polling automático del cliente (intervalo recomendado: 5 segundos)

**Justificación**:
- La especificación define "tiempo real" como latencia < 5 segundos, no streaming continuo.
- El entorno de obra tiene conectividad variable; el polling es más resiliente que
  WebSockets o SSE ante reconexiones y caídas de red.
- YAGNI: el polling se implementa con un GET estándar que ya se necesita; WebSockets/SSE
  requieren infraestructura adicional sin beneficio demostrable para este volumen de datos.
- El servidor no necesita mantener estado de conexión por cliente.

**Alternativas consideradas**:
- Server-Sent Events (SSE) — descartado: requiere mantener conexiones abiertas en el servidor,
  más complejo de gestionar en proxies y balanceadores.
- WebSockets — descartado: overkill para un flujo unidireccional de baja frecuencia.

---

### 2. Detección de solicitudes duplicadas

**Decisión**: Validación en la capa de dominio (`SolicitudDomainService`) antes de persistir,
consultando si existe una solicitud con estado 'pendiente' para la misma combinación
(proyectoId, frenteId, actividadId). Si existe, el caso de uso lanza una excepción de negocio
específica (`SolicitudDuplicadaException`). El controlador la mapea a HTTP 409 Conflict con
un cuerpo de respuesta que incluye el ID de la solicitud existente. La confirmación explícita
del inspector se implementa como un parámetro booleano `forzar=true` en el cuerpo de la petición.

**Justificación**:
- La lógica de negocio (qué es un duplicado) pertenece al dominio, no a la infraestructura.
- 409 Conflict es el código HTTP semánticamente correcto para este escenario.
- El parámetro `forzar` evita un segundo endpoint y mantiene la API simple.

**Alternativas consideradas**:
- Constraint de base de datos único — descartado: no modeliza correctamente la regla de negocio
  (el duplicado solo aplica a estado 'pendiente', no a todas las solicitudes).
- Endpoint separado `/confirmar-duplicado` — descartado: añade complejidad de flujo innecesaria.

---

### 3. Estructura de paquetes con Arquitectura Limpia en Spring Boot

**Decisión**: Cuatro paquetes de primer nivel bajo `org.ups.liberacionesobra`:
`domain`, `application`, `infrastructure`, `presentation`. Las interfaces de repositorio
viven en `domain.repository` (puertos de salida); las implementaciones JPA en
`infrastructure.persistence`. Spring inyecta las implementaciones en los casos de uso
sin que el dominio conozca Spring.

**Justificación**: Implementación directa del Principio I de la constitución. Garantiza
que los tests unitarios del dominio no necesiten contexto de Spring.

---

### 4. Configuración de JaCoCo

**Decisión**: Añadir el plugin `jacoco` al `build.gradle`. Configurar `jacocoTestReport`
para generar HTML y XML. Configurar `jacocoTestCoverageVerification` con umbrales
>= 0.80 para cobertura de líneas e instrucciones. Excluir el paquete generado por
openapi-generator y las clases de configuración de Spring del cómputo de cobertura.
La tarea `check` depende de `jacocoTestCoverageVerification`.

**Justificación**: Cumplimiento del Principio V de la constitución. La puerta de calidad
en el build impide regresiones silenciosas de cobertura.

---

### 5. Generación de código OpenAPI

**Decisión**: Usar `openapi-generator-gradle-plugin` con generador `spring`. El contrato
fuente está en `src/main/resources/openapi/liberacionesobra-api.yaml`. El código generado
se escribe a `build/generated-sources/openapi` (excluido de control de versiones). Los
controladores implementarán las interfaces generadas; los DTOs generados se mapearán al
modelo de la capa de aplicación mediante MapStruct o mappers manuales.

**Justificación**: Cumplimiento del Principio IV de la constitución (API First).
El contrato es la única fuente de verdad; la implementación nunca puede divergir
silenciosamente.

**Alternativas consideradas**:
- Springdoc/Swagger con anotaciones en el código — descartado: invierte el flujo
  (código → documentación en lugar de contrato → código).
