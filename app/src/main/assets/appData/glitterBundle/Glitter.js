/*Read only*/
/*Please dont change*/
"use strict";

//頁面切換動畫
class Animator {
    constructor() {
        this.translation = 0;
        this.rotation = 1;
        this.verticalTranslation = 2;
    }
}

//應用類型
class AppearType {
    constructor() {
        this.Web = 0;
        this.Android = 1;
        this.Ios = 2;
    }
}

//Html類型
class HtmlType {
    constructor() {
        this.Page = 0;
        this.Dialog = 1;
        this.Frag = 2;
    }
}

class Glitter {
    constructor() {
        this.webUrl=''
        this. callBackId=0
        this. callBackList = new Map();
        this.appearType=new AppearType()
        var getUrl = window.location ;
        //公用變數
        this.publicBeans={}
        //
        this.baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
        //Html類型
        this.htmlType = new HtmlType()
        //轉場動畫
        this.animator = new Animator()
        //設定類型
        this.type = new AppearType().Web
        //現在所有的Iframe
        this.iframe = []
        //現在所有的Frag
        this.ifrag = []
        //現在所有的Dialog
        this.dialog = []
        //現在所有的Sheet
        this.sheetList = []
        //Web的紀錄
        this.webPro = {}
        //PageIndex
        this.pageIndex = 0
        //PageIndex
        this.bottomSheet = []
        //存紀錄
        this.setPro = function (tag, data) {
            switch (glitter.type) {
                case appearType.Web: {
                    glitter.webPro[tag] = data
                    waitSetPro(tag, data)
                    break
                }
                case appearType.Android: {
                    window.GL.setPro(tag, data)
                    break
                }
                case appearType.Ios: {
                    var map={
                        tag:tag,
                        data:data
                    }
                    window.webkit.messageHandlers.setPro.postMessage(JSON.stringify(map));
                    break
                }
            }

        }
        //取紀錄
        this.getPro = function (tag,fun) {
            var id=glitter.callBackId+=1
            glitter.callBackList.set(id,fun)
            switch (glitter.type) {
                case appearType.Web: {
                    glitter.callBackList.get(id)(glitter.webPro[tag])
                    glitter.callBackList.delete(id)
                    return
                }
                case appearType.Android: {
                    glitter.callBackList.get(id)(window.GL.getPro(tag))
                    glitter.callBackList.delete(id)
                    return
                }
                case appearType.Ios: {
                    var map={
                        tag:tag,
                        callback:id
                    }
                    window.webkit.messageHandlers.getPro.postMessage(JSON.stringify(map));
                    break
                }
            }
        }
        //設定某頁面的資料
        this.setPageData = function (tag, objName, value) {
            return window.Jz.setPageData(tag, objName, value)
        }

        //取得某頁面的資料
        this.getPageData = function (tag, data) {
            return window.Jz.getPageData(tag, data)
        }

        //取得MainActivity的資料
        this.getActivityData = function () {
            return JSON.parse(window.Jz.getActivityData())
        }

        //取得MainActivity的資料
        this.setActivityData = function (objName, value) {
            return window.Jz.setActivityData(objName, value)
        }

        //取得現在Html的Tag名稱
        this.getTagName = function () {
            return window.Jz.getTagName()
        }

//Log顯示
        this.logE = function (a, b) {
            window.Jz.logE(a, b);
        }

//設定首頁
        this.setHome = function (url, tag, obj) {
            this.showLoadingView()
            glitter.pageIndex = glitter.pageIndex + 1
            var map = {}
            map.id = tag
            map.obj = obj
            map.goback = true
            map.pageIndex = glitter.pageIndex
            glitter.iframe = []
            glitter.iframe = glitter.iframe.concat(map)
            $('#framePlace').empty();
            $('#framePlace').append('<iframe style="display: none;" name="' + map.id + '" src="' + this.baseUrl + ((glitter.type===appearType.Android) ? '':'/') + url + '?tag=' + tag + '&pageIndex=' + glitter.pageIndex + '&type=Page"  id="' + tag + '"></iframe>')
            $('#'+tag).load(function(){                             //  等iframe載入完畢
                $('#'+tag).show()
            });
            glitter.changePageListener(tag)
        }
        //吐司顯示
        this.toast = function (a) {
            window.Jz.toast(a);
        }
        //設定側滑選單
        this.setNavigation = function (src, obj) {
            glitter.addScript('glitterBundle/NaviGation.js')
            $("#Navigation").html('')
            $("#Navigation").html('<iframe src="' + this.baseUrl + ((glitter.type===appearType.Android) ? '':'/') + src + '"  style="width: 100%;"></iframe>')
        }
        //顯示加載畫面
        this.showLoadingView = function () {
            $('#loadingView').show()
        }
        //設定加載畫面
        this.setLoadingView = function (link) {
            $('#loadingView').hide()
            $('#loadingView').append('<iframe  src="' + this.baseUrl + ((glitter.type===appearType.Android) ? '':'/') + link + '" style="width: 100%;height: 100%;background-color: rgba(0, 0, 0, 0.5);"></iframe>')
        }
        //關閉加載畫面
        this.hideLoadingView = function () {
            $('#loadingView').hide()
        }
        //打開側滑選單
        this.openNavigation = function () {
            window.drawer.open();
        }
        //關閉側滑選單
        this.closeNavigation = function () {
            window.drawer.close();
        }
        //開關側滑選單
        this.toggleNavigation = function () {
            window.drawer.toggle();
        }
        //按鈕監聽
        this.keyEventListener = function (obj) {

        }
        //關閉側滑選單
        this.closeNavigation = function () {
            if (this.onclose || $(document.getElementById('navigation')).is(":hidden")) {
                return
            }
            this.onclose = true
            var elem = document.getElementById('navigation');
            $("#navigation").show()
            var ind = 0
            var pos = $('#navigation').width();
            var id = setInterval(frame, 1);

            function frame() {
                if (ind >= pos) {
                    $("#navigation").hide()
                    clearInterval(id);
                    this.onclose = false
                } else {
                    ind += 5;
                    elem.style.transform = 'translateX(-' + ind + 'px)';
                }
            }
        }
        //頁面切換
        this.changePage = function (link, tag, goBack, obj) {
            this.showLoadingView()
            glitter.pageIndex = glitter.pageIndex + 1
            var map = {}
            map.id = tag
            map.obj = obj
            map.goback = goBack
            map.pageIndex = glitter.pageIndex
            glitter.iframe = glitter.iframe.concat(map)
            $('#framePlace').append('<iframe name="' + tag + '" src="' + this.baseUrl + ((glitter.type===appearType.Android) ? '':'/') + link + '?tag=' + tag + '&pageIndex=' + glitter.pageIndex + '&type=Page"  id="' + tag + '" style="background-color: white;width: 100%;height: 100%;display: none;"></iframe>')
            $('#'+tag).load(function(){                             //  等iframe載入完畢
                $('#' + tag).show()
                for (var i = 0; i < glitter.iframe.length - 1; i++) {
                    console.log(glitter.iframe)
                    $('#' + glitter.iframe[i].id).hide()
                }
                document.getElementById(glitter.iframe[glitter.iframe.length - 1].id).contentWindow.lifeCycle.onPause();
                glitter.changePageListener(tag)
            });
        }
        //頁面切換防止白頻
        this.changeWait = function () {

        }
        //Fragment切換
        this.changeFrag = function (root, link, tag, obj) {
            glitter.pageIndex = glitter.pageIndex + 1
            root.innerHTML = ''
            var ife = []
            for (var a = 0; a < glitter.ifrag.length; a++) {
                if (glitter.ifrag[a].id !== 'Frag-' + tag) {
                    ife = ife.concat(glitter.ifrag[a])
                }
            }
            glitter.ifrag = ife;
            var map = {}
            map.id = 'Frag-' + tag
            map.obj = obj
            map.goback = false
            map.pageIndex = glitter.pageIndex
            glitter.ifrag = glitter.ifrag.concat(map)
            if (link.indexOf('http') !== -1) {
                root.innerHTML = '<iframe name="' + map.id + '" src="' + link + '?tag=' + map.id + '&pageIndex=' + glitter.pageIndex + '&type=Frag"  id="' + map.id + '" style="width: 100%;height: 100%;border-width: 0;"></iframe>'
            } else {
                root.innerHTML = '<iframe name="' + map.id + '" src="' + this.baseUrl +((glitter.type===appearType.Android) ? '':'/')+ link + '?tag=' + map.id + '&pageIndex=' + glitter.pageIndex + '&type=Frag"  id="' + map.id + '" style="width: 100%;height: 100%;border-width: 0;"></iframe>'
            }

        }

        //頁面切換與動畫
        this.changePageWithAnimation = function (link, tag, goBack, animator, obj) {
            this.showLoadingView()
            glitter.pageIndex = glitter.pageIndex + 1
            var map = {}
            map.id = tag
            map.obj = obj
            map.goback = goBack
            map.pageIndex = glitter.pageIndex
            document.getElementById(glitter.iframe[glitter.iframe.length - 1].id).contentWindow.lifeCycle.onPause();
            glitter.iframe = glitter.iframe.concat(map)
            $('#framePlace').append('<iframe name="' + map.id + '" src="' + this.baseUrl + ((glitter.type===appearType.Android) ? '':'/') + link + '?tag=' + tag + '&pageIndex=' + glitter.pageIndex + '&type=Page"  id="' + tag + '" style="background-color: white;width: 100%;height: 100%;"></iframe>')
            if (animator === this.animator.translation) {
                var elem = document.getElementById(tag);
                elem.style.transform = 'translateX(' + $('#' + tag).width() + 'px)'
                var pos = $('#' + tag).width();
                var id = setInterval(frame, 1);

                function frame() {
                    if (pos <= 0) {
                        clearInterval(id);
                        for (var i = 0; i < glitter.iframe.length - 1; i++) {
                            $('#' + glitter.iframe[i].id).hide()
                        }
                    } else {
                        pos -= 10;
                        elem.style.transform = 'translateX(' + pos + 'px)';
                    }
                }

                glitter.changePageListener(tag)
            }

        }

        //顯示Dialog
        this.openDiaLog = function (url, tag, swipe, cancelable, obj,dismiss) {
            if(glitter.dialog.filter(function (item) {
               return  item.id===('Dialog-' + tag)
                }).length!==0){
                return
            }
            glitter.pageIndex+=1
            var map = {}
            map.pageIndex = glitter.pageIndex
            map.id = 'Dialog-' + tag
            map.obj = obj
            map.dismiss=dismiss
            map.cancelable = cancelable
            glitter.dialog = glitter.dialog.concat(map)
            $('#diaPlace').show()
            $('#diaPlace').append('<iframe name="' + map.id + '" src="' + this.baseUrl + ((glitter.type===appearType.Android) ? '':'/') + url + '?tag=' + map.id + '&pageIndex=' + glitter.pageIndex + '&type=Dialog"  id="' + map.id + '" style="display: none;z-index:'+glitter.pageIndex+';position: absolute;"></iframe>')
            var element = document.getElementById(map.id)
            if (!swipe) {
                element.style.backgroundColor = "rgba(0, 0, 0, 0.5)"
            }
            setTimeout("$('#" + map.id + "').show()", 200)
        }

        //關閉所有Dialog
        this.closeDiaLog = function () {
            var tempDialog=glitter.dialog
            glitter.dialog = []
            for (var i = 0; i < tempDialog.length; i++) {
                if(tempDialog[i].dismiss!==undefined){
                    tempDialog[i].dismiss()
                }
                $('#' + tempDialog[i].id).remove()
            }
            $('#diaPlace').html('')
            $('#diaPlace').hide()
        }


        //取得Dialog參數內容
        this.getDialog = function (tag) {
            for (var i = 0; i < this.dialog.length; i++) {
                if (this.dialog[i].id === tag) {
                    return this.dialog[i]
                }
            }
        }

        //關閉特定頁面Dialog
        this.closeDiaLogWithTag = function (tag) {
            console.log(tag)
            var tempArray = []
            for (var i = 0; i < glitter.dialog.length; i++) {
                var id = glitter.dialog[i].id
                if (id === ('Dialog-' + (tag)) || id === tag) {
                    if(glitter.dialog[i].dismiss!==undefined){
                        glitter.dialog[i].dismiss()
                    }
                    $('#' + id).remove()
                } else {
                    tempArray=tempArray.concat(glitter.dialog[i])
                }
            }
            glitter.dialog = tempArray
            if(glitter.dialog.length===0){
                $('#diaPlace').html('')
                $('#diaPlace').hide()
            }

        }

        //判斷此頁面為何種類型
        this.getHtmlType = function (tag) {
            for (var i = 0; i < glitter.dialog.length; i++) {
                if (glitter.dialog[i].id === tag) {
                    return this.htmlType.Dialog
                }
            }
            for (var a = 0; a < glitter.iframe.length; a++) {
                if (glitter.iframe[a].id === tag) {
                    return this.htmlType.Page
                }
            }
        }
//取得切換頁面的夾帶資料
        this.getObjectBundle = function (tag) {
            for (var i = 0; i < this.iframe.length; i++) {
                if (this.iframe[i].id === tag) {
                    return this.iframe[i].obj
                }
            }
            for (var i = 0; i < this.ifrag.length; i++) {
                if (this.ifrag[i].id === tag) {
                    return this.ifrag[i].obj
                }
            }
        }
        //顯示上滑Dialog
        this.openBottomSheet = function (url, tag, obj, height, topHandle) {
            glitter.pageIndex = glitter.pageIndex + 1
            var map = {}
            map.id = 'BottomSheet-' + tag
            map.obj = obj
            map.pageIndex = glitter.pageIndex
            $("body").append('<div id="' + map.id + '" >' +
                '<div class="c-bottom-sheet__scrim ">' +
                '<div class="c-bottom-sheet__sheet">' +
                '<div class="c-bottom-sheet__handle">' + '<span></span>' + '<span></span>' + '</div>' +
                '<div id="c-bottom-sheet__list" class="c-bottom-sheet__list" style="overflow-x: hidden;">' + '<iframe  name="' + map.id + '" src="' + this.baseUrl + ((glitter.type===appearType.Android) ? '':'/') + url + '?tag=' + map.id + '&pageIndex=' + glitter.pageIndex + '&type=BottomSheet" id="' + map.id + '" style="height: 100%"></iframe>' + '</div>' +
                '</div>' +
                '</div>')
            glitter.bottomSheet = glitter.bottomSheet.concat(map)
            if (topHandle) {
                $('.c-bottom-sheet__handle').show()
            } else {
                $('.c-bottom-sheet__handle').hide()
            }
            $('.c-bottom-sheet__list').css('height', height + 'px')
            try {
                glitter.sheetList = glitter.sheetList.concat(new BottomSheet(map.id, map.pageIndex))
                glitter.sheetList[glitter.sheetList.length - 1].activate()
            } catch (e) {
            }

        }
        //關閉上滑Dialog
        this.closeBottomSheet = function () {
            var tempAr = glitter.sheetList
            for (var a = 0; a < tempAr.length; a++) {
                tempAr[a].deactivate()
            }
            glitter.sheetList=[]
            glitter.bottomSheet=[]
        }
        //關閉上滑Dialog
        this.closeBottomSheetWithTag = function (tag) {
            var tempAr = glitter.sheetList
            var data=[]
            var bts=[]
            for (var a = 0; a < tempAr.length; a++) {
                if (tempAr[a].id === ('BottomSheet-' + tag)) {
                    tempAr[a].deactivate()
                    data=tempAr.filter(function (data){return (data.id=== ('BottomSheet-' + tag))})
                    bts=glitter.bottomSheet.filter(function (data){return (data.id=== ('BottomSheet-' + tag))})
                }
            }
            glitter.sheetList=data
            glitter.bottomSheet=bts
        }


        //Dialog是否正在顯示
        this.diaIsShowing = function (tag) {
            return window.Jz.diaIsShowing(tag);
        }


        //跳轉至系統特定功能
        this.intent = function (string) {
            window.Jz.intent(string);
        }
        //返回上一頁
        this.goBack = function () {
            if(glitter.dialog.length>0 && ($('#diaPlace').css('display') !== 'none')){
                var last=glitter.dialog[glitter.dialog.length-1]
                if(last.cancelable){
                    glitter.closeDiaLogWithTag(last.id)
                    return;
                }else{
                    return;
                }
            }
            var index = 0
            for (var i = 0; i < glitter.iframe.length; i++) {
                var tindex = glitter.iframe.length - i - 1
                if (tindex <= 0) {
                    this.goBackOnRootPage()
                    return;
                }
                if (glitter.iframe[tindex].goback) {
                    index = tindex
                    break
                }
            }
            if (index <= 0) {
                this.goBackOnRootPage()
                return
            }
            document.getElementById(glitter.iframe[glitter.iframe.length - 1].id).contentWindow.lifeCycle.onDestroy();
            var tag = glitter.iframe[index].id
            $('#' + tag).remove()
            console.log("remove" + tag)
            glitter.iframe.splice(index, 1);
            console.log("show" + glitter.iframe[index - 1].id)
            $('#' + glitter.iframe[index - 1].id).show()
            document.getElementById(glitter.iframe[index - 1].id).contentWindow.lifeCycle.onResume();
            glitter.changePageListener(glitter.iframe[index - 1].tag)
        }
        //添加script內容
        this.addScript = function (url, callback, callbackError) {
            var script = document.createElement('script');
            script.setAttribute('src', this.baseUrl + url);
            try {
                if (script.readyState) {  //IE
                    script.onreadystatechange = function () {
                        if (script.readyState === "loaded" || script.readyState === "complete") {
                            script.onreadystatechange = null;
                            callback();
                        }
                    };
                } else {
                    //其餘瀏覽器支援onload
                    script.onload = function () {
                        if (callback !== undefined) {
                            callback();
                        }
                    };
                }
                document.getElementsByTagName("head")[0].appendChild(script);
            } catch (e) {

                if (null !== callbackError) {
                    alert(e)
                    if (callbackError !== undefined) {
                        callbackError();
                    }
                }
            }
        }
        //添加css
        this.addCss = function (fileName) {
            var head = document.head;
            var link = document.createElement("link");
            link.type = "text/css";
            link.rel = "stylesheet";
            link.href = fileName;
            head.appendChild(link);
        }
        //返回鍵的監聽
        this.onBackPressed = function () {
            this.goBack()
        }
        //設定title Bar
        this.setTitleBar = function (url) {
            $("#titleBar").show()
            $("#titleBar").html('<iframe  src="' + this.baseUrl + ((glitter.type===appearType.Android) ? '':'/') + url + '" id="titleBarFrame" style="height:60px;width: 100%;"></iframe>')
            $("#framePlace").css("height", "calc(100% - 60px)")
        }
        //關閉應用
        this.closeApp = function () {
            console.log("closeApp")
            switch (glitter.type){
                case appearType.Android:
                    window.GL.closeApp()
                    break
                case appearType.Ios:
                    window.webkit.messageHandlers.closeApp.postMessage("");
                    break
                case appearType.Web:
                    window.close()
                    break
            }
        }
        //當已反回到RootPage時，在按下返回鍵時所需執行的動作
        this.goBackOnRootPage = function () {
        }
        //返回首頁
        this.goMenu = function () {
            var tempFrame = []
            tempFrame = tempFrame.concat(glitter.iframe[0])
            $('#' + glitter.iframe[0].id).show()
            for (var i = 1; i < glitter.iframe.length; i++) {
                $('#' + glitter.iframe[i].id).remove()
            }
            glitter.iframe = tempFrame
            glitter.changePageListener(glitter.iframe[0].id)
        }
        //頁面切換監聽
        this.changePageListener = function (tag) {

        }
        //顯示BARCODE掃描
        this.openQrScanner = function (callback) {
            switch (glitter.type){
                case appearType.Android:
                    glitter.qrScanBack = callback
                    window.GL.openQrScanner()
                    break
                case appearType.Web:
                    glitter.qrScanBack = callback
                    callback(JSON.parse(window.GL.getGPS()))
                    break
                case appearType.Ios:
                    glitter.qrScanBack = callback
                    window.webkit.messageHandlers.openQrScanner.postMessage('');

                    break
            }

        }
        //掃描回條
        this.qrScanBack = function (data) {}
        //權限請求
        this.requestPermission = function (array) {
         window.GL.requestPermission(array)
        }
        /*
        * 取得定位
        * [latitude,longitude,address]
        * */
        this.getGPS = function (callback) {
            switch (glitter.type){
                case appearType.Android:
                    callback(JSON.parse(window.GL.getGPS()))
                    break
                case appearType.Web:
                    callback(JSON.parse(window.GL.getGPS()))
                    break
                case appearType.Ios:
                    var id = glitter.callBackId += 1
                    glitter.callBackList.set(id,function (result) {
                       callback(JSON.parse(result))
                    })
                    var imap = {
                        callback:id
                    }
                    window.webkit.messageHandlers.getGPS.postMessage(JSON.stringify(imap));
                    break
            }
        }
        /*
        * 取得資料庫
        * */
        this.dataBase=new DataBase()
        /*
        * 序列化工句
        * */
        this.serialUtil=new Serialization()
        /*
        * 播放
        * */
        this.playSound=function (rout) {
            switch (glitter.type){
                case appearType.Android:
                    window.GL.playSound(rout)
                    break
                case appearType.Ios:
                    var imap = {
                        rout: rout
                    }
                    window.webkit.messageHandlers.playSound.postMessage(JSON.stringify(imap));
                    break
                case appearType.Web:
                    break
            }
        }
        /*
        * 定位套件GpsUtil
        * */
        this.gpsUtil={
            callback:new GpsCallBack(),
            manager:new GpsManager()
        }
        /*
       * 藍牙套件
       * */
        this.bleUtil={
            callback:new GlBle(),
            manager:new BleControl()
        }
        /*
        * Unicode轉換
        * */
        this.unicodeToString=function (data){
            try {
                return JSON.parse(`{\"unicode\":\"${data}\"}`).unicode
            }catch (e){
                console.log(e)
                return data
            }
        }
        this.showToast=function (string,sec){
            $('#toast').html(string)
            $('#toast').show()
            setTimeout(function (){
                $('#toast').hide()
            },(sec===undefined) ? 2000:sec)
        }
    }
}

