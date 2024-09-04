FROM node:8.16.2-alpine
RUN apk add --no-cache bash curl
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash
RUN . ~/.nvm/nvm.sh && nvm install 8.16.2 && nvm use 8.16.2 && nvm install-latest-npm
WORKDIR /opt/app
COPY . .
RUN npm install
CMD ["npm", "start"]
EXPOSE 9999
