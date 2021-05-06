"use strict";
function onCreate() {
  // glitter.setHome('lpage/Page_Home.html','Page_Home')
  //   return

    //當判斷為登入頁面時
    if(glitter.getUrlParameter('page')==='SignIn'){
        glitter.addScript("model/Md_User.js", () => {
            glitter.addScript('PublicBeans.js', () => {
                glitter.md_user = new Md_User()
                glitter.publicBeans = new PublicBeans()
                glitter.publicBeans.requestAllApi=function (){requestAllApi()}
                glitter.setHome('page/Page_Sign_In.html', 'Page_Sign_In', glitter.getUrlParameter('returnUrl'))
            }, () => {
                onCreate()
            })
        }, () => {
            onCreate()
        })
    }else{
        glitter.defalutAnimator=glitter.animator.translation
        initPublic()
        glitter.setLoadingView('dialog/Dia_DataLoading.html')
        if (glitter.deviceType === glitter.deviceTypeEnum.Web) {
            switch (glitter.getBrowserDeviceType()) {
                case "Android":
                    window.location = "squ://tsport.com/";
                    break
                case "IOS":
                    break
                case "Desktop":
                    break
            }
        }
    }
  // setTimeout(function (){
  //     window.scrollTo(0,50)
  // },3000)
  // alert(JSON.parse('{"readUTF":"b\\u0003b\\u0003","readBytes":[98,3,98,3],"readHEX":"62036203"}'))
}

function initPublic() {
    glitter.addScript("model/Md_User.js", () => {
        glitter.addScript('PublicBeans.js', () => {
            glitter.md_user = new Md_User()
            glitter.publicBeans = new PublicBeans()
            glitter.publicBeans.toLocalPage = function () {location.href = `${glitter.publicBeans.domain}/Petagram/home`}
            glitter.publicBeans.requestAllApi=function (){requestAllApi()}
            staticView()
            initMyJsInterFace()
            initApiFunction()
            glitter.setHome('page/Page_Logo.html', 'Page_Logo', new URL(location.href))
        }, () => {
            initPublic()
        })
    }, () => {
        initPublic()
    })
}

function staticView() {
    //影像顯示View
    glitter.publicBeans.getVideoView = function (data, style,controls) {
        return `<video id="hls-video" style="` + style + `"  class="video-js vjs-big-play-centered"
       playsinline webkit-playsinline
       autoplay ${(controls) ? "controls":""} preload="true"
       x-webkit-airplay="true" x5-video-player-fullscreen="true" x5-video-player-typ="h5" width="auto" height="auto" allowfullscreen="true" webkitallowfullscreen="true" mozallowfullscreen="true"poster="${data.videoLink}thumb.jpg">
    <source src="${ data.videoLink}root.m3u8" type="application/x-mpegURL" id="source">
</video>`
    }
    //公用加載listView
    glitter.publicBeans.getLoadingView = `<div id="loadingView" style="display:flex;flex-direction:column;align-items:center;justify-content:center;height: 100px; width: 100vw;text-align: center;vertical-align: middle;">
    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"  id="id_svgCanvas" x="0px" y="0px"  width="34px" height="34px" style="fill: black;" xml:space="preserve" preserveAspectRatio="none">
<path  d="M4 15h2v3h12v-3h2v3c0 1.1-.9 2-2 2H6c-1.1 0-2-.9-2-2m11.59-8.41L13 12.17V4h-2v8.17L8.41 9.59 7 11l5 5 5-5-1.41-1.41z"/>
</svg>
    <h1 style="font-size: 13px;color: black;margin-top: 0;">加載中...</h1>
</div>`
    glitter.publicBeans.getLoadingViewId = function (id){
        return `<div id="loadingView${id}" style="display:flex;flex-direction:column;align-items:center;justify-content:center;height: 100px; width: 100vw;text-align: center;vertical-align: middle;">
    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"  id="id_svgCanvas" x="0px" y="0px"  width="34px" height="34px" style="fill: #ffffff;" xml:space="preserve" preserveAspectRatio="none">
<path  d="M4 15h2v3h12v-3h2v3c0 1.1-.9 2-2 2H6c-1.1 0-2-.9-2-2m11.59-8.41L13 12.17V4h-2v8.17L8.41 9.59 7 11l5 5 5-5-1.41-1.41z"/>
</svg>
    <h1 style="font-size: 13px;color: #ffffff;margin-top: 0;">加載中...</h1>
</div>`
    }
    glitter.publicBeans.getLoadingWhite = `<div id="loadingView" style="display:flex;flex-direction:column;align-items:center;justify-content:center;height: 100px; width: 100vw;text-align: center;vertical-align: middle;">
     <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"  id="id_svgCanvas" x="0px" y="0px" width="34px" height="34px"  style="fill: rgb(222, 220, 217);" xml:space="preserve" preserveAspectRatio="none">
<path  d="M4 15h2v3h12v-3h2v3c0 1.1-.9 2-2 2H6c-1.1 0-2-.9-2-2m11.59-8.41L13 12.17V4h-2v8.17L8.41 9.59 7 11l5 5 5-5-1.41-1.41z"/>
</svg>
    <h1 style="font-size: 13px;color: white;margin-top: 0;">加載中...</h1>
</div>`
}

