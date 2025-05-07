@echo off
setlocal EnableDelayedExpansion

REM === "mapping" from .env var names to sonar args ===
set MAP_DEV_SONAR_TOKEN=sonar.token
set MAP_DEV_SONAR_PROJECT_KEY=sonar.projectKey
set MAP_DEV_SONAR_PROJECT_NAME=sonar.projectName
set MAP_DEV_SONAR_HOST_URL=sonar.host.url

SET SONAR_ARGS=-Dsonar.verbose=true

REM === Load environment variables from .env  ===
for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
	set "line=%%A"
	echo "!line!"
    if not "!line!"=="" (
        if "!line:~0,1!" NEQ "#" (
			set "key=%%A"
            set "value=%%B"
			
			if "!key!"=="DEV_SONAR_SUPPORTS_BRANCH" (
				set DEV_SONAR_SUPPORTS_BRANCH=!value!
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
if "%DEV_SONAR_SUPPORTS_BRANCH%"=="true" (
	set SONAR_ARGS=%SONAR_ARGS% -Dsonar.branch.name=%BRANCH_NAME%
)

echo SonarQube analysis for branch : %BRANCH_NAME%
echo Sonar args %SONAR_ARGS%

REM === Run maven with Sonar ===
./mvnw sonar:sonar %SONAR_ARGS%

endlocal