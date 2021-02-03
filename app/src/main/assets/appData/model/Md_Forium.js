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

//新聞請求
function getForium(result, id) {
    var map = {}
    map['request'] = 'getForium'
    map['language'] = navigator.language
    map['id'] = id
    glitter.publicBeans.postRequest(map,function (data) {
        var json = JSON.parse(data)
        if (json["data"] === undefined) {
            result('error')
        } else {
            json = JSON.parse(json["data"])
            result(json)
        }
    },function (data) {
        result('error')
    })
}
//論壇發布
function postForium(dat,result) {
    var map = {}
    map['request'] = 'postForium'
    map['language'] = navigator.language
    map['data'] = JSON.stringify(dat)
    glitter.publicBeans.postRequest(map,function (data) {
        var json = JSON.parse(data)
        result(json.result)
    },function (data) {
        result('error')
    })
}
//按讚
function postLike(id,result) {
    var map = {}
    map['request'] = 'postFoLike'
    map['language'] = navigator.language
    map['id'] = id
    map['account'] =  glitter.md_user.account
    glitter.publicBeans.postRequest(map,function (data) {
        var json = JSON.parse(data)
        result(json)
    },function (data) {
        result('error')
    })
}
//收槽
function postFSub(id,result) {
    var map = {}
    map['request'] = 'postFSub'
    map['language'] = navigator.language
    map['id'] = id
    map['account'] =  glitter.md_user.account
    glitter.publicBeans.postRequest(map,function (data) {
        var json = JSON.parse(data)
        result(json)
    },function (data) {
        result('error')
    })
}
//判斷有沒有對這文章按讚
function checkLike(id,result) {
    var map = {}
    map['request'] = 'checkFLike'
    map['language'] = navigator.language
    map['id'] = id
    map['account'] =  glitter.md_user.account
    glitter.publicBeans.postRequest(map,function (data) {
        var json = JSON.parse(data)
        result(json)
    },function (data) {
        result('error')
    })
}
//判斷有沒有對這文章收藏
function checkSub(id,result) {
    var map = {}
    map['request'] = 'checkFSub'
    map['language'] = navigator.language
    map['id'] = id
    map['account'] =  glitter.md_user.account
    glitter.publicBeans.postRequest(map,function (data) {
        var json = JSON.parse(data)
        result(json)
    },function (data) {
        result('error')
    })
}
//取得View
function appendViews(frag, data) {
    var model = new Md_Forium()
    model = data
    data.pick=JSON.parse('{"unicode": "' + data.pick + '"}')["unicode"]
    var title = JSON.parse('{"unicode": "' + data.title + '"}')["unicode"]
    var html = ''
    if (data.image === undefined||data.image==='NA'||data.image==='') {
        html =
            '        <div class="ListItem" id="'+model.id+'">\n' +
            '            <div class="itembar">\n' +
            '                <img src="' + data.head + '" class="headImg">\n' +
            '                <t1 class="name">' + data.pick + '</t1>\n' +
            '                <img src="../img/spi.png" style="width: 15px;height: 15px;margin-top: 5px;">\n' +
            '                <t1 class="circle">' + data.circle + '</t1>\n' +
            '            </div>\n' +
            '            <div style="width: 100%;display: flex;margin-top: -10px;">\n' +
            '                <div style="width: calc(100% - 10px);">\n' +
            '                    <h1 class="title" >' + title + '</h1>\n' +
            '                    <h1 class="content" >' + title + '</h1>\n' +
            '                </div>\n' +
            '            </div>\n' +
            '            <div style="bottom: 0;width: 100%;height: 1px;background-color: gray;position: relative;"></div>\n' +
            '        </div>\n'
    } else {
        html =
            '        <div class="ListItem" id="'+model.id+'">\n' +
            '            <div class="itembar">\n' +
            '                <img src="' + data.head + '" class="headImg">\n' +
            '                <t1 class="name">' + data.pick + '</t1>\n' +
            '                <img src="../img/spi.png" style="width: 15px;height: 15px;margin-top: 5px;">\n' +
            '                <t1 class="circle">' + data.circle + '</t1>\n' +
            '            </div>\n' +
            '            <div style="width: 100%;display: flex;margin-top: -10px;">\n' +
            '                <div style="width: calc(100% - 80px);">\n' +
            '                    <h1 class="title" >' + title + '</h1>\n' +
            '                    <h1 class="content" >' + title + '</h1>\n' +
            '                </div>\n' +
            '                <img src="' + data.image + '" class="hint">\n' +
            '            </div>\n' +
            '            <div style="width: 100%;height: 1px;background-color: gray;position: relative;"></div>\n' +
            '        </div>\n'
    }
    model.title = JSON.parse('{"unicode": "' + model.title + '"}')["unicode"]
    model.content=JSON.parse('{"unicode": "' + model.content + '"}')["unicode"]
    $('#' + frag).append(html)
    $( "#"+model.id ).click(function() {
        goDetail(data)
    });
}

//跳轉至Detail
function goDetail(model) {
    glitter.changePageWithAnimation('page/Page_Show_Article.html', 'Page_Show_Article', true,glitter.animator.translation, model)
}

