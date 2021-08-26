/*Read only*/

/*Please dont change*/
"use strict"; //頁面切換動畫

class Animator {
    constructor() {
        this.translation = "translation";
        this.rotation = 1;
        this.verticalTranslation = 2;
    }

} //應用類型


class AppearType {
    constructor() {
        this.Web = 0;
        this.Android = 1;
        this.Ios = 2;
    }

} //Html類型


class HtmlType {
    constructor() {
        this.Page = 0;
        this.Dialog = 1;
        this.Frag = 2;
    }

}

class Glitter {
    constructor() {
        this.webUrl = '';
        this.callBackId = 0;
        this.callBackList = new Map();
        this.deviceTypeEnum = new AppearType();
        this.location = window.location;
        var getUrl = window.location; //公用變數

        this.publicBeans = {};
        this.share = {};
        this.baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1]; //Html類型

        this.htmlType = new HtmlType(); //轉場動畫

        this.animator = new Animator(); //預設轉場動畫

        this.defalutAnimator = undefined; //當前顯示裝置類型

        this.deviceType = new AppearType().Web; //現在所有的Iframe

        this.iframe = []; //現在所有的Frag

        this.ifrag = []; //現在所有的Dialog

        this.dialog = []; //現在所有的Sheet

        this.sheetList = []; //Web的紀錄

        this.webPro = {}; //PageIndex

        this.pageIndex = 0; //PageIndex

        this.bottomSheet = []; //View

        this.getBoundingClientRect = {}; //Identification

        this.uuid = getCookieByName("uuid");

        if (this.uuid === undefined) {
            this.uuid = _uuid();
            let oneYear = 2592000 * 12;
            document.cookie = `uuid=${this.uuid}; max-age=${oneYear}; path=/`;
        } //存紀錄


        this.setPro = function (tag, data, callBack) {
            glitter.runJsInterFace("setPro", {
                uuid: glitter.uuid,
                name: tag,
                data: data
            }, callBack);
        }; //取紀錄


        this.getPro = function (tag, callBack) {
            glitter.runJsInterFace("getPro", {
                uuid: glitter.uuid,
                name: tag
            }, callBack);
        }; //設定首頁


        this.setHome = function (url, tag, obj) {
            window.history.pushState({}, document.title, "?page=" + tag);
            this.showLoadingView();
            glitter.pageIndex = glitter.pageIndex + 1;
            var map = {};
            map.id = tag;
            map.obj = obj;
            map.goback = true;
            map.pageIndex = glitter.pageIndex;
            glitter.iframe = [];
            glitter.iframe = glitter.iframe.concat(map);
            $('#framePlace').empty();
            $('#framePlace').append('<iframe  style="display: none;" name="' + map.id + '" src="' + url + '?tag=' + tag + '&pageIndex=' + map.pageIndex + '&type=Page"  id="' + map.pageIndex + '"></iframe>');

            glitter.changeWait = function () {
                $('#' + map.pageIndex).show();
            };

            glitter.changePageListener(tag);
        }; //設定側滑選單


        this.naviGationObj = {};

        this.setNavigation = function (src, obj) {
            if (window.drawer === undefined) {
                glitter.addScript('glitterBundle/NaviGation.js', function () {
                });
            }

            this.naviGationObj = obj;
            $("#Navigation").html('');
            $("#Navigation").html('<iframe src="' + src + '?naviGation=true"  style="width: 100%;"></iframe>');
        }; //顯示加載畫面


        this.showLoadingView = function () {
            $('#loadingView').show();
        }; //設定加載畫面


        this.setLoadingView = function (link) {
            $('#loadingView').hide();
            $('#loadingView').append('<iframe  src="' + link + '" style="width: 100%;height: 100%;background-color: rgba(0, 0, 0, 0.5);"></iframe>');
        }; //關閉加載畫面


        this.hideLoadingView = function () {
            $('#loadingView').hide();
        }; //打開側滑選單


        this.openNavigation = function () {
            if (window.drawer !== undefined) {
                window.drawer.open();
            } else {
                var timer = setInterval(function () {
                    if (window.drawer !== undefined) {
                        window.drawer.open();
                        clearInterval(timer);
                    }
                }, 100);
            }
        }; //關閉側滑選單


        this.closeNavigation = function () {
            window.drawer.close();
        }; //開關側滑選單


        this.toggleNavigation = function () {
            window.drawer.toggle();
        }; //按鈕監聽


        this.keyEventListener = function (obj) {
        }; //頁面切換


        this.changePage = function (link, tag, goBack, obj) {
            window.history.pushState({}, document.title, "?page=" + tag);
            this.showLoadingView();
            glitter.pageIndex = glitter.pageIndex + 1;
            var map = {};
            map.id = tag;
            map.obj = obj;
            map.goback = goBack;
            map.pageIndex = glitter.pageIndex;
            glitter.iframe = glitter.iframe.concat(map);
            $('#framePlace').append(`<iframe class="${this.defalutAnimator}" name="${tag}" src="${link}?tag=${tag}&pageIndex=${glitter.pageIndex}&type=Page"  id="${map.pageIndex}" style="background-color: white;width: 100%;height: 100%;display: none;"></iframe>`);

            glitter.changeWait = function () {
                try {
                    $('#' + map.pageIndex).show();
                    document.getElementById(glitter.iframe[glitter.iframe.length - 1].pageIndex).contentWindow.lifeCycle.onPause();
                    glitter.changePageListener(tag);
                } catch (e) {
                }
            };
        }; //頁面切換防止白頻


