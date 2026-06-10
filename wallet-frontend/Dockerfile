# BUILD
FROM node:21-alpine AS build
WORKDIR /app
COPY *.json .
RUN npm install
COPY . .
RUN npm run build

# RUN
FROM httpd:trixie
WORKDIR /usr/local/apache2/conf
ARG API_URL
RUN echo "LoadModule proxy_module modules/mod_proxy.so" >> httpd.conf
RUN echo "LoadModule proxy_http_module modules/mod_proxy_http.so" >> httpd.conf
RUN echo "ProxyPass /api ${API_URL}" >> httpd.conf
RUN echo "ProxyPassReverse /api ${API_URL}" >> httpd.conf
WORKDIR /usr/local/apache2/htdocs
COPY --from=build /app/dist/*/browser .
