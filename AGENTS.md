# AGENTS.md - AI Development Guide

## Project Overview

**simple-htmx-crud-auth** is a Spring Boot 4.0.5 web application combining:
- **Backend**: Spring MVC + Spring Data JPA for REST/MVC endpoints
- **Frontend**: HTMX with server-rendered HTML templates (emphasis on interactivity without SPA complexity)
- **Database**: H2 (embedded, in-memory by default for development)
- **Authentication**: Built-in Spring Security integration expected
- **Real-time**: WebSocket support for bidirectional communication

## Build & Development Commands

```bash
# Build the application
./mvnw clean package

# Run the application locally
./mvnw spring-boot:run

# Run tests
./mvnw test

# H2 Console: accessible at http://localhost:8087/h2-console (when running)
```

**Key build configuration**: Java 21, Maven 3.x required. Lombok annotation processing configured in compiler plugin for both compile and test phases.

## Architecture & Patterns

### Package Structure
- `src/main/java/mindaz/simplehtmxcrudauth/` - Application entry point and components
  - Typically includes: Controllers, Services, Repositories, Entities, DTOs
  - Use **Spring Stereotype annotations** (@Controller, @Service, @Repository) for component scanning
  
### HTMX Integration Pattern
- Controllers return HTML fragments (Thymeleaf templates) instead of JSON
- Endpoints are designed for partial DOM updates via HTMX attributes (hx-get, hx-post, etc.)
- Templates in `src/main/resources/templates/` - structure mirrors controller paths for discoverability

### Authentication & Authorization
- Expect Spring Security configuration for auth workflows
- Auth typically handles login/logout, session management
- Use `@PreAuthorize`, `@Secured` annotations on controller methods for method-level authorization

### Data Layer
- Spring Data JPA repositories (extend `JpaRepository<T, ID>`)
- H2 embedded database - data persists per session during local development
- Entity relationships and lazy-loading patterns follow standard Hibernate conventions

## Key Conventions

### Controller Naming & Routing
- Controllers typically map to resource names: `UserController` → `/users`, `ProductController` → `/products`
- Use **request/response method pattern**: GET for retrieval, POST for creation, PUT/PATCH for updates, DELETE for removal
- Return `ModelAndView` or `String` (template name) for HTMX responses, not JSON

### Template Location Convention
- Template structure mirrors controller path structure for easy discovery
- Example: `UserController.getUser()` → `templates/users/view.html` or `templates/user-detail.html`

### Testing Approach
- Use **MockMvc** for controller layer testing (`@WebMvcTest`)
- Use **@DataJpaTest** for repository testing with test database context
- Test both positive and authentication-required scenarios

## External Integration Points

- **Spring Security**: Authentication/authorization
- **Spring Data JPA**: ORM and database abstraction
- **Thymeleaf**: Server-side template engine (not included in pom.xml yet - add if using templates)
- **WebSocket**: Real-time client-server messaging (configured but usage TBD)
- **H2 Console**: Built-in browser UI for database inspection during development

## Important Developer Notes

1. **Lombok Enabled**: Use `@Data`, `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor` to avoid boilerplate
2. **Annotation Processing**: Lombok requires annotation processor configuration - already set up in pom.xml
3. **HTMX-First Mindset**: API design favors returning HTML over JSON; think in terms of DOM fragments and partial updates
4. **No Frontend Bundler**: Avoid adding webpack/npm dependencies unless explicitly needed - keep server-side rendering focus
5. **Development Database**: H2 is ephemeral; schema/seed data should be scripted (SQL files in `schema.sql`, `data.sql` or Liquibase)

## Common Modifications

- **Add an Entity**: Create class in appropriate package, annotate with `@Entity`, extend BaseEntity if exists
- **Add a REST/MVC Endpoint**: Create Controller class, use `@RequestMapping` with @GetMapping/@PostMapping, return template name for HTMX
- **Add Authentication Flow**: Extend `WebSecurityConfigurerAdapter`, configure in `SecurityConfiguration` class
- **Add Real-time Feature**: Use WebSocket handlers in `config/` folder, JavaScript on client side with STOMP library

## Deployment & Docker

### Local Development
```bash
./mvnw spring-boot:run  # Uses H2 in-memory database
```

### Docker Deployment (PostgreSQL)
```bash
docker-compose up --build -d
```

**Services:**
- PostgreSQL: postgres://techapp:techapp_pass_2024@localhost:5432/techeco_db
- App: http://localhost:8087

### Microservices Migration Strategy

**Phase 1: Extract Product Service**
- Create `product-service/` module with Product, Category, ProductImage entities
- Maintain `ProductRepository` and `ProductService` interfaces
- Product Controller calls internal service OR external REST API (toggle via config)
- Database: Separate Postgres instance for products

**Phase 2: Extract User/Auth Service**
- Create `auth-service/` module with User entity, authentication, role management
- OAuth2 token server for inter-service communication
- Other services validate JWT tokens from auth-service

**Phase 3: Order Service**
- Create `order-service/` for Cart and Order management
- Consume Product and User service APIs
- Event-driven order processing via RabbitMQ/Kafka

**API Gateway Pattern:**
- API Gateway (Spring Cloud Gateway) routes `/api/v1/products/*` → product-service
- Routes `/api/v1/users/*` → auth-service
- Routes `/api/v1/orders/*` → order-service
- Aggregates responses for client

**Current Architecture (Ready for extraction):**
- Services already have clear boundaries (`domain.product`, `domain.user`, `domain.order`)
- DTOs in `shared.*` for inter-service contracts
- Event model in `shared.event` for async communication
- Repository interfaces allow swapping implementations

