class Md_User {
    constructor() {
        //帳號
        this.account = ''
        //密碼
        this.password = ''
        //名稱
        this.pick = ''
        //頭貼
        this.head = ''
        //選擇的地區
        this.area = ''
        //選擇的圈子
        this.circle = ''
    }
}
//登入頁面的登入
function login(admin, password) {
    glitter.openDiaLog('dialog/Dia_DataLoading.html', 'SignIn', false, false, '{}')
    let json = {}
    json.request = "login"
    json.account = admin
    json.password = password
    glitter.publicBeans.postRequest(json, function (data) {
        glitter.closeDiaLogWithTag('SignIn')
        var mapData = JSON.parse(data)
        if (mapData.result === "true") {
            glitter.publicBeans.token=mapData.token
            var dat=JSON.parse(mapData.data)
            var map={}
            map.account=dat.account
            map.password=dat.password
            glitter.md_user=dat
            glitter.setPro('Md_User', JSON.stringify(map))
            glitter.setHome('page/Page_Home.html', 'Page_Home', '{}')
        } else {
            glitter.openDiaLog('dialog/Dia_Info.html', 'info', false, true, '登入失敗，請檢查帳號密碼是否輸入正確!!')
        }
    }, function (data) {
        glitter.closeDiaLogWithTag('SignIn')
        glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, true, '{}')
    })
}