function initMyJsInterFace() {
    glitter.publicBeans.share = function (elememt, doc) {
        switch (glitter.deviceType) {
            case glitter.deviceTypeEnum.Android:
                elememt.show()
                elememt.select()
                doc.execCommand("Copy");
                glitter.openDiaLog('dialog/Dia_Info.html', 'Dia_Info', false, true, `分享連結複製成功!!`)
                elememt.hide()
                break
            case glitter.deviceTypeEnum.Ios:
                elememt.show()
                elememt.select()
                doc.execCommand("Copy");
                glitter.openDiaLog('dialog/Dia_Info.html', 'Dia_Info', false, true, `分享連結複製成功!!`)
                elememt.hide()
                break
            case glitter.deviceTypeEnum.Web:
                elememt.show()
                elememt.select()
                doc.execCommand("Copy");
                glitter.openDiaLog('dialog/Dia_Info.html', 'Dia_Info', false, true, `分享連結複製成功!!`)
                elememt.hide()
                break
        }
    }
}

/*
* 所有api和資料暫存
* */
function initApiFunction() {
    //用戶影像Model
    glitter.publicBeans.userVideoModel = {
        data: [],
        circle:'All',
        onLoadIng: false,
        prePend:false,
        isBt:false,
        request: function (id, prefix) {
            if (this.onLoadIng) {
                return
            }
            this.onLoadIng = true
            glitter.publicBeans.postRequest({request: 'getCVideo', id: id, prefix: prefix,circle:this.circle}, function (result) {
                var json = JSON.parse(result)
                var originSize=glitter.publicBeans.userVideoModel.data.length
                var item=JSON.parse(json["data"])
                if (json["data"] !== undefined) {
                    if (prefix) {
                        glitter.publicBeans.userVideoModel.data = item.concat(glitter.publicBeans.userVideoModel.data)
                    } else {
                        glitter.publicBeans.userVideoModel.data = glitter.publicBeans.userVideoModel.data.concat(item)
                    }
                }
                item.map(function (it){
                    it.itemType='userVideoModel'
                })
                glitter.publicBeans.userVideoModel.isBt=(glitter.publicBeans.userVideoModel.data.length===originSize)
                glitter.publicBeans.userVideoModel.onLoadIng = false
            }, function error(data) {
                glitter.publicBeans.userVideoModel.onLoadIng = false
                glitter.publicBeans.userVideoModel.request(id, prefix)
            })
        },
        requestOld: function () {
            if(this.isBt){return}
            this.prePend=false
            var id = (this.data.length === 0) ? "-1" : this.data[this.data.length - 1].id
            this.request(id, false)
        },
        requestNew: function () {
            this.isBt=false
            this.prePend=true
            var id = (this.data.length === 0) ? "-1" : this.data[0].id
            this.request(id, true)
        }
    }
    //用戶文章Model
    glitter.publicBeans.userArticleModel = {
        data: [],
        onLoadIng: false,
        prePend:false,
        isBt:false,
        area:undefined,
        circle:'All',
        request: function (id, prefix) {
            if (this.onLoadIng) {return}
            this.onLoadIng = true
            glitter.publicBeans.postRequest({request: 'getForium', area:this.area,id: id, prefix: prefix,circle:glitter.publicBeans.userArticleModel.circle}, function (result) {
                glitter.publicBeans.userArticleModel.onLoadIng = false
                var json = JSON.parse(result)
                var originSize=glitter.publicBeans.userArticleModel.data.length
                var item=JSON.parse(json["data"])
                item.map(function (it){it.itemType='userArticleModel'})
                if (json["data"] !== undefined) {
                    if (prefix) {
                        glitter.publicBeans.userArticleModel.data = item.concat(glitter.publicBeans.userArticleModel.data)
                    } else {
                        glitter.publicBeans.userArticleModel.data = glitter.publicBeans.userArticleModel.data.concat(item)
                    }
                }
                glitter.publicBeans.userArticleModel.isBt=(glitter.publicBeans.userArticleModel.data.length===originSize)
            }, function error(data) {
                glitter.publicBeans.userArticleModel.onLoadIng = false
                glitter.publicBeans.userArticleModel.request(id, prefix)
            })
        },
        requestOld: function () {
            if(this.isBt){return}
            this.prePend=false
            var id = (this.data.length === 0) ? "-1" : this.data[this.data.length - 1].id
            this.request(id, false)
        },
        requestNew: function () {
            this.isBt=false
            this.prePend=true
            var id = (this.data.length === 0) ? "-1" : this.data[0].id
            this.request(id, true)
        }
    }
    //新聞Model
    glitter.publicBeans.newsModel = {
        data: [],
        onLoadIng: false,
        prePend:false,
        isBt:false,
        circle:'All',
        request: function (id, prefix) {
            if (this.onLoadIng) {return}
            this.onLoadIng = true
            glitter.publicBeans.postRequest({request: 'getNews', id: id, prefix: prefix,circle:glitter.publicBeans.newsModel.circle}, function (result) {
                glitter.publicBeans.newsModel.onLoadIng = false
                var json = JSON.parse(result)
                var originSize=glitter.publicBeans.newsModel.data.length
                var item=JSON.parse(json["data"])
                item.map(function (it){it.itemType='userArticleModel'})
                if (json["data"] !== undefined) {
                    if (prefix) {
                        glitter.publicBeans.newsModel.data = item.concat(glitter.publicBeans.newsModel.data)
                    } else {
                        glitter.publicBeans.newsModel.data = glitter.publicBeans.newsModel.data.concat(item)
                    }
                }
                glitter.publicBeans.newsModel.isBt=(glitter.publicBeans.newsModel.data.length===originSize)
            }, function error(data) {
                glitter.publicBeans.newsModel.onLoadIng = false
                glitter.publicBeans.newsModel.request(id, prefix)
            })
        },
        requestOld: function () {
            if(this.isBt){return}
            this.prePend=false
            var id = (this.data.length === 0) ? "-1" : this.data[this.data.length - 1].id
            this.request(id, false)
        },
        requestNew: function () {
            this.isBt=false
            this.prePend=true
            var id = (this.data.length === 0) ? "-1" : this.data[0].id
            this.request(id, true)
        }
    }
    //用戶揪團Model
    glitter.publicBeans.userGroup = {
        data: [],
        onLoadIng: false,
        prePend:false,
        circle:"All",
        isBt:false,
        request: function (id, prefix) {
            if (this.onLoadIng) {
                return
            }
            this.onLoadIng = true
            glitter.publicBeans.postRequest({request: 'getGroupData', id: id, prefix: prefix,type:'Group',circle:this.circle}, function (result) {
                var json = JSON.parse(result)
                var originSize=glitter.publicBeans.userGroup.data.length
                if (json["data"] !== undefined) {
                    var item=JSON.parse(json["data"])
                    item.map(function (it){it.itemType='userGroup'})
                    if (prefix) {
                        glitter.publicBeans.userGroup.data = item.concat(glitter.publicBeans.userGroup.data)
                    } else {
                        glitter.publicBeans.userGroup.data = glitter.publicBeans.userGroup.data.concat(item)
                    }
                }
                glitter.publicBeans.userGroup.isBt=(glitter.publicBeans.userGroup.data.length===originSize)
                glitter.publicBeans.userGroup.onLoadIng = false
            }, function error(data) {
                glitter.publicBeans.userGroup.onLoadIng = false
                glitter.publicBeans.userGroup.request(id, prefix)
            })
        },
        requestOld: function () {
            if(this.isBt){return}
            this.prePend=false
            var id = (this.data.length === 0) ? "-1" : this.data[this.data.length - 1].id
            this.request(id, false)
        },
        requestNew: function () {
            this.isBt=false
            this.prePend=true
            var id = (this.data.length === 0) ? "-1" : this.data[0].id
            this.request(id, true)
        }
    }
    //用戶活動Model
    glitter.publicBeans.activityGroup = {
        data: [],
        onLoadIng: false,
        prePend:false,
        circle:'All',
        isBt:false,
        request: function (id, prefix) {
            if (this.onLoadIng) {
                return
            }
            this.onLoadIng = true
            glitter.publicBeans.postRequest({request: 'getGroupData', id: id, prefix: prefix,type:'Activity',circle:this.circle}, function (result) {
                var json = JSON.parse(result)
                var originSize=glitter.publicBeans.activityGroup.data.length
                if (json["data"] !== undefined) {
                    var item=JSON.parse(json["data"])
                    item.map(function (it){it.itemType='activityGroup'})
                    if (prefix) {
                        glitter.publicBeans.activityGroup.data = item.concat(glitter.publicBeans.activityGroup.data)
                    } else {
                        glitter.publicBeans.activityGroup.data = glitter.publicBeans.activityGroup.data.concat(item)
                    }
                }
                glitter.publicBeans.activityGroup.isBt=(glitter.publicBeans.activityGroup.data.length===originSize)
                glitter.publicBeans.activityGroup.onLoadIng = false
            }, function error(data) {
                glitter.publicBeans.activityGroup.onLoadIng = false
                glitter.publicBeans.activityGroup.request(id, prefix)
            })
        },
        requestOld: function () {
            if(this.isBt){return}
            this.prePend=false
            var id = (this.data.length === 0) ? "-1" : this.data[this.data.length - 1].id
            this.request(id, false)
        },
        requestNew: function () {
            this.isBt=false
            this.prePend=true
            var id = (this.data.length === 0) ? "-1" : this.data[0].id
            this.request(id, true)
        }
    }
    //用戶賽事Model
    glitter.publicBeans.contestGroup = {
        data: [],
        onLoadIng: false,
        prePend:false,
        circle:"All",
        isBt:false,
        request: function (id, prefix) {
            if (this.onLoadIng) {
                return
            }
            this.onLoadIng = true
            glitter.publicBeans.postRequest({request: 'getGroupData', id: id, prefix: prefix,type:'Contest',circle:this.circle}, function (result) {
                var json = JSON.parse(result)
                var originSize=glitter.publicBeans.contestGroup.data.length
                if (json["data"] !== undefined) {
                    var item=JSON.parse(json["data"])
                    item.map(function (it){it.itemType='contestGroup'})
                    if (prefix) {
                        glitter.publicBeans.contestGroup.data = item.concat(glitter.publicBeans.contestGroup.data)
                    } else {
                        glitter.publicBeans.contestGroup.data = glitter.publicBeans.contestGroup.data.concat(item)
                    }
                }
                glitter.publicBeans.contestGroup.isBt=(glitter.publicBeans.contestGroup.data.length===originSize)
                glitter.publicBeans.contestGroup.onLoadIng = false
            }, function error(data) {
                glitter.publicBeans.contestGroup.onLoadIng = false
                glitter.publicBeans.contestGroup.request(id, prefix)
            })
        },
        requestOld: function () {
            if(this.isBt){return}
            this.prePend=false
            var id = (this.data.length === 0) ? "-1" : this.data[this.data.length - 1].id
            this.request(id, false)
        },
        requestNew: function () {
            this.isBt=false
            this.prePend=true
            var id = (this.data.length === 0) ? "-1" : this.data[0].id
            this.request(id, true)
        }
    }
    //推薦用戶(影片)
    glitter.publicBeans.adUser = {
        data: [],
        onLoadIng: false,
        prePend:false,
        isBt:false,
        request: function () {
            if (this.onLoadIng) {
                return
            }
            this.onLoadIng = true
            glitter.publicBeans.postRequest({request: 'adUser'}, function (result) {
                var json = JSON.parse(result)
                glitter.publicBeans.adUser.data=(json["data"])
            }, function error(data) {
                glitter.publicBeans.adUser.onLoadIng = false
                glitter.publicBeans.adUser.request()
            })
        }
    }
    glitter.publicBeans.adUser.request()
    /* 影片畫面跳轉 */
    glitter.publicBeans.goVideo=function (id){
        if(glitter.publicBeans.pauseFragC!== undefined){
            glitter.publicBeans.pauseFragC()
        }
        glitter.publicBeans.postWithDialog({
            request: 'getVideoByID',
            account: glitter.md_user.account,
            id: id
        }, function (res) {
            var result = JSON.parse(res)
            if (result.result === 'true') {
                var item = JSON.parse(result.data)
                if(item.length===1){
                    glitter.changePage('page/Page_Show_Video.html','Page_Show_Video',true,item[0])
                }else{
                    glitter.openDiaLog('dialog/Dia_Info.html','Dia_Info',true,false,"影片已遭刪除",function (){})
                }
            }else{
                glitter.openDiaLog('dialog/Dia_Info.html','Dia_Info',true,false,"取得影片失敗",function (){})
            }
        }, function () {
        })
    }
    /*查看並挑轉至用戶資料*/
    glitter.publicBeans.goUserInfo=function (account){
        glitter.publicBeans.postWithDialog({
            request: 'goUserInfo',
            toAccount: account
        },function (res) {

            var result = JSON.parse(res)

            if (result.result === 'true') {
                var item = result.data

                if(item.length===1){
                    glitter.changePage('page/Page_User_Info.html','Page_User_Info',true,item[0])
                }else{
                    glitter.openDiaLog('dialog/Dia_Info.html','Dia_Info',false,true,"檔案不存在",function (){})
                }
            }else{
                glitter.openDiaLog('dialog/Dia_Info.html','Dia_Info',false,true,"取得檔案失敗",function (){})
            }
        },function (){

        })
    }
    /* 文章畫面跳轉 */
    glitter.publicBeans.goArticle=function (id){
        glitter.publicBeans.postWithDialog({
            request: 'getArticleById',
            account: glitter.md_user.account,
            id: id
        }, function (res) {
            var result = JSON.parse(res)
            if (result.result === 'true') {
                var item = JSON.parse(result.data)
                if (item.length === 1) {
                    glitter.changePage('page/Page_Show_Article.html', 'Page_Show_Article',true, item[0])
                }else{
                    glitter.openDiaLog('dialog/Dia_Info.html', 'Dia_Info', false, true, `此文章已被刪除!!`)
                }
            } else {
                glitter.openDiaLog('dialog/Dia_Info.html', 'Dia_Info', false, true, `發生錯誤，請稍後再試!!`)
            }
        }, function () {
        })
    }
    /* 活動畫面跳轉 */
    glitter.publicBeans.goActivity=function (id){
        glitter.publicBeans.postWithDialog({
            request: 'getGroupById',
            account: glitter.md_user.account,
            id: id
        }, function (res) {
            var result = JSON.parse(res)
            if (result.result === 'true') {
                var item = JSON.parse(result.data)
                if (item.length === 1) {
                    glitter.changePage('page/Page_Show_Group.html', 'Page_Show_Group',true, item[0])
                }else{
                    glitter.openDiaLog('dialog/Dia_Info.html', 'Dia_Info', false, true, `此活動已被刪除!!`)
                }
            } else {
                glitter.openDiaLog('dialog/Dia_Info.html', 'Dia_Info', false, true, `發生錯誤，請稍後再試!!`)
            }
        }, function () {
        })
    }
    //跳轉至商店
    glitter.publicBeans.goCommodity=function (id){
        glitter.publicBeans.postWithDialog({
            request: 'getCommodityById',
            account: glitter.md_user.account,
            id: id
        }, function (res) {
            var result = JSON.parse(res)
            if (result.result === 'true') {
                var item = JSON.parse(result.data)
                if (item.length === 1) {
                    glitter.changePage('page/Page_Show_Shopping.html', 'Page_Show_Shopping',true, item[0])
                }else{
                    glitter.openDiaLog('dialog/Dia_Info.html', 'Dia_Info', false, true, `此商品已被刪除!!`)
                }
            } else {
                glitter.openDiaLog('dialog/Dia_Info.html', 'Dia_Info', false, true, `發生錯誤，請稍後再試!!`)
            }
        }, function () {
        })
    }
    /*查詢畫面跳轉*/
    glitter.publicBeans.search=function (pos){
        glitter.changePage('page/Page_Serch.html', 'Page_Serch',true, pos)
    }
    //查詢商品Model
    glitter.publicBeans.commodityModel = {
        data: [],
        onLoadIng: false,
        prePend:false,
        isBt:false,
        circle:'All',
        request: function (id, prefix) {
            if (this.onLoadIng) {return}
            this.onLoadIng = true
            glitter.publicBeans.postRequest({request: 'getCommodity', id: id, prefix: prefix,circle:glitter.publicBeans.commodityModel.circle}, function (result) {
                glitter.publicBeans.commodityModel.onLoadIng = false
                var json = JSON.parse(result)
                var originSize=glitter.publicBeans.commodityModel.data.length
                var item=JSON.parse(json["data"])
                item.map(function (it){it.itemType='commodity'})
                if (json["data"] !== undefined) {
                    if (prefix) {
                        glitter.publicBeans.commodityModel.data = item.concat(glitter.publicBeans.commodityModel.data)
                    } else {
                        glitter.publicBeans.commodityModel.data = glitter.publicBeans.commodityModel.data.concat(item)
                    }
                }
                glitter.publicBeans.commodityModel.isBt=(glitter.publicBeans.commodityModel.data.length===originSize)
            }, function error(data) {
                glitter.publicBeans.commodityModel.onLoadIng = false
                glitter.publicBeans.commodityModel.request(id, prefix)
            })
        },
        requestOld: function () {
            if(this.isBt){return}
            this.prePend=false
            var id = (this.data.length === 0) ? "-1" : this.data[this.data.length - 1].id
            this.request(id, false)
        },
        requestNew: function () {
            this.isBt=false
            this.prePend=true
            var id = (this.data.length === 0) ? "-1" : this.data[0].id
            this.request(id, true)
        }
    }
}
/*
* 加載所有API
* */
function requestAllApi(){
    glitter.publicBeans.userVideoModel.requestNew()
    glitter.publicBeans.userArticleModel.requestNew()
    glitter.publicBeans.userGroup.requestNew()
    glitter.publicBeans.contestGroup.requestNew()
    glitter.publicBeans.activityGroup.requestNew()
}
