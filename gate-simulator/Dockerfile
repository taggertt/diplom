FROM node:8.16.2-alpine
WORKDIR /opt/app
RUN npm install -g npm@7.0.0
COPY . .
RUN npm install
CMD ["npm", "start"]
EXPOSE 9999
