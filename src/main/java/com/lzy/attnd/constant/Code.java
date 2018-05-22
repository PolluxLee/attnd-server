package com.lzy.attnd.constant;

public interface Code {
    int GLOBAL_SUCCESS = 1000;
    int GLOBAL_PARAM_INVALID = 1001;
    int GLOBAL_NOAUTH = 1003;
    int GLOBAL_SYS_ERROR = 1004;
    //error occur when db operate
    int GLOBAL_DB_ERROR = 1005;
    //work normal but execute not success
    int GLOBAL_DB_FAILED = 1006;
    //user not exist (user is visitor)
    int GLOBAL_USER_NOT_EXIST = 1007;

    int USER_NOT_EXIST = 2001;
    int USER_EXIST = 2002;

    int ATTND_NOT_EXIST = 3001;
    //signin cipher not matcher the attnd
    int ATTND_CIPHER_NOT_CORRESPOND = 3002;
    //has signin
    int ATTND_HAS_SIGNIN = 3003;
    //attnd has del
    int ATTND_HAS_DEL = 3005;
    int ATTND_ONGOING = 3006;
    //sign in and type is 'A' and user not in the group
    int SIGNIN_NOT_BELONG_GROUP = 3007;
    //attnd creator not allow to signin
    int SIGNIN_CREATOR = 3008;

    int GROUP_NOTEXIST=4001;

    int ADD_FLAG = 1;
    int UPD_FLAG = 2;

    //signin status in attnd situation
    int SIGNIN_OK = 1;
    int SIGNIN_LOCATION_BEYOND = 2;
    int SIGNIN_EXPIRED = 3;
    int SIGNIN_NOT_EXIST = 4;

    //attnd status
    int ATTND_NORMAL=1;
    int ATTND_ENTRY=2;
    int ATTND_NOGROUP=3;
    int ATTND_DEL=4;

    //cipher type
    char CIPHER_ATTND = 'A';
    char CIPHER_ENTRY = 'G';
    char CIPHER_NOGROUP = 'N';
    char CIPHER_SINGLE = 'S';

    //group type
    int GROUP_DEL=2;


    //right control code
    int RIGHT_VISITOR = 1;
    int RIGHT_USER = 2;
}