        this.changeWait = function () {
        }; //Fragment切換


        this.changeFrag = function (root, link, tag, obj) {
            glitter.pageIndex = glitter.pageIndex + 1;
            root.innerHTML = '';
            var ife = [];

            for (var a = 0; a < glitter.ifrag.length; a++) {
                if (glitter.ifrag[a].id !== 'Frag-' + tag) {
                    ife = ife.concat(glitter.ifrag[a]);
                }
            }

            glitter.ifrag = ife;
            var map = {};
            map.id = 'Frag-' + tag;
            map.obj = obj;
            map.goback = false;
            map.pageIndex = glitter.pageIndex;
            glitter.ifrag = glitter.ifrag.concat(map);

            if (link.indexOf("http://") !== -1 || link.indexOf("https://") !== -1) {
                root.innerHTML = '<iframe name="' + map.id + '" src="' + link + '"  id="' + map.id + '" style="width: 100%;height: 100%;border-width: 0;"></iframe>';
            } else {
                root.innerHTML = '<iframe name="' + map.id + '" src="' + link + '?tag=' + map.id + '&pageIndex=' + glitter.pageIndex + '&type=Frag"  id="' + map.id + '" style="width: 100%;height: 100%;border-width: 0;"></iframe>';
            }
        }; //移除堆棧
        this.removePage = function (tag) {
            if (typeof tag === 'string') {
                glitter.iframe = glitter.iframe.filter(function (data) {
                    if (data.id === tag) {
                        $("#" + data.pageIndex).remove();
                    }

                    return data.id !== tag;
                });
            } else {
                glitter.iframe = glitter.iframe.filter(function (data) {
                    if (tag.indexOf(data.id) !== -1) {
                        $("#" + data.pageIndex).remove();
                    }
                    return tag.indexOf(data.id) === -1;
                });
            }
        };//頁面切換與動畫
        this.changePageWithAnimation = function (link, tag, goBack, type, obj) {
            window.history.replaceState({}, document.title, "?page=" + tag);
            this.showLoadingView();
            glitter.pageIndex = glitter.pageIndex + 1;
            var map = {};
            map.id = tag;
            map.obj = obj;
            map.goback = goBack;
            map.pageIndex = glitter.pageIndex;
            glitter.iframe = glitter.iframe.concat(map);
            var animation = "";

            switch (type) {
                case this.animator.translation:
                    animation = "translation";
                    break;

                case this.animator.rotation:
                    break;

                case this.animator.verticalTranslation:
                    break;
            }

            $('#framePlace').append(`<iframe class="${animation}" name="${tag}" src="${link}?tag=${tag}&pageIndex=${glitter.pageIndex}&type=Page"  id="${map.pageIndex}" style="background-color: white;width: 100%;height: 100%;display: none;"></iframe>`);

            glitter.changeWait = function () {
                $('#' + map.pageIndex).show();
                setTimeout(function () {
                    for (var i = 0; i < glitter.iframe.length - 1; i++) {
                        //console.log(glitter.iframe)
                        $('#' + glitter.iframe[i].id).hide();
                    }
                }, 500);
                document.getElementById(glitter.iframe[glitter.iframe.length - 1].pageIndex).contentWindow.lifeCycle.onPause();
                glitter.changePageListener(tag);
            };
        }; //顯示Dialog


        this.openDiaLog = function (url, tag, swipe, cancelable, obj, dismiss) {
            if (glitter.dialog.filter(function (item) {
                return item.id === 'Dialog-' + tag;
            }).length !== 0) {
                return;
            }

            glitter.pageIndex += 1;
            var map = {};
            map.pageIndex = glitter.pageIndex;
            map.id = 'Dialog-' + tag;
            map.obj = obj;
            map.dismiss = dismiss;
            map.cancelable = cancelable;
            glitter.dialog = glitter.dialog.concat(map);
            $('#diaPlace').show();
            $('#diaPlace').append('<iframe name="' + map.id + '" src="' + url + '?tag=' + map.id + '&pageIndex=' + glitter.pageIndex + '&type=Dialog"  id="' + map.id + '" style="display: none;z-index:' + glitter.pageIndex + ';position: absolute;"></iframe>');
            var element = document.getElementById(map.id);

            if (!swipe) {
                element.style.backgroundColor = "rgba(0, 0, 0, 0.5)";
            }

            setTimeout("$('#" + map.id + "').show()", 200);
        }; //關閉所有Dialog


