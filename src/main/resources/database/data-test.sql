DELETE FROM user where openid = 'toid123' or openid =  'toid456';
INSERT INTO user(name, openid,remark) VALUES('lzy','toid123','{}');
INSERT INTO user(name, openid,remark) VALUES('lzp','toid456','{}');