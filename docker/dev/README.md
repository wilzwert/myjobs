# Docker for dev

You can use the dev/docker-compose.yml to provide 
- a MongoDB instance as a standalone replica set
- a SMTP4Dev container, useful to send and check emails locally 

Replica Set is mandatory to provide transactions support.

Please generate the `mongodb-keyfile` first, otherwise the MongoDb container won't start. 

`openssl rand -base64 756 > mongodb-keyfile`