# IntelliJ IDEA Configuration for Test Mode

## 1. Run Configuration Setup

### Step 1: Create New Run Configuration
1. Go to **Run** → **Edit Configurations**
2. Click **+** → **Spring Boot**
3. Name: `IPL Voting Test`

### Step 2: Configuration Settings
- **Main Class:** `com.example.ipl.IplVotingManagementApplication`
- **Active Profiles:** `test`
- **Program Arguments:** (leave empty)
- **VM Options:** `-Dspring.profiles.active=test`

### Step 3: Environment Variables
Add these environment variables:
```
JWT_SECRET=testSecretKey123456789012345678901234567890
JWT_REFRESH_SECRET=testRefreshSecretKey123456789012345678901234567890
FRONTEND_URL=http://localhost:3000
```

### Step 4: Working Directory
Set to: `$MODULE_WORKING_DIR$`

## 2. Database Setup

### Option 1: Run schema.sql manually
```sql
-- Execute in MySQL Workbench or command line
source schema.sql
```

### Option 2: Let Hibernate create tables
The application will auto-create tables with `spring.jpa.hibernate.ddl-auto=update`

## 3. Quick Start Steps

1. **Start MySQL Server**
2. **Create Database:** Run `CREATE DATABASE IF NOT EXISTS library;`
3. **Run Configuration:** Select "IPL Voting Test" and click Run
4. **Verify:** Check console for "Started IplVotingManagementApplication"

## 4. Test Endpoints

**Register:**
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","password":"password123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"password123"}'
```