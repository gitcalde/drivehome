# AGENTS.md

## Proyecto
DriveHome es un launcher Android basado en Open Car Launcher (MIT). Kotlin, vistas XML nativas, Gradle 8.9, JDK 17, minSdk 29. Hardware objetivo NWD K2401, pendiente de diagnóstico físico.

## Método común obligatorio
Leer `../dev-workflow/WORKFLOW.md`, `../dev-workflow/TASK_TEMPLATE.md` y `../dev-workflow/HANDOFF_TEMPLATE.md` desde el workspace local. Si no están disponibles, registrar el bloqueo y solicitar los documentos originales antes de modificar código. No sustituirlos por plantillas inventadas.
Seleccionar una sola tarea, asignar responsable y completar la plantilla antes de modificar código. Consultar el handoff existente, estado Git e instrucciones relevantes. Distinguir hechos de hipótesis. No repetir investigaciones documentadas ni ampliar alcance. Un único responsable por tarea. Cambios pequeños, aislados y reversibles. Revisar diff, registrar comandos y resultados reales, completar handoff y dejar siguiente acción concreta. No repetir una corrección equivalente más de dos veces sin evidencia nueva.

## Reglas técnicas
- Conservar arquitectura Kotlin/XML y contratos existentes; no añadir dependencias ni refactors ajenos a la tarea.
- Mantener licencia y atribución originales.
- Separar integración NWD de funcionalidades Android genéricas. No dar por confirmados paquetes, propiedades o servicios propietarios hasta verificarlos en el equipo.
- Mantener Android 10 como mínimo hasta que una tarea aprobada establezca otra cosa.
- Priorizar interfaz horizontal, botones grandes, bajo consumo y funcionamiento sin conexión.
- No modificar firmware, MCU, launcher OEM, propiedades persistentes, root ni ADB permanente sin autorización explícita y procedimiento de recuperación.
- No publicar APK, etiquetas ni claves de firma sin aprobación. Nunca versionar secretos o keystores.

## Git y validación (propuesta inicial)
- Rama independiente por tarea, PR hacia main; no hacer merge sin aceptación humana.
- Baseline original: 6cff1d9be66d49739a6e8df1c1235585e57e0e65. Build original: 45636869555, intento 2, exitoso.
- Validaciones focalizadas según alcance; para código Android, `gradle --no-daemon :app:testDebugUnitTest` y `gradle --no-daemon :app:assembleDebug` cuando proceda.
- CI existente en .github/workflows/build.yml. No lanzar ciclos de compilación indiscriminados.
- Validar visualmente en emulador API 29; la integración NWD requiere prueba física independiente.
- Documentar explícitamente aquello que no se ha ejecutado o comprobado.

## Bloqueos
Detener solo la parte afectada, conservar trabajo válido, registrar evidencia e impacto y pedir decisión humana si cambia alcance, arquitectura, datos o publicación.
