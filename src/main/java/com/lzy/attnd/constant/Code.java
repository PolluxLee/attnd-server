package com.lzy.attnd.constant;

public interface Code {
    int GLOBAL_SUCCESS = 1000;
    int GLOBAL_PARAM_INVALID = 1001;
    int GLOBAL_PARAM_ERROR = 1002;
    int GLOBAL_NOAUTH = 1003;
    int GLOBAL_SYS_ERROR = 1004;
    int GLOBAL_DB_ERROR = 1005;
    //work normal but execute not success
    int GLOBAL_DB_FAILED = 1006;


    int USER_NOT_EXIST = 2001;
    int USER_EXIST = 2002;

    int ATTND_NOT_EXIST = 3001;
    int ATTND_CIPHER_NOT_CORRESPOND = 3002;
    int ATTND_HAS_SIGNIN = 3003;
    int ATTND_EXPIRED = 3004;

    int ADD_FLAG = 1;
    int UPD_FLAG = 2;

    int SIGNIN_OK = 1;
    int SIGNIN_LOCATION_BEYOND = 2;
    int SIGNIN_EXPIRED = 3;

    int ATTND_NORMAL=1;
    int ATTND_ENTRY=2;
    int ATTND_NOGROUP=3;

    char CIPHER_ATTND = 'A';
    char CIPHER_ENTRY = 'G';
    char CIPHER_NOGROUP = 'N';
    char CIPHER_SINGLE = 'S';

}