        this.closeDiaLog = function () {
            var tempDialog = glitter.dialog;
            glitter.dialog = [];

            for (var i = 0; i < tempDialog.length; i++) {
                if (tempDialog[i].dismiss !== undefined) {
                    tempDialog[i].dismiss();
                }

                $('#' + tempDialog[i].id).remove();
            }

            $('#diaPlace').html('');
            $('#diaPlace').hide();
        }; //取得Dialog參數內容


        this.getDialog = function (tag) {
            for (var i = 0; i < this.dialog.length; i++) {
                if (this.dialog[i].id === tag) {
                    return this.dialog[i];
                }
            }
        }; //關閉特定頁面Dialog


        this.closeDiaLogWithTag = function (tag) {
            //console.log(tag)
            var tempArray = [];

            for (var i = 0; i < glitter.dialog.length; i++) {
                var id = glitter.dialog[i].id;

                if (id === 'Dialog-' + tag || id === tag) {
                    if (glitter.dialog[i].dismiss !== undefined) {
                        glitter.dialog[i].dismiss();
                    }

                    $('#' + id).remove();
                } else {
                    tempArray = tempArray.concat(glitter.dialog[i]);
                }
            }

            glitter.dialog = tempArray;

            if (glitter.dialog.length === 0) {
                $('#diaPlace').html('');
                $('#diaPlace').hide();
            }
        }; //判斷此頁面為何種類型


        this.getHtmlType = function (tag) {
            for (var i = 0; i < glitter.dialog.length; i++) {
                if (glitter.dialog[i].id === tag) {
                    return this.htmlType.Dialog;
                }
            }

            for (var a = 0; a < glitter.iframe.length; a++) {
                if (glitter.iframe[a].id === tag) {
                    return this.htmlType.Page;
                }
            }
        }; //取得切換頁面的夾帶資料


        this.getObjectBundle = function (tag) {
            for (var i = 0; i < this.iframe.length; i++) {
                if (this.iframe[i].id === tag) {
                    return this.iframe[i].obj;
                }
            }

            for (var i = 0; i < this.ifrag.length; i++) {
                if (this.ifrag[i].id === tag) {
                    return this.ifrag[i].obj;
                }
            }
        }; //顯示上滑Dialog


        this.openBottomSheet = function (url, tag, obj, height, topHandle, dragAble) {
            if (dragAble === undefined) {
                dragAble = true;
            }

            glitter.pageIndex = glitter.pageIndex + 1;
            var map = {};
            map.id = 'BottomSheet-' + tag;
            map.obj = obj;
            map.pageIndex = glitter.pageIndex;
            $("body").append(`<div id="${map.id}" style="z-index:${map.pageIndex};width: 100%;height: 100%;"><div class="c-bottom-sheet__scrim"><div class="c-bottom-sheet__sheet"><div class="c-bottom-sheet__handle"><span></span><span></span></div><div id="c-bottom-sheet__list" class="c-bottom-sheet__list" style="overflow-x: hidden;"><iframe  name="${map.id}" src="${url}?tag=${map.id}&pageIndex=${glitter.pageIndex}&type=BottomSheet" id="${map.id}" style="height: 100%"></iframe></div></div></div>`);
            glitter.bottomSheet = glitter.bottomSheet.concat(map);

            if (topHandle) {
                $('#' + map.id + ' .c-bottom-sheet__handle').show();
            } else {
                $('#' + map.id + ' .c-bottom-sheet__handle').hide();
            }

            $('#' + map.id + ' .c-bottom-sheet__sheet').css('height', height);
            $('#' + map.id + ' .c-bottom-sheet__list').css('height', height);

            try {
                glitter.sheetList = glitter.sheetList.concat(new BottomSheet(map.id, map.pageIndex, dragAble));
                glitter.sheetList[glitter.sheetList.length - 1].activate();
            } catch (e) {
            }
        }; //Sheet滾動視圖處理程序


        this.sheetHandler = {
            addScrollviewHandler: function (e) {
                var sheetLenstiner = glitter.sheetList[glitter.sheetList.length - 1];
                e.scroll(function () {
                    var scrollTop = e.scrollTop();

                    if (sheetLenstiner !== undefined) {
                        sheetLenstiner.dragAble = scrollTop === 0;

                        if (scrollTop !== 0) {
                            sheetLenstiner.sheet.style.setProperty("transform", `translateY(0)`);
                            sheetLenstiner.sheet.style.setProperty("transition", `unset`);
                        }
                    }
                });
            },
            addDragListener: function (e) {
                var sheetLenstiner = glitter.sheetList[glitter.sheetList.length - 1];
                new TouchDragListener({
                    el: e,
                    touchStartCallback: function () {
                        sheetLenstiner.dragAble = true;
                    },
                    touchEndCallback: function () {
                        sheetLenstiner.dragAble = true;
                    },
                    touchMoveCallback: function () {
                        sheetLenstiner.dragAble = true;
                    }
                });
            }
        }; //關閉上滑Dialog

        this.closeBottomSheet = function () {
            var tempAr = glitter.sheetList;

            for (var a = 0; a < tempAr.length; a++) {
                tempAr[a].deactivate();
            }

            glitter.sheetList = [];
            glitter.bottomSheet = [];
        }; //關閉上滑Dialog


