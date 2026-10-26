# BusNovaTech
Proyecto de Estructura de Datos

## Estado actual
Esta version contiene el modulo de tiquetes y un menu de pruebas. Todavia no es el sistema completo.
Responsable de este modulo: Gregory Alpizar Montes
Pendiente completar los nombres de los demas integrantes y el numero de grupo antes de entregar.

## Ejecucion
Abrir pom.xml como proyecto Maven en NetBeans y usar JDK 25, como indica el pom.xml.
Ejecutar BusNovaTech. Por ahora abre PruebaTiquetes; al integrar el menu general se cambia esa llamada.
PruebaTiquetes tambien se puede ejecutar directamente con Run File.
No se necesitan librerias externas.

## Clases y metodos
- Tiquete: constructor con numero, nombre, identificacion, edad, moneda y tipo de bus; getters para consultar datos y setters para restaurar la compra, registrar abordaje y servicio
- GestionTiquetes: crearTiquete solicita los datos; pedirTexto, pedirEdad, pedirMoneda y pedirTipoBus validan las entradas; cancelar devuelve null y no crea el tiquete
- NodoTiquete: guarda un tiquete y su siguiente nodo; getTiquete, getSiguiente y setSiguiente permiten recorrer la estructura
- ListaTiquetes: agregar rechaza numeros repetidos; buscar consulta por numero; retirar devuelve el tiquete retirado o null; getPrimero permite recorrer la lista
- ArchivoTiquetes: guardar escribe la lista y el consecutivo; cargar reconstruye la lista; getSiguienteNumero devuelve el consecutivo leido al cargar; convertirJson y escaparTexto preparan el texto; validarTiquete revisa datos antes de guardar
- Los metodos privados saltarEspacios, esperar, leerTexto, leerUnicode, leerNumero, leerCampo, verSiguiente, leerEntero y leerTiquete interpretan el archivo
- PruebaTiquetes: main permite crear, consultar y retirar tiquetes de prueba
- BusNovaTech: main inicia temporalmente PruebaTiquetes

## Archivos y datos
Se usa tiquetes.json en la carpeta desde donde se ejecuta el programa. tiquetes.tmp es el archivo temporal del guardado. Ambos estan excluidos de Git para que cada computadora conserve sus datos.
Si no existe tiquetes.json, se inicia una lista vacia con consecutivo 1. Si existe pero esta danado, se informa el error y no se inicia el menu.
El lector manual espera los campos en el orden que produce guardar; no es un lector general de cualquier JSON.
Las horas se guardan en milisegundos. Un tiquete nuevo tiene servicio NA y hora de abordaje -1.
Monedas: Colones o Dolares. Tipos de bus: P, D o N.
Retirar un tiquete no reduce el consecutivo. La lista en memoria se persiste al llamar guardar.
El guardado usa un archivo temporal y reemplazo; no garantiza recuperacion ante cualquier corte de energia.

## Integracion pendiente
- Configuracion, login y menu general
- Buses, colas, asignacion y recuperacion de sus posiciones
- Atencion, pago, historial y tipo de cambio
- Rutas y los demas requisitos del proyecto

## Acuerdos para integrar
Usar la misma clase Tiquete entre modulos, sin crear otra copia con campos diferentes.
ListaTiquetes es el registro de pendientes, no la cola de un bus. Encolar no debe retirar el tiquete del registro pendiente.
Al cargar las colas, buscar los tiquetes por numero en la lista ya cargada y conservar el orden almacenado por el modulo de colas.
Atencion debe actualizar servicio y hora de abordaje, guardar el historial cuando corresponda y coordinar el retiro de cola y pendientes con el guardado de todos los archivos.
No usar la opcion de retiro de prueba como flujo de pago real: no genera historial ni actualiza colas.
Los nombres de metodos y el manejo de errores entre modulos se deben acordar al recibir las clases de los companeros.

## Comprobaciones manuales
1. Crear un tiquete y cancelar otra captura: solo debe existir el primero
2. Cerrar y abrir: se conservan datos y consecutivo
3. Retirar un tiquete intermedio: se conservan los demas
4. Retirar primero, ultimo y unico; crear otro despues de vaciar la lista
5. Escribir texto en edad o una opcion fuera del menu: se muestra el error
6. Usar nombres con tildes, comillas y barra invertida; cerrar y abrir para comprobarlos
