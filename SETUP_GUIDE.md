# Guía de Configuración e Inicio - Medical Appointments App

## Requisitos Previos

1. **Java JDK 8 o superior** instalado
2. **MySQL** instalado y en ejecución
3. **Android Studio** (para ejecutar la aplicación Android)

---

## Paso 1: Configurar Base de Datos

### 1.1 Iniciar MySQL
Asegúrate de que MySQL esté ejecutándose en tu sistema.

### 1.2 Ejecutar Script de Configuración
Abre MySQL Workbench o la consola de MySQL y ejecuta el archivo:
```
C:\Users\Miralmonte\MedicalAppointmentsAp\backend\setup_db.sql
```

O desde la línea de comandos:
```bash
mysql -u root -p < C:\Users\Miralmonte\MedicalAppointmentsAp\backend\setup_db.sql
```

Este script:
- Crea la base de datos `aplicacionCitas`
- Crea las tablas `patients`, `doctors`, y `appointments`
- Inserta 12 médicos de ejemplo con diferentes especialidades
- Inserta un paciente y 2 citas de prueba

### 1.3 Verificar Configuración
En `backend/src/main/resources/application.properties`, verifica que la configuración de MySQL coincida con tu instalación:
```properties
spring.datasource.url = jdbc:mysql://localhost:3306/aplicacionCitas
spring.datasource.username = root
spring.datasource.password = lapalamala1
```

**IMPORTANTE:** Cambia `lapalamala1` por tu contraseña de MySQL.

---

## Paso 2: Iniciar el Backend (Spring Boot)

### 2.1 Configurar JAVA_HOME (si es necesario)
Si al ejecutar Maven obtienes "JAVA_HOME not found", configúralo:

**En Windows:**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-XX.X.X"
```
(Reemplaza con la ruta correcta de tu JDK)

### 2.2 Compilar el Backend
```powershell
cd C:\Users\Miralmonte\MedicalAppointmentsAp\backend
.\mvnw.cmd clean compile
```

### 2.3 Ejecutar el Servidor
```powershell
.\mvnw.cmd spring-boot:run
```

El servidor debería iniciarse en: `http://localhost:8090`

### 2.4 Verificar que el Backend Funciona
Abre un navegador y visita:
- **Lista de médicos:** http://localhost:8090/api/doctors
- **Lista de citas:** http://localhost:8090/api/appointments

Deberías ver respuestas JSON con los datos de ejemplo.

---

## Paso 3: Ejecutar la Aplicación Android

### 3.1 Abrir el Proyecto en Android Studio
1. Abre Android Studio
2. File → Open
3. Selecciona la carpeta: `C:\Users\Miralmonte\MedicalAppointmentsAp\android`

### 3.2 Configurar Emulador o Dispositivo
- **Opción A:** Usa un emulador de Android (AVD Manager en Android Studio)
- **Opción B:** Conecta un dispositivo Android físico con USB debugging habilitado

### 3.3 Ejecutar la Aplicación
1. Espera a que Gradle termine de sincronizar
2. Click en el botón "Run" (▶️) o presiona Shift+F10
3. Selecciona tu emulador o dispositivo

### 3.4 Usar la Aplicación

**Primera vez:**
1. Regístrate con un email y contraseña
2. Inicia sesión
3. Navega a "Crear Cita"
4. Selecciona un médico del dropdown
5. Selecciona fecha y hora
6. Describe tus síntomas
7. Presiona "Agendar Cita"

**Ver tus citas:**
- Navega a "Mis Citas" desde el menú inferior
- Verás todas tus citas programadas con:
  - Nombre del médico
  - Especialidad
  - Fecha y hora
  - Síntomas

---

## Solución de Problemas

### Backend no inicia
- **Problema:** `JAVA_HOME not found`
  - **Solución:** Configura JAVA_HOME como se indicó arriba

- **Problema:** `Communications link failure` o error de MySQL
  - **Solución:** Verifica que MySQL esté ejecutándose y las credenciales sean correctas

### Android no puede conectarse al backend
- **Problema:** `Failed to connect to /10.0.2.2:8090`
  - **Solución:** Asegúrate de que el backend esté ejecutándose en puerto 8090
  - La IP `10.0.2.2` es la dirección especial del emulador para localhost

- **Si usas dispositivo físico:** Cambia la IP en `RetrofitClient.kt`:
  ```kotlin
  private const val BASE_URL = "http://TU_IP_LOCAL:8090/"
  ```

### Errores de compilación en Android
- **Solución:** Invalida caché y reinicia Android Studio
  - File → Invalidate Caches / Restart

---

## Estructura de la Aplicación

### Backend (Spring Boot)
```
backend/
├── src/main/java/com/medicalapp/
│   ├── controller/      # REST Controllers
│   ├── model/           # Entidades JPA (Appointment, Doctor, Patient)
│   ├── repository/      # Repositorios JPA
│   ├── service/         # Lógica de negocio
│   └── dto/             # Objetos de transferencia de datos
└── setup_db.sql         # Script de inicialización de BD
```

### Android
```
android/app/src/main/
├── java/com/medicalapp/
│   ├── pantallas/       # Activities (CrearCita, MisCitas, etc.)
│   ├── model/           # Modelos de datos (Cita, Doctor)
│   ├── APIS/            # Interfaces Retrofit
│   └── adapters/        # Adaptadores RecyclerView
└── res/layout/          # Archivos XML de diseño
```

---

## Características Implementadas

✅ **Backend:**
- Modelo de datos completo para citas médicas
- API REST para CRUD de citas y médicos
- Filtros por médico, fecha, paciente
- Base de datos MySQL con datos de ejemplo

✅ **Android:**
- Registro e inicio de sesión con Firebase
- Selección de médico desde lista cargada del servidor
- Selector de fecha con MaterialDatePicker
- Selector de hora con MaterialTimePicker
- Campo de descripción de síntomas
- Lista de citas agendadas con detalles completos
- Diseño moderno con Material Design

---

## Próximos Pasos (Opcionales)

- [ ] Implementar edición completa de citas
- [ ] Agregar filtros avanzados por fecha/especialidad en "Mis Citas"
- [ ] Implementar notificaciones de recordatorio de citas
- [ ] Agregar fotos de perfil de médicos
- [ ] Historial médico del paciente
