DELETE FROM user where openid = 'toid123' or openid =  'toid456' or id=3 or id =4;
INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(1,'lzy','toid123','{}','23','[1]');
INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(2,'lzp','toid456','{}','24','[2]');
INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(3,'lz','toid789','{}','25','[1]');
INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(4,'fa','toid222','{}','25','[1]');

DELETE FROM usergroup WHERE name='网工151' or id=2;
INSERT INTO usergroup(id,name, creatorname, creatorid, remark) VALUES (1,'网工151','lzy',1,'{}');
INSERT INTO usergroup(id,name, creatorname, creatorid, remark) VALUES (2,'计科151','lzy',1,'{}');

DELETE FROM attnd WHERE cipher='Gwvk1' or cipher='Awvk2' OR cipher ='Awvq3' or id = 4 or id = 5;
INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(1,'操作系统1','Gwvk1',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','外环西路','网工151',1	,'lzy',2,'{}','2018-05-20');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(2,'计算机网络','Awvk2',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','外环西路','计科151',1	,'lzy',1,'{}','2018-05-21');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(3,'编译原理','Awvq3',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','外环西路','网工151',1	,'lzy',1,'{}','2018-05-18');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(4,'高级网站开发','NQSA4',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','外环西路','',1	,'lzy',3,'{}','2018-05-22');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(5,'数据结构','Awvq1',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','外环西路','',1	,'lzy',3,'{}','2018-05-15');

DELETE FROM signin where id=1 or id =2 or id=3 or id=4;
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (1,'toid789','Awvq1','{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','{}',1,75.4);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (2,'toid123','Awvq1','{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','{}',2,75.4);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (3,'toid123','Gwvk1','{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','{}',1,75.4);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (4,'toid789','Gwvk1','{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','{}',1,75.4);