# TechEcom - E-Commerce Application

A modern Spring Boot 4.0.5 e-commerce application with modular architecture, built for scalability to microservices.

## Features

- **E-Commerce Platform**: Browse 200+ products across 5 categories (CPUs, GPUs, Motherboards, RAM, Storage)
- **Real-time Admin Monitoring**: WebSocket integration for live user activity tracking
- **Role-Based Access Control**: Admin, Manager, and User roles
- **Product Management**: Search, filtering by category, detailed product views
- **Shopping Cart**: Full cart management with persistent storage
- **Modern Stack**: Tailwind CSS + HTMX for interactive UI without SPA complexity
- **Database Options**: H2 (development) or PostgreSQL (production)
- **Docker Support**: Complete Docker Compose setup for easy deployment

## Quick Start

### Option 1: Local Development with H2 (Easiest)

```bash
# Clone or navigate to project
cd simple-htmx-crud-auth

# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```

Visit: `http://localhost:8087`

**Demo Credentials:**
- Admin: `admin@admin.com` / `admin000`
- Manager: `manager@example.com` / `manager000`
- User: `user1@example.com` / `user1000`

**Access:**
- Products: `http://localhost:8087/products`
- Admin Dashboard: `http://localhost:8087/admin`
- H2 Console: `http://localhost:8087/h2-console`

---

### Option 2: Docker Setup (PostgreSQL)

**Prerequisites:**
- Docker Desktop installed and running

**Run with Docker Compose:**

```bash
# Navigate to project directory
cd simple-htmx-crud-auth

# Build and start all services
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

**What starts:**
- PostgreSQL 16 database on port 5432
- Spring Boot application on port 8087

**Check logs:**
```bash
docker-compose logs -f app
```

**Stop services:**
```bash
docker-compose down
```

---

## Project Structure

```
simple-htmx-crud-auth/
├── src/main/java/mindaz/simplehtmxcrudauth/
│   ├── domain/
│   │   ├── user/              # User management
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── product/           # Product catalog
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── order/             # Shopping cart
│   │       ├── entity/
│   │       ├── repository/
│   │       └── service/
│   ├── presentation/
│   │   ├── auth/              # Login/Register
│   │   ├── product/           # Product pages
│   │   ├── cart/              # Cart pages
│   │   └── admin/             # Admin dashboard
│   ├── infrastructure/
│   │   ├── security/          # Spring Security config
│   │   ├── websocket/         # WebSocket setup
│   │   └── init/              # Database seeding
│   └── shared/
│       ├── audit/             # Audit logging
│       └── event/             # Event models
├── src/main/resources/
│   ├── templates/             # Thymeleaf templates
│   ├── static/                # CSS, JS, images
│   ├── application.properties # H2 config (dev)
│   └── application-postgres.properties # PostgreSQL config
├── docker-compose.yml         # Docker setup
├── Dockerfile                 # App container
└── pom.xml                    # Maven dependencies
```

---

## Architecture Highlights

### Modular Design
- **Clear Domain Boundaries**: User, Product, Order domains are loosely coupled
- **Service Layer Abstraction**: Easy to extract into microservices later
- **Repository Pattern**: Swappable data sources
- **Event-Driven**: WebSocket events for real-time features

### Database
- **H2 (Development)**: In-memory, auto-initialized, H2 Console at `/h2-console`
- **PostgreSQL (Production)**: Persistent, scalable, Docker-managed

### Security
- Spring Security 7.0.4
- BCrypt password hashing
- Role-based access control (@PreAuthorize)
- Session management

### Real-time Features
- WebSocket STOMP protocol
- Audit logging to `/topic/audit-log`
- Admin live monitoring dashboard

---

## Database Seeding

**Auto-seeded on startup:**
- 1 Admin (admin@admin.com / admin000)
- 1 Manager (manager@example.com / manager000)
- 5 Regular Users (user1@example.com - user5@example.com / user[N]000)
- 5 Product Categories
- 200+ Products with Pexels images

See `DataInitializer.java` to customize seed data.

---

## API Endpoints

### Public
- `GET  /` - Home page
- `GET  /login` - Login page
- `POST /login` - Login (form)
- `POST /logout` - Logout
- `GET  /register` - Register page
- `POST /register` - Register user

### Products (Authenticated Users)
- `GET  /products` - Browse all products
- `GET  /products/{id}` - Product details
- `GET  /products/category/{categoryId}` - Filter by category
- `GET  /products/search?query=...` - Search products

### Cart (Authenticated Users)
- `GET  /cart` - View cart
- `POST /cart/add/{productId}` - Add to cart
- `POST /cart/remove/{productId}` - Remove from cart
- `POST /cart/update/{productId}` - Update quantity

### Admin (Admin Role Only)
- `GET  /admin` - Dashboard
- `GET  /admin/users` - User management
- `POST /admin/users/create` - Create user
- `POST /admin/users/{id}/role/{role}` - Add role to user
- `POST /admin/users/{id}/delete` - Delete user
- `GET  /admin/audit` - Audit logs

### WebSocket
- `WS  /ws/audit` - Subscribe to real-time audit events

---

## Development Workflows

### View Logs
```bash
# Local
./mvnw spring-boot:run | grep -i error

