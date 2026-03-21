@echo off
REM ============================================================
REM  Secure Bank Transaction System — Windows build script
REM  Requirements: JDK 11 or higher installed and on PATH
REM  Usage: Double-click this file OR run from Command Prompt
REM ============================================================

echo Checking Java installation...
java -version >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not on PATH.
    echo Please install JDK 11+ from https://adoptium.net/
    pause
    exit /b 1
)
java -version

echo.
echo Checking javac (Java compiler)...
javac -version >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo ERROR: javac not found. Please install the JDK (not just the JRE).
    echo Download from: https://adoptium.net/
    pause
    exit /b 1
)
javac -version

echo.
echo Creating output directory...
if not exist "out" mkdir out

echo.
echo Compiling all Java source files...
javac -d out -sourcepath src ^
  src\bank\exception\BankSystemException.java ^
  src\bank\exception\AccountNotFoundException.java ^
  src\bank\exception\AccountLockedException.java ^
  src\bank\exception\AuthenticationException.java ^
  src\bank\exception\InsufficientBalanceException.java ^
  src\bank\exception\TransactionLimitException.java ^
  src\bank\exception\ValidationException.java ^
  src\bank\model\TransactionType.java ^
  src\bank\model\TransactionStatus.java ^
  src\bank\model\Transaction.java ^
  src\bank\model\UserAccount.java ^
  src\bank\util\EncryptionUtil.java ^
  src\bank\util\ValidationUtil.java ^
  src\bank\repository\UserRepository.java ^
  src\bank\repository\TransactionRepository.java ^
  src\bank\repository\FlatFileUserRepository.java ^
  src\bank\repository\FlatFileTransactionRepository.java ^
  src\bank\service\AuthService.java ^
  src\bank\service\AccountService.java ^
  src\bank\service\TransactionMonitorService.java ^
  src\bank\service\TransactionService.java ^
  src\bank\gui\MainWindow.java ^
  src\bank\gui\LoginPanel.java ^
  src\bank\gui\CreateAccountPanel.java ^
  src\bank\gui\UserDashboardPanel.java ^
  src\bank\gui\AdminDashboardPanel.java ^
  src\BankApplication.java

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo *** COMPILATION FAILED — see errors above ***
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.
echo Launching Secure Bank Transaction System...
echo (Close this window to stop the application)
echo.
java -cp out BankApplication

pause
