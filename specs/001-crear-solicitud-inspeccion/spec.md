# Especificación de Funcionalidad: Crear solicitud de inspección vinculada a frente y actividad

**Rama de funcionalidad**: `001-crear-solicitud-inspeccion`

**Creada**: 2026-06-27

**Estado**: Borrador

**Épica**: E-01 · Flujo central de liberaciones

**Entrada**: Como inspector de calidad, quiero crear una solicitud de inspección vinculada a
proyecto, frente y actividad, para que el punto quede registrado en el sistema desde que
producción lo solicita y no se pierda en un mensaje de WhatsApp.

## Escenarios de Usuario y Pruebas *(obligatorio)*

### Historia de Usuario 1 — Registrar solicitud de inspección (Prioridad: P1)

El inspector de calidad necesita capturar formalmente que una actividad constructiva está lista
para ser inspeccionada. Hoy ese aviso se hace por WhatsApp y se pierde; el sistema debe registrar
la solicitud vinculándola al proyecto, frente y actividad correspondientes, para que exista un
rastro oficial desde el primer momento.

**Por qué esta prioridad**: Es el punto de entrada de todo el flujo de liberación. Sin el registro
de la solicitud el resto del proceso no puede existir. Aporta valor inmediato al eliminar la
dependencia de canales informales.

**Prueba independiente**: Se puede probar de forma aislada creando una solicitud con datos válidos
y verificando que el sistema la persiste con estado 'pendiente' y fecha de creación. No requiere
ninguna otra historia implementada.

**Escenarios de aceptación**:

1. **Dado** que producción ha indicado que una actividad está lista para inspección,
   **Cuando** el inspector selecciona proyecto, frente y actividad, y confirma la solicitud,
   **Entonces** el sistema registra la solicitud con estado 'pendiente' y almacena la fecha y
   hora exacta de creación.

---

### Historia de Usuario 2 — Consultar solicitudes del frente en tiempo real (Prioridad: P2)

El residente de frente necesita ver el estado actualizado de las solicitudes de inspección de su
frente sin tener que recargar la pantalla manualmente, para poder reaccionar a tiempo cuando
llega una nueva solicitud.

**Por qué esta prioridad**: Sin visibilidad en tiempo real el residente no se entera de las nuevas
solicitudes salvo que consulte activamente, lo que reduce la utilidad del sistema frente al
WhatsApp actual. Depende de la HU1 para que existan solicitudes que mostrar.

**Prueba independiente**: Se puede probar creando una solicitud desde un perfil de inspector y
verificando que aparece en la vista del residente con estado 'pendiente' sin que este recargue
la pantalla. La HU1 debe estar implementada como prerrequisito.

**Escenarios de aceptación**:

1. **Dado** que el residente ha abierto la vista de su frente,
   **Cuando** un inspector crea una nueva solicitud de inspección para ese frente,
   **Entonces** el nuevo punto aparece automáticamente en el listado del residente con estado
   'pendiente', sin necesidad de ninguna acción manual por parte del residente.

---

### Historia de Usuario 3 — Validar datos obligatorios y controlar duplicados (Prioridad: P3)

El sistema debe garantizar la integridad del registro evitando que se creen solicitudes
incompletas o duplicadas accidentalmente para el mismo punto de inspección.

**Por qué esta prioridad**: Los errores de datos degradan la calidad del proceso pero no bloquean
el flujo principal. Son salvaguardas de calidad que refuerzan la confianza en el sistema.
Depende de la HU1.

**Prueba independiente**: Se puede probar intentando guardar solicitudes con campos vacíos y
también intentando duplicar una solicitud activa, verificando que el sistema responde
correctamente en ambos casos. La HU1 debe estar implementada como prerrequisito.

**Escenarios de aceptación**:

1. **Dado** que los campos proyecto, frente y actividad son obligatorios,
   **Cuando** el inspector intenta guardar una solicitud sin completar alguno de esos campos,
   **Entonces** el sistema muestra un mensaje de error específico para cada campo faltante y
   no crea la solicitud.

2. **Dado** que ya existe una solicitud activa con estado 'pendiente' para la misma combinación
   de proyecto, frente y actividad,
   **Cuando** el inspector intenta guardar una nueva solicitud con esos mismos datos,
   **Entonces** el sistema muestra una advertencia informando de la solicitud activa existente
   y requiere confirmación explícita del inspector para proceder con la creación de una
   solicitud adicional.

---

### Casos Extremos

- ¿Qué ocurre si el inspector pierde la conectividad justo después de pulsar "Guardar"?
  ¿La solicitud se guarda o se descarta?
