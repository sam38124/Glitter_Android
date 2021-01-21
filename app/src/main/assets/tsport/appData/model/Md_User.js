class Md_User {
    constructor() {
        var glitter = window.parent.glitter
        var map = {}
        try {
            map = JSON.parse(glitter.getPro('Md_User'))
        } catch (e) {
            map.account = ''
            map.password = ''
            map.pick = ''
            map.head = ''
            map.area = ''
            map.circle = ''
        }
        //帳號
        this.account = map.account
        //密碼
        this.password = map.password
        //名稱
        this.pick = map.pick
        //頭貼
        this.head = map.head
        //選擇的地區
        this.area = map.area
        //選擇的圈子
        this.circle = map.circle
    }
}

var md_user = new Md_User()

function login(admin, password) {
    getGlitter().openDiaLog('dialog/Dia_DataLoading.html', 'SignIn', false, false, '{}')
    let json = {}
    json.request = "login"
    json.account = admin
    json.password = password
    rootActivity().publicBeans.postRequest(json, function (data) {
        getGlitter().closeDiaLogWithTag('SignIn')
        var mapData = JSON.parse(data)
        if (mapData.result === "true") {
            getGlitter().setPro('Md_User', JSON.stringify(mapData.data))
            getGlitter().setHome('page/Page_Home.html', 'Page_Home.html', '{}')
        } else {
            getGlitter().openDiaLog('dialog/Dia_Info.html', 'info', false, true, '登入失敗，請檢查帳號密碼是否輸入正確!!')
        }
    }, function (data) {
        getGlitter().closeDiaLogWithTag('SignIn')
        getGlitter().openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, true, '{}')
    })
}

