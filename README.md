# MyJobs

[![Frontend CI](https://img.shields.io/github/actions/workflow/status/wilzwert/myjobs/ci-frontend.yml?label=Frontend%20CI&logo=Github)](https://github.com/wilzwert/myjobs/actions/workflows/ci-frontend.yml)
[![Backend CI](https://img.shields.io/github/actions/workflow/status/wilzwert/myjobs/ci-backend.yml?label=Backend%20CI&logo=Github)](https://github.com/wilzwert/myjobs/actions/workflows/ci-backend.yml)
[![Staging](https://img.shields.io/github/actions/workflow/status/wilzwert/myjobs/staging.yml?label=Staging&logo=Github)](https://github.com/wilzwert/myjobs/actions/workflows/ci-backend.yml)



[![Backend coverage](https://img.shields.io/codecov/c/github/wilzwert/myjobs?flag=backend&label=Backend%20coverage&logo=JUnit5)](https://wilzwert.github.io/myjobs/coverage-backend/)
[![Backend Quality Gate Status](https://img.shields.io/sonar/quality_gate/MyJobs_backend?server=https%3A%2F%2Fsonarcloud.io&logo=sonarcloud&label=Backend%20quality%20gate)](https://sonarcloud.io/summary/new_code?id=MyJobs_backend&branch=master)



[![Frontend coverage](https://img.shields.io/codecov/c/github/wilzwert/myjobs?flag=frontend&label=Frontend%20coverage&logo=Jasmine)](https://wilzwert.github.io/myjobs/coverage-frontend/)
[![Frontend Quality Gate Status](https://img.shields.io/sonar/quality_gate/MyJobs_frontend?server=https%3A%2F%2Fsonarcloud.io&logo=sonarcloud&label=Frontend%20quality%20gate)](https://sonarcloud.io/summary/new_code?id=MyJobs_frontend)

[Backend coverage report](https://wilzwert.github.io/myjobs/coverage-backend/)

[Frontend coverage report](https://wilzwert.github.io/myjobs/coverage-frontend/)


## Overview 

### Goals

This project has several goals : 
1. help me manage my job search by keeping track of jobs statuses and applications (spoiler alert : there's a good chance this project will never do what I originally intended)
2. learn as much and as fast as possible about Spring Boot, Hexagonal Architecture, Clean Architecture, MongoDB and Domain-Driven Design (DDD), and lots of other things I will forget (or not)
3. Make me scream "Man, TDD is so cool and efficient" then  go to 5. or 6., each with a 50% probability
4. Make me scream "Man, TDD si so counterintuitive and inefficient" then go to 5. or 6., each with a 50% probabilty
5. Go back to 3. or 4., each with a 50% probability
6. Try, fail, misunderstand, try again, fail again, succeed, do better, wish I knew more and better, refactor, unlearn, relearn, fail again, succeed, question everything, go to sleep

### Features

#### Localization
The app is almost fully available in English and French.

#### Auth
- registration (protected by Recaptcha)
- login (protected by Recaptcha)
- token refresh
- logout
- reset password request

#### Registration
- check username / email availability
- account creation email with email validation linkg
- email validation

#### User
- personal info edition
- configure a delay (in days) for receiving late jobs reminders : if an active job has not been updated in this delay, an email will be sent
- password edition

#### Jobs
- creation by URL (Experimental : job metadata is extracted from source JSON-LD metadata or html tags, but it is not fully operational / tested)
- "regular" creation
- attachements creation / deletion
- activities creation (application, email, interview...)
- rating
- status management (CREATED, APPLICATION, RELAUNCH...)
- filter by status
- a "meta status" exists to display only "stale" jobs
- sorting by date or rating asc/desc
- automated late follow ups reminded by email 

## Stack

### Backend

The backend is written in Java and is an attempt at learning, understanding and  using hexagonal / clean architecture following DDD principles. 

It is composed of 3 modules : 
- core : the core of my hexa architecture, composed of an application layer and a domain. Almost only Vanilla Java (except for jackson jr for JSON parsing), which result in lots of hand-written boilerplate code, but keeps it as pure as possible, or at least dependency-free
- infrastructure : serves as the entry point of the application and provides all technical implementations such as REST controllers, database access, and external service integration. Uses Spring Boot.
- report : a module with no code, only used to aggregate coverage reports

Java 21 needed, built with Maven, tested with Junit / Mockito, coverage reports with Jacoco...

### Frontend

The backend is an Angular 19 project in a kind of n-tier architecture with low level services used by features (i.e. Components).

Localization uses Angular Localization, unit and integration tests use Jest, e2e tests are made with Cypress (although they are no real e2e tests written at the moment) . 

The design is very poor at the moment, although the use of Material makes it usable.

### Hosting

The project is currently hosted with these tools :
- OVH domain
- Private S3 butcket for angular build files
- Cloudfront for URL rewriting
- ACM for SSL certificates
- Cloud Run for the backend (scheduled batch have yet to be triggered with dedicated "internal" API endpoints). This may change for a "async-compatible" provider in the near future
- S3 buckets for uploads storage (separated butckets for production and testing)
- MailJet for sending mails (does not work at the moment, even with validated DKIM / SPF)
- Mongo Atlas for MongoDB

## Roadmap

There are of course a lot of features I'd like to add ; here are some of them : 
- Security : improve security for connections between frontent and backend
- Optimization (backend load and performance, frontend SSR...)
- Design and UX improvements (routing in the frontend, home page, global UX)
- GPDR compliance (automatic user data deletion, legal info available on the frontend...)

