@ECHO OFF
SETLOCAL

SET WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar
SET WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Downloading Maven Wrapper jar...
  curl.exe -fsSL "%WRAPPER_URL%" -o "%WRAPPER_JAR%"
)

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Failed to download maven-wrapper.jar
  EXIT /B 1
)

java -Dmaven.multiModuleProjectDirectory=. -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
EXIT /B %ERRORLEVEL%
