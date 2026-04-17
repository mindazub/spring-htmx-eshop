# 🔧 Troubleshooting Guide

## Build Issues

### ❌ Maven Compilation Error: "Cannot load from object array because 'this.hashes' is null"

**Cause:** Java/Lombok version mismatch

**Solution:**
```bash
# Clean Maven cache
rm -rf ~/.m2/repository  # Mac/Linux
rmdir /s %USERPROFILE%\.m2\repository  # Windows

# Rebuild
./mvnw clean compile -U
```

---

### ❌ POM Parse Error: "Non-parseable POM"

**Cause:** Invalid XML syntax (usually whitespace/formatting)

**Solution:**
1. Check for non-ASCII characters
2. Ensure all tags properly closed
3. Validate XML structure
```bash
./mvnw validate
```

---

### ❌ "Lombok not found" or "Annotation processor failed"

**Cause:** Lombok version mismatch or IDE configuration

**Solution:**
```bash
# Clear and rebuild
./mvnw clean compile -e

# IntelliJ IDEA:
# Settings → Build → Compiler → Annotation Processors
# ✓ Enable annotation processing
# ✓ Use compiler integrated from IDE
```

---

## Docker Issues

### ❌ Docker Compose "Connection refused"

**Cause:** Services not fully started

**Solution:**
```bash
# Check service status
docker-compose ps

# View logs
docker-compose logs postgres
docker-compose logs app

# Wait for health checks to pass
docker-compose logs -f
```

---

### ❌ PostgreSQL "psql: connection refused"

**Cause:** Database container not ready

**Solution:**
```bash
# Check if container is running
docker ps | grep postgres

# Restart services
docker-compose restart

# View PostgreSQL logs
docker-compose logs postgres

# Check health status
docker-compose ps postgres
# STATUS should be "healthy"
```

---

### ❌ Port Already in Use (8087 or 5432)

**Cause:** Another application using the port

**Solution - Docker:**
```yaml
# Edit docker-compose.yml
services:
  postgres:
    ports:
      - "5433:5432"  # Use 5433 instead of 5432
  app:
    ports:
      - "9090:8087"  # Use 9090 instead of 8087
```

**Solution - Local Maven:**
```bash
# Run on different port
./mvnw spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=9090"
```

---

### ❌ Volume Permission Denied

**Cause:** Docker volume permissions issue

**Solution:**
```bash
# Remove and recreate volumes
docker-compose down -v
docker-compose up --build -d

# Or check volume permissions
docker volume ls
docker volume inspect techeco-postgres_data
```

---

## Database Issues

### ❌ "JDBC Connection to H2 failed"

**Cause:** H2 JDBC URL incorrect

**Solution:** Verify `application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
```

Visit: http://localhost:8087/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (leave blank)

---

### ❌ PostgreSQL "Database does not exist"

**Cause:** Database not created in container

**Solution:**
```bash
# Enter PostgreSQL container
docker-compose exec postgres psql -U techapp

# Create database
CREATE DATABASE techeco_db;
\l  # List databases

# Exit
\q
```

Or simply restart:
```bash
docker-compose down -v
docker-compose up --build -d
```

---

### ❌ H2 Console Shows Empty Tables

**Cause:** Data initialization didn't run

**Solution:**
```bash
# Check DataInitializer logs
docker-compose logs app | grep -i "data\|seed\|initializer"

# Or manually reinit
# Delete H2 database, restart app
./mvnw spring-boot:run

# Check H2 console again
```

---

## Application Issues

### ❌ "Login always fails"

**Cause:** User not created or password mismatch

**Solution:**
```bash
# Check if users table has data
# H2 Console or PostgreSQL:
SELECT * FROM users;

# If empty, restart to trigger DataInitializer
docker-compose restart app
# or
./mvnw spring-boot:run

# Demo credentials:
# admin@admin.com / admin000
# manager@example.com / manager000
```

---

### ❌ Products page shows "No products found"

**Cause:** Data not seeded

**Solution:**
```bash
# Check products table
# In H2 Console or PostgreSQL:
SELECT COUNT(*) FROM products;

# Should show 200+

# If empty, check DataInitializer:
docker-compose logs app | grep -i "product"

# Rebuild if needed:
docker-compose down -v
docker-compose up --build -d
```

---

### ❌ WebSocket audit logs not updating (Admin dashboard)

**Cause:** WebSocket connection failed

