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
            glitter.serialUtil.storeObject(dat,"md_user","Md_User",function (){
                switch (glitter.deviceType){
                    case glitter.deviceTypeEnum.Android:
                        glitter.toAssetRoot(gBundle)
                        break
                    case glitter.deviceTypeEnum.Web:
                        glitter.location.href=gBundle
                        break
                    case glitter.deviceTypeEnum.Ios:
                        glitter.reloadPage()
                        break
                }

            },function (){
                glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, true, '{}')
            })

        } else {
            glitter.openDiaLog('dialog/Dia_Info.html', 'info', false, true, '登入失敗，請檢查帳號密碼是否輸入正確!!')
        }
    }, function (data) {
        glitter.closeDiaLogWithTag('SignIn')
        glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, true, '{}')
    })
}

//FB登入
function loginWithFacebook(admin, pick) {
    glitter.openDiaLog('dialog/Dia_DataLoading.html', 'SignIn', false, false, '{}')
    let json = {}
    json.request = "login"
    json.account = admin
    json.password = "facebookLogin"
    glitter.publicBeans.postRequest(json, function (data) {
        glitter.closeDiaLogWithTag('SignIn')
        var mapData = JSON.parse(data)
        if (mapData.result === "true") {
            glitter.publicBeans.token=mapData.token
            var dat=JSON.parse(mapData.data)
            var map={}
            map.account=dat.account
            map.password=dat.password
            glitter.serialUtil.storeObject(dat,"md_user","Md_User",function (){
                switch (glitter.deviceType){
                    case glitter.deviceTypeEnum.Android:
                        glitter.reloadPage()
                        break
                    case glitter.deviceTypeEnum.Web:
                        glitter.location.href=gBundle
                        break
                    case glitter.deviceTypeEnum.Ios:
                        glitter.reloadPage()
                        break
                }

            },function (){
                glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, true, '{}')
            })
        } else {
            glitter.changePage('page/Page_Register_FaceBook.html','Page_Register_FaceBook',true,{
                account:admin,
                pick:pick,
                password:'facebookLogin',
                returnUrl:gBundle
            })
        }
    }, function (data) {
        glitter.closeDiaLogWithTag('SignIn')
        glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, true, '{}')
    })
}
