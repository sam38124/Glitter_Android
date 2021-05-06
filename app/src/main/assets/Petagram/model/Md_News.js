class Md_News {
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
function getNews(result, id) {
    var map = {}
    map['request'] = 'getNews'
    map['language'] = navigator.language
    map['id'] = id
    glitter.publicBeans.postRequest(map,function (data) {
        var json = JSON.parse(data)
        json = JSON.parse(json["data"])
        result(json)
    },function (data) {
        result('error')
    })
}

//取得View
function appendNews(frag,data) {
    var model = new Md_News()
    model = data
    var title = JSON.parse('{"unicode": "' + data.title + '"}')["unicode"]
    var html = ''
    //console.log('image=='+data.image)

    if (data.image === undefined || data.image==='NA') {
        html =
            `        <div class="ListItem" onclick="glitter.openNewTab('${data.link}');">
            <div class="itembar">
                <img src="${data.head}" class="headImg">
                <t1 class="name">${data.pick}</t1>
                <img src="../img/spi.png" style="width: 15px;height: 15px;margin-top: 5px;">
                <t1 class="circle">${data.circle}</t1>
            </div>
            <div style="width: 100%;display: flex;margin-top: -10px;">
                <div style="width: calc(100% - 10px);">
                    <h1 class="title" >${title}</h1>
                    <h1 class="content" >${title}</h1>
                </div>
            </div>
            <div style="bottom: 0;width: 100%;height: 1px;background-color: gray;position: relative;"></div>
        </div>
`
    } else {
        html =
            `        <div class="ListItem" onclick="glitter.openNewTab('${data.link}');">
            <div class="itembar">
                <img src="${data.head}" class="headImg">
                <t1 class="name">${data.pick}</t1>
                <img src="../img/spi.png" style="width: 15px;height: 15px;margin-top: 5px;">
                <t1 class="circle">${data.circle}</t1>
            </div>
            <div style="width: 100%;display: flex;margin-top: -10px;">
                <div style="width: calc(100% - 80px);">
                    <h1 class="title" >${title}</h1>
                    <h1 class="content" >${title}</h1>
                </div>
                <img src="${data.image}" class="hint">
            </div>
            <div style="width: 100%;height: 1px;background-color: gray;position: relative;"></div>
        </div>
`
    }
    $('#' + frag).append(html)
}

