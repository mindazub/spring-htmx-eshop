# Authentication & Session Management Guide

## Overview

This Spring Boot application implements a complete Laravel-like authentication and session management system with:
- User registration with validation
- User login with password hashing (BCrypt)
- Session management (30-minute timeout)
- Logout with audit logging
- Role-based access control (ADMIN, MANAGER, USER)
- Audit trail for all authentication events

## Features Implemented

### 1. **User Registration** (`/register`)
- Full name, email, and password fields
- Password confirmation validation
- Email uniqueness check
- Password minimum length requirement (6 characters)
- Server-side validation
- Client-side validation feedback

### 2. **User Login** (`/login`)
- Email (username) and password authentication
- Secure password hashing using BCrypt
- Error messages for invalid credentials
- Session expiration warnings
- Demo credentials displayed for testing

### 3. **Session Management**
Similar to Laravel's session system:
- **Session Duration**: 30 minutes of inactivity (configurable via `server.servlet.session.timeout`)
- **Session Storage**: In-memory by default (can be changed to JDBC for persistence)
- **Cookie Settings**:
  - Name: `JSESSIONID`
  - HttpOnly: true (prevents JavaScript access)
  - SameSite: Lax (CSRF protection)
  - Secure: false (development; should be true in production)
- **Session Fixation Protection**: Migrates session ID on login
- **Session Concurrency**: Limits concurrent sessions per user to 1

### 4. **Logout** (`/logout`)
- Secure session invalidation
- Authentication clearing
- Cookie deletion
- Audit event logging
- Automatic redirect to home page

### 5. **Role-Based Access Control**
- **ADMIN**: Full access to `/admin/**` endpoints
- **MANAGER**: Access to `/manager/**` endpoints
- **USER**: Access to protected user resources like `/cart/**`
- Public endpoints: `/`, `/login`, `/register`, `/products`, `/products/**`

### 6. **Audit Logging**
All authentication events are logged:
- User ID
- Email
- Event type (LOGIN, LOGOUT, PAGE_VIEW)
- Timestamp
- IP Address
- User Agent

## Architecture

### Security Configuration (`SecurityConfiguration.java`)
```java
// Key components:
- PasswordEncoder: BCryptPasswordEncoder with default strength (10)
- DaoAuthenticationProvider: Uses UserService for authentication
- SecurityFilterChain: Defines HTTP security rules and form login
- CustomAuthenticationSuccessHandler: Logs successful logins
- CustomLogoutSuccessHandler: Logs logout events
```

### User Entity (`User.java`)
Implements Spring Security's `UserDetails` interface:
```java
@Entity
public class User implements UserDetails {
    - id: Long (Primary Key)
    - email: String (Unique)
    - password: String (Hashed with BCrypt)
    - fullName: String
    - roles: Set<UserRole> (ADMIN, MANAGER, USER)
    - enabled: Boolean
    - accountNonLocked: Boolean
    - accountNonExpired: Boolean
    - credentialsNonExpired: Boolean
    - createdAt: LocalDateTime
    - lastLoginAt: LocalDateTime
    - deleted: Boolean (soft delete)
}
```

### User Service (`UserService.java`)
- Implements `UserDetailsService` for Spring Security integration
- Password encoding for new users
- User lookup by email
- Last login timestamp updates
- Role management

### Session Helper (`SessionHelper.java`)
Provides convenient methods similar to Laravel's auth helpers:
```java
sessionHelper.isAuthenticated()           // Check if user is logged in
sessionHelper.getCurrentUser()            // Get current User object
sessionHelper.getCurrentUserId()          // Get user ID
sessionHelper.getCurrentUserEmail()       // Get user email
sessionHelper.isAdmin()                   // Check admin role
sessionHelper.isManager()                 // Check manager role
sessionHelper.getSessionIdleSeconds()     // Get idle time
sessionHelper.isSessionAboutToExpire()    // Warn before expiration
```

## API Endpoints

