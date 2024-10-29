@echo off
echo Installing required components...

REM Download and extract JDK 17
echo Downloading JDK 17...
powershell -Command "& {Invoke-WebRequest -Uri 'https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_windows-x64_bin.zip' -OutFile 'jdk17.zip'}"
echo Extracting JDK 17...
powershell -Command "& {Expand-Archive -Path 'jdk17.zip' -DestinationPath 'C:\Program Files\Java' -Force}"

REM Download and extract JavaFX SDK
echo Downloading JavaFX SDK...
powershell -Command "& {Invoke-WebRequest -Uri 'https://download2.gluonhq.com/openjfx/17.0.2/openjfx-17.0.2_windows-x64_bin-sdk.zip' -OutFile 'javafx-sdk.zip'}"
echo Extracting JavaFX SDK...
powershell -Command "& {Expand-Archive -Path 'javafx-sdk.zip' -DestinationPath 'C:\Program Files\Java' -Force}"

REM Set environment variables
setx JAVA_HOME "C:\Program Files\Java\jdk-17"
setx JAVAFX_HOME "C:\Program Files\Java\javafx-sdk-17.0.2"
setx PATH "%PATH%;%JAVA_HOME%\bin"

echo Installation complete!
pause
