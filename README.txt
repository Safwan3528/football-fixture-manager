Football Fixture Management System
================================

Requirements:
- Windows Operating System
- Internet connection (for first-time setup)

Installation Steps:
1. Run 'install.bat' as Administrator (right-click -> Run as Administrator)
   This will install:
   - Java Development Kit (JDK) 17
   - JavaFX SDK 17.0.2

2. After installation completes, you can run the application using:
   - Double-click 'run.bat'
   OR
   - Double-click 'FootballFixtureManagement.exe'

If you already have Java 17 and JavaFX installed:
1. Make sure JAVA_HOME points to your Java 17 installation
2. Make sure JAVAFX_HOME points to your JavaFX SDK installation
3. Run the application using run.bat or the .exe file

Troubleshooting:
- If you get "Java environment required" error:
  * Run install.bat to set up the required environment
  * Make sure your system environment variables are set correctly
- If the application doesn't start:
  * Open Command Prompt
  * Navigate to the application folder
  * Run: java -version
  * Should show version 17 or higher
