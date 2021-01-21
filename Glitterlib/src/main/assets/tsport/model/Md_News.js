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
    $.ajax({
        type: "POST",
        url: publicBeans.apiRoot,
        data: '' + JSON.stringify(map),
        timeout:5000,
        success: function (data) {
            var json = JSON.parse(data)
            json = JSON.parse(json["data"])
            result(json)
        },
        error: function (data) {
            result('error')
        }
    });
}

//取得View
function appendNews(frag,data) {
    var cssId = 'newsCss';  // you could encode the css path itself to generate id..
    if (!document.getElementById(cssId))
    {
        var head  = document.getElementsByTagName('head')[0];
        var link  = document.createElement('link');
        link.id   = cssId;
        link.rel  = 'stylesheet';
        link.type = 'text/css';
        link.href = '../css/Frag_News.css';
        link.media = 'all';
        head.appendChild(link);
    }
    var model = new Md_News()
    model = data
    var title = JSON.parse('{"unicode": "' + data.title + '"}')["unicode"]
    var html = ''
    if (data.img === undefined) {
        html =
            '        <div class="ListItem" onclick="changeWebContainer(\'' + data.link + '\');">\n' +
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
            '        <div class="ListItem" onclick="changeWebContainer(\'' + data.link + '\');">\n' +
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
            '                <img src="' + data.img + '" class="hint">\n' +
            '            </div>\n' +
            '            <div style="width: 100%;height: 1px;background-color: gray;position: relative;"></div>\n' +
            '        </div>\n'
    }
    $('#' + frag).append(html)
}