        this.closeBottomSheetWithTag = function (tag) {
            var tempAr = glitter.sheetList;
            var data = [];
            var bts = [];

            for (var a = 0; a < tempAr.length; a++) {
                if (tempAr[a].id === 'BottomSheet-' + tag) {
                    tempAr[a].deactivate();
                    data = tempAr.filter(function (data) {
                        return data.id !== 'BottomSheet-' + tag;
                    });
                    bts = glitter.bottomSheet.filter(function (data) {
                        return data.id !== 'BottomSheet-' + tag;
                    });
                }
            }

            glitter.sheetList = data;
            glitter.bottomSheet = bts;
        }; //Dialog是否正在顯示


        this.diaIsShowing = function (tag) {
            return window.Jz.diaIsShowing(tag);
        }; //跳轉至系統特定功能


        this.intent = function (string) {
            window.Jz.intent(string);
        }; //返回上一頁


        this.goBack = function () {
            if (glitter.dialog.length > 0 && $('#diaPlace').css('display') !== 'none') {
                var last = glitter.dialog[glitter.dialog.length - 1];

                if (last.cancelable) {
                    glitter.closeDiaLogWithTag(last.id);
                    return;
                } else {
                    return;
                }
            }

            try {
                document.getElementById(glitter.iframe[glitter.iframe.length - 1].pageIndex).contentWindow.lifeCycle.onDestroy();
            } catch (e) {
            }

            if (glitter.iframe.length === 1) {
                return;
            }

            $('#' + glitter.iframe[glitter.iframe.length - 1].pageIndex).remove();
            glitter.iframe = glitter.iframe.filter(function (data) {
                return data.goback && data.pageIndex !== glitter.iframe[glitter.iframe.length - 1].pageIndex;
            });
            var index = glitter.iframe.length - 1;
            $('#' + glitter.iframe[index].pageIndex).show();

            try {
                document.getElementById(glitter.iframe[index].pageIndex).contentWindow.lifeCycle.onResume();
            } catch (e) {
            }

            glitter.changePageListener(glitter.iframe[index].id);
        }; //添加script內容


        this.addScript = function (url, callback, callbackError) {
            var script = document.createElement('script');
            script.setAttribute('src', url);

            try {
                if (script.readyState) {
                    //IE
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
                    alert(e);

                    if (callbackError !== undefined) {
                        callbackError();
                    }
                }
            }
        }; //添加多項Script


        this.addMtScript = function (urlArray, success, error) {
            var index = 0;

            function addScript() {
                if (index === urlArray.length) {
                    success();
                    return;
                }

                let script = document.createElement('script');
                script.setAttribute('src', urlArray[index]);

                try {
                    if (script.readyState) {
                        //IE
                        script.onreadystatechange = function () {
                            if (script.readyState === "loaded" || script.readyState === "complete") {
                                script.onreadystatechange = null;
                                index++;
                                addScript();
                            }
                        };
                    } else {
                        //其餘瀏覽器支援onload
                        script.onload = function () {
                            if (success !== undefined) {
                                index++;
                                addScript();
                            }
                        };
                    }

                    document.getElementsByTagName("head")[0].appendChild(script);
                } catch (e) {
                    error(`Add ${urlArray[index]} ERROR!!`);
                }
            }

            addScript();
        }; //添加css


        this.addCss = function (fileName) {
            var head = document.head;
            var link = document.createElement("link");
            link.type = "text/css";
            link.rel = "stylesheet";
            link.href = fileName;
            head.appendChild(link);
        }; //返回鍵的監聽


        this.onBackPressed = function () {
            this.goBack();
        }; //設定title Bar


        this.setTitleBar = function (url) {
            $("#titleBar").show();
            $("#titleBar").html('<iframe  src="' + url + '" id="titleBarFrame" style="height:60px;width: 100%;"></iframe>');
            $("#framePlace").css("height", "calc(100% - 60px)");
        }; //關閉應用


        this.closeApp = function () {
            //console.log("closeApp")
            switch (glitter.deviceType) {
                case appearType.Android:
                    window.GL.closeApp();
                    break;

                case appearType.Ios:
                    window.webkit.messageHandlers.closeApp.postMessage("");
                    break;

                case appearType.Web:
                    window.close();
                    break;
            }
        }; //當已反回到RootPage時，在按下返回鍵時所需執行的動作


        this.goBackOnRootPage = function () {
        }; //返回首頁


        this.goMenu = function () {
            var tempFrame = [];
            tempFrame = tempFrame.concat(glitter.iframe[0]);
            $('#' + glitter.iframe[0].pageIndex).show();

            for (var i = 1; i < glitter.iframe.length; i++) {
                $('#' + glitter.iframe[i].pageIndex).remove();
            }

            glitter.iframe = tempFrame;
            glitter.changePageListener(glitter.iframe[0].id);
        }; //頁面切換監聽


        this.changePageListener = function (tag) {
        }; //顯示BARCODE掃描


