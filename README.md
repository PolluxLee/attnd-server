# Attnd-Server [![Build Status](https://travis-ci.org/WisperDin/attnd-server.svg?branch=master)](https://travis-ci.org/WisperDin/attnd-server)
A back-end server of the WeChat attendance applet implemented using the spring boot framework

# Environment(Integrated in Docker)
- Spring boot v2.0.1
- Mysql v8.0.11
- Redis v4.0.9
- Maven v3.5
- Java v1.8

# Usage

- Docker Version v18.03.0-ce
- Docker-Compose Version v1.21.2

**envionment variables need when running docker-compose**
```sh
#reading properties via profile
export SPRING_PROFILES_ACTIVE=docker 
export DB_HOST=127.0.0.1:3306
export DB_PASSWORD=yourdbpassword
export DB_DBNAME=yourdbname
export DB_USERNAME=yourusername
export CACHE_HOST=127.0.0.1
export CACHE_PORT=6379
export CACHE_PASSWORD=cachepwd
export APP_PORT=8888
#param for wechat 
export APP_APPID=1
export APP_APPSECRET=1
#log file position
export LOG_HOME=/app/log
#switch to the test
export TEST_SW=off
```

- `git clone https://github.com/WisperDin/attnd-server.git`
- `docker-compose build`
- `docker-compose up`
