# 📱 Guía Paso a Paso: Instalar APK Actualizada

## 📍 Ubicación de la APK

Tu APK actualizada está aquí:
```
C:\Users\Miralmonte\MedicalAppointmentsAp\android\app\build\outputs\apk\debug\app-debug.apk
```

---

## 🎯 Opción 1: Instalar en Emulador Android (MÁS FÁCIL)

### Paso 1: Abrir Android Studio
1. Abre **Android Studio**
2. Click en **"Open"**
3. Selecciona la carpeta: `C:\Users\Miralmonte\MedicalAppointmentsAp\android`

### Paso 2: Iniciar Emulador
1. En Android Studio, click en **Device Manager** (ícono de teléfono en la barra lateral)
2. Si no tienes un emulador:
   - Click en **"Create Device"**
   - Selecciona **Pixel 5** o cualquier teléfono
   - Selecciona **API 33** o superior
   - Click **Finish**
3. Click en el botón ▶️ (Play) junto al emulador para iniciarlo
4. Espera a que el emulador arranque completamente

### Paso 3: Instalar APK en el Emulador

**Método A - Arrastrar y Soltar (MÁS SIMPLE):**
1. Abre el explorador de archivos
2. Navega a: `C:\Users\Miralmonte\MedicalAppointmentsAp\android\app\build\outputs\apk\debug`
3. **Arrastra** `app-debug.apk` y **suéltala** sobre la ventana del emulador
4. ¡Listo! La app se instalará automáticamente

**Método B - Usando ADB:**
```powershell
cd C:\Users\Miralmonte\MedicalAppointmentsAp\android\app\build\outputs\apk\debug
adb install -r app-debug.apk
```

### Paso 4: Abrir la App
1. En el emulador, busca el ícono de "AppCitas"
2. Tócalo para abrir
3. ¡La app está lista con todos los arreglos!

---

## 🎯 Opción 2: Instalar en Dispositivo Físico Android

### Requisitos Previos:
✅ Cable USB
✅ Activar "Opciones de desarrollador" en tu teléfono
✅ Activar "Depuración USB"

### Paso 1: Activar Depuración USB en tu Teléfono

1. Abre **Configuración** en tu Android
2. Ve a **Acerca del teléfono**
3. Toca **Número de compilación** 7 veces seguidas
4. Aparecerá un mensaje: "Ahora eres desarrollador"
5. Vuelve atrás y entra en **Opciones de desarrollador**
6. Activa **Depuración USB**

### Paso 2: Conectar el Teléfono

1. Conecta tu teléfono a la PC con USB
2. En el teléfono aparecerá: "¿Permitir depuración USB?"
3. Marca "Permitir siempre desde este equipo"
4. Toca **Permitir**

### Paso 3: Verificar Conexión

Abre PowerShell y ejecuta:
```powershell
adb devices
```

Deberías ver algo como:
```
List of devices attached
ABC123XYZ    device
```

### Paso 4: Instalar APK

```powershell
cd C:\Users\Miralmonte\MedicalAppointmentsAp\android\app\build\outputs\apk\debug
adb install -r app-debug.apk
```

Verás:
```
Performing Streamed Install
Success
```

### Paso 5: Configurar IP del Backend (IMPORTANTE)

Como usas dispositivo físico, necesitas cambiar la IP:

1. En tu PC, abre PowerShell y ejecuta:
   ```powershell
   ipconfig
   ```

2. Busca tu **IPv4** (ej: `192.168.1.100`)

3. Abre Android Studio y edita `RetrofitClient.kt`:
   ```kotlin
   private const val BASE_URL = "http://192.168.1.100:8090/"
   ```
   (Reemplaza con TU IPv4)

4. Recompila:
   ```powershell
   cd C:\Users\Miralmonte\MedicalAppointmentsAp\android
   .\gradlew.bat assembleDebug
   ```

5. Reinstala la APK con el comando del Paso 4

### Paso 6: Abrir la App
1. Busca "AppCitas" en el cajón de aplicaciones
2. ¡Listo!

---

## 🎯 Opción 3: Compartir APK por Otras Vías

### Por Email/Drive:
1. Copia la APK: `C:\Users\Miralmonte\MedicalAppointmentsAp\android\app\build\outputs\apk\debug\app-debug.apk`
2. Envíala a tu email o súbela a Google Drive
3. Ábrela en tu teléfono
4. Acepta "Instalar desde fuentes desconocidas"
5. Instala

### Por Bluetooth:
1. Activa Bluetooth en PC y teléfono
2. Empareja los dispositivos
3. Envía `app-debug.apk` por Bluetooth
4. Instala en el teléfono

---

## ✅ Verificar que Todo Funciona

Una vez instalada la app:

### 1. Probar Edición de Nombre
- Ve a **Mi Perfil**
- Toca la tarjeta de nombre de usuario
- Cambia tu nombre
- Verifica que se guarde

### 2. Probar Google Maps
- En **Mi Perfil**, busca "Madrid" en el Search
- O toca el mapa para seleccionar un punto
- Presiona "Elegir sitio favorito"
- Sal y vuelve → debe estar guardado

### 3. Probar Carga de Médicos
- **IMPORTANTE:** Primero asegúrate que el servidor esté corriendo
- Ve a **Crear Cita**
- Deberías ver 12 médicos en el dropdown
- Si no aparecen:
  - Verifica que el servidor esté en http://localhost:8090 (emulador)
  - O en http://TU_IP:8090 (dispositivo físico)

---

## 🐛 Problemas Comunes

### "No se puede instalar la app"
- Desinstala la versión anterior primero
- Asegúrate de activar "Fuentes desconocidas" en Configuración

### "adb: command not found"
- ADB viene con Android Studio
- Ruta típica: `C:\Users\TuUsuario\AppData\Local\Android\Sdk\platform-tools`
- Agrégala al PATH o usa la ruta completa

### "Los médicos no cargan"
- Verifica: http://localhost:8090/api/doctors en un navegador
- Si NO devuelve JSON → el servidor no está corriendo
- Si SÍ devuelve JSON → problema de red en el dispositivo

### "Error de red / Unable to resolve host"
- **Emulador:** Usa `10.0.2.2` (ya está configurado)
- **Dispositivo físico:** Asegúrate de cambiar a tu IP local
- Verifica que PC y teléfono estén en la misma red WiFi

---

## 🎉 ¡Listo!

Ahora tienes la app actualizada con:
- ✅ Nombre de usuario editable
- ✅ Google Maps funcional con búsqueda
- ✅ Selección de ubicación favorita
- ✅ Todo compilado y listo para usar

**¿Necesitas ayuda con algún paso específico?** ¡Avísame!
