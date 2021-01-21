class Glitter {
    //設定某頁面的資料
    static setPageData(tag, objName, value) {
        return window.Jz.setPageData(tag, objName, value)
    }

    //取得某頁面的資料
    static getPageData(tag, data) {
        return window.Jz.getPageData(tag, data)
    }

    //取得MainActivity的資料
    static getActivityData() {
        return JSON.parse(window.Jz.getActivityData())
    }

    //取得MainActivity的資料
    static setActivityData(objName, value) {
        return window.Jz.setActivityData(objName, value)
    }

    //取得現在Html的Tag名稱
    static getTagName() {
        return window.Jz.getTagName()
    }

//Log顯示
    static logE(a, b) {
        window.Jz.logE(a, b);
    }

//設定首頁
    static setHome(url, tag, json) {
        window.Jz.setHome(url, tag, JSON.stringify(json));
    }

//吐司顯示
    static toast(a) {
        window.Jz.toast(a);
    }

//頁面切換
    static changePage(link, tag, goBack, json) {
        window.Jz.changePage(link, tag, goBack, JSON.stringify(json));
    }

//頁面切換與動畫
    static changePage(link, tag, goBack, animator, json) {
        window.Jz.changePage(link, tag, goBack, animator, JSON.stringify(json));
    }

    //設定全域變數
    static setStaticVariable(name, value) {
        window.Jz.setStaticVariable(name, JSON.stringify(value));
    }

    //取得全域變數
    static getStaticVariable(name) {
        return JSON.parse(window.Jz.getStaticVariable(name));
    }

    //設定側邊抽屜
    static setDrawer(url, json) {
        window.Jz.setDrawer(url, json);
    }

    //顯示Dialog
    static showDiaLog(url, tag, swipe, cancelable, json) {
        window.Jz.showDiaLog(url, tag, swipe, cancelable, JSON.stringify(json));
    }

    //顯示上滑Dialog
    static showBottomSheetDialog(url, tag, swipe, cancelable, json) {
        window.Jz.showBottomSheetDialog(url, tag, swipe, cancelable, JSON.stringify(json));
    }

    //關閉所有Dialog
    static closeDiaLog() {
        window.Jz.closeDiaLog()
    }

    //關閉特定頁面Dialog
    static closeDiaLog(tag) {
        window.Jz.closeDiaLog(tag)
    }

    //Dialog是否正在顯示
    static diaIsShowing(tag) {
        return window.Jz.diaIsShowing(tag);
    }

    //android權限請求
    static permissionRequest(json) {
        window.Jz.permissionRequest(JSON.stringify(json));
    }

    //跳轉至系統特定功能
    static intent(string) {
        window.Jz.intent(string);
    }

    //取得JSON
    static  getValue(value){
        return JSON.parse(window.Jz.getJSON())[value]
    }
    //設定JSON DATA
    static setValue(tag,value){
        let a=JSON.parse(window.Jz.getJSON())
        a[tag]=value
        window.Jz.setJSON(JSON.stringify(a))
    }
}

//頁面切換動畫
class Animator {
    static translation = 0
    static rotation = 1
    static verticalTranslation = 2
}

//權縣請求回條
class Permission_C {
    static requestFalse = function (string) {
    }
    static requestSuccess = function (string) {
    }
}

//Fragment監聽
class JzFragment {
    static viewInit = function () {
    }
    static onResume = function () {
    }
    static onPause = function () {
    }
    static onDestroy = function () {
    }
}

//Activity監聽
class JzActivity {
    static changePageListener = function (tag, url) {
    }
    static dialogListener = function (tag, url) {
    }
    static keyEventListener = function (event) {
    }
    static viewInit = function () {
    }
    static onResume = function () {
    }
    static onPause = function () {
    }
    static onDestroy = function () {
    }
}