package pit

import (
	"encoding/json"
	"fmt"
	"github.com/oliveagle/jsonpath"
	"github.com/valyala/fasthttp"
	"go.uber.org/atomic"
	"log"
	"math/rand"
	"net/http"
	_ "net/http/pprof"
	"net/url"
	"os"
	"strconv"
	"sync"
	"testing"
	"time"
	"container/list"
	"io"
)

type reqParams struct {
	proto       string //协议
	hostEP      string //请求的终端
	desc        string //描述
	reqURL      string
	expectResp  string
	expectJson  map[string]interface{}
	getFromJson map[string]func(v interface{})
	method      string
	args        map[string]string
	jsonArgs    string
}

//display performance info or not
//   true: display
//  false: hide
//控制是否显示调试数据
var enableFncPerf = true

func reqTest(t *testing.T, params *reqParams, info *UserInfo) {

	if params.hostEP == "" {
		params.hostEP = srv
	}

	if params.proto == "" {
		params.proto = protocol
	}

	defer timeTrack(time.Now(),params.reqURL)

	var err error

	req := &fasthttp.Request{}
	resp := &fasthttp.Response{}

	switch params.method {
	case "GET":
		dstURL, err := url.Parse(params.proto + "://" + params.hostEP + params.reqURL)
		if err != nil {
			log.Fatalf("%s: err:%s", params.desc, err.Error())
			return
		}

		//key-value对参数
		queries := url.Values{}
		for k, v := range params.args {
			queries.Add(k, v)
		}

		dstURL.RawQuery = queries.Encode()
		u := dstURL.String()

		req.SetRequestURI(u)
		req.Header.SetMethod(http.MethodGet)

		if info.Cookie != nil {
			req.Header.SetCookieBytesKV(info.Cookie.Key(), info.Cookie.Value())
		}

		err = info.Client.Do(req, resp)
		if err != nil {
			t.Fatal(params.desc + " failed with " + err.Error())
		}

		if info.Cookie == nil {
			//查cookie
			cookie := fasthttp.Cookie{}
			cookie.SetKey("attnd")
			chk := resp.Header.Cookie(&cookie)
			if chk {
				info.Cookie = &cookie
			} else {
				log.Fatalln("no cookie")
				return
			}
		}

	case "POST":
		var body string
		//看参数是键值对还是json
		if params.jsonArgs != "" {
			req.Header.Set("Content-Type", "application/json")
			body = params.jsonArgs
		} else {
			//key-value对参数
			queries := url.Values{}
			for k, v := range params.args {
				queries.Add(k, v)
			}
			body = queries.Encode()
		}

		if body == "" {
			log.Fatalf("%s: err:%s", params.desc, "body empty")
			return
		}
		req.SetRequestURI(params.proto + "://" + params.hostEP + params.reqURL)
		req.Header.SetMethod(http.MethodPost)
		req.SetBodyString(body)

		if info.Cookie != nil {
			req.Header.SetCookieBytesKV(info.Cookie.Key(), info.Cookie.Value())
		}

		err = info.Client.Do(req, resp)
		if err != nil {
			log.Fatalln(params.desc + " failed with " + err.Error())
		}

	default:
		log.Fatalln(params.desc + "unknown request method: " + params.method)
		return
	}

	if enableFncPerf {
		log.Println(fmt.Sprintf("desc:%s,code:%d,reqBody:%s",params.desc,resp.StatusCode(),string(resp.Body())))
	}

	if params.expectJson != nil {
		var json_data interface{}
		err = json.Unmarshal(resp.Body(), &json_data)
		if err != nil {
			log.Fatalln(params.desc + " Unmarshal failed " + err.Error())
			return
		}
		for k, v := range params.expectJson {
			if k == "" {
				log.Fatalln(params.desc + " k empty ")
				return
			}
			data, err := jsonpath.JsonPathLookup(json_data, k)
			if err != nil {
				log.Fatalln(params.desc + " JsonPathLookup failed " + err.Error())
				return
			}
			if v != data {
				log.Fatalf(params.desc+"k:%s ----- v:%s != data:%s", k, v, data)
				return
			}
		}
	}

	if params.getFromJson != nil {
		var json_data interface{}
		err = json.Unmarshal(resp.Body(), &json_data)
		if err != nil {
			log.Fatalln(params.desc + " Unmarshal failed " + err.Error())
			return
		}
		for k, fun := range params.getFromJson {
			if k == "" {
				log.Fatalln(params.desc + " k empty ")
				return
			}
			data, err := jsonpath.JsonPathLookup(json_data, k)
			if err != nil {
				log.Fatalln(params.desc + " JsonPathLookup failed " + err.Error())
				return
			}

			fun(data)
		}

	}

	if params.expectResp != "" && params.expectResp != string(resp.Body()) {
		t.Fatal(params.desc + fmt.Sprintf(" failed as params.expectResp--%s != resp.Body()--%s", params.expectResp, string(resp.Body())))
	}

}