        this.openQrScanner = function (callback) {
            switch (glitter.deviceType) {
                case appearType.Android:
                    glitter.qrScanBack = callback;
                    window.GL.openQrScanner();
                    break;

                case appearType.Web:
                    glitter.qrScanBack = callback;
                    callback(JSON.parse(window.GL.getGPS()));
                    break;

                case appearType.Ios:
                    glitter.qrScanBack = callback;
                    window.webkit.messageHandlers.openQrScanner.postMessage('');
                    break;
            }
        }; //掃描回條


        this.qrScanBack = function (data) {
        }; //權限請求


        this.requestPermission = function (array, callback) {
            var id = glitter.callBackId += 1;
            glitter.callBackList.set(id, callback);
            window.GL.requestPermission(array, id);
        };
        /*
        * 取得定位
        * [latitude,longitude,address]
        * */


        this.getGPS = function (callback) {
            switch (glitter.deviceType) {
                case appearType.Android:
                    callback(JSON.parse(window.GL.getGPS()));
                    break;

                case appearType.Web:
                    callback(JSON.parse(window.GL.getGPS()));
                    break;

                case appearType.Ios:
                    var id = glitter.callBackId += 1;
                    glitter.callBackList.set(id, function (result) {
                        callback(JSON.parse(result));
                    });
                    var imap = {
                        callback: id
                    };
                    window.webkit.messageHandlers.getGPS.postMessage(JSON.stringify(imap));
                    break;
            }
        };
        /*
        * 取得資料庫
        * */


        this.dataBase = new DataBase();
        /*
        * 序列化工句
        * */

        this.serialUtil = new Serialization();
        /*
        * 播放
        * */

        this.playSound = function (rout) {
            switch (glitter.deviceType) {
                case appearType.Android:
                    window.GL.playSound(rout);
                    break;

                case appearType.Ios:
                    var imap = {
                        rout: rout
                    };
                    window.webkit.messageHandlers.playSound.postMessage(JSON.stringify(imap));
                    break;

                case appearType.Web:
                    break;
            }
        };
        /*
        * 定位套件GpsUtil
        * */


        this.gpsUtil = {
            callback: new GpsCallBack(),
            manager: new GpsManager()
        };
        /*
        * Unicode轉換
        * */

        this.unicodeToString = function (data) {
            try {
                return eval("'" + data + "'");
            } catch (e) {
                return data;
            }
        };

        this.stringToUnicode = function (data) {
            return data.split('').map(function (value, index, array) {
                var code = value.charCodeAt(0);
                var code16 = code.toString(16).toLowerCase();
                if (code < 0xf) {
                    return "\\\\u" + "000" + code16;
                } else if (code < 0xff) {
                    return "\\\\u" + "00" + code16;
                } else if (code < 0xfff) {
                    return "\\\\u" + "0" + code16;
                } else {
                    return "\\\\u" + code16;
                }
            }).join('');
        };

        this.showToast = function (string, sec) {
            $('#toast').html(string);
            $('#toast').show();
            setTimeout(function () {
                $('#toast').hide();
            }, sec === undefined ? 2000 : sec);
        };
        /**
         * 取得夾帶參數
         * **/


        this.getUrlParameter = function (sParam) {
            var sPageURL = window.location.search.substring(1),
                sURLVariables = sPageURL.split('&'),
                sParameterName,
                i;

            for (i = 0; i < sURLVariables.length; i++) {
                sParameterName = sURLVariables[i].split('=');

                if (sParameterName[0] === sParam) {
                    return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
                }
            }
        };
        /**
         * 取得Browser 類型
         * {Android,IOS,Desktop}
         * **/


        this.getBrowserDeviceType = function () {
            var a = navigator.userAgent;
            var isAndroid = a.indexOf('Android') > -1 || a.indexOf('Adr') > -1; //android终端

            var isiOS = !!a.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端

            if (isAndroid) {
                return "Android";
            }

            if (isiOS) {
                return "IOS";
            }

            return "Desktop";
        };
        /**
         * 開啟新視窗
         * Android and Ios呼叫 ｗｅｂｖｉｅｗ
         * */


        this.openNewTab = function (link) {
            switch (glitter.deviceType) {
                case glitter.deviceTypeEnum.Web:
                    window.open(link);
                    break;

                case glitter.deviceTypeEnum.Android:
                    window.GL.openNewTab(link);
                    break;

                case glitter.deviceTypeEnum.Ios:
                    window.webkit.messageHandlers.openNewTab.postMessage(link);
                    break;
            }
        };
        /**
         * 添加觀察者模式
         * */


