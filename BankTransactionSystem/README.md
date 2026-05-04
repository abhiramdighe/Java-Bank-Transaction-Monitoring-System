# Secure Bank Transaction System — Java

A layered Java/Swing desktop banking application converted and redesigned from C++.

---

## Default Login Credentials

| Role  | Username | Password  |
|-------|----------|-----------|
| Admin | `admin`  | `admin123`|

> A new user account can be created from the login screen using **Create New Account**.

---

## Requirements

- **JDK 11 or higher** (JDK 17 or 21 recommended)
- **MySQL 8.0 or higher** (for database storage)
- **MySQL Connector/J** (JDBC driver)

Download the JDK from: https://adoptium.net/
Download MySQL from: https://dev.mysql.com/downloads/mysql/
Download MySQL Connector/J from: https://dev.mysql.com/downloads/connector/j/

---

## Database Setup

1. **Install MySQL** and start the MySQL service
2. **Create database and tables** by running the SQL script:
   ```sql
   mysql -u root -p < database_schema.sql
   ```
3. **Download MySQL Connector/J** (JDBC driver JAR file)
4. **Place the JAR file** in the project root directory (same level as `src/` folder)
5. **Update database credentials** in `src/bank/util/DatabaseConfig.java` if needed

---

## How to Run

### Option 1 — Windows (easiest)
Double-click `compile_and_run.bat`

### Option 2 — Linux / macOS
```bash
chmod +x compile_and_run.sh
./compile_and_run.sh
```

### Option 3 — VS Code with Java Extension Pack
1. Install the **Extension Pack for Java** from the VS Code Marketplace
2. Open the `BankTransactionSystem` folder in VS Code
3. Press **F5** (or Run → Start Debugging) — VS Code auto-detects the project

### Option 4 — Manual (any terminal)
```bash
mkdir -p out

javac -d out -sourcepath src \
  src/bank/exception/*.java \
  src/bank/model/*.java \
  src/bank/util/*.java \
  src/bank/repository/*.java \
  src/bank/service/*.java \
  src/bank/gui/*.java \
  src/BankApplication.java

java -cp out BankApplication
```

---

## Project Structure

```
BankTransactionSystem/
├── src/
│   ├── BankApplication.java          ← Entry point
│   ├── bank/
│   │   ├── exception/                ← Custom exceptions
│   │   │   ├── BankSystemException.java
│   │   │   ├── AccountNotFoundException.java
│   │   │   ├── AccountLockedException.java
│   │   │   ├── AuthenticationException.java
│   │   │   ├── InsufficientBalanceException.java
│   │   │   ├── TransactionLimitException.java
│   │   │   └── ValidationException.java
│   │   ├── model/                    ← Data classes
│   │   │   ├── UserAccount.java
│   │   │   ├── Transaction.java
│   │   │   ├── TransactionType.java
│   │   │   └── TransactionStatus.java
│   │   ├── util/                     ← Utilities
│   │   │   ├── EncryptionUtil.java
│   │   │   ├── ValidationUtil.java
│   │   │   └── DatabaseConfig.java
│   │   ├── repository/               ← Storage layer
│   │   │   ├── UserRepository.java
│   │   │   ├── TransactionRepository.java
│   │   │   ├── FlatFileUserRepository.java
│   │   │   ├── FlatFileTransactionRepository.java
│   │   │   ├── DatabaseUserRepository.java
│   │   │   └── DatabaseTransactionRepository.java
│   │   ├── service/                  ← Business logic
│   │   │   ├── AuthService.java
│   │   │   ├── AccountService.java
│   │   │   ├── TransactionService.java
│   │   │   └── TransactionMonitorService.java
│   │   └── gui/                      ← Swing UI
│   │       ├── MainWindow.java
│   │       ├── LoginPanel.java
│   │       ├── CreateAccountPanel.java
│   │       ├── UserDashboardPanel.java
│   │       └── AdminDashboardPanel.java
├── .vscode/
│   ├── launch.json                   ← VS Code run config
│   └── settings.json                 ← VS Code Java config
├── database_schema.sql               ← MySQL database setup
├── compile_and_run.bat               ← Windows build & run
├── compile_and_run.sh                ← Linux/macOS build & run
└── README.md
```

---

## Data Storage

The system automatically detects and uses the best available storage method:

### MySQL Database (Preferred)
- **Database**: `bank_system`
- **Tables**: `users`, `transactions`
- **Requirements**: MySQL server running, JDBC driver JAR in project root
- **Setup**: Run `database_schema.sql` to create tables

### Flat-File Storage (Fallback)
The system stores data in two locations created automatically on first run:

| File / Folder               | Contents                                  |
|-----------------------------|-------------------------------------------|
| `users.txt`                 | All user accounts (pipe-delimited)        |
| `bills/<username>_activity.txt` | Transaction history per user          |

These files are created in the **same directory where you run the application**.

---

## Business Rules (preserved from C++)

| Operation  | Rule                                              |
|------------|---------------------------------------------------|
| Deposit    | Maximum Rs. 50,000 per single transaction         |
| Withdrawal | Amount must not exceed current balance            |
| Loan       | Account balance must be ≥ 10% of loan amount      |
| Login Lock | Account locked for 24 hours after 3 failed attempts |

---

## Architecture

```
GUI Layer (Swing)
      ↓
Service Layer (Business Logic)
      ↓
Repository Layer (Interface + Multiple Implementations)
      ↓
Model Layer (Data classes)
```

The repository layer supports multiple storage backends:
- **DatabaseUserRepository** / **DatabaseTransactionRepository**: MySQL database storage
- **FlatFileUserRepository** / **FlatFileTransactionRepository**: File-based storage

The application automatically selects the best available storage method at runtime.
