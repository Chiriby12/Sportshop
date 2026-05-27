@echo off
title SportShop - Iniciando servicios...
color 0A

echo.
echo  ================================================
echo   SPORTSHOP - Iniciando microservicios
echo  ================================================
echo.

echo  [1/5] Arrancando Auth (puerto 8080)...
start "sportshop-auth" cmd /k "cd /d C:\SportShop\sportshop-auth && .\gradlew.bat bootRun"
timeout /t 15 /nobreak > nul

echo  [2/5] Arrancando Catalog (puerto 8081)...
start "sportshop-catalog" cmd /k "cd /d C:\SportShop\sportshop-catalog && .\gradlew.bat bootRun"
timeout /t 10 /nobreak > nul

echo  [3/5] Arrancando Admin (puerto 8082)...
start "sportshop-admin" cmd /k "cd /d C:\SportShop\sportshop-admin && .\gradlew.bat bootRun"
timeout /t 10 /nobreak > nul

echo  [4/5] Arrancando Notifications (puerto 8083)...
start "sportshop-notifications" cmd /k "cd /d C:\SportShop\sportshop-notifications && .\gradlew.bat bootRun"
timeout /t 10 /nobreak > nul

echo  [5/5] Arrancando Frontend (puerto 3000)...
start "sportshop-frontend" cmd /k "cd /d C:\SportShop\sportshop-frontend && npm run dev"

echo.
echo  ================================================
echo   Todos los servicios iniciados.
echo   Frontend: http://localhost:3000
echo  ================================================
echo.
pause