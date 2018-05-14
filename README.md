# attnd-server [![Build Status](https://travis-ci.org/WisperDin/attnd-server.svg?branch=master)](https://travis-ci.org/WisperDin/attnd-server)
A back-end server of the WeChat attendance applet implemented using the spring boot framework

# Usage
**envionment variables need**
```sh
#reading properties via profile
SPRING_PROFILES_ACTIVE=docker 
DB_HOST=127.0.0.1:3306
DB_PASSWORD=yourdbpassword
DB_DBNAME=yourdbname
DB_USERNAME=yourusername
CACHE_HOST=127.0.0.1
CACHE_PORT=6379
CACHE_PASSWORD=cachepwd
APP_PORT=8888
#param for wechat 
APP_APPID=1
APP_APPSECRET=1
#log file position
LOG_HOME=/app/log
```

- `git clone https://github.com/WisperDin/attnd-server.git`
- `docker-compose build`
- `docker-compose up`
