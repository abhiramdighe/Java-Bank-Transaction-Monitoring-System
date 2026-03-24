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
- No external libraries — uses only the Java standard library

Download the JDK from: https://adoptium.net/

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
│   │   │   └── ValidationUtil.java
│   │   ├── repository/               ← Storage layer
│   │   │   ├── UserRepository.java
│   │   │   ├── TransactionRepository.java
│   │   │   ├── FlatFileUserRepository.java
│   │   │   └── FlatFileTransactionRepository.java
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
├── compile_and_run.bat               ← Windows build & run
├── compile_and_run.sh                ← Linux/macOS build & run
└── README.md
```

---

## Data Storage

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
Repository Layer (Interface + Flat-file impl.)
      ↓
Model Layer (Data classes)
```

To add a **database** in the future, implement `UserRepository` and
`TransactionRepository` with JDBC and swap them in `BankApplication.java`.
No other files need to change.
