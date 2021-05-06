"use strict";
var compressRatio = 1, // 圖片壓縮比例
    imgNewWidth = $(document).width(), // 圖片新寬度
    canvas = document.createElement("canvas"),
    context = canvas.getContext("2d"),
    file, fileReader, dataUrl;

var imgages = 0
//圖片上傳
async function uploadImage() {
    var fileList = $('#filed').get(0).files
    var pass = 0
    for (var a = 0; a < fileList.length; a++) {
        let imgc = imgages++
        let img = new Image()
        let file = fileList[a];
        var reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function getFileInfo(evt) {
            dataUrl = evt.target.result // 取得圖片
            img.src = dataUrl;
        };
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
                //console.log('滾動完畢')
            });
            canvas.toBlob(function (blob) {
                getFileBase64Encode(file).then(b64 =>
                    glitter.publicBeans.uploadImage(b64, function success(result) {
                        var jsData = JSON.parse(result)
                        if (jsData.result === "true") {
                            var url = jsData.url
                            $('#textArea').append('<br/><img  id="' + imgc + '" style="margin-left:10px;max-width: ' + vwidth + 'px;" src="' + url + '"> <br/>\t')
                        } else {
                            glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
                            })
                        }
                        pass += 1
                        if (pass === fileList.length) {
                            glitter.closeDiaLog("uploadImage")
                        }
                    }, function error(error) {
                        pass += 1
                        glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
                        })
                    }));
            }, "image/jpeg", compressRatio);
        }

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
                var url = jsData.url
                callback(jsData.url)
            } else {
                glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
                })
            }
        }, function error(error) {
            glitter.openDiaLog('dialog/Dia_DisConnect.html', 'DisConnect', false, false, function () {
            })
        }));
}

function getFileBase64Encode(blob) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(blob);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });
}