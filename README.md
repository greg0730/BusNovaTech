# BusNovaTech

Proyecto final del curso Estructura de Datos. Esta entrega contiene un avance funcional de tiquetes, buses y colas. El sistema completo sigue en desarrollo.

## Grupo e integrantes

- Número de grupo: pendiente de completar
- Gregory Alpízar Montes
- Demás integrantes que participaron en esta entrega: pendiente de completar

## Requisitos y ejecución

- Java JDK 25 y NetBeans con soporte para proyectos Maven
- Abrir la carpeta del proyecto o su archivo `pom.xml` en NetBeans
- Ejecutar `BusNovaTech` con Run Project
- No se necesitan librerías externas para este avance

Actualmente `BusNovaTech` abre `PruebaIntegracion`, un menú temporal con opciones para crear y buscar tiquetes, consultar buses y ver un resumen. No requiere credenciales todavía porque el login está pendiente.

La prueba usa cuatro buses definidos en el código: uno preferencial, uno directo y dos normales. Esa configuración es únicamente para la prueba; la cantidad dinámica de buses es un requisito pendiente para el sistema completo.

## Implementado en este avance

- Registro de nombre, identificación, edad, moneda y tipo de bus con validaciones y opción de cancelar
- Número consecutivo, hora de compra, hora de abordaje inicial en -1 y servicio inicial NA
- Lista de pendientes y colas de tiquetes mediante nodos
- Asignación por tipo de bus y, para buses normales, por cantidad de personas en espera
- Inicio automático de atención cuando el inspector está libre y la cola está vacía
- Consulta de buses, personas en atención y orden de espera
- Búsqueda de tiquetes pendientes y resumen de la prueba
- Guardado y carga de tiquetes y posiciones de las colas en archivos JSON
- Preparación y recuperación de un guardado interrumpido de ambos archivos, si la preparación está completa

## Pendiente

- Configuración dinámica de terminal, buses y usuarios en `config.json`, e inicio de sesión
- Integración con un menú general definitivo
- Flujo completo de abordar, selección de servicio, pago o rechazo de pago y marcado de atendido
- Registro y consulta del historial en `atendidos.json`, con terminal y bus
- Consulta en línea al servicio del Banco Central para el tipo de cambio
- Grafo dirigido ponderado, rutas más cortas y persistencia en `grafo.json`
- Integración y pruebas del sistema completo con las partes de los compañeros
- Completar datos del grupo, integrantes y correcciones de entregas anteriores según corresponda

## Archivos de datos

La prueba de integración utiliza la carpeta `prueba-integracion` en la carpeta desde la cual se ejecuta el proyecto. Allí se guardan `tiquetes.json` y `colas.json`. Dentro de `guardado-pendiente` se preparan los archivos antes de instalarlos. Si una preparación completa queda pendiente, al iniciar se intenta terminar el guardado. Una preparación incompleta detiene la carga e informa el error.

Estos datos de prueba están excluidos de Git. Use una sola ejecución sobre la misma carpeta. La recuperación no garantiza resolver daños físicos, borrados o todos los cortes de energía.

Los lectores JSON actuales esperan el orden de campos que producen los métodos de guardado; no son lectores generales. Las horas se guardan como milisegundos y en el menú se muestran con la zona horaria de la computadora.

`PruebaTiquetes` y otras clases de prueba anteriores son independientes. Su opción de retirar no coordina el historial ni la cola y no debe usarse como flujo de pago sobre los datos integrados.

## Clases y métodos principales

- `Tiquete`: guarda los datos del cliente, número, horas, servicio y tipo de bus; sus métodos de consulta y actualización permiten leer y restaurar los datos
- `GestionTiquetes`: `crearTiquete` solicita los datos; los métodos de captura validan texto, edad, moneda y tipo de bus
- `NodoTiquete` y `ListaTiquetes`: enlazan y mantienen los pendientes; `agregar`, `buscar` y `retirar` administran la lista
- `ColaTiquetes`: `agregar`, `consultar`, `retirar` y `contar` administran la espera; `contiene` busca por número
- `Bus`: administra su cola y el tiquete en atención; `iniciarAtencion`, `finalizarAtencion` y `restaurarAtencion` gestionan ese estado
- `NodoBus` y `ListaBuses`: enlazan los buses; `agregar`, `buscar` y `seleccionar` permiten registrarlos y escoger uno
- `GestionBuses`: `asignar` escoge un bus y puede iniciar atención; `buscarBusDelTiquete` lo localiza y `abordar` inicia atención en un bus
- `ArchivoTiquetes`: `guardar` y `cargar` escriben y leen los pendientes y el siguiente número
- `ArchivoColas`: `guardar` y `cargar` conservan y recuperan los buses, el tiquete en atención y el orden de espera
- `ArchivoEstado`: `guardar` prepara ambos archivos y `cargar` recupera un guardado pendiente antes de leer el estado
- `BusNovaTech`: inicia el menú temporal de integración
- `PruebaIntegracion`: contiene el menú y consultas de este avance
- `PruebaGuardadoEstado`: ejecuta comprobaciones del guardado en una carpeta temporal separada
- `PruebaCola`, `PruebaBus`, `PruebaGuardadoColas` y `PruebaTiquetes`: pruebas individuales de clases y funciones

## Comprobación

Para la prueba automática, ejecutar `PruebaGuardadoEstado` con Run File. El resultado esperado en Output es `PRUEBAS COMPLETADAS: 43`. La prueba crea una carpeta temporal independiente y muestra su ubicación; no utiliza la carpeta `prueba-integracion`.

En el menú de integración se puede crear un tiquete, revisar su bus y cola, cerrar el programa y volver a abrirlo para comprobar que se conservan los datos. Cancelar durante la captura no debe crear un tiquete.
