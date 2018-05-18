DELETE FROM user where openid = 'toid123' or openid =  'toid456';
INSERT INTO user(id,name, openid,remark,stuid) VALUES(1,'lzy','toid123','{}','23');
INSERT INTO user(id,name, openid,remark,stuid) VALUES(2,'lzp','toid456','{}','24');

DELETE FROM usergroup WHERE name='网工151';
INSERT INTO usergroup(name, creatorname, creatorid, remark) VALUES ('网工151','lzy',2,'{}');

DELETE FROM attnd WHERE cipher='Gwvk1' or cipher='Awvk2';
INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark) VALUES
(1,'操作系统1','Gwvk1',15577418,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','外环西路','网工151',1	,'lzy',2,'{}');

INSERT INTO attnd(id,name,cipher,starttime,lasttime,location,addrname,groupname,teacherid,teachername,status,remark) VALUES
(2,'计算机网络','Awvk2',15577418,20,'{"accuracy": 30.0, "latitude": 23.4, "longitude": 174.4}','外环西路','',2	,'lzp',1,'{}');