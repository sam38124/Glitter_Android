"use strict";
var compressRatio = 1, // 圖片壓縮比例
    imgNewWidth = $(document).width(), // 圖片新寬度
    img = new Image(),
    canvas = document.createElement("canvas"),
    context = canvas.getContext("2d"),
    file, fileReader, dataUrl;

var imgc = 0
//圖片上傳
async function  uploadImage() {
    imgc++
    var file = $('#filed').get(0).files[0];
    var reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = getFileInfo;
    img.onload = function (e) {
        var width = this.width, height = this.height
        var imgNewHeight = imgNewWidth * height / width
        canvas.width = imgNewWidth;
        canvas.height = imgNewHeight;
        context.clearRect(0, 0, imgNewWidth, imgNewHeight);
        context.drawImage(img, 0, 0, imgNewWidth, imgNewHeight);
        var newImg = canvas.toDataURL("image/jpeg", compressRatio);
        var vwidth = $('#textArea').width() - 20
        $('#' + imgc).load(function () {
            // 加载完成
            document.getElementById(imgc).scrollIntoView();
            console.log('滾動完畢')
        });
        canvas.toBlob(function (blob) {
            $('#filed').val('')
            getFileBase64Encode(file).then(b64 =>
                glitter.publicBeans.uploadImage(b64, function success(result) {
                    var jsData = JSON.parse(result)
                    if (jsData.result === "true") {
                        var url=glitter.publicBeans.domain+jsData.url
                        $('#textArea').append('<br/><img  id="' + imgc + '" style="margin-left:10px;max-width: ' + vwidth + 'px;" src="' + url + '"> <br/>\t')
                    } else {
                        glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
                        })
                    }
                }, function error(error) {
                    glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
                    })
                }));
        }, "image/jpeg", compressRatio);
    }

}
//影片上傳
async function uploadVideo(callback) {
    var file = $('#video').get(0).files[0];
    $('#video').val('')
    var reader = new FileReader();
    reader.readAsDataURL(file);
    getFileBase64Encode(file).then(b64 =>
        glitter.publicBeans.uploadVideo(b64, function success(result) {
            var jsData = JSON.parse(result)
            if (jsData.result === "true") {
                var url=glitter.publicBeans.domain+'/'+jsData.url
                callback(jsData.url)
//                 $('#textArea').append(`<div id="${jsData.url}" style="width:100%;height:auto;"><video id="hls-video" style="width:  100%; height: 100%;background-color: black;margin: 0;"  class="video-js vjs-big-play-centered"
//        playsinline webkit-playsinline
//        autoplay controls preload="true"
//        x-webkit-airplay="true" x5-video-player-fullscreen="true" x5-video-player-typ="h5" width="auto" height="auto" allowfullscreen="true" webkitallowfullscreen="true" mozallowfullscreen="true"poster="${url}/thumb.jpg">
//     <source src="${url}/root.m3u8" type="application/x-mpegURL" id="source">
// </video></div>`)
                // glitter.changeFrag(document.getElementById(jsData.url),'page/Page_Play_Video.html','Page_Play_Video.html',jsData.url)
            } else {
                glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
                })
            }
        }, function error(error) {
            glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {})
        }));
}
function getFileInfo(evt) {
    dataUrl = evt.target.result,
// 取得圖片
        img.src = dataUrl;
}

function getFileBase64Encode(blob) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(blob);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });
}