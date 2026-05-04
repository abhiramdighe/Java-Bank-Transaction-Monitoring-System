#!/usr/bin/env bash
# ============================================================
#  Secure Bank Transaction System — Linux / macOS build script
#  Requirements: JDK 11 or higher
#  Usage:  chmod +x compile_and_run.sh && ./compile_and_run.sh
# ============================================================

set -e

echo "======================================================"
echo "  Secure Bank Transaction System — Build & Run"
echo "======================================================"

# ── Check Java ──────────────────────────────────────────────
if ! command -v javac &> /dev/null; then
    echo ""
    echo "ERROR: javac not found."
    echo "Install the JDK:"
    echo "  Ubuntu/Debian : sudo apt-get install default-jdk"
    echo "  macOS (Homebrew): brew install openjdk"
    echo "  Or download from: https://adoptium.net/"
    exit 1
fi

echo "Java version  : $(java -version 2>&1 | head -1)"
echo "Javac version : $(javac -version 2>&1)"
echo ""

# ── Create output directory ─────────────────────────────────
mkdir -p out

# ── Compile ─────────────────────────────────────────────────
echo "Compiling all source files..."

javac -d out -sourcepath src \
  src/bank/exception/BankSystemException.java \
  src/bank/exception/AccountNotFoundException.java \
  src/bank/exception/AccountLockedException.java \
  src/bank/exception/AuthenticationException.java \
  src/bank/exception/InsufficientBalanceException.java \
  src/bank/exception/TransactionLimitException.java \
  src/bank/exception/ValidationException.java \
  src/bank/model/TransactionType.java \
  src/bank/model/TransactionStatus.java \
  src/bank/model/Transaction.java \
  src/bank/model/UserAccount.java \
  src/bank/util/EncryptionUtil.java \
  src/bank/util/ValidationUtil.java \
  src/bank/repository/UserRepository.java \
  src/bank/repository/TransactionRepository.java \
  src/bank/repository/FlatFileUserRepository.java \
  src/bank/repository/FlatFileTransactionRepository.java \
  src/bank/service/AuthService.java \
  src/bank/service/AccountService.java \
  src/bank/service/TransactionMonitorService.java \
  src/bank/service/TransactionService.java \
  src/bank/gui/MainWindow.java \
  src/bank/gui/LoginPanel.java \
  src/bank/gui/CreateAccountPanel.java \
  src/bank/gui/UserDashboardPanel.java \
  src/bank/gui/AdminDashboardPanel.java \
  src/BankApplication.java

echo ""
echo "Compilation successful!"
echo ""
echo "Launching Secure Bank Transaction System..."
echo "------------------------------------------------------"

# ── Run ─────────────────────────────────────────────────────
java -cp out BankApplication