**Solution:**
1. Check browser console for errors: F12 → Console
2. Verify WebSocket URL: Should be `ws://localhost:8087/ws/audit`
3. Check firewall/proxy blocking WebSocket
4. Restart app:
```bash
docker-compose restart app
```

---

### ❌ "403 Forbidden" on admin pages

**Cause:** User doesn't have ADMIN role

**Solution:**
```bash
# Login with correct credentials
# Only admin@admin.com has admin access

# Check user roles in database:
# SELECT * FROM users u 
# LEFT JOIN user_roles ur ON u.id = ur.user_id;

# Grant admin role:
# UPDATE user_roles SET role = 'ADMIN' WHERE user_id = 1;
```

---

## Performance Issues

### ❌ Slow Page Loads

**Cause:** Database queries not optimized

**Solution:**
```properties
# In application.properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG

# Then check logs for slow queries
docker-compose logs app | grep -i "select\|insert\|update"
```

---

### ❌ Memory Usage High

**Cause:** H2 keeping all data in memory

**Solution:**
```bash
# Use PostgreSQL instead
docker-compose up -d

# Or configure H2 to use disk:
# spring.datasource.url=jdbc:h2:file:~/test;MODE=MySQL
```

---

## Network Issues

### ❌ App can't connect to PostgreSQL (Docker)

**Cause:** Network or DNS resolution

**Solution:**
```bash
# Check docker network
docker network ls
docker network inspect techeco-network

# Check if services can reach each other
docker-compose exec app ping postgres

# Rebuild network
docker-compose down
docker-compose up --build -d
```

---

### ❌ Browser can't connect to localhost:8087

**Cause:** Host not accessible

**Solution:**
```bash
# Check if app is running
docker ps | grep app

# Check port binding
docker port techeco-app

# Try with IP instead:
# http://127.0.0.1:8087
# http://192.168.1.x:8087  (your machine IP)
```

---

## IDE Issues

### ❌ IntelliJ: "Cannot resolve symbol"

**Cause:** Lombok or Maven not properly integrated

**Solution:**
1. **Enable Annotation Processing:**
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - ✓ Enable annotation processing
   - ✓ Use compiler integrated from IDE

2. **Install Lombok Plugin:**
   - File → Settings → Plugins
   - Search "Lombok"
   - Install & restart

3. **Rebuild Project:**
   - Build → Rebuild Project

---

### ❌ VS Code: "Maven not found"

**Cause:** Maven not in PATH

**Solution:**
```bash
# Windows
set PATH=%PATH%;C:\path\to\maven\bin

# Mac/Linux
export PATH=$PATH:/path/to/maven/bin

# Or use mvnw wrapper
./mvnw clean install
```

---

## Advanced Debugging

### Enable Full Debug Logging
```properties
# application.properties
logging.level.root=DEBUG
logging.level.org.springframework=DEBUG
logging.level.org.hibernate=DEBUG
```

### View Full Logs
```bash
# Docker
docker-compose logs -f --tail=100 app

# Local Maven
./mvnw spring-boot:run -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

### Database Query Logging
```bash
# Enter PostgreSQL
docker-compose exec postgres psql -U techapp -d techeco_db

# Run query
SELECT * FROM products LIMIT 5;
\dt  # List tables
\d products  # Describe table
```

---

## Quick Fixes Checklist

- [ ] `./mvnw clean compile`
- [ ] Delete `target/` folder
- [ ] Delete `~/.m2/repository` (Maven cache)
- [ ] Check port availability: `netstat -ano | findstr :8087`
- [ ] Restart Docker daemon
- [ ] `docker-compose down -v && docker-compose up --build -d`
- [ ] Check logs: `docker-compose logs -f`
- [ ] Rebuild IDE: Ctrl+Shift+F9 (IntelliJ)
- [ ] Clear browser cache: Ctrl+Shift+Delete
- [ ] Try incognito mode in browser

---

## Emergency Recovery

**Complete Reset:**
```bash
# Stop everything
docker-compose down -v
rm -rf target

# Clean Maven cache
rm -rf ~/.m2/repository

# Rebuild from scratch
./mvnw clean package -DskipTests

# Docker fresh start
docker-compose up --build -d
```

---

## Still Stuck? 🆘

1. **Check logs first:** `docker-compose logs -f`
2. **Verify all services:** `docker-compose ps`
3. **Rebuild everything:** `docker-compose down -v && docker-compose up --build -d`
4. **Restart IDE/VS Code**
5. **Check browser console:** F12 → Console
6. **Search project:** `grep -r "error" src/`

---

**If all else fails, contact system administrator or check project documentation.**

