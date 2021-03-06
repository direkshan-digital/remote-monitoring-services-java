@ECHO off & setlocal enableextensions enabledelayedexpansion

:: Note: use lowercase names for the Docker images
SET DOCKER_IMAGE="azureiotpcs/iothub-manager-java"

:: strlen("\scripts\docker\") => 16
SET APP_HOME=%~dp0
SET APP_HOME=%APP_HOME:~0,-16%
cd %APP_HOME%

:: Check dependencies
docker version > NUL 2>&1
IF %ERRORLEVEL% NEQ 0 GOTO MISSING_DOCKER

:: Check settings
call .\scripts\env-vars-check.cmd
IF %ERRORLEVEL% NEQ 0 GOTO FAIL

:: Start the application
:: Some settings are used to connect to an external dependency, e.g. Azure IoT Hub and IoT Hub Manager API
:: Depending on which settings and which dependencies are needed, edit the list of variables
echo Starting IoT Hub Manager...
docker run -it -p 9002:9002 ^
    -e PCS_AUTH_WEBSERVICE_URL ^
    -e PCS_IOTHUB_CONNSTRING ^
    -e PCS_STORAGEADAPTER_WEBSERVICE_URL ^
    %DOCKER_IMAGE%:testing

:: - - - - - - - - - - - - - -
goto :END

:FAIL
    echo Command failed
    endlocal
    exit /B 1

:MISSING_DOCKER
    echo ERROR: 'docker' command not found.
    echo Install Docker and make sure the 'docker' command is in the PATH.
    echo Docker installation: https://www.docker.com/community-edition#/download
    exit /B 1

:END
endlocal
