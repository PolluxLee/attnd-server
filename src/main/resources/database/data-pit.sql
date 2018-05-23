
DELETE FROM user where id in (1,2,3,4,5,6,7);
DELETE FROM usergroup where id in (1,2,3,4,5);
DELETE FROM attnd WHERE id in (1,2,3,4,5,6);
DELETE FROM signin where id in (1,2,3,4,5);

INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(1,'lzy','toid123','{}','23','[]');
INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(2,'lzp','toid456','{}','24','[2]');
INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(3,'lz','toid789','{}','25','[1]');
INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(4,'fa','toid222','{}','25','[1]');
INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(5,'zdx','toid111','{}','','[]');
INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(6,'jxy','toid333','{}','','[]');
INSERT INTO user(id,name, openid,remark,stuid,groupid) VALUES(7,'lb','toid444','{}','','[]');


INSERT INTO usergroup(id,name, creatorname, creatorid, remark) VALUES (1,'网工151','lzy',1,'{}');
INSERT INTO usergroup(id,name, creatorname, creatorid, remark) VALUES (2,'计科151','zdx',5,'{}');
INSERT INTO usergroup(id,name, creatorname, creatorid, remark) VALUES (3,'软工152','zdx',5,'{}');
INSERT INTO usergroup(id,name, creatorname, creatorid, remark) VALUES (4,'教育151','jxy',6,'{}');
INSERT INTO usergroup(id,name, creatorname, creatorid, remark,status) VALUES (5,'软工151','fa',4,'{}',4);


INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(1,'操作系统1','Gwvk1',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','文新501','网工151',1	,'lzy',2,'{}','2018-05-20');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(2,'计算机网络','Awvk2',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','理南315','软工152',5	,'zdx',1,'{}','2018-05-21');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(3,'编译原理','Awcq4',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','理北414','教育151',6	,'jxy',1,'{}','2018-05-18');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(4,'高级网站开发','NQSA4',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','文清301','',1	,'lzy',3,'{}','2018-05-22');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(5,'数据结构','Awvq5',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','电子417','网工151',1	,'lzy',1,'{}','2018-05-15');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark,createdat) VALUES
(6,'通信原理','AZXQ6',1522512000,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','副楼401','网工151',1	,'lzy',4,'{}','2018-05-17');

INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (1,'toid789','Awvk2','{"accuracy": 30.0, "latitude": 23.2405, "longitude": 174.4}','{}',1,92.60);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (2,'toid222','Awvq1','{"accuracy": 30.0, "latitude": 23.245, "longitude": 174.4}','{}',2,926.01);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (3,'toid222','Gwvk1','{"accuracy": 30.0, "latitude": 23.406, "longitude": 174.4}','{}',3,111.12);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (4,'toid444','NQSA4','{"accuracy": 30.0, "latitude": 23.2407, "longitude": 174.4}','{}',1,129.64);
INSERT INTO signin(id,openid, cipher, location, remark,status,distance) VALUES (5,'toid333','NQSA4','{"accuracy": 30.0, "latitude": 23.2407, "longitude": 174.4}','{}',4,129.64);