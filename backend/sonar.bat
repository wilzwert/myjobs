REM === this script is provided as a shortcut for running sonar:sonar plugin in DEV ("local") or not dev env ===
@echo off
setlocal EnableDelayedExpansion

set "env=%1"
echo [INFO] Environment: !env!

REM === "mapping" from .env var names to sonar args ===
if "!env!" == "dev" (
    set MAP_DEV_SONAR_TOKEN=sonar.token
    set MAP_DEV_SONAR_PROJECT_KEY=sonar.projectKey
    set MAP_DEV_SONAR_PROJECT_NAME=sonar.projectName
    set MAP_DEV_SONAR_HOST_URL=sonar.host.url
) else (
    set MAP_SONAR_TOKEN=sonar.token
    set MAP_SONAR_PROJECT_KEY=sonar.projectKey
    set MAP_SONAR_PROJECT_NAME=sonar.projectName
    set MAP_SONAR_HOST_URL=sonar.host.url
)

SET SONAR_ARGS=-Dsonar.verbose=true

REM === Load environment variables from .env  ===
for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
	set "line=%%A"

    if not "!line!"=="" (
        if "!line:~0,1!" NEQ "#" (
			set "key=%%A"
			set "value=%%B"

			if "!env!"=="dev" (
			    if "!key!"=="DEV_SONAR_SUPPORTS_BRANCH" (
				    set SONAR_SUPPORTS_BRANCH=!value!
                )
			) else (
			    if "!key!"=="SONAR_SUPPORTS_BRANCH" (
                    set SONAR_SUPPORTS_BRANCH=!value!
                )
			)

			set "mapName=MAP_!key!"
			
			for %%X in (!mapName!) do (
                REM === get mapped variable value ===
                set "mappedKey=!%%X!"
            )
			
			echo [DEBUG] key=%%A, value=%%B, mappedKey=!mappedKey!
            if defined mappedKey (
                call set SONAR_ARGS=!SONAR_ARGS! -D!mappedKey!=%%B
            )
        )
    )
)

REM === get Git current branch ===
for /f "delims=" %%i in ('git rev-parse --abbrev-ref HEAD') do set BRANCH_NAME=%%i

REM === add branch to sonar args ONLY IF sonarqube version supports it ===
if "%SONAR_SUPPORTS_BRANCH%"=="true" (
	set SONAR_ARGS=%SONAR_ARGS% -Dsonar.branch.name=%BRANCH_NAME%
	echo [INFO] SonarQube analysis for branch : %BRANCH_NAME%
)

echo [INFO] Launching Sonar with args: %SONAR_ARGS%

REM === Run maven with Sonar ===
./mvnw clean install org.sonarsource.scanner.maven:sonar-maven-plugin:sonar %SONAR_ARGS%

endlocal