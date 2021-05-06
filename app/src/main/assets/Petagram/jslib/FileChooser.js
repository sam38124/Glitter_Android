"use strict";
var compressRatio = 1, // 圖片壓縮比例
    img = new Image(),
    canvas = document.createElement("canvas"),
    context = canvas.getContext("2d"),
    file, fileReader, dataUrl;
var imgc = 0
class FileChooser {
    constructor() {
        //圖片上傳
        this.uploadImage = function () {
            img = new Image()
            imgc++
            var file = $('#filed').get(0).files[0];
            $('#filed').val('')
            var reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = getFileInfo;
            img.onload = function (e) {
                var width = this.width, height = this.height
                var imgNewWidth = $(document).width() // 圖片新寬度
                var imgNewHeight = imgNewWidth * height / width
                canvas.width = imgNewWidth;
                canvas.height = imgNewHeight;
                context.clearRect(0, 0, imgNewWidth, imgNewHeight);
                context.drawImage(img, 0, 0, imgNewWidth, imgNewHeight);
                $('#' + imgc).load(function () {
                    // 加载完成
                    document.getElementById(imgc).scrollIntoView();
                    //console.log('滾動完畢')
                });
                canvas.toBlob(function (blob) {
                    $('#filed').val('')
                    getFileBase64Encode(file).then(b64 =>
                        glitter.publicBeans.uploadImage(b64, function success(result) {
                            var jsData = JSON.parse(result)
                            if (jsData.result === "true") {
                                var url = jsData.url
                                fileChooser.imageCallBack(url)
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
        this.uploadVideo = function () {
            var file = $('#video').get(0).files[0];
            $('#video').val('')
            var reader = new FileReader();
            reader.readAsDataURL(file);
            getFileBase64Encode(file).then(b64 =>
                glitter.publicBeans.uploadVideo(b64, function success(result) {
                    var jsData = JSON.parse(result)
                    if (jsData.result === "true") {
                        var url =  jsData.url
                        fileChooser.videoCallBack(url)
                    } else {
                        glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
                        })
                    }
                }, function error(error) {
                    glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
                    })
                }));
        }
        //初始設定
        this.setUp=function () {
            if($('#filed').length>0){return}
            $('body').append(`<input type="file" accept="image/*" multiple="multiple" style="display: none" id="filed">`)
            $('body').append(`  <input type="file" accept="video/mp4,video/x-m4v,video/*" multiple="multiple" style="display: none" id="video">`)
            $('#filed').change(function (e) {
                glitter.openDiaLog('dialog/Dia_DataLoading.html', 'uploadImage', false, false, '{}')
                fileChooser.uploadImage()
            })
            $('#video').change(function (e) {
                glitter.openDiaLog('dialog/Dia_DataLoading.html', 'uploadImage', false, false, '{}')
                fileChooser.uploadVideo()
            })
        }
        //選擇圖片
        this.selectImage=function (callback){
            fileChooser.setUp()
            fileChooser.imageCallBack=callback
            $('#filed').click()
        }
        //選擇影片
        this.selectVideo=function (callback){
            fileChooser.setUp()
            fileChooser.videoCallBack=callback
            $('#video').click()
        }
        this.videoCallBack=function (a){}
        this.imageCallBack=function (a){}
    }
}

function getFileInfo(evt) {
    dataUrl = evt.target.result,
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

var fileChooser=new FileChooser()