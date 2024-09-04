FROM node:8.16.2-alpine
WORKDIR /opt/app
RUN npm install -g npm@latest
COPY . .
RUN npm install
CMD ["npm", "start"]
EXPOSE 9999
