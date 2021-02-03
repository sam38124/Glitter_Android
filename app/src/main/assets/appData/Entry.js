"use strict";

function onCreate() {
    initPublic()
}

function initPublic() {
    glitter.addScript("model/Md_User.js", () => {
        glitter.addScript('PublicBeans.js', () => {
            glitter.md_user = new Md_User()
            glitter.publicBeans = new PublicBeans()
            staticView()
            glitter.setHome('page/Page_Logo.html', 'Page_Logo', '{}')
        }, () => {
            initPublic()
        })
    }, () => {
        initPublic()
    })
}

function staticView() {
    //影像顯示View
    glitter.publicBeans.getVideoView = function (data,style) {
        return `<video id="hls-video" style="`+style+`"  class="video-js vjs-big-play-centered"
       playsinline webkit-playsinline
       autoplay controls preload="true"
       x-webkit-airplay="true" x5-video-player-fullscreen="true" x5-video-player-typ="h5" width="auto" height="auto" allowfullscreen="true" webkitallowfullscreen="true" mozallowfullscreen="true"poster="${glitter.publicBeans.domain + '/' + data.videoLink}/thumb.jpg">
    <source src="${glitter.publicBeans.domain + '/' + data.videoLink}/root.m3u8" type="application/x-mpegURL" id="source">
</video>`
    }
}