const srv = "127.0.0.1:8888"
const protocol = "HTTP"

func setup() {
	//随机种子
	randSrc = rand.New(rand.NewSource(time.Now().UnixNano()))
}

const reTryTimes = 10

func TestMain(m *testing.M) {

	/*serverOk := make(chan int)
	url := fmt.Sprintf("http://%s/", srv)
	go func() {
		reTry := 0
		for {
			if reTry >= reTryTimes {
				panic(m)
			}
			time.Sleep(2 * time.Second)
			resp, err := http.Get(url)
			if err != nil {
				log.Println(fmt.Sprintf("retry...%d", reTry))
				reTry++
				continue
			}
			if resp.StatusCode != 200 {
				log.Println(fmt.Sprintf("retry...%d", reTry))
				reTry++
				continue
			}
			serverOk <- 1
		}
	}()
	<-serverOk*/

	/*	log.Println("Server Ready")*/
	//test setup
	enableFncPerf = true

	rt = make(map[string]*ReportInfo)

	fmt.Println("start testing\ncleanup old test data if exists any")
	setup()

	clearTestData()

	iRsl := m.Run()

	//test teardown
	//fmt.Println("clean after test")
	//clearTestData()
	fmt.Println("test complete")
	os.Exit(iRsl)
}

var lock sync.Mutex

var rt map[string]*ReportInfo

func responseTimeTrack(url string,rtDuration time.Duration) {
	if url == "" || rtDuration <= 0 {
		log.Fatalln("responseTimeTrack param invalid")
		return
	}
	if rt[url]==nil {
		ri := &ReportInfo{
			SrcRT:list.New(),
		}
		rt[url] = ri
	}
	rt[url].SrcRT.PushBack(rtDuration)
}

func createName() string {
	lock.Lock()
	defer lock.Unlock()
	//todo..lock when concurrent
	strlen := 8
	const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
	result := make([]byte, strlen)
	for i := range result {
		result[i] = chars[randSrc.Intn(len(chars))]
	}
	return string(result)
}

var randSrc *rand.Rand

type UserInfo struct {
	Client     *fasthttp.Client
	Name       string
	OpenID     string
	ID         int
	StuID      string
	SessionKey string
	Cookie     *fasthttp.Cookie
}

type Location struct {
	Latitude  float64 `json:"latitude"`
	Longitude float64 `json:"longitude"`
	Accuracy  float64 `json:"accuracy"`
}

type Attnd struct {
	AttndID     int       `json:"attnd_id,omitempty"`
	AttndName   string    `json:"attnd_name"`
	Location    *Location `json:"location"`
	AddrName    string    `json:"addr_name"`
	TeacherName string    `json:"teacher_name"`
	TeacherID   int       `json:"teacher_id,omitempty"`
	Cipher      string    `json:"cipher"`
	StartTime   int64     `json:"start_time"`
}

func timeTrack(start time.Time, name string) {
	elapsed := time.Since(start)
	responseTimeTrack(name,elapsed)
	if elapsed > 1*time.Second {
		fmt.Printf("%30s: %s\n", name, elapsed.String())
	}

}

func addAttnd(info *UserInfo, attnd *Attnd, t *testing.T) {
	attndData, err := json.Marshal(attnd)
	if err != nil {
		log.Fatalln("Marshal attnd failed: " + err.Error())
		return
	}
	reqTest(t, &reqParams{
		expectResp: "",
		method:     "POST",
		reqURL:     "/api/attnd",
		jsonArgs:   string(attndData),
		expectJson: map[string]interface{}{
			"$.code":                 1000.0,
			"$.data.userinfo.name":   "郭思文",
			"$.data.userinfo.openid": info.OpenID,
		},
		getFromJson: map[string]func(v interface{}){
			"$.data.cipher": func(v interface{}) {
				attnd.Cipher = v.(string)
			},
			"$.data.attnd_id": func(v interface{}) {
				attnd.AttndID = int(v.(float64))
			},
		},
	}, info)
}