# Docker
docker-compose logs -f app
docker-compose logs -f postgres
```

### Access Database

**H2 Console (Local):**
- URL: `http://localhost:8087/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (empty)

**PostgreSQL (Docker):**
```bash
docker-compose exec postgres psql -U techapp -d techeco_db
# Inside psql:
\dt  # List tables
SELECT * FROM users;
SELECT * FROM products;
```

### Rebuild & Restart
```bash
# Local
./mvnw clean package -DskipTests
./mvnw spring-boot:run

# Docker
docker-compose down
docker-compose up --build -d
```

---

## Configuration

### Environment Variables (Docker)

See `docker-compose.yml` for these environment variables used by the app:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/techeco_db
SPRING_DATASOURCE_USERNAME=techapp
SPRING_DATASOURCE_PASSWORD=techapp_pass_2024
SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop
```

Modify `docker-compose.yml` to change database credentials.

### Spring Profiles

**Active Profile: Default (H2)**
```bash
./mvnw spring-boot:run
```

**Active Profile: PostgreSQL (Docker)**
- Automatically set via environment in docker-compose.yml
- Or manually: `SPRING_PROFILES_ACTIVE=postgres`

---

## Troubleshooting

### Build Errors
```bash
# Clean Maven cache
rm -rf ~/.m2/repository
./mvnw clean compile
```

### Database Connection Issues
```bash
# Check PostgreSQL container
docker-compose logs postgres

# Verify container is healthy
docker-compose ps
```

### Port Already in Use
```bash
# Change in docker-compose.yml or local run:
# 8087:8087 → 9090:8087 (external:internal)
```

### Lombok Not Generating
```bash
# Ensure IDE has annotation processing enabled
# Rebuild project: Ctrl+Shift+F9 (IntelliJ)
./mvnw clean compile
```

---

## Future Enhancements

- [ ] Order management and checkout
- [ ] Payment integration (Stripe/PayPal)
- [ ] Email notifications
- [ ] Product reviews and ratings
- [ ] Wishlist functionality
- [ ] Microservices extraction (Product, User, Order services)
- [ ] Redis caching layer
- [ ] API rate limiting
- [ ] Enhanced admin analytics dashboard
- [ ] Mobile app (React Native)

---

## Technology Stack

| Layer | Technology |
|-------|-----------|
| **Java** | OpenJDK 21 |
| **Framework** | Spring Boot 4.0.5 |
| **ORM** | Spring Data JPA, Hibernate |
| **Database** | H2, PostgreSQL |
| **Security** | Spring Security 7.0.4 |
| **Frontend** | Thymeleaf, HTMX, Tailwind CSS |
| **Real-time** | WebSocket, STOMP |
| **Build** | Maven 3.9 |
| **Container** | Docker, Docker Compose |

---

## License

MIT License - Feel free to use for learning and commercial projects.

---

## Support

For issues or questions:
1. Check logs: `docker-compose logs app`
2. Verify database: `docker-compose ps`
3. Rebuild: `docker-compose down && docker-compose up --build`
4. Check seed data: Visit `/admin` and review audit logs


