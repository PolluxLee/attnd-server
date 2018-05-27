DELETE FROM user where openid like 'toid%';
DELETE FROM attnd WHERE teacherid in (1,2,3,4);
DELETE FROM signin where openid like 'toid%';



INSERT INTO user(id,name, openid,remark,stuid) VALUES(1,'lzy','toid123','{}','23');
INSERT INTO user(id,name, openid,remark,stuid) VALUES(2,'lzp','toid456','{}','');
INSERT INTO user(id,name, openid,remark,stuid) VALUES(3,'lz','toid789','{}','25');
INSERT INTO user(id,name, openid,remark,stuid) VALUES(4,'wj','toid222','{}','');


INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,teacherid,teachername,status,remark,createdat) VALUES
(1,'操作系统1','Awvk1',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','文新512',1	,'lzy',1,'{}','2018-05-20');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,teacherid,teachername,status,remark,createdat) VALUES
(2,'计算机网络','AwXk2',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','理南315',1	,'lzy',1,'{}','2018-05-21');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,teacherid,teachername,status,remark,createdat) VALUES
(3,'编译原理','ACcq3',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','理北414',4	,'wj',1,'{}','2018-05-18');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,teacherid,teachername,status,remark,createdat) VALUES
(4,'高级网站开发','AQSA4',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','文清301',4	,'wj',4,'{}','2018-05-22');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,teacherid,teachername,status,remark,createdat) VALUES
(5,'数据结构','AwvF5',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','电子417',1	,'lzy',1,'{}','2018-05-15');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,teacherid,teachername,status,remark,createdat) VALUES
(6,'通信原理','AZXQ6',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','副楼401',1	,'lzy',4,'{}','2018-05-17');

INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (1,'toid789','ACcq3','{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','{}',1,175.4);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (2,'toid123','ACcq3','{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','{}',2,64.4);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (3,'toid123','AQSA4','{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','{}',1,32.75);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (4,'toid789','Awvk1','{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','{}',1,17.3);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (5,'toid789','AQSA4','{"accuracy": 32.0, "latitude": 23.4, "longitude": 174.4}','{}',3,450.4);



