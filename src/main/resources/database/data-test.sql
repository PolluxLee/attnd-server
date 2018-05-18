DELETE FROM user where openid = 'toid123' or openid =  'toid456';
INSERT INTO user(id,name, openid,remark,stuid) VALUES(1,'lzy','toid123','{}','23');
INSERT INTO user(id,name, openid,remark,stuid) VALUES(2,'lzp','toid456','{}','24');

DELETE FROM usergroup WHERE name='网工151';
INSERT INTO usergroup(name, creatorname, creatorid, remark) VALUES ('网工151','lzp',2,'{}');