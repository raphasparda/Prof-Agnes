@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM Begin all REM://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-3.10.7
@REM Maven Wrapper Startup Script for Windows

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $env:PSModulePath = $scriptDir; Get-Content -Path '%~dp0.mvn\wrapper\maven-wrapper.properties' -Raw | ForEach-Object { $_ -replace '\\','/' } | Select-String -Pattern '(distributionUrl)=(.*)' | ForEach-Object { $_.Matches[0].Groups[1].Value + '=' + $_.Matches[0].Groups[2].Value }}"`) DO (
    @IF "%%A"=="distributionUrl" SET __MVNW_CMD__=%%B
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=

@SET MVNW_JAVA_COMMAND=java
@IF NOT "%JAVA_HOME%"=="" SET MVNW_JAVA_COMMAND=%JAVA_HOME%\bin\java

@SET WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"

@"%MVNW_JAVA_COMMAND%" %MVNW_JVMCONFIG% %JVM_CONFIG_MAVEN_PROPS% %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% -classpath %WRAPPER_JAR% -Dmaven.multiModuleProjectDirectory=%~dp0 org.apache.maven.wrapper.MavenWrapperMain %*
@IF %ERRORLEVEL% NEQ 0 GOTO error
@GOTO end

:error
@SET ERROR_CODE=%ERRORLEVEL%
@IF "%MAVEN_TERMINATE_CMD%"=="on" EXIT %ERROR_CODE%
@EXIT /B %ERROR_CODE%

:end
@ENDLOCAL & SET ERROR_CODE=%ERRORLEVEL%
@IF NOT "%MAVEN_SKIP_RC%"=="" GOTO skiprc
@IF EXIST "%USERPROFILE%\.mavenrc_post.cmd" CALL "%USERPROFILE%\.mavenrc_post.cmd"
@IF EXIST "%USERPROFILE%\.mavenrc_post.bat" CALL "%USERPROFILE%\.mavenrc_post.bat"
:skiprc
@IF "%MAVEN_TERMINATE_CMD%"=="on" EXIT %ERROR_CODE%
@EXIT /B %ERROR_CODE%
