@echo off
REM Script para iniciar el servidor backend de Medical Appointments

echo ================================================
echo   Medical Appointments Backend - Servidor
echo ================================================
echo.

REM Configurar JAVA_HOME para usar el JDK de Android Studio
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr

echo JAVA_HOME configurado: %JAVA_HOME%
echo.

echo Iniciando servidor Spring Boot en puerto 8090...
echo.
echo IMPORTANTE: Asegurate de que MySQL este ejecutandose
echo            y que hayas ejecutado setup_db.sql
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run

pause
