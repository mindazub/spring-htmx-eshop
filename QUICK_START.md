# 🚀 Quick Start: Authentication System

## Starting the Application

```bash
# Navigate to project directory
cd simple-htmx-crud-auth

# Build and run
./mvnw spring-boot:run
```

**Application will start at**: `http://localhost:8087`

---

## 🔑 Demo Credentials

| Role | Email | Password |
|------|-------|----------|
| **Admin** | admin@admin.com | admin000 |
| **Manager** | manager@example.com | manager000 |
| **User** | user1@example.com | user1000 |

---

## 📖 Testing the System

### 1️⃣ **Register New User**
```
1. Go to: http://localhost:8087/register
2. Enter:
   - Full Name: Your Name
   - Email: your@email.com
   - Password: TestPass123 (min 6 chars)
   - Confirm Password: TestPass123
3. Click "Create My Account"
4. You'll be redirected to login page
```

### 2️⃣ **Login to Your Account**
```
1. Go to: http://localhost:8087/login
2. Enter your email and password
3. Click "Login to Your Account"
4. You'll be logged in and redirected to products page
5. Notice your email appears in the top navbar
```

### 3️⃣ **Access Protected Resources**
```
After login, you can:
- Browse products (already public but tracked in audit)
- Add items to cart (authentication required)
- View user session info via: http://localhost:8087/api/user-info
```

### 4️⃣ **Logout**
```
1. Click "Logout" button in the top navbar
2. Session is cleared
3. You're redirected to home page
4. Trying to access protected resources requires re-login
```

### 5️⃣ **Session Management Testing**
```
Option 1: Session Timeout Test
- Login successfully
- Wait 30 minutes without any page interaction
- Try to access a protected resource (e.g., /cart)
- You'll see "Your session has expired" message

Option 2: Concurrent Session Test
- Login as user1 in Browser A
- Open Browser B (or incognito window)
- Login as user1 in Browser B
- Go back to Browser A and refresh
- Browser A will show: "Your session has expired"
- This prevents simultaneous logins (security feature)
```

---

## 🎯 Key Features to Try

### ✅ Error Handling
```
Try logging in with:
- Wrong email: Should show "Invalid email or password"
- Wrong password: Should show "Invalid email or password"
- Non-existent email: Should show "Invalid email or password"
```

### ✅ Registration Validation
```
Try registering with:
- Password < 6 chars: Shows "Password must be at least 6 characters"
- Passwords don't match: Shows "Passwords do not match"
- Existing email: Shows "Email already exists"
- Empty full name: Shows "Full name is required"
```

### ✅ Session Security
```
1. Login successfully
2. Open browser developer tools (F12)
3. Go to "Application" → "Cookies"
4. You'll see JSESSIONID cookie with:
   - HttpOnly: ✅ (JavaScript cannot access)
   - Secure: ❌ (Should be ✅ in production)
   - SameSite: Lax ✅ (CSRF protection)
```

### ✅ Audit Logging
```
1. Check the database audit table:
   - Admin: http://localhost:8087/h2-console
   - Login to H2 Console with default credentials (empty password)
   - Query: SELECT * FROM audit_event ORDER BY timestamp DESC LIMIT 10
   - You'll see all login/logout events with timestamps and IP addresses
```

---

## 🔒 Security Features Implemented

| Feature | Status | Details |
|---------|--------|---------|
| Password Hashing | ✅ | BCrypt with 10 iterations |
| Session Timeout | ✅ | 30 minutes inactivity |
| Session Fixation Protection | ✅ | New session ID on login |
| HttpOnly Cookies | ✅ | JavaScript cannot access |
| SameSite Cookies | ✅ | CSRF protection (Lax) |
| Single Session Per User | ✅ | Prevents concurrent logins |
| Role-Based Access | ✅ | ADMIN, MANAGER, USER roles |
| Audit Logging | ✅ | Login/logout events tracked |
| Email Uniqueness | ✅ | Prevents duplicate accounts |
| Account Status Checks | ✅ | Enabled, non-locked, non-expired |

---

## 📁 Important Files

| File | Purpose |
|------|---------|
| `SecurityConfiguration.java` | Spring Security setup & session config |
| `AuthController.java` | Login/register/logout endpoints |
| `SessionHelper.java` | Easy session access (like Laravel) |
| `CustomAuthenticationSuccessHandler.java` | Login event logging |
| `CustomLogoutSuccessHandler.java` | Logout event logging |
| `User.java` | User entity with Spring Security integration |
| `UserService.java` | User operations & password encoding |
| `application.properties` | Session management configuration |
| `AUTH_GUIDE.md` | Detailed authentication documentation |
| `AUTHENTICATION_IMPLEMENTATION.md` | Implementation summary |

