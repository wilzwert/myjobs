# Demo

It is possible to start a frontend app  demo  by building a docker image using the provided `frontend.Dockerfile`.

It will : 
- build the angular frontend with both localizations (fr / en)
- build an image based on nginx:alpine that handles languages redirections, exposing port 80