"use strict";

//頁面切換動畫
class Animator {
    constructor() {
        this.translation = 0;
        this.rotation = 1;
        this.verticalTranslation = 2;
    }
}

//設定類型
class AppearType {
    constructor() {
        this.Web = 0;
        this.Android = 1;
        this.Ios = 2;
    }
}

class Glitter {
    constructor() {
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
            document.cookie = '' + tag + '=' + data + '; max-age=' + (2592000 * 12 * 10) + '; path=/';
        }
        //取紀錄
        this.getPro = function (tag) {
            return getCookieByName(tag)
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
            $('body').empty();
            $('body').append('<iframe src="' + url + '?tag=' + tag + '" id="' + tag + '"></iframe>')
        }

//吐司顯示
        this.toast = function (a) {
            window.Jz.toast(a);
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
            $('body').append('<iframe src="' + link + '?tag=' + tag + '" id="' + tag + '"></iframe>')
        }
        //Fragment切換
        this.changeFrag = function (root, link, tag) {
            root.innerHTML = ''
            root.innerHTML='<iframe src="' + link + '?tag=' + tag + '" id="' + tag + '" style="width: 100%;height: 100%;border-width: 0;"></iframe>'
        }

        //頁面切換與動畫
        this.changePageWithAnimation = function (link, tag, goBack, animator, json) {
            var map = {}
            map.id = tag
            map.json = json
            glitter.iframe = glitter.iframe.concat(map)
            $('body').append('<iframe src="' + link + '?tag=' + tag + '" id="' + tag + '"></iframe>')
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
            map.id = tag
            map.json = json
            map.cancelable = cancelable
            glitter.dialog = glitter.dialog.concat(map)
            $('body').append('<iframe src="' + url + '?tag=' + tag + '" id="Dialog-' + map.id + '"></iframe></div>')
            var element = document.getElementById('Dialog-' + map.id)
            if (!swipe) {
                element.style.backgroundColor = "rgba(0, 0, 0, 0.5)"
            }
        }

        //關閉所有Dialog
        this.closeDiaLog = function () {
            for (var i = 0; i < glitter.dialog.length; i++) {
                $('#Dialog-' + glitter.dialog[i].id).remove()
            }
            glitter.dialog = {}
        }

        //關閉特定頁面Dialog
        this.closeDiaLogWithTag = function (tag) {
            console.log(tag)
            var tempArray = []
            for (var i = 0; i < glitter.dialog.length; i++) {
                var id = glitter.dialog[i].id
                if (id === (tag)) {
                    $('#Dialog-' + glitter.dialog[i].id).remove()
                } else {
                    tempArray.concat(glitter.dialog[i])
                }
            }
            glitter.dialog = tempArray
        }

//取得切換頁面的夾帶資料
        this.getJsonBundle = function (tag) {
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
        this.showBottomSheetDialog = function (url, tag, swipe, cancelable, json) {
            window.Jz.showBottomSheetDialog(url, tag, swipe, cancelable, JSON.stringify(json));
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