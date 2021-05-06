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
    var map = {}
    map['request'] = 'getMainData'
    map['language'] = navigator.language
    map['id'] = id
    glitter.publicBeans.postRequest(map,function (data) {
        var json = JSON.parse(data)
        result(json)
    },function (data) {
        result('error')
    })
}
//下滑影片請求
function getVideo(result, id) {
    var map = {}
    map['request'] = 'getVideo'
    map['language'] = navigator.language
    map['id'] = id
    glitter.publicBeans.postRequest(map,function (data) {
        var json = JSON.parse(data)
        result(json)
    },function (data) {
        result('error')
    })
}



//滾動播放控制
var lastPlayPosition=0
var players=[]
function playVideos() {
    try {
        var onScreenList=[]
        for (var i = 0; i < players.length; i++)
        {
            var videoPlayer = $('#v' + players[i].id);
            if (isOnScreen(videoPlayer))
            {
                onScreenList=onScreenList.concat(players[i])
            }
            else
            {
                $(`#video${players[i].id}`).html('')
                $(`#image${players[i].id}`).show()
                $(`#video${players[i].id}`).hide()
            }
        }

        var position=parseInt(onScreenList.length/2,10)
        if($(window).scrollTop()<=0){position=0}
        if(lastPlayPosition!==onScreenList[position].id){
            for(var a=0;a<onScreenList.length;a++){
                if(a===position){
                    $(`#image${onScreenList[a].id}`).hide()
                    $(`#video${onScreenList[a].id}`).show()
                    onYouTubeIframeAPIReady(onScreenList[a])
                }else{
                    $(`#image${onScreenList[a].id}`).show()
                    $(`#video${onScreenList[a].id}`).hide()
                }
            }
        }
        lastPlayPosition=onScreenList[position].id
    }catch (e){}
}
//判斷元件是否在螢幕上
function isOnScreen(element) {
    var elementOffsetTop = element.offset().top;
    var elementHeight = element.height();
    var screenScrollTop = $(window).scrollTop();
    var screenHeight = $(window).height();
    var scrollIsAboveElement = elementOffsetTop + elementHeight - screenScrollTop >= 0;
    var elementIsVisibleOnScreen = screenScrollTop + screenHeight - elementOffsetTop >= 0;
    return scrollIsAboveElement && elementIsVisibleOnScreen;
}
//播放yt影片
function onYouTubeIframeAPIReady(map) {
    var player;
    player = new YT.Player(`video${map.id}`, {
        videoId: map.link,   // YouTube 影片ID
        width: $(document).width()-20,            // 播放器寬度 (px)
        height: $(document).width()*0.7,           // 播放器高度 (px)
        playerVars: {
            autoplay: 1,            // 自動播放影片
            controls: 0,            // 顯示播放器
            showinfo: 1,            // 隱藏影片標題
            modestbranding: 1,      // 隱藏YouTube Logo
            loop: 1,                // 重覆播放
            fs: 0,                  // 隱藏全螢幕按鈕
            cc_load_policty: 0,     // 隱藏字幕
            iv_load_policy: 3,      // 隱藏影片註解
            autohide: 1 ,// 影片播放時，隱藏影片控制列
            volume:0
        },
        events: {
            onReady: function(e) {
                e.target.mute();      //播放時靜音
                e.target.playVideo(); //強制播放(手機才會自動播放，但僅限於Android)
            },
            onStateChange: function(event) {
                event.target.mute();      //播放時靜音

            }
        }
    });
}


let t1 = 0;
let t2 = 0;
let timer = null; // 定时器

 function scrollLinstener() {
    clearTimeout(timer);
    timer = setTimeout(isScrollEnd, 500);
    t1 = document.documentElement.scrollTop || document.body.scrollTop;
}

function isScrollEnd() {
    t2 = document.documentElement.scrollTop || document.body.scrollTop;
    if(t2 === t1){
        playVideos()
    }
 }

window.addEventListener("resize", scrollLinstener, false);
window.addEventListener("scroll", scrollLinstener, false);
