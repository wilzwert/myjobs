# Simple frontend container
# This is not really meant to be used as is in production
# But it can provide a way to demo the "real thing"
# 1- Build
FROM node:alpine AS builder
ARG CONFIGURATION=development
ARG API_URL=http://localhost:8080
ARG API_SECURE=false
ARG GOOGLE_RECAPTCHA_SITE_KEY=keynotset
WORKDIR /app
COPY ./frontend .
RUN echo "[INFO] configuraion : $CONFIGURATION"
RUN echo "[INFO] API_URL : $API_URL"
RUN echo "[INFO] API_SECURE : $API_SECURE"
RUN npm install -g @angular/cli
RUN npm install

ENV API_URL=${API_URL}
ENV API_SECURE=${API_SECURE}
ENV GOOGLE_RECAPTCHA_SITE_KEY=${GOOGLE_RECAPTCHA_SITE_KEY}

RUN ng build --localize --configuration=${CONFIGURATION}

# # 2 - Run
FROM nginx:alpine
COPY ./docker/demo/frontend_nginx.conf /etc/nginx/nginx.conf
COPY --from=builder  /app/dist/myjobs/ /usr/share/nginx/html/
EXPOSE 80