func TestUserProcess(t *testing.T) {
	//defer timeTrack(time.Now(), "spider")

	teacher1 := &UserInfo{
		Client:     &fasthttp.Client{},
		OpenID:     "_T__1" + createName(),
		SessionKey: "000",
	}
	stu1 := &UserInfo{
		Client:     &fasthttp.Client{},
		OpenID:     "_T__2" + createName(),
		SessionKey: "111",
	}
	stu2 := &UserInfo{
		Client:     &fasthttp.Client{},
		OpenID:     "_T__3" + createName(),
		SessionKey: "123",
	}
	stu3 := &UserInfo{
		Client:     &fasthttp.Client{},
		OpenID:     "_T__4" + createName(),
		SessionKey: "222",
	}
	stu4 := &UserInfo{
		Client:     &fasthttp.Client{},
		OpenID:     "_T__5" + createName(),
		SessionKey: "333",
	}

	spider <- 1
	/*	defer func() {
		spider <- -1
	}()*/

	login := func(info *UserInfo) {
		reqTest(t, &reqParams{
			expectResp: "ok",
			method:     "GET",
			reqURL:     "/api/mocklogin",
			args: map[string]string{
				"id":          strconv.Itoa(info.ID),
				"name":        info.Name,
				"openid":      info.OpenID,
				"stuid":       info.StuID,
				"session_key": info.SessionKey,
			},
		}, info)
	}

	login(teacher1)
	login(stu1)
	login(stu2)
	login(stu3)
	login(stu4)

	attnd1 := &Attnd{
		AttndName:   "_T__" + createName(),
		StartTime:   1526996690000,
		TeacherName: "郭思文",
		AddrName:    "文新510",
		Location: &Location{
			Latitude:  23.4,
			Longitude: 174.4,
			Accuracy:  30.0,
		},
	}
	addAttnd(teacher1, attnd1, t)

	stu1.Name = "__T_11" + createName()
	stu2.Name = "__T_22" + createName()
	stu2.StuID = "__T_33" + createName()
	stu3.Name = "__T_44" + createName()
	stu3.StuID = "__T_55" + createName()
	stu4.Name = "__T_66" + createName()
	fillInfo := func(info *UserInfo) {
		reqTest(t, &reqParams{
			expectResp: "",
			method:     "POST",
			reqURL:     "/api/user/info",
			jsonArgs:   fmt.Sprintf(`{"name":"%s","stu_id":"%s"}`, info.Name, info.StuID),
			expectJson: map[string]interface{}{
				"$.code": 1000.0,
			},
			desc: "fillInfo ",
		}, info)
	}

	fillInfo(stu1)
	fillInfo(stu2)
	fillInfo(stu3)
	fillInfo(stu4)

	signin := func(cipher string, info *UserInfo) {
		reqTest(t, &reqParams{
			expectResp: "",
			method:     "POST",
			reqURL:     "/api/attnd/signin",
			jsonArgs:   fmt.Sprintf(`{"cipher":"%s","location":{"latitude":23.4,"longitude":174.4005,"accuracy":30.0}}`, cipher),
			expectJson: map[string]interface{}{
				"$.code": 1000.0,
				"$.data": 1.0,
			},
			desc: "signin ",
		}, info)
	}

	signinLate := func(cipher string, info *UserInfo) {
		reqTest(t, &reqParams{
			expectResp: "",
			method:     "POST",
			reqURL:     "/api/attnd/signin",
			jsonArgs:   fmt.Sprintf(`{"cipher":"%s","location":{"latitude":23.4,"longitude":174.4005,"accuracy":30.0}}`, cipher),
			expectJson: map[string]interface{}{
				"$.code": 1000.0,
				"$.data": 3.0,
			},
			desc: "signin late",
		}, info)
	}

	signin_beyond := func(cipher string, info *UserInfo) {
		reqTest(t, &reqParams{
			expectResp: "",
			method:     "POST",
			reqURL:     "/api/attnd/signin",
			jsonArgs:   fmt.Sprintf(`{"cipher":"%s","location":{"latitude":28.4,"longitude":174.4005,"accuracy":30.0}}`, cipher),
			expectJson: map[string]interface{}{
				"$.code": 1000.0,
				"$.data": 2.0,
			},
			desc: "signin_beyond ",
		}, info)
	}

	signin_repeat := func(cipher string, info *UserInfo) {
		reqTest(t, &reqParams{
			expectResp: "",
			method:     "POST",
			reqURL:     "/api/attnd/signin",
			jsonArgs:   fmt.Sprintf(`{"cipher":"%s","location":{"latitude":28.4,"longitude":174.4005,"accuracy":30.0}}`, cipher),
			expectJson: map[string]interface{}{
				"$.code": 3003.0,
			},
			desc: "signin_repeat ",
		}, info)
	}

	wg := &sync.WaitGroup{}

	//stu1 signin
	WaitGroupWrapper(wg, func() {
		signin(attnd1.Cipher, stu1)
	})

	//stu2 signin beyond
	WaitGroupWrapper(wg, func() {
		signin_beyond(attnd1.Cipher, stu2)
	})

	//stu3 signin
	WaitGroupWrapper(wg, func() {
		signin(attnd1.Cipher, stu3)
	})
	wg.Wait()

	//stu1 try to signin again
	WaitGroupWrapper(wg, func() {
		signin_repeat(attnd1.Cipher, stu1)
	})
	wg.Wait()

	chkAttnd := func(info *UserInfo, attnd *Attnd) {
		reqTest(t, &reqParams{
			expectResp: "",
			method:     "GET",
			reqURL:     "/api/attnd",
			args: map[string]string{
				"cipher": attnd.Cipher,
			},
			expectJson: map[string]interface{}{
				"$.code":                    1000.0,
				"$.data.status":             1.0,
				"$.data.attnd_name":         attnd.AttndName,
				"$.data.start_time":         float64(attnd.StartTime),
				"$.data.addr_name":          attnd.AddrName,
				"$.data.teacher_name":       attnd.TeacherName,
				"$.data.location.longitude": attnd.Location.Longitude,
			},
			desc: "chkAttnd ",
		}, info)
	}

	chkAttnd(teacher1, attnd1)

	reqTest(t, &reqParams{
		expectResp: "",
		method:     "GET",
		reqURL:     "/api/attnd/situation",
		args: map[string]string{
			"cipher":        attnd1.Cipher,
			"page":          "1",
			"page_size":     "20",
			"signin_status": "0",
		},
		expectJson: map[string]interface{}{
			"$.code":                        1000.0,
			"$.data.count":                  3.0,
			"$.data.attnds[0].openid":       stu1.OpenID,
			"$.data.attnds[0].name":         stu1.Name,
			"$.data.attnds[0].stu_id":       stu1.StuID,
			"$.data.attnds[0].attnd_status": 1.0,
			"$.data.attnds[0].distance":     51.020,

			"$.data.attnds[1].openid":       stu2.OpenID,
			"$.data.attnds[1].name":         stu2.Name,
			"$.data.attnds[1].stu_id":       stu2.StuID,
			"$.data.attnds[1].attnd_status": 2.0,
			"$.data.attnds[1].distance":     555947.880,

			"$.data.attnds[2].openid":       stu3.OpenID,
			"$.data.attnds[2].name":         stu3.Name,
			"$.data.attnds[2].stu_id":       stu3.StuID,
			"$.data.attnds[2].attnd_status": 1.0,
			"$.data.attnds[2].distance":     51.020,

			"$.data.my_signin.openid":       stu1.OpenID,
			"$.data.my_signin.name":         stu1.Name,
			"$.data.my_signin.stu_id":       stu1.StuID,
			"$.data.my_signin.attnd_status": 1.0,
			"$.data.my_signin.distance":     51.020,
		},
		desc: "chkAttndSituation ",
	}, stu1)

	reqTest(t, &reqParams{
		expectResp: "",
		method:     "GET",
		reqURL:     "/api/attnd/situation",
		args: map[string]string{
			"cipher":        attnd1.Cipher,
			"page":          "1",
			"page_size":     "20",
			"signin_status": "0",
		},
		expectJson: map[string]interface{}{
			"$.code":                        1000.0,
			"$.data.count":                  3.0,
			"$.data.attnds[0].openid":       stu1.OpenID,
			"$.data.attnds[0].name":         stu1.Name,
			"$.data.attnds[0].stu_id":       stu1.StuID,
			"$.data.attnds[0].attnd_status": 1.0,
			"$.data.attnds[0].distance":     51.020,

			"$.data.attnds[1].openid":       stu2.OpenID,
			"$.data.attnds[1].name":         stu2.Name,
			"$.data.attnds[1].stu_id":       stu2.StuID,
			"$.data.attnds[1].attnd_status": 2.0,
			"$.data.attnds[1].distance":     555947.880,

			"$.data.attnds[2].openid":       stu3.OpenID,
			"$.data.attnds[2].name":         stu3.Name,
			"$.data.attnds[2].stu_id":       stu3.StuID,
			"$.data.attnds[2].attnd_status": 1.0,
			"$.data.attnds[2].distance":     51.020,
		},
		desc: "chkAttndSituation ",
	}, teacher1)

	//teacher end the attnd
	reqTest(t, &reqParams{
		expectResp: "",
		method:     "POST",
		reqURL:     "/api/attnd/end",
		args: map[string]string{
			"cipher": attnd1.Cipher,
		},
		expectJson: map[string]interface{}{
			"$.code": 1000.0,
		},
	}, teacher1)

	//stu4 signin after the end of the attnd
	signinLate(attnd1.Cipher, stu4)

	//teacher upd stu1 to late
	stu1NewStatus := 3
	reqTest(t, &reqParams{
		expectResp: "",
		method:     "POST",
		reqURL:     "/api/signin/status/upd",
		args: map[string]string{
			"cipher": attnd1.Cipher,
			"openid":stu1.OpenID,
			"attnd_status":strconv.Itoa(stu1NewStatus),
		},
		expectJson: map[string]interface{}{
			"$.code": 1000.0,
		},
	}, teacher1)

	//chk attnd situation 2
	reqTest(t, &reqParams{
		expectResp: "",
		method:     "GET",
		reqURL:     "/api/attnd/situation",
		args: map[string]string{
			"cipher":        attnd1.Cipher,
			"page":          "1",
			"page_size":     "20",
			"signin_status": "0",
		},
		expectJson: map[string]interface{}{
			"$.code":                        1000.0,
			"$.data.count":                  4.0,
			"$.data.attnds[0].openid":       stu1.OpenID,
			"$.data.attnds[0].name":         stu1.Name,
			"$.data.attnds[0].stu_id":       stu1.StuID,
			"$.data.attnds[0].attnd_status": float64(stu1NewStatus),  //stu1 --> update to 3 (late)
			"$.data.attnds[0].distance":     51.020,

			// new sign in after attnd end with status 3 (late)
			"$.data.attnds[1].openid":       stu4.OpenID,
			"$.data.attnds[1].name":         stu4.Name,
			"$.data.attnds[1].stu_id":       stu4.StuID,
			"$.data.attnds[1].attnd_status": 3.0,
			"$.data.attnds[1].distance":     51.020,
			
			"$.data.attnds[2].openid":       stu2.OpenID,
			"$.data.attnds[2].name":         stu2.Name,
			"$.data.attnds[2].stu_id":       stu2.StuID,
			"$.data.attnds[2].attnd_status": 2.0,
			"$.data.attnds[2].distance":     555947.880,

			"$.data.attnds[3].openid":       stu3.OpenID,
			"$.data.attnds[3].name":         stu3.Name,
			"$.data.attnds[3].stu_id":       stu3.StuID,
			"$.data.attnds[3].attnd_status": 1.0,
			"$.data.attnds[3].distance":     51.020,


		},
		desc: "chkAttndSituation after attnd end",
	}, teacher1)

	//teacher get attndlist before del
	reqTest(t, &reqParams{
		expectResp: "",
		method:     "GET",
		reqURL:     "/api/attndlist",
		args: map[string]string{
			"list_type": "1",
			"page": "1",
			"page_size": "10",
			"query": "",
		},
		expectJson: map[string]interface{}{
			"$.code": 1000.0,
			"$.data.count": 1.0,
			"$.data.attnds[0].status": 5.0, //has end
			"$.data.attnds[0].attnd_name": attnd1.AttndName,
			"$.data.attnds[0].teacher_name": attnd1.TeacherName,
			"$.data.attnds[0].addr_name": attnd1.AddrName,
			"$.data.attnds[0].cipher": attnd1.Cipher,
		},
	}, teacher1)

	//student signin get attndlist before del
	reqTest(t, &reqParams{
		expectResp: "",
		method:     "GET",
		reqURL:     "/api/attndlist",
		args: map[string]string{
			"list_type": "2",
			"page": "1",
			"page_size": "10",
			"query": "",
		},
		expectJson: map[string]interface{}{
			"$.code": 1000.0,
			"$.data.count": 1.0,
			"$.data.attnds[0].status": 5.0, //has end
			"$.data.attnds[0].attnd_name": attnd1.AttndName,
			"$.data.attnds[0].teacher_name": attnd1.TeacherName,
			"$.data.attnds[0].addr_name": attnd1.AddrName,
			"$.data.attnds[0].cipher": attnd1.Cipher,
		},
	}, stu1)

	//get his addr name
	reqTest(t, &reqParams{
		expectResp: "",
		method:     "GET",
		reqURL:     "/api/attnd/hisaddr",
		expectJson: map[string]interface{}{
			"$.code": 1000.0,
			"$.data[0]": attnd1.AddrName,
		},
	}, teacher1)

	//get his addr name
	reqTest(t, &reqParams{
		expectResp: "",
		method:     "GET",
		reqURL:     "/api/attnd/hisname",
		expectJson: map[string]interface{}{
			"$.code": 1000.0,
			"$.data[0]": attnd1.AttndName,
		},
	}, teacher1)


	//teacher del attnd
	reqTest(t, &reqParams{
		expectResp: "",
		method:     "POST",
		reqURL:     "/api/attnd/del",
		args: map[string]string{
			"cipher": attnd1.Cipher,
		},
		expectJson: map[string]interface{}{
			"$.code": 1000.0,
		},
	}, teacher1)

	//teacher get attndlist
	reqTest(t, &reqParams{
		expectResp: "",
		method:     "GET",
		reqURL:     "/api/attndlist",
		args: map[string]string{
			"list_type": "1",
			"page": "1",
			"page_size": "10",
			"query": "",
		},
		expectJson: map[string]interface{}{
			"$.code": 1000.0,
			"$.data.count": 0.0,
		},
	}, teacher1)

	spider <- -1
}