---

## 🛠️ Configuration Reference

### Session Timeout (in `application.properties`)
```properties
# Change timeout duration (current: 30 minutes)
server.servlet.session.timeout=60m    # 60 minutes
server.servlet.session.timeout=1h     # 1 hour
server.servlet.session.timeout=24h    # 24 hours
```

### Session Storage (for production with multiple servers)
```properties
# Current: In-memory (H2)
spring.session.store-type=none

# Production: Use JDBC for shared session store
spring.session.store-type=jdbc

# Add dependency: org.springframework.session:spring-session-jdbc
```

### Security Cookie (for HTTPS in production)
```properties
# Current: Development mode
server.servlet.session.cookie.secure=false

# Production: Enable for HTTPS
server.servlet.session.cookie.secure=true
```

---

## 📊 Database Access

### H2 Console (In-Memory Database)
```
URL: http://localhost:8087/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave empty)
```

### Query Users
```sql
SELECT * FROM users;
```

### Query Audit Events
```sql
SELECT * FROM audit_event ORDER BY timestamp DESC;
```

### Query User Roles
```sql
SELECT u.email, ur.role 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id;
```

---

## 🧪 API Endpoints for Testing

| Endpoint | Method | Purpose | Auth Required |
|----------|--------|---------|---|
| `/login` | GET | Show login page | ❌ |
| `/login` | POST | Process login | ❌ |
| `/register` | GET | Show registration page | ❌ |
| `/register` | POST | Process registration | ❌ |
| `/logout` | POST | Logout user | ✅ |
| `/api/user-info` | GET | Get current user (JSON) | ✅ |
| `/products` | GET | List products | ❌ |
| `/products/{id}` | GET | View product details | ❌ |
| `/cart` | GET | View cart | ✅ |
| `/admin` | GET | Admin dashboard | ✅ ADMIN |

---

## 🐛 Troubleshooting

### **Session Not Persisting**
```
Issue: Logged in user gets logged out frequently
Solution:
1. Check timeout setting in application.properties
2. Ensure cookies are enabled in browser
3. Check if JSESSIONID cookie exists (F12 → Application → Cookies)
```

### **Can't Register New User**
```
Issue: Registration fails with error message
Solution:
1. Check password is at least 6 characters
2. Check email doesn't already exist
3. Ensure both password fields match
4. Check for validation error message on page
```

### **Can't Login After Registration**
```
Issue: Login fails after creating account
Solution:
1. Verify email is correct (check what you registered)
2. Verify password is correct (passwords are case-sensitive)
3. Check if account is enabled in database
4. Clear browser cache/cookies and try again
```

### **Only 1 User Can Login**
```
Issue: Second user can't login (concurrent session limit)
This is INTENTIONAL - prevents multiple concurrent logins
Solution:
- Use different browser/incognito window for each user
- Or disable this feature in SecurityConfiguration if needed
```

---

## 📝 Next Steps

1. **Review `AUTH_GUIDE.md`** for detailed documentation
2. **Test all demo credentials** to see role-based access control
3. **Register a new user** to test the registration flow
4. **Check H2 console** to see audit logs of your actions
5. **Review security configuration** in `SecurityConfiguration.java`
6. **Explore `SessionHelper` class** to see helper methods for session access

---

## ✨ What Makes This Laravel-like?

✅ **Session Management**: 30-min timeout, automatic logout
✅ **Session Helpers**: `SessionHelper` class provides Laravel-like helper functions
✅ **Auth Middleware**: Role-based access control similar to Laravel's `auth` middleware
✅ **Audit Trail**: Login/logout events logged like Laravel's auth events
✅ **Password Hashing**: BCrypt hashing similar to Laravel's Hash facade
✅ **User Model**: Spring's `UserDetails` equivalent to Laravel's User model

---

## 🎓 Learning Path

1. **Understand Flow**: Read `AUTHENTICATION_IMPLEMENTATION.md`
2. **Try Features**: Use Quick Start section above
3. **Review Code**: Look at `SecurityConfiguration.java` and `AuthController.java`
4. **Explore Security**: Check `SessionHelper.java` for available methods
5. **Advanced**: Read `AUTH_GUIDE.md` for production deployment tips

---

**Ready to get started? Run the application and visit `http://localhost:8087/login`! 🚀**