### Authentication Endpoints
| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/login` | GET | Display login form |
| `/login` | POST | Process login (handled by Spring Security) |
| `/register` | GET | Display registration form |
| `/register` | POST | Process user registration |
| `/logout` | POST | Logout user and invalidate session |
| `/api/user-info` | GET | Get current user info (JSON) |

### Protected Endpoints
- `/products` - Public (read-only)
- `/products/{id}` - Public (read-only)
- `/cart/**` - Requires authentication
- `/admin/**` - Requires ADMIN role
- `/manager/**` - Requires ADMIN or MANAGER role

## Configuration

### Session Settings (`application.properties`)
```properties
# Session Management
spring.session.store-type=none                      # Use in-memory; change to 'jdbc' for persistence
server.servlet.session.timeout=30m                  # 30 minutes inactivity timeout
server.servlet.session.cookie.name=JSESSIONID       # Session cookie name
server.servlet.session.cookie.http-only=true        # Prevent JavaScript access
server.servlet.session.cookie.secure=false          # Should be true in production (HTTPS)
server.servlet.session.cookie.same-site=lax         # CSRF protection
server.servlet.session.persistent=true              # Persist across requests
```

### Security Settings (`SecurityConfiguration.java`)
```java
// Session Fixation: Migratetion on login (creates new session ID)
// Maximum Sessions: 1 per user (prevents concurrent logins)
// CSRF: Disabled (for HTMX compatibility)
// Form Login: Custom success/failure handlers for audit logging
```

## Testing

### Demo Credentials
```
Admin:    admin@admin.com / admin000
Manager:  manager@example.com / manager000
User:     user1@example.com / user1000
```

### Test Scenarios

#### 1. User Registration
1. Navigate to `/register`
2. Fill in: Full Name, Email, Password, Confirm Password
3. Click "Create My Account"
4. Should redirect to login page with success message
5. Login with new credentials

#### 2. User Login
1. Navigate to `/login`
2. Enter email (username) and password
3. Click "Login to Your Account"
4. Should redirect to `/products` on success
5. Should show error message on invalid credentials

#### 3. Session Management
1. Login successfully
2. Wait 30 minutes without activity
3. Try to access protected resource (e.g., `/cart`)
4. Should redirect to `/login?expired` with expiration message

#### 4. Concurrent Session Prevention
1. Login as user1 in browser A
2. Try to login as user1 in browser B (or new incognito window)
3. Browser A's session should be invalidated
4. Browser A should be redirected to `/login?expired`

#### 5. Logout
1. Login successfully
2. Click "Logout" button
3. Should redirect to home page
4. Session cookie should be deleted
5. Attempting to access protected resource should require re-login

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
```

### User Roles Table
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    PRIMARY KEY (user_id, role)
);
```

## Security Best Practices Implemented

1. **Password Hashing**: BCrypt with default strength (10 iterations)
2. **Session Fixation Protection**: New session ID generated after login
3. **CSRF Token**: Disabled for HTMX compatibility (consider enabling in production)
4. **HttpOnly Cookies**: Session cookie not accessible via JavaScript
5. **Secure SameSite**: Cookie set to Lax for CSRF protection
6. **Audit Logging**: All login/logout events logged with IP and user agent
7. **Role-Based Access Control**: Proper role hierarchy (ADMIN > MANAGER > USER)
8. **Account Status Checks**: Verify enabled, non-locked, non-expired accounts
9. **Soft Delete**: Users are soft-deleted, not permanently removed

## Improvements Over Basic Spring Security

1. **Laravel-like Session Helper**: Easy access to user info and session state
2. **Custom Success/Failure Handlers**: Audit logging on login/logout
3. **Session Concurrency Control**: Prevent multiple simultaneous logins
4. **Session Timeout Warnings**: Can be displayed to users before expiration
5. **Last Login Tracking**: Stored in database for audit purposes
6. **Role Hierarchy**: Clear separation of admin/manager/user roles
7. **Comprehensive Error Messages**: User-friendly feedback on login failure
8. **Flash Message Support**: Error/success messages persist across redirects

## Future Enhancements

1. **Email Verification**: Confirm email before enabling account
2. **Password Reset**: Forgot password flow with email verification
3. **Two-Factor Authentication**: TOTP/SMS-based 2FA
4. **Remember Me**: "Stay logged in" checkbox for session persistence
5. **Account Lockout**: Lock account after N failed login attempts
6. **Session Store Persistence**: Move to JDBC for multi-instance deployments
7. **Social Login**: OAuth integration (Google, GitHub, etc.)
8. **Activity Log**: Detailed user action audit trail
9. **API Authentication**: JWT tokens for API endpoints
10. **LDAP/AD Integration**: Enterprise directory authentication

## Troubleshooting

### Session Expires Too Quickly
- Check `server.servlet.session.timeout` setting in `application.properties`
- Verify client is sending cookies with each request
- Check browser cookie settings

### Users Can Login Simultaneously
- Ensure `sessionConcurrency(concurrency -> concurrency.maximumSessions(1))` is configured
- Verify browser cookies are being handled correctly
- Check if using load balancer (sessions need shared store)

### Logout Not Working
- Verify POST form with `/logout` endpoint
- Check if CSRF is enabled (POST requires CSRF token)
- Verify `CustomLogoutSuccessHandler` is injected properly

### Password Not Hashing
- Ensure `PasswordEncoder` bean is configured
- Verify `UserService.createUser()` uses encoder
- Check password encoding in `DataInitializer`

## References

- [Spring Security Reference](https://spring.io/projects/spring-security)
- [Spring Boot Security Auto-configuration](https://spring.io/blog/2022/09/21/spring-boot-3-0-0-rc1-released)
- [Laravel Authentication System](https://laravel.com/docs/authentication)
- [BCrypt Hashing](https://en.wikipedia.org/wiki/Bcrypt)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

