# Myjobs

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 18.2.3.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice. To use this command, you need to first add a package that implements end-to-end testing capabilities.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.

## Docker
To build the app and run it locally in a Docker container with a basic nginx config : 
- make sure you have the backend running on localhost:8080
- make sure the backend allows all origins ; by default this is done for 'dev' profile and should be the only profile to do so (see backend/infrastructure/...application.yml)
- run `ng build --localize --configuration=integration`, `cd docker` and then `docker-compose up --build`.  
This will start a container that exposes your app on http://localhost:8081, consuming the API on http://localhost:8080
NOTE : this is for testing / demo purposes only !

## Sonar
To manually run a Sonar analysis while developing, you can use the script provided : `./sonar-scan.bat`.
It allows to get Sonar feedback without having to trigger CI. If your Sonar target supports branches, the git branch is auto detected.
This can be done in 2 different environments by providing an arg to the script, e.g. : `./sonar-scan.bat dev`
At this time only 2 environments exist : 'dev' and 'non dev' i.e. no arg.
You can configure the run by providing some vars in the .env file. See .env.example for more information.

> **_NOTE:_** a shell version of this script should be written 