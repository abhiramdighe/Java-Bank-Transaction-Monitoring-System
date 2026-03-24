# 🏦 Secure Bank Transaction Monitoring System — Java

A modern **desktop banking application** built using **Java Swing**, featuring a layered architecture, MySQL database integration, and real-time API enhancements.
This project simulates core banking operations with a clean UI and scalable backend design.

---

## 🚀 Key Features

* 🔐 Secure User Authentication & Account Management
* 💾 MySQL Database Integration (JDBC-based)
* 💸 Deposit & Withdrawal Operations
* 📊 Transaction Monitoring & History Tracking
* 💱 Real-Time Currency Exchange (API Integration)
* 🌙 Modern Dark-Themed Dashboard UI
* 🧾 Admin Dashboard with System Insights
* ⚙️ Layered Architecture for Scalability

---

## 🛠️ Tech Stack

* **Language:** Java (JDK 11+)
* **Frontend:** Java Swing (Custom Dark UI)
* **Backend:** Core Java + Service Layer
* **Database:** MySQL
* **Connectivity:** JDBC
* **API Integration:** REST (Currency Exchange API)
* **Version Control:** Git & GitHub

---

## 🔑 Default Login Credentials

| Role  | Username | Password   |
| ----- | -------- | ---------- |
| Admin | `admin`  | `admin123` |

> New users can be registered via the **Create New Account** option.

---

## ▶️ How to Run

### 🔹 Option 1 — Windows

```bash
compile_and_run.bat
```

### 🔹 Option 2 — Linux / macOS

```bash
chmod +x compile_and_run.sh
./compile_and_run.sh
```

### 🔹 Option 3 — Manual Compilation

```bash
mkdir -p out

javac -cp ".;mysql-connector-j-9.6.0.jar" -d out -sourcepath src \
  src/bank/exception/*.java \
  src/bank/model/*.java \
  src/bank/util/*.java \
  src/bank/repository/*.java \
  src/bank/service/*.java \
  src/bank/gui/*.java \
  src/BankApplication.java

java -cp ".;out;mysql-connector-j-9.6.0.jar" BankApplication
```

---

## 🗂️ Project Structure

```
BankTransactionSystem/
├── src/
│   ├── BankApplication.java
│   ├── bank/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── util/
│   │   ├── repository/
│   │   ├── service/
│   │   └── gui/
├── compile_and_run.bat
├── compile_and_run.sh
├── database_schema.sql
└── README.md
```

---

## 🧠 System Architecture

```
GUI Layer (Swing UI)
        ↓
Service Layer (Business Logic)
        ↓
Repository Layer (Data Access)
        ↓
Database Layer (MySQL)
```

✔ Clean separation of concerns
✔ Easily extendable (e.g., switch to REST backend)

---

## 🗄️ Database Schema (MySQL)

### Users Table

* username (PK)
* password
* full_name
* gender
* age
* phone_encrypted
* email_encrypted
* role
* balance
* last_transaction_amount
* daily_transaction_count
* failed_login_attempts
* lock_timestamp

### Transactions Table

* transaction_id (PK)
* username
* type
* amount
* status
* balance_after
* timestamp

---

## 📊 Business Rules

| Operation  | Rule                                    |
| ---------- | --------------------------------------- |
| Deposit    | Max ₹50,000 per transaction             |
| Withdrawal | Cannot exceed balance                   |
| Loan       | Requires ≥10% balance                   |
| Login Lock | Locked after 3 failed attempts (24 hrs) |

---

## 🌐 API Integrations

* 💱 Currency Exchange API (USD → INR live rate)
* (Expandable for IFSC, SMS, Email, etc.)

---

## 📸 Screenshots

> Add screenshots of your UI here
> Example:

```
![Dashboard](screenshots/dashboard.png)
```

---

## 👨‍💻 Developed By

* **Abhiram Dighe (244011)**
* **Pracheta Satapathy (244012)**

---

## 🎓 Project Context

This project was developed as part of the **Internal Assessment** for:

* Java Programming
* Database Management Systems

**Diploma in Computer Engineering — Semester IV**

---

## 🚀 Future Enhancements

* 📱 SMS / Email Notifications
* 🏦 IFSC Lookup Integration
* 📈 Investment / Stock Module
* 🤖 AI Chatbot Assistant
* 🔐 Advanced Fraud Detection

---

## 📌 License

This project is intended for academic and educational purposes.
