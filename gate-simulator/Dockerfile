FROM node:8.16.2-alpine
WORKDIR /opt/app
RUN nvm install-latest-npm
COPY . .
RUN npm install
CMD ["npm", "start"]
EXPOSE 9999
