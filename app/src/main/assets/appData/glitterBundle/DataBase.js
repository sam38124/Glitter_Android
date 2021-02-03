"use strict";

class DataBase {
    constructor() {
        this.exSql = function (dataBase, text, success, error) {
            switch (glitter.type) {
                case appearType.Android:
                    window.DataBase.exSql(dataBase, text)
                    success()
                    break
                case appearType.Ios:
                    var id = glitter.callBackId += 1
                    glitter.callBackList.set(id,function (result) {
                        if (result) {
                            success()
                        } else {
                            error()
                        }
                    })
                    var imap = {
                        dataBase: dataBase,
                        text: text,
                        callback:id
                    }
                    window.webkit.messageHandlers.exSql.postMessage(JSON.stringify(imap));
                    break
                case appearType.Web:
                    var map = {}
                    map.text = text
                    map.dataBase = dataBase
                    $.ajax({
                        type: "POST",
                        url: glitter.webUrl+'/DataBase/exSql',
                        timeout: 60000,
                        data: JSON.stringify(map), // serializes the form's elements.
                        success: function (data) {
                            console.log("success:" + data)
                            success(JSON.parse(data))
                        },
                        error: function (data) {
                            error()
                            console.log("error:" + data)
                        }
                    });
                    break
            }
        }
        this.query = function (dataBase, text, success, error) {
            switch (glitter.type){
                case appearType.Android:
                    try {
                        success(JSON.parse(window.DataBase.query(dataBase, text)))
                    } catch (e) {
                        error()
                    }
                    break
                case appearType.Web:
                    var map = {}
                    map.text = text
                    map.dataBase = dataBase
                    $.ajax({
                        type: "POST",
                        url: glitter.webUrl+'/DataBase/Query',
                        timeout: 60000,
                        data: JSON.stringify(map), // serializes the form's elements.
                        success: function (data) {
                            console.log("success:" + data)
                            success(JSON.parse(data))
                        },
                        error: function (data) {
                            error()
                            console.log("error:" + data)
                        }
                    });
                    break
                case appearType.Ios:
                    var id = glitter.callBackId += 1
                    glitter.callBackList.set(id,function (result) {
                        console.log("db_ios:"+result)
                        var data=JSON.parse(result)
                        console.log("db_iosData:"+data)
                        success(data)
                    })
                    var imap = {
                        dataBase: dataBase,
                        text: text,
                        callback:id
                    }
                    window.webkit.messageHandlers.query.postMessage(JSON.stringify(imap));
                    break
            }
        }
        this.initByFile = function (dataBase, rout, success, error) {
            switch (glitter.type){
                case appearType.Web:
                    var map = {}
                    map.rout = window.location.pathname.replace("/Glitter", "Glitter").replace("/glitterBundle/Application.html", "/") + rout
                    map.dataBase = dataBase
                    $.ajax({
                        type: "POST",
                        url: glitter.webUrl+'/DataBase/initByFile',
                        timeout: 60000,
                        data: JSON.stringify(map), // serializes the form's elements.
                        success: function (data) {
                            success()
                        },
                        error: function (data) {
                            error()
                            console.log("error:" + data)
                        }
                    });
                    break
                case appearType.Android:
                    try {
                        window.DataBase.initByFile(dataBase, rout)
                        success()
                    } catch (e) {
                        error()
                    }
                    break
                case appearType.Ios:
                    var id = glitter.callBackId += 1
                    glitter.callBackList.set(id,function (result) {
                        if (result) {
                            success()
                        } else {
                            error()
                        }
                    })
                    var imap = {
                        dataBase: dataBase,
                        rout: rout,
                        callback:id
                    }
                    window.webkit.messageHandlers.initByFile.postMessage(JSON.stringify(imap));
                    break
            }

        }
    }
}
