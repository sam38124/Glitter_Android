
class BleCallBack {
    //藍芽掃描回調
    static scanBack = function (deviceName, address, broadcast) {
    }
    //藍芽正在連線
    static onConnecting = function () {
    }
    //藍芽連線失敗
    static onConnectFalse = function () {
    }
    //藍芽斷線
    static onDisconnect = function () {
    }
    //連線成功
    static onConnectSuccess = function () {
    }
    //收到訊息
    static rx = function (hex) {
    }
    //傳送訊息
    static tx = function (hex) {
    }
    //Android要打開定位才能掃描藍芽
    static needGPS = function () {
    }
    //Android需要定位和藍芽權限
    static requestPermission = function (permission) {
    }
}

class BleHelper {
    //初始化
    static initial() {
        window.JzBle.initial();
    }

    //開使掃描藍芽
    static startScan() {
        window.JzBle.startScan();
    }

    //關閉掃描藍芽
    static stopScan() {
        window.JzBle.stopScan();
    }

    //藍芽是否連線
    static isConnected() {
        return window.JzBle.isConnected();
    }

    //藍芽連線
    static connect(address, timeout) {
        window.JzBle.connect(address, timeout);
    }

    //藍芽是否正在掃描
    static isDiscovering() {
        return window.JzBle.isDiscovering();
    }

    //寫入資料
    static writeHex(hex, rxChannel, txChannel) {
        window.JzBle.writeHex(hex, rxChannel, txChannel);
    }
}