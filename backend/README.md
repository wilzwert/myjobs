# Backend

## Environments / profiles

Profiles are used to adapt project configuration based on execution environment.

For example, log levels, SMTP info, CORS configuration, async capability, Recaptcha keys, AWS configuration, MongoDB connection string...

Basic configuration is made in application.yml and may be overriden by application-xxx.yml

Theses files themselves heavily rely on environment variables, which can also be provided by .env file. When such variables are not provided, thy default to hard coded values in application.yml. 

As you can see, there are only few hard coded differences between the application.properties files, as lots of things are externally configurable.

Please see .env.example for more information on available variables.

### Dev

Should be used for local development. If you use the provided /docker/dev/docker-compose.yml to provide MongoDB and SMTP4Dev, most of the default values should work, but you still have to provide secrets (JWT, Recaptcha... ). AWS cofiguration is not mandatory to run the project in dev mode, but is required for integration tests. By default in dev mode, the project uses local storage to avoid S3 costs.

### Test

Used for unit testing (mvn test) 

### Integration

Used for integration tests (mvn verify, or during CI). Integration tests use TestContainers to run MongoDB. 
mvn verify is also executed during CI.

### Staging

Used for staging tests run in the staging Githbub Action ; not intended to be run manually. 

### Prod

Used for production. In that environment, no need for .env, as the environment variables are set on the hosting provider. This is where the BATCH_ENABLED and ASYNC_ENABLED may be useful. As a side note : in production, logs are written in Json to allow external parsing.


## Sonar
To manually run a Sonar analysis while developing, you can use the script provided : `./sonar-scan.bat`.
It allows to get Sonar feedback without having to trigger CI. If your Sonar target supports branches, the git branch is auto detected.
This can be done in 2 different environments by providing an arg to the script, e.g. : `./sonar-scan.bat dev`
At this time only 2 environments exist : 'dev' and 'non dev' i.e. no arg.
You can configure the run by providing some vars in the .env file. See .env.example for more information.

> **_NOTE:_** a shell version of this script should be written 