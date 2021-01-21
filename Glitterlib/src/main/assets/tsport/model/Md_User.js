class Md_User {
    constructor() {
        var glitter=window.parent.glitter
        var map={}
        try{
            map=JSON.parse(glitter.getPro('Md_User'))
        }catch (e) {
            map.account=''
            map.password=''
            map.pick=''
            map.head=''
            map.area=''
            map.circle=''
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
        //登入
        this.login = function () {
            let json = {}
            json.request = "login"
            json.account = "fb1662627190550319"
            json.password = "loginWithFB"
            return window.parent.glitter.postRequest(publicBeans.apiRoot, json, 10 * 1000)
        }
    }
}

var md_user=new Md_User()

