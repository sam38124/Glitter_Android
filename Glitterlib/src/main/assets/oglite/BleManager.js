 var deviceList = []
//進行初始化
BleHelper.initial()

BleCallBack.needGPS = function () {

}
BleCallBack.onConnecting = function () {

}
BleCallBack.onConnectFalse = function () {

}
BleCallBack.onDisconnect = function () {

}
BleCallBack.onConnectSuccess = function () {

}
BleCallBack.rx = function (hex) {

}
BleCallBack.tx = function (hex) {

}
BleCallBack.needGPS = function () {

}
BleCallBack.scanBack = function (deviceName, address, broadcast) {
    // (deviceName.indexOf('OG_LITE') !== -1
    if ((deviceName !== 'null') && (deviceList.findIndex(e => e === deviceName) === -1)) {
        deviceList = deviceList.concat([deviceName])
    }
}
BleCallBack.requestPermission = function (permission) {
    var requestSuccess=0
    Permission_C.requestFalse = function (string) {
    }
    Permission_C.requestSuccess = function (string) {
        requestSuccess++
        if(requestSuccess===2){BleHelper.startScan()}
    }
    Glitter.permissionRequest(permission)
}



// var deviceList = []
// let worker = new Worker('../BleManager.js');
// //藍芽UUID
// let rxUUID = "00008D81-0000-1000-8000-00805F9B34FB"
// let txUUID = "00008D82-0000-1000-8000-00805F9B34FB"
// //藍芽掃描回調
// Glitter.ble.scanBack = function (deviceName, address, broadcast) {
//     if (Glitter.ut.getTagName() === "BleScanner") {
//         if ((deviceName !== 'null') && (deviceList.findIndex(e => e === deviceName) === -1) && (deviceName.indexOf('OG_LITE') !== -1)) {
//             deviceList = deviceList.concat([deviceName])
//             $('#bleList').append(' <div class="bleitem" style="padding: 10px;cursor: pointer;" onclick="Glitter.ble.connect(\'' + address + '\',10);' +
//                 ' Glitter.ut.showDiaLog(\'dialog/DataLoading.html\',175*2,75*2,\'DataLoading\',false,false)">\n' +
//                 deviceName +
//                 '        </div>')
//         }
//     }
// }
// //連線成功回調
// Glitter.ble.onConnectSuccess = function () {
//     if (Glitter.ut.getTagName() === "BleScanner") {
//         Glitter.ut.closeDiaLog("BleScanner")
//     }
//     Glitter.ble.stopScan()
//     Glitter.ut.closeDiaLog('DataLoading')
//     Glitter.ble.writeHex('0AF9F5', rxUUID, txUUID)
// }
//
// Glitter.ble.rx = function (string) {
//     Glitter.ut.logE('Glitter.ble.rx', string)
// }
// Glitter.ble.tx = function (string) {
//     Glitter.ut.logE('Glitter.ble.tx', string)
// }
// //定時查看藍芽狀況
// setInterval(function () {
//     Glitter.ut.logE('定時器', 'isConnect:' + Glitter.ble.isConnected() + '-getTagName:' + Glitter.ut.getTagName())
//     if (!Glitter.ble.isConnected()) {
//         if (!Glitter.ble.isDiscovering()) {
//             Glitter.ble.startScan()
//         }
//         if (!Glitter.ut.diaIsShowing('BleScanner')) {
//             Glitter.ut.showMarginDialog('dialog/BleScanner.html', 0, 50, 'BleScanner', false, true)
//         }
//     }
// }, 3000)
//
// //暫停
// function sleep(milliseconds) {
//     var start = new Date().getTime();
//     while (1)
//         if ((new Date().getTime() - start) > milliseconds)
//             break;
// }