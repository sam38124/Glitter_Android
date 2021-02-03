class Md_Forium {
    constructor() {
        this.id = ''
        this.head = ''
        this.pick = ''
        this.account = ''
        this.link = ''
        this.title = ''
        this.image = ''
        this.time = ''
        this.circle = ''
    }
}

//影像請求
function getCVideo(result, id) {
    var map = {}
    map['request'] = 'getCVideo'
    map['language'] = navigator.language
    map['id'] = id
    glitter.publicBeans.postRequest(map, function (data) {
        var json = JSON.parse(data)
        if (json["data"] === undefined) {
            result('error')
        } else {
            json = JSON.parse(json["data"])
            result(json)
        }
    }, function (data) {
        result('error')
    })
}

//發布影像
function postCVideo(dat, result) {
    var map = {}
    map['request'] = 'postCVideo'
    map['language'] = navigator.language
    map['data'] = JSON.stringify(dat)
    glitter.publicBeans.postRequest(map, function (data) {
        var json = JSON.parse(data)
        result(json.result)
    }, function (data) {
        result('error')
    })
}

//按讚
function postLike(id, result) {
    var map = {}
    map['request'] = 'postCVLike'
    map['language'] = navigator.language
    map['id'] = id
    map['account'] =  glitter.md_user.account
    glitter.publicBeans.postRequest(map, function (data) {
        var json = JSON.parse(data)
        result(json)
    }, function (data) {
        result('error')
    })
}

//收槽
function postFSub(id, result) {
    var map = {}
    map['request'] = 'postCVSub'
    map['language'] = navigator.language
    map['id'] = id
    map['account'] =  glitter.md_user.account
    glitter.publicBeans.postRequest(map, function (data) {
        var json = JSON.parse(data)
        result(json)
    }, function (data) {
        result('error')
    })
}

//判斷有沒有對這文章按讚
function checkLike(id, result) {
    var map = {}
    map['request'] = 'checkCVLike'
    map['language'] = navigator.language
    map['id'] = id
    map['account'] =  glitter.md_user.account
    glitter.publicBeans.postRequest(map, function (data) {
        var json = JSON.parse(data)
        result(json)
    }, function (data) {
        result('error')
    })
}

//判斷有沒有對這文章收藏
function checkSub(id, result) {
    var map = {}
    map['request'] = 'checkCVSub'
    map['language'] = navigator.language
    map['id'] = id
    map['account'] =  glitter.md_user.account
    glitter.publicBeans.postRequest(map, function (data) {
        var json = JSON.parse(data)
        result(json)
    }, function (data) {
        result('error')
    })
}

