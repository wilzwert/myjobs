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