var concurrentSpider atomic.Uint32
var doneSpider atomic.Uint32
var spider = make(chan int, 1024)

func spiderMove() {
	for c := range spider {
		if c > 0 {
			concurrentSpider.Inc()
		} else {
			concurrentSpider.Dec()
			doneSpider.Inc()
		}
	}
}

func WaitGroupWrapper(group *sync.WaitGroup, fn func()) {
	group.Add(1)
	go func() {
		fn()
		group.Done()
	}()
}

var startNext = make(chan bool, 1024*4)

type ReportInfo struct {
	AverRT time.Duration
	MaxRT time.Duration
	MinRT time.Duration
	SrcRT *list.List
}


func reporter(writer io.Writer)  {
	writer.Write([]byte("detail::::\n"))
	for k,v :=  range rt {
		writer.Write([]byte("\n"+k+"\n"))
		v.MaxRT = v.SrcRT.Front().Value.(time.Duration)
		v.MinRT = v.SrcRT.Front().Value.(time.Duration)
		for e:=v.SrcRT.Front();e!=nil;e = e.Next()  {
			val := e.Value.(time.Duration)
			if val<v.MinRT {
				v.MinRT = val
			}
			if val>v.MaxRT {
				v.MaxRT = val
			}
			v.AverRT += (val - v.AverRT) / time.Duration(v.SrcRT.Len())
			writer.Write([]byte(val.String()+"\n"))
		}
		writer.Write([]byte("\n"))
	}
	writer.Write([]byte("overview::::\n"))
	for k,v :=  range rt {
		writer.Write([]byte("\n"+k+"\n"))
		writer.Write([]byte("\n AverRT ::::"+v.AverRT.String()+"\n"))
		writer.Write([]byte("\n MaxRT ::::"+v.MaxRT.String()+"\n"))
		writer.Write([]byte("\n MinRT ::::"+v.MinRT.String()+"\n"))
	}
}

//go test -run TestA
func TestParallel(t *testing.T) {

	enableFncPerf = false
	go spiderMove()

	remiseCPUTime := time.Millisecond * 20

	go func() {
		log.Println(http.ListenAndServe("localhost:6060", nil))
	}()

	file,err := os.Create("./reporter.log")
	if err != nil {
		t.Fatal(err)
		return
	}

	//startNext <- true
	for {

		//<-startNext
		fmt.Printf("active spider is %d, done spider is %d\n", concurrentSpider.Load(), doneSpider.Load())
		go TestUserProcess(t)
		time.Sleep(remiseCPUTime)
		if concurrentSpider.Load() >= 100 {
			reporter(file)
			time.Sleep(time.Second * 12)
		}

	}
}