//取得View
function appendViews(frag, data) {
    var model = new Md_Forium()
    model = data
    data.pick = JSON.parse('{"unicode": "' + data.pick + '"}')["unicode"]
    var title = JSON.parse('{"unicode": "' + data.title + '"}')["unicode"]
    var content = JSON.parse('{"unicode": "' + data.content + '"}')["unicode"]
    var html = ''
    html += `
    <div class="item" style="background-color: white;" id="`+model.id+`">
<div class="itemFlex">
    <img src="`+model.head+`">
    <h3>
        `+model.pick+`
    </h3>
    <img src="../img/spi.png" style="width: 15px;height: 15px;margin-top: 3px;">
      <t1 class="circle" >` + data.circle +`</t1>
    <div style="flex: auto;"></div>
    <div style="width: 20px;height: 20px;fill: #6a6a6a;">
        <svg viewBox="0 0 24 24" focusable="false" role="img" aria-hidden="true" class="ite">
            <g transform="translate(148)">
                <circle cx="2" cy="2" r="2" transform="translate(-138 4)"></circle>
                <circle cx="2" cy="2" r="2" transform="translate(-138 10)"></circle>
                <circle cx="2" cy="2" r="2" transform="translate(-138 16)"></circle>
            </g>
        </svg>
    </div>
</div>
<div style="height: 50vw;width:calc(100% - 20px);border-radius:10px;margin-left: 10px;margin-right: 10px;display: inline-block;">
`+content.replace('height:auto','height:100%')+ `
</div>
<!--    <img  src="../img/1563807726.jpg">-->
    <div style="display: flex;align-items: center;">
        <div style="display: inline-block;flex: auto;">
            <h3 class="title">`+title+`</h3>
            <h3 class="time">`+model.time+`</h3>
        </div>
       <div style="cursor:pointer;width: 20px;height: 20px;margin-right: 10px;fill: #315CA1;position: relative;z-index: 2;"  id="L`+model.id+`">
        <svg viewBox="0 0 24 24" focusable="false" role="img" aria-hidden="true" class="ite" id="heartBt">
            <path d="M16.5 4A5.49 5.49 0 0012 6.344 5.49 5.49 0 007.5 4 5.5 5.5 0 002 9.5C2 16 12 22 12 22s10-6 10-12.5A5.5 5.5 0 0016.5 4z"
                  fill-rule="evenodd"></path>
        </svg>
    </div>
    <div style="cursor:pointer;width: 20px;height: 20px;margin-right: 10px;fill:#315CA1;"  id="S`+model.id+`">
        <svg viewBox="0 0 24 24" focusable="false" role="img" aria-hidden="true" class="ite" id="subBt">
            <path d="M17.65 21.39L12 17.5l-5.65 3.88A1.5 1.5 0 014 20.15V5a2.5 2.5 0 012.5-2.5h11A2.5 2.5 0 0120 5v15.15a1.5 1.5 0 01-2.35 1.24z"></path>
        </svg>
    </div>
    </div>
</div>
    `
    model.title = JSON.parse('{"unicode": "' + model.title + '"}')["unicode"]
    model.content = JSON.parse('{"unicode": "' + model.content + '"}')["unicode"]
    $('#' + frag).append(html)
    $("#" + model.id).click(function () {
        goDetail(data)
    });
    $('#L'+model.id).css("fill",(model.isLike==="false")? "#aaaaaa" : "deeppink")
    $('#S'+model.id).css("fill",(model.isSub==="false")? "#aaaaaa" : "#315CA1")
    $('#L'+model.id).click(function (event) {
        event.stopPropagation()
        glitter.openDiaLog('dialog/Dia_DataLoading.html', 'post', false, false, '{}')
   postLike(model.id,function (result) {
       glitter.closeDiaLog('post')
       if (result === 'error') {
           glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
           })
       } else {
           if (result.result === 'true') {
               if (result.data === "-1") {
                   $('#L'+model.id).css('fill', 'rgba(0, 0, 0, 0.2)')
               } else {
                   $('#L'+model.id).css('fill', 'deeppink')
               }
           }
       }
   })
    })
    $('#S'+model.id).click(function (event) {
        event.stopPropagation()
        glitter.openDiaLog('dialog/Dia_DataLoading.html', 'post', false, false, '{}')
        postFSub(model.id,function (result) {
            glitter.closeDiaLog('post')
            if (result === 'error') {
                glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
                })
            } else {
                if (result.result === 'true') {
                    if (result.data === "-1") {
                        $('#S'+model.id).css('fill', 'rgba(0, 0, 0, 0.2)')
                    } else {
                        $('#S'+model.id).css('fill', '#315CA1')
                    }
                }
            }
        })
    })
}

//跳轉至Detail
function goDetail(model) {
    glitter.openBottomSheet('page/Page_Show_Video.html','Page_Show_Video',model,window.innerHeight+85,false)
    // glitter.openBottomSheet('dialog/Dia_Post_List.html','Dia_Post_List','{}',100,true)
}

var players = [];

window.addEventListener("resize", playVideos, false);
window.addEventListener("scroll", playVideos, false);

// Loop through all the players, check if video player is visible in the viewport. If it is visible, play it. If not, do not play it.
function playVideos() {
    for (var i = 0; i < players.length; i++)
    {
        var videoPlayer = $('#' + players[i]);
        var videoPlayerElem = $('#' + players[i]+' video').get(0);
        var topON=(i>0)
        if (isOnScreen(videoPlayer))
        {
            videoPlayerElem.muted=true
            videoPlayerElem.play();
        }
        else
        {
            videoPlayerElem.muted=true
            videoPlayerElem.pause();
        }
    }
}

function isOnScreen(element) {
    var elementOffsetTop = element.offset().top;
    var elementHeight = element.height();

    var screenScrollTop = $(window).scrollTop();
    var screenHeight = $(window).height();

    var scrollIsAboveElement = elementOffsetTop + elementHeight - screenScrollTop >= 0;
    var elementIsVisibleOnScreen = screenScrollTop + screenHeight - elementOffsetTop >= 0;

    return scrollIsAboveElement && elementIsVisibleOnScreen;
}
