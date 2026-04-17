#!/bin/bash

# TechEcom Quick Start Script

echo "🚀 TechEcom Application Setup"
echo "================================"

# Check if Docker is available
if command -v docker &> /dev/null; then
    read -p "Start with Docker (Postgres)? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "Starting Docker Compose..."
        docker-compose up --build -d
        echo "✅ Services started!"
        echo "📱 App: http://localhost:8087"
        echo "🗄️  PostgreSQL: localhost:5432"
        echo ""
        echo "Demo Credentials:"
        echo "  Admin: admin@admin.com / admin000"
        echo "  Manager: manager@example.com / manager000"
        echo "  User: user1@example.com / user1000"
        exit 0
    fi
fi

echo "Starting with local H2..."
./mvnw clean package -DskipTests
./mvnw spring-boot:run


