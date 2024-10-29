@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
set JAVAFX_HOME=C:\Program Files\Java\javafx-sdk-17.0.2

java --module-path "%JAVAFX_HOME%\lib" ^
--add-modules javafx.controls,javafx.fxml,javafx.graphics ^
--add-opens javafx.graphics/javafx.scene=ALL-UNNAMED ^
--add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED ^
-jar football-fixture-management-1.0-SNAPSHOT.jar

pause
