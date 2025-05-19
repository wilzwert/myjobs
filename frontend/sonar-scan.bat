@echo off
setlocal EnableDelayedExpansion

set "env=%1"
echo [INFO] Environnement: !env!

REM === "mapping" from .env var names to sonar args ===
if "!env!" == "dev" (
    set MAP_DEV_SONAR_TOKEN=sonar.token
    set MAP_DEV_SONAR_ORGANIZATION=sonar.organization
    set MAP_DEV_SONAR_PROJECT_KEY=sonar.projectKey
    set MAP_DEV_SONAR_PROJECT_NAME=sonar.projectName
    set MAP_DEV_SONAR_HOST_URL=sonar.host.url
) else (
    set MAP_SONAR_TOKEN=sonar.token
    set MAP_SONAR_ORGANIZATION=sonar.organization
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
                set "mappedKey=!%%X!"
            )

            if defined mappedKey (
                call set SONAR_ARGS=!SONAR_ARGS! -D!mappedKey!=%%B
            )
        )
    )
)

REM === get Git current branch  ===
for /f "delims=" %%i in ('git rev-parse --abbrev-ref HEAD') do set BRANCH_NAME=%%i

REM === add branch to sonar args ONLY IF sonarqube version supports it ===
if "%SONAR_SUPPORTS_BRANCH%"=="true" (
    set SONAR_ARGS=%SONAR_ARGS% -Dsonar.branch.name=%BRANCH_NAME%
    echo [INFO] Analyse de la branche: %BRANCH_NAME%
)

REM === Specific Angular / Jest config ===
set SONAR_ARGS=%SONAR_ARGS% -Dsonar.projectBaseDir=.
set SONAR_ARGS=%SONAR_ARGS% -Dsonar.sources=src/app
set SONAR_ARGS=%SONAR_ARGS% -Dsonar.tests=src/app
set SONAR_ARGS=%SONAR_ARGS% -Dsonar.test.inclusions=src/app/**/*.spec.ts
set SONAR_ARGS=%SONAR_ARGS% -Dsonar.exclusions=.angular,coverage,node_modules,*.json
set SONAR_ARGS=%SONAR_ARGS% -Dsonar.coverage.exclusions=src/app/**/*.spec.ts,node_modules,.angular
set SONAR_ARGS=%SONAR_ARGS% -Dsonar.typescript.lcov.reportPaths=coverage/jest/lcov.info,coverage/e2e/lcov.info
set SONAR_ARGS=%SONAR_ARGS% -Dsonar.javascript.lcov.reportPaths=coverage/jest/lcov.info
set SONAR_ARGS=%SONAR_ARGS% -Dsonar.sourceEncoding=UTF-8

echo [INFO] Launching Sonar with args: %SONAR_ARGS%

REM === run tests with coverage; no fail as we explicitely ask for the sonar scan ===
cmd /c npm run test:coverage || echo [INFO] There are failed tests, but we run the sonar-scanner anyway.

REM === Scan sonar-scanner ===
npx sonar-scanner %SONAR_ARGS%

echo [INFO] Run ended.

endlocal