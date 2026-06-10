<div align="center">

# ⚡ AKIRA
### Sistema de Alquiler y Venta de Vehículos Eléctricos

*Inspirado en la estética futurista del anime japonés Akira*

</div>

---

## ¿Qué es AKIRA?

**AKIRA** es una plataforma administrativa y comercial desarrollada en Java que automatiza y centraliza los procesos de una empresa de vehículos eléctricos. El sistema permite gestionar el inventario, ventas, contratos de alquiler y operaciones financieras tanto para empleados como para clientes, a través de una interfaz gráfica moderna e intuitiva construida con JavaFX.

> *El nombre y la estética del proyecto están inspirados en la película de animación japonesa Akira y su visión futurista del mundo.*

---

## 🎯 Objetivo

Desarrollar un sistema que automatice y centralice procesos clave del negocio — inventario, ventas, contratos y finanzas — mejorando la eficiencia operativa, la seguridad del sistema y la experiencia del cliente mediante una plataforma escalable.

---

## ✅ Funcionalidades Implementadas

- **Gestión de cuentas** — Registro e inicio de sesión con perfiles diferenciados: cliente y empleado.
- **Inventario de vehículos** — Control completo del catálogo de vehículos eléctricos disponibles: autos, motos, bicicletas y patinetas.
- **Contratos de alquiler y venta** — Registro y seguimiento de todas las operaciones comerciales realizadas.
- **Panel del empleado** — Dashboard con resumen de métricas: total de vehículos, ingresos, clientes y contratos activos.
- **Asistente virtual IA** — Chatbot integrado con la API de Claude que consulta el inventario en tiempo real y ayuda a los clientes a encontrar el vehículo ideal.
- **Historial de transacciones** — Registro de compras, ventas y alquileres con datos del cliente y el empleado responsable.

---

## 🏗️ Estructura del Proyecto

El sistema sigue una **arquitectura por capas** que separa claramente la interfaz, la lógica de negocio y el acceso a datos:

```
src/main/java/
├── model/          # Entidades del dominio (VehiculoElectrico, Cliente, Empleado, Contrato...)
├── dao/            # Acceso a la base de datos (JDBC con Oracle)
├── service/        # Lógica de negocio y validaciones
└── view/           # Controladores JavaFX

src/main/resources/
├── FXML/           # Interfaces de usuario
├── css/            # Estilos (paleta roja/negra)
└── Imagenes/       # Recursos gráficos
```

### Jerarquía de Modelos

```
VehiculoElectrico (abstracta)
├── AutoElectrico
├── MotoElectrica
├── BicicletaElectrica
└── PatinetaElectrica

Persona (abstracta)
├── Cliente
└── Empleado
```

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje principal |
| JavaFX 21.0.6 | Interfaz gráfica |
| Oracle Database XE 21c | Persistencia de datos |
| JDBC (ojdbc11) | Conexión a la base de datos |
| API de Anthropic (Claude) | Asistente virtual con IA |
| Maven | Gestión de dependencias |
| Git / GitHub | Control de versiones |

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos

- Java JDK 21+
- Maven 3.8+
- Oracle Database XE 21c corriendo en `localhost:1521/XEPDB1`
- Usuario de BD: `AKIRA` / Contraseña: `akira123`

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/Wildersitow/Akira.git
cd Akira

# 2. Compilar el proyecto
mvn clean compile

# 3. Ejecutar la aplicación
mvn javafx:run
```

---

## 👥 Equipo de Desarrollo

| Integrante | Rol | Responsabilidad |
|---|---|---|
| **Wilder Verdecia** | Líder de Desarrollo | Gestión del proyecto en Git/GitHub, capa View y Service |
| **Leonel Garrido** | Desarrollador 1 | Capa DAO, conexión Oracle, SQL scripts |
| **Erick Fuentes** | Desarrollador 2 | Modelo de clases, herencia, lógica de dominio |

**Docente:** Ing. Alfredo Bautista — Programación III

<div align="center">
  <sub>Proyecto académico desarrollado para Programación III · 2026</sub>
</div>