class LifeCycle {
    constructor(props) {

        this.onResume = function () {

        }
        this.onPause = function () {
        }
        this.onDestroy = function () {
        }
    }
}

//Application生命週期
var lifeCycle = new LifeCycle()
//glitter變數
var glitter = new Glitter()
//glitter變數
var rootGlitter = glitter
//顯示類型
var appearType = new AppearType()
// 監聽鍵盤按鍵事件，並回傳所按的按鍵為何
window.addEventListener('keydown', function (e) {
    var arrat = glitter.dialog
    for (var i = 0; i < arrat.length; i++) {
        if (arrat[i].cancelable) {
            glitter.closeDiaLog(arrat[i].id)
        }
    }
});

function parseCookie() {
    var cookieObj = {};
    var cookieAry = document.cookie.split(';');
    var cookie;
    for (var i = 0, l = cookieAry.length; i < l; ++i) {
        cookie = jQuery.trim(cookieAry[i]);
        cookie = cookie.split('=');
        cookieObj[cookie[0]] = cookie[1];
    }
    return cookieObj;
}


function getCookieByName(name) {
    var value = parseCookie()[name];
    if (value) {
        value = decodeURIComponent(value);
    }

    return value;
}



function waitGetPro() {
    $.ajax({
        type: "POST",
        url: glitter.webUrl+"/getPro",
        timeout: 5000,
        success: function (data) {
            glitter.webPro = JSON.parse(data)
            onCreate()
        },
        error: function (data) {
            waitGetPro()
        }
    });
}
var onWait=false
function waitSetPro(key,data){
    if(onWait){
        function re(){waitSetPro(key,data)}
        setTimeout(re,100)
        return
    }
    onWait=true
    $.ajax({
        type: "POST",
        url: glitter.webUrl+"/setPro?key="+key,
        data: data ,
        timeout: 5000,
        success: function (data) {
            onWait=false
        },
        error: function (d) {
            console.log(d)
            onWait=false
            waitSetPro(key,data)
        }
    });
}

$(document).ready(function () {
    if(window.GL !== undefined){
        glitter.type=appearType.Android
    }else if(navigator.userAgent === 'iosGlitter'){
        glitter.type=appearType.Ios
    }

    switch (glitter.type){
        case appearType.Web:{
            glitter.baseUrl = "../"
            waitGetPro()
            break
        }
        case appearType.Android:{
            break
        }
        case  appearType.Ios:{
            glitter.baseUrl = "../"
            onCreate()
            break
        }
    }
});
if (navigator.userAgent.match(/iPhone/i) || navigator.userAgent.match(/iPad/i)) {
    /* iOS hides Safari address bar */
    window.addEventListener("load",function() {
        setTimeout(function() {
            window.scrollTo(0, 1);
        }, 1000);
    });
}
