DELETE FROM user where openid = 'toid123' or openid =  'toid456';
INSERT INTO user(name, openid,remark,stuid) VALUES('lzy','toid123','{}','23');
INSERT INTO user(name, openid,remark,stuid) VALUES('lzp','toid456','{}','24');