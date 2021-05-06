"use strict";
class PublicBeans {
    constructor() {
        //Domain路徑
       // this.domain = "http://192.168.43.219"
        this.domain = "https://www.squarestudio.tw"
        //api路徑
        this.apiRoot = this.domain + '/Api';
        //token號碼
        this.token = ""
        //資料請求標頭
        this.postRequest = function postRequest(map, success, error) {
            map.language = navigator.language
            if(map.account===undefined){   map.account=glitter.md_user.account}
            if(map.app===undefined){   map.app="PawCha"}
            $.ajax({
                type: "POST",
                url: this.apiRoot,
                data: '' + JSON.stringify(map),
                timeout: 10*1000,
                success: function (data) {
                    console.log(data)
                    if (data === "NoToken") {
                        login(function (data) {
                            if(data==='success'){
                                glitter.publicBeans.postRequest(map, success, error)
                            }else{
                                setTimeout(error(data), 1000)
                            }
                        })
                    } else {
                        success(data)
                    }
                },
                error: function (data) {
                    setTimeout(error(data), 1000)
                }
            });
        }
        this.postWithDialog = function(map, success, error) {
            glitter.openDiaLog('dialog/Dia_DataLoading.html', 'post', false, false, '{}')
            map.language = navigator.language
            map.token=this.token
            if(map.account===undefined){   map.account=glitter.md_user.account}
            if(map.app===undefined){   map.app="PawCha"}
            $.ajax({
                type: "POST",
                url: this.apiRoot,
                data: '' + JSON.stringify(map),
                timeout: 5000,
                success: function (data) {
                    console.log(data)
                    if (data === "NoToken") {
                        login(function (data) {
                            if(data==='success'){
                                glitter.publicBeans.postRequest(map, success, error)
                            }else{
                                setTimeout(error(data), 1000)
                            }
                        })
                    } else {
                        glitter.closeDiaLogWithTag('post')
                        success(data)
                    }
                },
                error: function (data) {
                    glitter.closeDiaLogWithTag('post')
                    glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
                    })
                    setTimeout(error(data), 1000)
                }
            });
        }
        //圖片上傳統一標頭
        this.uploadImage = function (data2, success, error) {
            glitter.openDiaLog('dialog/Dia_DataLoading.html', 'uploadImage', false, false, '{}')
            $.ajax({
                type: "POST",
                url: this.domain + '/UploadImg?token='+glitter.publicBeans.token+'&account='+glitter.md_user.account,
                data: data2,
                timeout: 5000,
                success: function (data) {
                    if (data === "NoToken") {
                        login(function (data) {
                            if(data==='success'){
                                glitter.publicBeans.uploadImage(data2, success, error)
                            }else{
                                setTimeout(error(data), 1000)
                                glitter.closeDiaLog("uploadImage")
                            }
                        })
                    } else {
                        success(data)
                    }
                },
                error: function (data) {
                    //console.log('updateError')
                    error(data)
                    glitter.closeDiaLog("uploadImage")
                }
            });
        }

        //影片上傳統一標頭
        this.uploadVideo = function (data2, success, error) {
            //console.log('startRequest:UploadImageView')
            var tit=undefined
            var loadingMap={}
            loadingMap.getTit=function(e){
                tit=e
            }
            glitter.closeDiaLog("uploadImage")
            glitter.openDiaLog('dialog/Dia_DataLoading.html', 'uploadVideo', false, false, loadingMap)
            $.ajax({
                type: "POST",
                url: this.domain + '/UploadVideo?token='+glitter.publicBeans.token+'&account='+glitter.md_user.account,
                data: data2,
                processData : false,
                //必須false才會自動加上正確的Content-Type
                contentType : false ,
                timeout: 1000*180,
                success: function (data) {
                    if (data === "NoToken") {
                        login(function (data) {
                            if(data==='success'){
                                glitter.publicBeans.uploadVideo(data2, success, error)
                            }else{
                                //console.log('updateError')
                                setTimeout(error(data), 1000)
                                glitter.closeDiaLog("uploadVideo")
                            }
                        })
                    } else {
                        //console.log('repsonse' + data)
                        success(data)
                        glitter.closeDiaLog("uploadVideo")
                    }
                },
                error: function (data) {
                    //console.log('updateError')
                    error(data)
                    glitter.closeDiaLog("uploadVideo")
                }
                ,　xhr: function(){
                    var xhr = $.ajaxSettings.xhr();
                    if(onprogress && xhr.upload) {
                        xhr.upload.addEventListener("progress" , onprogress, false);
                        return xhr;
                    }
                } });
            function onprogress(evt){
                var loaded = evt.loaded;     //已經上傳大小情況
                var tot = evt.total;      //附件總大小
                var per = Math.floor(100*loaded/tot);  //已經上傳的百分比
                if(tit!==undefined){
                    tit.innerHTML=("上傳中.."+per)
                    //console.log("上傳中.."+per)
                }
            }
        }
    }
}

//重新登入
function login(fun) {
    var json = glitter.md_user
    json.request="login"
    glitter.publicBeans.postRequest(json, function (data) {
        var mapData = JSON.parse(data)
        if (mapData.result === "true") {
            glitter.publicBeans.token=mapData.token
            fun("success")
        } else {
            glitter.closeDiaLog()
            glitter.setHome('page/Page_Sign_In.html', 'Page_Sign_In', '{}')
        }
    }, function (data) {
        fun("error")
    })
}