        this.addObserver = function (map, obj, callback) {
            if (map.GlitterJsonStringConverSion === undefined) {
                map.GlitterJsonStringConverSion = JSON.parse(JSON.stringify(map));
            }

            if (map.ObServerCallBack === undefined) {
                map.ObServerCallBack = {};
            }

            if (map.ObServerCallBack[obj] === undefined) {
                map.ObServerCallBack[obj] = [];
            }

            map.ObServerCallBack[obj] = map.ObServerCallBack[obj].concat(callback);
            var keys = Object.keys(map);

            for (var a = 0; a < keys.length; a++) {
                let keyVa = keys[a];

                if (keyVa === obj) {
                    Object.defineProperty(map, keyVa, {
                        get: function () {
                            return map.GlitterJsonStringConverSion[keyVa];
                        },

                        set(v) {
                            map.GlitterJsonStringConverSion[keyVa] = v;
                            var workSuccess = [];
                            this.ObServerCallBack[keyVa].map(function (it) {
                                try {
                                    it(v);
                                    workSuccess = workSuccess.concat(it);
                                } catch (e) {
                                }
                            });
                            this.ObServerCallBack[keyVa] = workSuccess;
                        }

                    });
                    callback(map[keyVa]);
                }
            }
        };
        /**
         * 添加觀察者模式
         * 當內容改變及互叫Observer
         * */


        this.addObjObserver = function (map, callback) {
            if (map.GlitterJsonStringConverSion === undefined) {
                map.GlitterJsonStringConverSion = JSON.parse(JSON.stringify(map));
            }

            if (map.ObServerCallBack === undefined) {
                map.ObServerCallBack = [];
            }

            map.ObServerCallBack = map.ObServerCallBack.concat(callback);
            var keys = Object.keys(map);

            for (var a = 0; a < keys.length; a++) {
                let keyVa = keys[a];

                if (keyVa !== 'GlitterJsonStringConverSion' && keyVa !== 'ObServerCallBack') {
                    Object.defineProperty(map, keyVa, {
                        get: function () {
                            return map.GlitterJsonStringConverSion[keyVa];
                        },

                        set(v) {
                            map.GlitterJsonStringConverSion[keyVa] = v;
                            var workSuccess = [];
                            this.ObServerCallBack.map(function (it) {
                                try {
                                    it(map["GlitterJsonStringConverSion"]);
                                    workSuccess = workSuccess.concat(it);
                                } catch (e) {
                                }
                            });
                            this.ObServerCallBack = workSuccess;
                        }

                    });
                }
            }
        };
        /**
         移除html標籤
         * */


        this.removeTag = function (str) {
            return str.replace(/<[^>]+>/g, ""); //去掉所有的html標記
        };
        /**
         判斷滾動位置，和滑動到哪裡
         * */


        this.addScrollListener = function (e, obj) {
            e.scroll(function () {
                var map = {
                    scrollTop: $(this)[0].scrollTop,
                    scrollHeight: $(this)[0].scrollHeight,
                    windowHeight: $(this).height()
                };

                if (map.scrollTop + map.windowHeight >= map.scrollHeight) {
                    if (obj.scrollBtn !== undefined) {
                        obj.scrollBtn();
                    }
                }

                if (map.scrollTop === 0) {
                    if (obj.scrollTp !== undefined) {
                        obj.scrollTp();
                    }
                }

                if (obj.position !== undefined) {
                    obj.position(map.scrollTop);
                }
            });
        };
        /**
         判斷是否到底部
         * */


        this.isScrollBtn = function (e) {
            var map = {
                scrollTop: e[0].scrollTop,
                scrollHeight: e[0].scrollHeight,
                windowHeight: e.height()
            };
            return map.scrollTop + map.windowHeight >= map.scrollHeight - 1;
        };
        /**
         判斷是否到頂部
         * */


        this.isScrollTp = function (e) {
            var map = {
                scrollTop: e[0].scrollTop,
                scrollHeight: e[0].scrollHeight,
                windowHeight: e.height()
            };
            return map.scrollTop === 0;
        };
        /**
         * 滑動到底部
         * */


        this.scrollToBtn = function (e) {
            var map = {
                scrollTop: e[0].scrollTop,
                scrollHeight: e[0].scrollHeight,
                windowHeight: e.height()
            };
            e.scrollTop(map.scrollHeight - map.windowHeight);
        };
        /**
         * Add window windowHeightChangeListener
         * */


        this.addWindowHeightChangeListener = function (callback) {
            windowHeightChangeListener = windowHeightChangeListener.concat(callback);
        };
        /**
         * Now windowHeight
         * */


        this.nowWindowHeight = function () {
            return window.innerHeight;
        };
        /**
         * Text ChangeListener
         * */


        this.addTextChangeListener = function (e, callback) {
            e.bind('input porpertychange', function () {
                callback();
            });
        };

        this.toAssetRoot = function (e) {
            window.GL.toAssetRoot(e);
        }; //檔案下載


        this.downloadFile = function (serverRout, fileName, timeOut, callBack) {
            let id = glitter.callBackId += 1;
            glitter.callBackList.set(id, function (result) {
                callBack(result);
            });

            switch (glitter.deviceType) {
                case appearType.Web: {
                    return;
                }

                case appearType.Android: {
                    window.GL.downloadFile(serverRout, fileName, id, timeOut);
                    return;
                }

                case appearType.Ios: {
                    var map = {
                        fileName: fileName,
                        rout: serverRout,
                        callback: id,
                        timeOut: timeOut
                    };
                    window.webkit.messageHandlers.downloadFile.postMessage(JSON.stringify(map));
                    break;
                }
            }
        }; //重新加載Application


