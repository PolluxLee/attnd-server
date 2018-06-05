CREATE TABLE IF NOT EXISTS user(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  stuid varchar(255) NOT NULL,
  openid varchar(255) NOT NULL UNIQUE,
  status INT NOT NULL DEFAULT 0,
  remark JSON NOT NULL ,
  createdat datetime NOT NULL default NOW(),
  updatedat datetime NOT NULL default NOW()
)DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS attnd(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  cipher varchar(32) UNIQUE,
  starttime BIGINT NOT NULL,
  lasttime INT NOT NULL,
  location JSON NOT NULL,
  addrname varchar(255) NOT NULL,
  teacherid INT NOT NULL,
  teachername varchar(255) NOT NULL,
  status INT NOT NULL DEFAULT 0,
  remark JSON NOT NULL ,
  createdat datetime NOT NULL default NOW(),
  updatedat datetime NOT NULL default NOW()
)DEFAULT CHARACTER SET = utf8mb4;


CREATE TABLE IF NOT EXISTS signin(
  id INT PRIMARY KEY AUTO_INCREMENT,
  openid varchar(255) NOT NULL,
  cipher varchar(32) NOT NULL,
  distance DOUBLE NOT NULL,
  location JSON NOT NULL,
  status INT NOT NULL DEFAULT 0,
  remark JSON NOT NULL ,
  createdat datetime NOT NULL default NOW(),
  updatedat datetime NOT NULL default NOW(),
  UNIQUE KEY(openid,cipher)
)DEFAULT CHARACTER SET = utf8mb4;

CREATE TABLE IF NOT EXISTS felog(
  id INT PRIMARY KEY AUTO_INCREMENT,
  userinfo JSON NOT NULL,
  loginfo JSON NOT NULL,
  createdat datetime NOT NULL default NOW()
)DEFAULT CHARACTER SET = utf8mb4;

/* logback table */
CREATE TABLE IF NOT EXISTS logging_event
  (
    timestmp         BIGINT NOT NULL,
    formatted_message  TEXT NOT NULL,
    logger_name       VARCHAR(254) NOT NULL,
    level_string      VARCHAR(254) NOT NULL,
    thread_name       VARCHAR(254),
    reference_flag    SMALLINT,
    arg0              VARCHAR(254),
    arg1              VARCHAR(254),
    arg2              VARCHAR(254),
    arg3              VARCHAR(254),
    caller_filename   VARCHAR(254) NOT NULL,
    caller_class      VARCHAR(254) NOT NULL,
    caller_method     VARCHAR(254) NOT NULL,
    caller_line       CHAR(4) NOT NULL,
    event_id          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY
  );

CREATE TABLE IF NOT EXISTS logging_event_property
  (
    event_id          BIGINT NOT NULL,
    mapped_key        VARCHAR(254) NOT NULL,
    mapped_value      TEXT,
    PRIMARY KEY(event_id, mapped_key),
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
  );

CREATE TABLE IF NOT EXISTS logging_event_exception
  (
    event_id         BIGINT NOT NULL,
    i                SMALLINT NOT NULL,
    trace_line       VARCHAR(254) NOT NULL,
    PRIMARY KEY(event_id, i),
    FOREIGN KEY (event_id) REFERENCES logging_event(event_id)
  );


DROP TRIGGER IF EXISTS  t_attnd_update_time_before_upd;
DROP TRIGGER IF EXISTS  t_attnd_update_time_before_ins;

CREATE TRIGGER t_attnd_update_time_before_upd BEFORE UPDATE on attnd FOR EACH ROW
    set NEW.updatedat = now();

CREATE TRIGGER t_attnd_update_time_before_ins BEFORE INSERT on attnd FOR EACH ROW
    set NEW.updatedat = now();


DROP TRIGGER IF EXISTS  t_user_update_time_before_upd;
DROP TRIGGER IF EXISTS  t_user_update_time_before_ins;

CREATE TRIGGER t_user_update_time_before_upd BEFORE UPDATE on user FOR EACH ROW
    set NEW.updatedat = now();

CREATE TRIGGER t_user_update_time_before_ins BEFORE INSERT on user FOR EACH ROW
    set NEW.updatedat = now();

/*DROP TRIGGER IF EXISTS  t_usergroup_update_time_before_upd;
DROP TRIGGER IF EXISTS  t_usergroup_update_time_before_ins;

CREATE TRIGGER t_usergroup_update_time_before_upd BEFORE UPDATE on usergroup FOR EACH ROW
    set NEW.updatedat = now();

CREATE TRIGGER t_usergroup_update_time_before_ins BEFORE INSERT on usergroup FOR EACH ROW
    set NEW.updatedat = now();*/


DROP TRIGGER IF EXISTS  t_signin_update_time_before_upd;
DROP TRIGGER IF EXISTS  t_signin_update_time_before_ins;

CREATE TRIGGER t_signin_update_time_before_upd BEFORE UPDATE on signin FOR EACH ROW
    set NEW.updatedat = now();

CREATE TRIGGER t_signin_update_time_before_ins BEFORE INSERT on signin FOR EACH ROW
    set NEW.updatedat = now();


