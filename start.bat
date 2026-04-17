@echo off
REM TechEcom Quick Start Script for Windows

echo.
echo ======== TechEcom Application Setup ========
echo.

REM Check if Docker is available
where docker >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Docker found. Choose setup option:
    echo 1. Docker with PostgreSQL
    echo 2. Local with H2
    set /p choice="Enter choice (1 or 2): "

    if "%choice%"=="1" (
        echo Starting Docker Compose...
        docker-compose up --build -d
        echo.
        echo ✅ Services started!
        echo 📱 App: http://localhost:8087
        echo 🗄️  PostgreSQL: localhost:5432
        echo.
        echo Demo Credentials:
        echo   Admin: admin@admin.com / admin000
        echo   Manager: manager@example.com / manager000
        echo   User: user1@example.com / user1000
        pause
        exit /b 0
    )
)

echo Starting with local H2...
call mvnw clean package -DskipTests
call mvnw spring-boot:run