        this.reloadPage = function () {
            switch (glitter.deviceType) {
                case glitter.deviceTypeEnum.Ios:
                    var id = glitter.callBackId += 1;
                    window.webkit.messageHandlers.reloadPage.postMessage("");
                    break;

                case glitter.deviceTypeEnum.Android:
                    window.GL.reloadPage();
                    break;

                case glitter.deviceTypeEnum.Web:
                    location.reload();
                    break;
            }
        }; //取得檔案


        this.getFile = function (fileName, type, callBack) {
            let id = glitter.callBackId += 1;
            glitter.callBackList.set(id, callBack);

            switch (glitter.deviceType) {
                case appearType.Web: {
                    return;
                }

                case appearType.Android: {
                    window.GL.getFile(fileName, type, id);
                    return;
                }

                case appearType.Ios: {
                    var map = {
                        fileName: fileName,
                        type: type,
                        callback: id
                    };
                    window.webkit.messageHandlers.getFile.postMessage(JSON.stringify(map));
                    break;
                }
            }
        }; //判斷檔案是否存在


        this.checkFileExists = function (fileName, callBack) {
            let id = glitter.callBackId += 1;
            glitter.callBackList.set(id, callBack);

            switch (glitter.deviceType) {
                case appearType.Web: {
                    return;
                }

                case appearType.Android: {
                    window.GL.checkFileExists(fileName, id);
                    return;
                }

                case appearType.Ios: {
                    var map = {
                        fileName: fileName,
                        callback: id
                    };
                    window.webkit.messageHandlers.checkFileExists.postMessage(JSON.stringify(map));
                    break;
                }
            }
        }; //Map資料Copy有則取代


        this.copyObj = function (copyTo, original) {
            var key = Object.keys(copyTo);

            for (var a = 0; a < key.length; a++) {
                if (original[key[a]] !== undefined) {
                    copyTo[key[a]] = original[key[a]];
                }
            }
        };
        /**
         * Run your JavaScript interface for Android/IOS/WEB
         * CallBack will response OBJ
         * */


        this.runJsInterFace = function (functionName, data, callBack) {
            let id = glitter.callBackId += 1;
            glitter.callBackList.set(id, callBack);
            let map = {
                functionName: functionName,
                callBackId: id,
                data: data
            };

            switch (glitter.deviceType) {
                case appearType.Web: {
                    $.ajax({
                        type: "POST",
                        url: "/RunJsInterFace",
                        data: JSON.stringify(map),
                        timeout: 60 * 1000,
                        success: function (data) {
                            callBack(JSON.parse(data));
                        },
                        error: function (data) {
                            callBack(undefined);
                        }
                    });
                    return;
                }

                case appearType.Android: {
                    window.GL.runJsInterFace(JSON.stringify(map));
                    return;
                }

                case appearType.Ios: {
                    window.webkit.messageHandlers.addJsInterFace.postMessage(JSON.stringify(map));
                    break;
                }
            }
        };
        /**
         * Run your restful post function
         * CallBack will response OBJ
         * */


        this.postRequest = function (routName, functionName, data, callBack, timeout) {
            let id = glitter.callBackId += 1;
            glitter.callBackList.set(id, callBack);
            let map = {
                routName: routName,
                functionName: functionName,
                callBackId: id,
                data: data
            };
            $.ajax({
                type: "POST",
                url: this.webUrl + "/PostApi",
                data: JSON.stringify(map),
                timeout: timeout,
                success: function (data) {
                    callBack(JSON.parse(data));
                },
                error: function (data) {
                    callBack(undefined);
                }
            });
        };
        /**
         * Select your Image
         * */


        this.chooseImageCallback = function (data) {
        };

        this.chooseImage = function (callback) {
            if (!document.getElementById("imageSelect")) {
                $('body').append(`<input type="file" accept="image/*"  style="display: none" id="imageSelect" multiple>`);
                $('#imageSelect').change(function (e) {
                    let files = $('#imageSelect').get(0).files;
                    var imageMap = [];
                    for (let a = 0; a < files.length; a++) {
                        let reader = new FileReader();
                        reader.readAsDataURL(files[a]);
                        reader.onload = function getFileInfo(evt) {
                            imageMap = imageMap.concat({
                                data: evt.target.result,
                                type: 'image',
                                name: files[a].name
                            });
                            if (imageMap.length === files.length) {
                                glitter.chooseImageCallback(imageMap);
                            }
                        };
                    }
                });
            }
            this.chooseImageCallback = callback;
            $('#imageSelect').val(undefined);
            $('#imageSelect').click();
        };
        this.copyText = function (text) {
            if (!document.getElementById("imageSelect")) {
                $('body').append(`<input type="text"   style="display: none" id="copyText" multiple>`);
            }
            $('#copyText').val(text)
            $('#copyText').show()
            $('#copyText').select()
            document.execCommand("Copy");
            $('#copyText').hide()
        }
        this.chooseVideoCallback = function (data) {
        };

