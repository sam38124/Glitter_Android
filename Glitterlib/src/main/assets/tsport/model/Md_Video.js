class Md_Video {
    constructor() {
        this.admin = ''
        this.id = ''
        this.pick = ''
        this.head = ''
        this.circle = ''
        this.link = ''
        this.title = ''
        this.time = ''
        this.reader = ''
        this.sub = ''
        this.love = ''
    }
}

//主頁影片請求
function getVideosMainData(result, id) {
    var publicBeans=new PublicBeans()
    var map = {}
    map['request'] = 'getMainData'
    map['language'] = navigator.language
    map['id'] = id
    $.ajax({
        type: "POST",
        url: publicBeans.apiRoot,
        data: '' + JSON.stringify(map),
        timeout:5000,
        success: function (data) {
            var json = JSON.parse(data)
            result(json)
        },
        error: function (data) {
            result('error')
        }
    });
}
//下滑影片請求
function getVideo(result, id) {
    console.log('idindex=='+id)
    var publicBeans=new PublicBeans()
    var map = {}
    map['request'] = 'getVideo'
    map['language'] = navigator.language
    map['id'] = id
    $.ajax({
        type: "POST",
        url: publicBeans.apiRoot,
        data: '' + JSON.stringify(map),
        timeout:5000,
        success: function (data) {
            var json = JSON.parse(data)
            result(json)
        },
        error: function (data) {
            result('error')
        }
    });
}
//取得推薦運動員的ListView
function getAdAthleteView(adAthlete) {
    initCss()
    var html =  '<div style="width: 100%;height: 100px;background-color: white;display: flex;overflow-x: scroll;">'
    for (let i = 0; i < adAthlete.length; i++) {
        let head = JSON.parse(adAthlete[i]).head
        let pick = JSON.parse(adAthlete[i]).pick
        html = html + ' <div style="display: inline-block">' +
            '            <img src="' + head + '" style="width: 60px;height: 60px;border-radius: 60px;margin: 10px;">' +
            '            <h3 style="font-size: 11px;color: black;margin-top: -10px;width: 60px;margin-left:10px;text-align: center;font-family: PMingLiU;overflow-x:hidden;' +
            'white-space: nowrap;' +
            'text-overflow: ellipsis;">' + pick + '</h3>' +
            '        </div>'
    }
    html = html + ' </div>'
    return html
}
//取得推薦影片的ListView
function getAdVideo(adVideo) {
    initCss()
    var html =  ' </div>'
    html = html + '<div style="width: 100%;height: 100px;display: flex;overflow-x: scroll;overflow-y: hidden;">'
    for (let i = 0; i < adVideo.length; i++) {
        var link = JSON.parse(adVideo[i]).link
        var title = JSON.parse(adVideo[i]).title
        html = html + '<div style="display: inline-block">' +
            '        <img src="https://img.youtube.com/vi/' + link + '/sddefault.jpg" class="Frag_Video_itemImg">' +
            '        <h3 class="Frag_Video_itemH3">' + title + '</h3>' +
            '    </div>'
    }
    html = html + '</div>'
    return html
}
//取得影片的ListView
function getVideoList(video) {
    initCss()
    html =  '</div>'
    var hei=$(document).width()*0.7
    for (let i = 0; i < video.length; i++) {
        var link = JSON.parse(video[i]).link
        var name = JSON.parse(video[i]).pick
        var circle = JSON.parse(video[i]).circle
        var title = JSON.parse(video[i]).title
        var head=JSON.parse(video[i]).head
        html = html + ' <div class="Frag_Video_item" style="height: '+hei+'px">' +
            '        <img class="Frag_Video_item_img" src="https://img.youtube.com/vi/' + link + '/sddefault.jpg" >' +
            '        <div class="Frag_Video_item_Btn_Div">\n' +
            '            <div class="Frag_Video_item_TopBar">\n' +
            '                <img class=\'Frag_Video_item_TopBarImg\' src="'+head+'">\n' +
            '                <h3 class="Frag_Video_item_TopBarName">'+name+'</h3>\n' +
            '                <img src="../img/spi.png" class="Frag_Video_item_SPI">\n' +
            '                <h3 class="CircleText">'+circle+'</h3>\n' +
            '            </div>\n' +
            '            <h3 class="Frag_Video_itemTitleText">'+title+'</h3>\n' +
            '        </div>' +
            '    </div>'
    }
    return html
}
//Css載入
function initCss() {
    var cssId = 'VideoCss';  // you could encode the css path itself to generate id..
    if (!document.getElementById(cssId))
    {
        var head  = document.getElementsByTagName('head')[0];
        var link  = document.createElement('link');
        link.id   = cssId;
        link.rel  = 'stylesheet';
        link.type = 'text/css';
        link.href = '../css/Frag_Video.css';
        link.media = 'all';
        head.appendChild(link);
    }
}