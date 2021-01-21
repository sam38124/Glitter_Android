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
        var getUrl = window.location;
        this.baseUrl = getUrl .protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
        //Html類型
        this.htmlType=new HtmlType()
        //轉場動畫
        this.animator = new Animator()
        //設定類型
        this.type = new AppearType().Web
        //現在所有的Iframe
        this.iframe = []
        //現在所有的Dialog
        this.dialog = []
        //存紀錄
        this.setPro = function (tag, data) {
            switch (glitter.type) {
                case appearType.Web:{
                    document.cookie = '' + tag + '=' + data + '; max-age=' + (2592000 * 12 * 10) + '; path=/';
                    break
                }
                case appearType.Android:{
                     window.GL.setPro(tag,data)
                    break
                }
                case appearType.Ios:{
                    break
                }
            }

        }
        //取紀錄
        this.getPro = function (tag) {
            switch (glitter.type) {
                case appearType.Web:{
                    return getCookieByName(tag)
                }
                case appearType.Android:{
                    return window.GL.getPro(tag)
                }
                case appearType.Ios:{
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
        this.setHome = function (url, tag, json) {
            var map = {}
            map.id = tag
            map.json = json
            glitter.iframe = glitter.iframe.concat(map)
            $('#framePlace').empty();
            $('#framePlace').append('<iframe src="' + this.baseUrl+'/'+url + '?tag=' + tag + '" id="' + tag + '"></iframe>')
        }
       //吐司顯示
        this.toast = function (a) {
            window.Jz.toast(a);
        }
        //設定側滑選單
        this.setNavigation = function (src, json) {
            glitter.addScript('NaviGation.js')
            $("#Navigation").html('')
            $("#Navigation").html('<iframe src="' + this.baseUrl+'/'+src  + '?json=' + json + '" style="width: 100%;"></iframe>')
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
        this.changePage = function (link, tag, goBack, json) {
            for (var i = 0; i < glitter.iframe.length; i++) {
                console.log(glitter.iframe)
                $('#' + glitter.iframe[i].id).hide()
            }
            var map = {}
            map.id = tag
            map.json = json
            glitter.iframe = glitter.iframe.concat(map)
            $('#framePlace').append('<iframe src="' + this.baseUrl+'/'+link  + '?tag=' + tag + '" id="' + tag + '"></iframe>')
        }
        //Fragment切換
        this.changeFrag = function (root, link, tag) {
            root.innerHTML = ''
            if(link.indexOf('http') !== -1){
                root.innerHTML = '<iframe src="' + link  + '?tag=' + tag + '" id="' + tag + '" style="width: 100%;height: 100%;border-width: 0;"></iframe>'
            }else{
                root.innerHTML = '<iframe src="' + this.baseUrl+'/'+link  + '?tag=' + tag + '" id="' + tag + '" style="width: 100%;height: 100%;border-width: 0;"></iframe>'
            }
        }
        //
        //頁面切換與動畫
        this.changePageWithAnimation = function (link, tag, goBack, animator, json) {
            var map = {}
            map.id = tag
            map.json = json
            glitter.iframe = glitter.iframe.concat(map)
            $('#framePlace').append('<iframe src="' + this.baseUrl+'/'+link  + '?tag=' + tag + '" id="' + tag + '"></iframe>')
            if (animator === this.animator.translation) {
                var elem = document.getElementById(tag);
                elem.style.transform = 'translateX(' + $('#' + tag).width() + 'px)'
                console.log($('#' + tag).width())
                var pos = $('#' + tag).width();
                var id = setInterval(frame, 1);

                function frame() {
                    if (pos <= 0) {
                        for (var i = 0; i < glitter.iframe.length - 1; i++) {
                            $('#' + glitter.iframe[i].id).hide()
                        }
                        clearInterval(id);
                    } else {
                        pos -= 5;
                        elem.style.transform = 'translateX(' + pos + 'px)';
                    }
                }
            }
        }

        //顯示Dialog
        this.openDiaLog = function (url, tag, swipe, cancelable, json) {
            var map = {}
            map.id = 'Dialog-'+tag
            map.json = json
            map.cancelable = cancelable
            glitter.dialog = glitter.dialog.concat(map)
            $('#framePlace').append('<iframe src="' + this.baseUrl+'/'+url  + '?tag=' + map.id + '" id="' + map.id + '"></iframe>')
            var element = document.getElementById(map.id)
            if (!swipe) {
                element.style.backgroundColor = "rgba(0, 0, 0, 0.5)"
            }
        }

        //關閉所有Dialog
        this.closeDiaLog = function () {
            for (var i = 0; i < glitter.dialog.length; i++) {
                $('#'+glitter.dialog[i].id).remove()
            }
            glitter.dialog = []
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
                if (id === 'Dialog-'+(tag) || id=== tag) {
                    $('#'+id).remove()
                } else {
                    tempArray.concat(glitter.dialog[i])
                }
            }
            glitter.dialog = tempArray
        }

        //判斷此頁面為何種類型
        this.getHtmlType = function (tag) {
            for(var i = 0; i < glitter.dialog.length; i++){
                if(glitter.dialog[i].id===tag){return this.htmlType.Dialog}
            }
            for(var a = 0; a < glitter.iframe.length; a++){
                if(glitter.iframe[a].id===tag){return this.htmlType.Page}
            }
        }
//取得切換頁面的夾帶資料
        this.getJsonBundle = function (tag) {
            for(var i = 0; i < glitter.dialog.length; i++){}
            for (var i = 0; i < this.iframe.length; i++) {
                if (this.iframe[i].id === tag) {
                    return this.iframe[i].json
                }
            }
        }

        //設定全域變數
        this.setStaticVariable = function (name, value) {
            window.Jz.setStaticVariable(name, JSON.stringify(value));
        }

        //取得全域變數
        this.getStaticVariable = function (name) {
            return JSON.parse(window.Jz.getStaticVariable(name));
        }

        //設定側邊抽屜
        this.setDrawer = function (url, json) {
            window.Jz.setDrawer(url, json);
        }


        //顯示上滑Dialog
        this.openBottomSheet = function (url, tag, json) {
            var map = {}
            map.id = 'Dialog-'+tag
            map.json = json
            $('#country-selector').remove()
            $("body").append('<div id="country-selector" class="c-bottom-sheet c-bottom-sheet--list">'+
                '<div class="c-bottom-sheet__scrim"></div>'+
                '<div class="c-bottom-sheet__sheet">'+
                '<div class="c-bottom-sheet__handle">'+
                '<span></span>'+
                '<span></span>'+
                '</div>'+
                '<div class="c-bottom-sheet__list" style="overflow-x: hidden;">'+
                '<iframe src="' + this.baseUrl+'/'+url  + '?tag=' + map.id + '" id="' + map.id + '" style="height: calc(100% - 140px)"></iframe>'+
                '</div>'+
                '</div>'+
                '<div class="c-bottom-sheet__container">'+
                '</div>'+
                '</div>')
            try {
                window.bottomSheet = new BottomSheet("country-selector");
                window.bottomSheet.activate()
            }catch (e) {}
        }
        //關閉上滑Dialog
        this.closeBottomSheet = function () {
            window.bottomSheet.deactivate()
        }


        //Dialog是否正在顯示
        this.diaIsShowing = function (tag) {
            return window.Jz.diaIsShowing(tag);
        }

        //android權限請求
        this.permissionRequest = function (json) {
            window.Jz.permissionRequest(JSON.stringify(json));
        }

        //跳轉至系統特定功能
        this.intent = function (string) {
            window.Jz.intent(string);
        }

        //取得JSON
        this.getValue = function (value) {
            return JSON.parse(window.Jz.getJSON())[value]
        }

        //設定JSON DATA
        this.setValue = function (tag, value) {
            let a = JSON.parse(window.Jz.getJSON())
            a[tag] = value
            window.Jz.setJSON(JSON.stringify(a))
        }

        //Post請求
        this.postRequest = function (url, json, timeout) {
            return window.Jz.postRequest(url, JSON.stringify(json), timeout)
        }

        //Get請求
        this.getWebResource = function (url, timeout) {
            return window.Jz.getWebResource(url, timeout)
        }

        //返回上一頁
        this.goBack = function () {
            var index = glitter.iframe.length - 1
            var tag = glitter.iframe[index].id
            $('#' + tag).remove()
            glitter.iframe.splice(index, 1);
            $('#' + glitter.iframe[index - 1].id).show()
        }
        //添加script內容
        this.addScript = function (url) {
            var my_script = document.createElement('script');
            my_script.setAttribute('src',url);
            document.head.appendChild(my_script);
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
    }
}

//glitter變數
var glitter = new Glitter()
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


$(document).ready(function() {
    glitter.baseUrl=window.location.href.replace('/glitterBundle/MainActivity.html','')
    if(window.GL===undefined){
        onCreate()
    }
});
