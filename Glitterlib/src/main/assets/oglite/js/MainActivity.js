//頁面參數宣告
class jsonData {
    static get admin() {
        return Glitter.getValue("admin")
    }

    static set admin(value) {
        Glitter.setValue("admin", value)
    }
}


JzActivity.changePageListener = function (tag, url) {
    Glitter.logE('JzActivity', 'changePageListener:tag->' + tag + 'url->' + url)
}
JzActivity.dialogListener = function (tag, url) {
    Glitter.logE('JzActivity', 'dialogListener:tag->' + tag + 'url->' + url)
}
JzActivity.keyEventListener = function (event) {
    Glitter.logE('JzActivity', 'keyEventListener:event->' + event['mKeyCode'])
}
JzActivity.viewInit = function () {
    // Glitter.setHome('index.html', 'index', '')
    Glitter.setHome('index.html', 'index', '')
    //檢查藍芽連線
    // TaskHandler.lifeTimer(3000, 1000 * 3, 'checkBLE()')
    jsonData.admin = 10
    setInterval(checkBLE, 3000)
}

function checkBLE() {
    Glitter.logE('Json', jsonData.admin)
    // if (!BleHelper.isConnected() && !BleHelper.isConnected() && canConnect) {
    //     if (!BleHelper.isDiscovering()) {
    //         BleHelper.startScan()
    //     }
    // }
}