        this.chooseVideo = function (callback) {
            if (!document.getElementById("videoSelect")) {
                $('body').append(`<input type="file" accept="video/*"  style="display: none" id="videoSelect">`);
                $('#videoSelect').change(function (e) {
                    let files = $('#videoSelect').get(0).files;
                    var videoMap = [];
                    for (let a = 0; a < files.length; a++) {
                        let reader = new FileReader();
                        reader.readAsDataURL(files[a]);
                        reader.onload = function getFileInfo(evt) {
                            videoMap = videoMap.concat({
                                data: evt.target.result,
                                type: 'video',
                                name: files[a].name
                            });
                            if (videoMap.length === files.length) {
                                glitter.chooseVideoCallback(videoMap);
                            }

                        };
                    }
                });
            }

            this.chooseVideoCallback = callback;
            $('#videoSelect').val(undefined);
            $('#videoSelect').click();
        };
        this.chooseMediaCallback = function (data) {
        };

        this.chooseMedia = function (data, callback) {
            if (!document.getElementById("chooseMedia")) {
                $('body').append(`<input type="file" accept="${data}"  style="display: none" id="chooseMedia" multiple>`);
                $('#chooseMedia').change(function (e) {
                    let files = $('#chooseMedia').get(0).files;
                    var imageMap = [];
                    for (let a = 0; a < files.length; a++) {
                        let reader = new FileReader();
                        reader.readAsDataURL(files[a]);
                        reader.onload = function getFileInfo(evt) {
                            imageMap = imageMap.concat({
                                link: evt.target.result,
                                file: files[a]
                            });
                            if (imageMap.length === files.length) {
                                glitter.chooseMediaCallback(imageMap);
                            }
                        };
                    }
                });
            }
            this.chooseMediaCallback = callback;
            $('#chooseMedia').val(undefined);
            $('#chooseMedia').click();
        };

        this.changeBottomSheetHeight = function (hei) {
            $('.c-bottom-sheet__sheet').css('height', `${hei}`)
            $('.c-bottom-sheet__list').css('height', `${hei}`)
        }
        //解析字串中的網址
        this.urlify = function (text) {
            var urlRegex = /(https?:\/\/[^\s]+)/g;
            return text.replace(urlRegex, function (url) {
                return `<a href="${url}" onclick="glitter.openNewTab('${url}')">${url}</a>`;
            })
        }


    }

}

class LifeCycle {
    constructor(props) {
        this.onResume = function () {
        };

        this.onPause = function () {
        };

        this.onDestroy = function () {
        };
    }

} //window height


var windowHeight = window.innerHeight;
var windowHeightChangeListener = [];
window.addEventListener("resize", function () {
    for (var a = 0; a < windowHeightChangeListener.length; a++) {
        try {
            windowHeightChangeListener[a](window.innerHeight);
        } catch (e) {
        }
    }
}); //Application生命週期

var lifeCycle = new LifeCycle(); //glitter變數

var glitter = new Glitter(); //glitter變數

var rootGlitter = glitter; //顯示類型

var appearType = new AppearType(); // 監聽鍵盤按鍵事件，並回傳所按的按鍵為何

window.addEventListener('keydown', function (e) {
    var arrat = glitter.dialog;

    for (var i = 0; i < arrat.length; i++) {
        if (arrat[i].cancelable) {
            glitter.closeDiaLog(arrat[i].id);
        }
    }
});

function _uuid() {
    var d = Date.now();

    if (typeof performance !== 'undefined' && typeof performance.now === 'function') {
        d += performance.now(); //use high-precision timer if available
    }

    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c === 'x' ? r : r & 0x3 | 0x8).toString(16);
    });
}

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

$(document).ready(function () {
    if (window.GL !== undefined) {
        glitter.deviceType = appearType.Android;
    } else if (navigator.userAgent === 'iosGlitter') {
        glitter.deviceType = appearType.Ios;
    }

    switch (glitter.deviceType) {
        case appearType.Web: {
            glitter.baseUrl = "";
            onCreate();
            break;
        }

        case appearType.Android: {
            glitter.baseUrl = "";
            onCreate();
            break;
        }

        case appearType.Ios: {
            glitter.baseUrl = "";
            onCreate();
            break;
        }
    }

    $('body').css('height', `${glitter.nowWindowHeight()}px`);
    glitter.addWindowHeightChangeListener(function (data) {
        $('body').css('height', `${data}px`);
    });
});
glitterInitial();

function glitterInitial() {
    if (glitter.deviceType !== glitter.deviceTypeEnum.Android) {
        window.addEventListener("popstate", function (e) {
            glitter.goBack();

            if (glitter.iframe.length > 0) {
                window.history.pushState(null, null, "?page=" + glitter.iframe[glitter.iframe.length - 1].id);
            }
        });
    }

    if (navigator.userAgent.match(/iPhone/i) || navigator.userAgent.match(/iPad/i)) {
        /* iOS hides Safari address bar */
        window.addEventListener("load", function () {
            setTimeout(function () {
                window.scrollTo(0, 1);
            }, 1000);
        });
    }

    glitter.getBoundingClientRect = $('html').get(0).getBoundingClientRect();
}