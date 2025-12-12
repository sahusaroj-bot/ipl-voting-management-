# IPL Voting Management - Security Enhancements

## Overview
Enhanced the authentication system with multiple security layers to protect against common vulnerabilities and attacks.

## Key Security Improvements

### 1. Password Security
- **BCrypt Password Hashing**: Passwords are now hashed using BCrypt with salt
- **No Plain Text Storage**: Eliminated plain text password storage
- **Secure Password Validation**: Proper password verification during login

### 2. Account Protection
- **Account Lockout Mechanism**: Accounts lock after 5 failed login attempts
- **Automatic Unlock**: Accounts unlock after 30 minutes
- **Failed Attempt Tracking**: System tracks and logs failed login attempts

### 3. JWT Token Security
- **Secure Key Generation**: Dynamic secure key generation using SecureRandom
- **Refresh Token Support**: Separate refresh tokens for extended sessions
- **Shorter Access Token Expiry**: 15-minute access tokens for better security
- **Token Type Validation**: Separate validation for access and refresh tokens

### 4. Rate Limiting
- **IP-based Rate Limiting**: Prevents brute force attacks
- **Configurable Limits**: 5 attempts per 15-minute window
- **Automatic Reset**: Rate limits reset after time window

### 5. Input Validation
- **Bean Validation**: Comprehensive input validation using Jakarta validation
- **Email Validation**: Proper email format validation
- **Username Constraints**: Length and uniqueness validation
- **Error Handling**: Structured error responses

### 6. Enhanced Security Headers
- **HSTS**: HTTP Strict Transport Security enabled
- **Frame Options**: Clickjacking protection
- **Content Type Options**: MIME type sniffing protection

### 7. Improved Authentication Flow
- **Separate Login Model**: Dedicated LoginRequest model for authentication
- **Structured Responses**: Consistent API response format
- **Comprehensive Logging**: Security event logging
- **User Registration**: Secure user registration endpoint

## API Endpoints

### Authentication Endpoints
- `POST /login` - User authentication with rate limiting
- `POST /register` - Secure user registration
- `POST /refresh` - Token refresh mechanism

### Security Features
- Input validation on all endpoints
- Rate limiting on login endpoint
- Account lockout protection
- Secure token generation and validation

## Environment Variables
Set these environment variables for production:

```bash
JWT_SECRET=your-secure-jwt-secret-key
JWT_REFRESH_SECRET=your-secure-refresh-secret-key
```

## Database Schema Updates
The User table now includes additional security fields:
- `email` - User email (unique)
- `account_locked` - Account lock status
- `failed_login_attempts` - Failed attempt counter
- `last_failed_login` - Timestamp of last failed login
- `account_locked_until` - Lock expiry timestamp
- `enabled` - Account enabled status

## Migration Notes
1. Update database schema to include new security fields
2. Hash existing passwords using BCrypt
3. Set environment variables for JWT secrets
4. Test all authentication flows
5. Monitor security logs for suspicious activity

## Security Best Practices Implemented
- Password hashing with BCrypt
- JWT token security with refresh mechanism
- Rate limiting and account lockout
- Input validation and sanitization
- Security headers configuration
- Comprehensive audit logging
- Secure key management