- ¿Qué sucede si un proyecto, frente o actividad es eliminado del catálogo mientras ya existe
  una solicitud pendiente vinculada a él?
- ¿Cómo responde la vista del residente cuando acumula decenas de solicitudes pendientes
  simultáneas?
- ¿Qué ocurre si dos inspectores crean simultáneamente la misma solicitud (condición de carrera)?

## Requisitos *(obligatorio)*

### Requisitos Funcionales

- **RF-001**: El sistema DEBE permitir al inspector crear una solicitud de inspección
  seleccionando un proyecto, un frente y una actividad de catálogos existentes.
- **RF-002**: El sistema DEBE registrar automáticamente el estado inicial 'pendiente' y la
  fecha/hora de creación al confirmar una solicitud.
- **RF-003**: El sistema DEBE validar que los campos proyecto, frente y actividad están
  completos antes de permitir el guardado, mostrando un mensaje de error específico por
  cada campo vacío.
- **RF-004**: El sistema DEBE detectar si ya existe una solicitud activa (estado 'pendiente')
  para la misma combinación de proyecto, frente y actividad, y requerir confirmación explícita
  del inspector antes de crear una solicitud adicional.
- **RF-005**: El sistema DEBE mostrar al residente las solicitudes de su frente actualizadas
  automáticamente, sin que el residente tenga que recargar la pantalla manualmente.
- **RF-006**: El sistema DEBE garantizar que cada solicitud quede vinculada de forma unívoca
  a un proyecto, un frente y una actividad.

### Entidades Clave

- **SolicitudInspeccion**: Petición formal de inspección de una actividad. Atributos clave:
  identificador único, proyecto, frente, actividad, estado (pendiente / aprobada / rechazada),
  fecha y hora de creación, inspector solicitante.
- **Proyecto**: Unidad organizativa de nivel superior que agrupa frentes y actividades.
  Sirve como contexto de catalogación.
- **Frente**: Sector o zona de trabajo dentro de un proyecto. Cada residente supervisa uno
  o más frentes.
- **Actividad**: Tarea constructiva concreta dentro de un frente que puede ser inspeccionada
  y liberada.

## Criterios de Éxito *(obligatorio)*

### Resultados Medibles

- **CE-001**: Los inspectores pueden registrar una nueva solicitud de inspección en menos de
  2 minutos desde que producción indica que la actividad está lista.
- **CE-002**: El 100% de las solicitudes creadas quedan registradas con estado 'pendiente' y
  fecha de creación, eliminando la dependencia de mensajes informales.
- **CE-003**: Las nuevas solicitudes aparecen en la vista del residente en menos de 5 segundos
  desde su creación, sin ninguna acción manual por parte del residente.
- **CE-004**: El 100% de los intentos de guardar con campos obligatorios vacíos son bloqueados
  y el usuario recibe retroalimentación específica sobre el campo faltante.
- **CE-005**: La tasa de solicitudes duplicadas creadas sin confirmación explícita del inspector
  es cero.

## Supuestos

- Los catálogos de proyectos, frentes y actividades existen previamente en el sistema y están
  disponibles al momento de crear la solicitud.
- Los usuarios (inspector y residente) ya están autenticados en el sistema; la autenticación
  y gestión de roles queda fuera del alcance de esta historia.
- El sistema opera principalmente en dispositivos móviles en obra con conectividad variable.
  Se asume conectividad disponible para el registro (soporte offline queda fuera del alcance
  de la versión 1).
- Cada residente está asociado a uno o más frentes en el sistema y esta asociación ya existe.
- "Tiempo real" significa actualización automática con latencia menor a 5 segundos, no
  necesariamente streaming continuo.
- El estado 'pendiente' es el único estado "activo" relevante para la regla de detección de
  duplicados; solicitudes en estado 'aprobada' o 'rechazada' no bloquean la creación de nuevas
  solicitudes para el mismo punto.
- Los catálogos de proyectos, frentes y actividades son de solo lectura en v1; no se contemplan
  operaciones de eliminación ni modificación de entidades de catálogo mientras existan solicitudes
  vinculadas a ellas. La integridad referencial en ese escenario queda diferida a v2.
- La detección de solicitudes duplicadas se implementa mediante verificación a nivel de aplicación
  (check-then-act). La concurrencia simultánea de dos inspectores creando el mismo punto es un
  caso extremo identificado pero aceptado en v1 dado el volumen esperado (obra individual, bajo
  número de inspectores concurrentes). La protección a nivel de base de datos (restricción UNIQUE
  parcial) queda diferida a v2 junto con un análisis de carga real.
