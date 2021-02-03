/*Read only*/
/*Please dont change*/
"use strict";

class GlBle {
    constructor() {
        //定位需求
        this.needGPS = function () {
            console.log("needGPS")
        }
        //onConnectFalse
        this.onConnectFalse = function () {
            console.log("onConnectFalse")
        }
        //onConnectSuccess
        this.onConnectSuccess = function () {
            console.log("onConnectSuccess")
        }
        //onConnecting
        this.onConnecting = function () {
            console.log("onConnecting")
        }
        //onDisconnect
        this.onDisconnect = function () {
            console.log("onDisconnect")
        }
        //When need permission
        this.requestPermission = function (array) {
            glitter.requestPermission(array)
            console.log("requestPermission--" + JSON.stringify(array))
        }
        /**
        * data {readUTF,readBytes,readHEX}
        * */
        this.rx = function (data) {
            console.log("rx--" + data.readHEX)
        }
        /**
        *data {readUTF,readBytes,readHEX}
        * */
        this.tx = function (data) {
            console.log("tx--" + data.readHEX)
        }
        /**
        *device {name,address}
        *scanRecord {readUTF,readBytes,readHEX}
        * */
        this.scanBack = function (device, scanRecord) {
            console.log("scanBack--deviceName:" + device.name + "--deviceAddress:" + device.address + "--scanRecord--" + scanRecord.readHEX)

        }
    }
}
// var id = glitter.callBackId += 1
// glitter.callBackList.set(id,function (result) {
//     console.log("db_ios:"+result)
//     success(JSON.parse(result))
// })
// var imap = {
//     dataBase: dataBase,
//     text: text,
//     callback:id
// }
// window.webkit.messageHandlers.query.postMessage(JSON.stringify(imap));
class BleControl {
    constructor() {
        //Start use ble
        this.start = function () {
            switch (glitter.type){
                case appearType.Android:
                    window.GL_ble.start()
                    break
                case appearType.Ios:
                    window.webkit.messageHandlers.start.postMessage('');
                    break
                case appearType.Web:
                    break
            }
        }
        //StartScan
        this.startScan = function () {
            switch (glitter.type){
                case appearType.Android:
                    window.GL_ble.startScan()
                    break
                case appearType.Ios:
                    window.webkit.messageHandlers.startScan.postMessage('');
                    break
                case appearType.Web:
                    break
            }

        }
        //StopScan
        this.stopScan = function () {
            switch (glitter.type){
                case appearType.Android:
                    window.GL_ble.stopScan()
                    break
                case appearType.Ios:
                    window.webkit.messageHandlers.stopScan.postMessage('');
                    break
                case appearType.Web:
                    break
            }
        }
        //writeHex
        this.writeHex = function (data, rxChannel, txChannel) {
            switch (glitter.type){
                case appearType.Android:
                    window.GL_ble.writeHex(data, rxChannel, txChannel)
                    break
                case appearType.Ios:
                    var imap = {
                        data: data,
                        txChannel: txChannel,
                        rxChannel:rxChannel
                    }
                    window.webkit.messageHandlers.writeHex.postMessage(JSON.stringify(imap));
                    break
                case appearType.Web:
                    break
            }

        }
        //writeUtf
        this.writeUtf = function (data, txChannel, rxChannel) {
            switch (glitter.type){
                case appearType.Android:
                    window.GL_ble.writeUtf(data, txChannel, rxChannel)
                    break
                case appearType.Ios:
                    var imap = {
                        data: data,
                        txChannel: txChannel,
                        rxChannel:rxChannel
                    }
                    window.webkit.messageHandlers.writeUtf.postMessage(JSON.stringify(imap));
                    break
                case appearType.Web:
                    break
            }
        }
        //writeBytes
        this.writeBytes = function (data, txChannel, rxChannel) {
            switch (glitter.type){
                case appearType.Android:
                    window.GL_ble.writeBytes(data, txChannel, rxChannel)
                    break
                case appearType.Ios:
                    var imap = {
                        data: data,
                        txChannel: txChannel,
                        rxChannel:rxChannel
                    }
                    window.webkit.messageHandlers.writeBytes.postMessage(JSON.stringify(imap));
                    break
                case appearType.Web:
                    break
            }
        }
        //isOpen
        this.isOPen=function (callback) {
            switch (glitter.type){
                case appearType.Android:
                    callback(window.GL_ble.isOpen())
                    break
                case appearType.Ios:
                    var id = glitter.callBackId += 1
                    glitter.callBackList.set(id,function (result) {
                        callback(result)
                    })
                    var imap = {
                        callback: id
                    }
                    window.webkit.messageHandlers.isOPen.postMessage(JSON.stringify(imap));
                    break
                case appearType.Web:
                    break
            }
        }
        /**
         * isDiscovering
         * If return false you need to open gps to scan ble
         * */
        this.gpsEnable=function () {
            switch (glitter.type){
                case appearType.Android:
                    return  window.GL_ble.gpsEnable()
                case appearType.Ios:
                   return  true
                case appearType.Web:
                    return  window.GL_ble.gpsEnable()
            }

        }
        /**
         * isDiscovering
         * You can use this function to check ble is on scanning or not
         * */
        this.isDiscovering=function (callback) {
            switch (glitter.type){
                case appearType.Android:
                    callback(window.GL_ble.isDiscovering())
                    break
                case appearType.Ios:
                    var id = glitter.callBackId += 1
                    glitter.callBackList.set(id,function (result) {
                        callback(result)
                    })
                    var imap = {
                        callback: id
                    }
                    window.webkit.messageHandlers.isDiscovering.postMessage(JSON.stringify(imap));
                    break
                case appearType.Web:
                    callback(window.GL_ble.isDiscovering())
                    break
            }
        }
        /**
        * ConnectId value
        * Android use address to connect
        * Ios use device name to connect
        * */
        this.connect=function (connectId,sec,callback){
            switch (glitter.type){
                case appearType.Android:
                    var id = glitter.callBackId += 1
                    glitter.callBackList.set(id,function (result) {
                        callback(result)
                    })
                    window.GL_ble.connect(connectId,sec,id)
                    break
                case appearType.Web:
                    break
                case appearType.Ios:
                    var id = glitter.callBackId += 1
                    glitter.callBackList.set(id,function (result) {
                        callback(result)
                    })
                    break
            }
        }
        /**
         * Disconnect
         * */
        this.disConnect=function (){
            switch (glitter.type){
                case appearType.Android:
                    window.GL_ble.disConnect()
                    break
                case appearType.Web:
                    break
                case appearType.Ios:

                    break
            }
        }
        /**
         * isPair
         * */
        this.isConnect=function (callback){
            switch (glitter.type){
                case appearType.Android:
                    callback( window.GL_ble.isConnect())
                    break
                case appearType.Web:
                    break
                case appearType.Ios:

                    break
            }
        }
    }
}

