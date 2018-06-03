package pit

import (
	"database/sql"
	"fmt"
	_ "github.com/go-sql-driver/mysql"
	"log"
)

//conn global dbms connection
var conn *sql.DB

const db = "liziyi"
const dbSrv = "127.0.0.1"
const dbPort = "3333"
const dbUser = "liziyi"
const dbUserPwd = "mylocaldb"

//InitDbByParams set the connection parameters
func initDbByParams(db, dbHost, dbPort, dbUser, dbPwd string) {
	if db == "" || dbHost == "" || dbPort == "" || dbUser == "" || dbPwd == "" {
		log.Fatal("missing some/all parameters, please supply db, dbHost, dbPort, dbUser, dbPwd ")
	}
	connInfo := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s",
		dbUser, dbPwd, dbHost, dbPort, db)
	var err error
	conn, err = sql.Open("mysql", connInfo)
	if err != nil {
		log.Fatal(err)
	}
	fmt.Println("begin ping db server " + dbHost + ":" + dbPort)
	err = conn.Ping()
	fmt.Println("done")
	if err != nil {
		conn.Close()
		log.Fatal(err)
	}
}

//GetDbConnByParams return connected *sq
func getDbConnByParams(db, dbHost, dbPort, dbUser, dbPwd string) *sql.DB {
	if conn != nil {
		return conn
	}
	if db == "" || dbHost == "" || dbPort == "" || dbUser == "" || dbPwd == "" {
		log.Fatal("missing some/all parameters, please supply db, dbHost, dbPort, dbUser, dbPwd ")
	}

	initDbByParams(db, dbHost, dbPort, dbUser, dbPwd)

	return conn
}

func clearTestData() {
	getDbConnByParams(db, dbSrv, dbPort, dbUser, dbUserPwd)

	var err error
	//clear signin
	_, err = conn.Exec(`delete from signin
	where substring(openid from 1 for 4)=  '_T__'`)
	if err != nil {
		log.Fatal(err)
	}

	//clear user
	_, err = conn.Exec(`delete from user
	where substring(openid from 1 for 4)=  '_T__'`)
	if err != nil {
		log.Fatal(err)
	}

	//clear attnd
	_, err = conn.Exec(`delete from attnd
	where substring(name from 1 for 4)=  '_T__'`)
	if err != nil {
		log.Fatal(err)
	}

}
