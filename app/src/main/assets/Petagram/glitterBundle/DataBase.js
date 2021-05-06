"use strict";
class DataBase {
    constructor() {
        this.exSql = function (dataBase, text, success, error) {
            switch (glitter.deviceType) {
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
                    $.support.cors = true;
                    var map = {}
                    map.text =text
                    map.dataBase = dataBase
                    $.ajax({
                        url: glitter.webUrl+'/DataBase/exSql',
                        timeout: 60000,
                        contentType: "application/text; charset=utf-8;",
                        type: "post",
                        dataType: "text",
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
            switch (glitter.deviceType){
                case appearType.Android:
                    try {
                        var response=window.DataBase.query(dataBase, text)
                       // console.log(response)
                        success(JSON.parse(response))
                    } catch (e) {
                        alert (e.stack)  // this will work on chrome, FF. will no not work on safari
                        alert (e.line)  // this will work on safari but not on IPhone
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
                            //console.log("success:" + data)
                            success(JSON.parse(data))
                        },
                        error: function (data) {
                            console.log(e)
                            error()
                            //console.log("error:" + data)
                        }
                    });
                    break
                case appearType.Ios:
                    var id = glitter.callBackId += 1
                    glitter.callBackList.set(id,function (result) {
                        //console.log("db_ios:"+result)
                        //console.log("db_iosData:"+data)
                        success((result))
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
            switch (glitter.deviceType){
                case appearType.Web:
                    var map = {}
                    map.rout = window.location.pathname.replace("/Glitter", "Glitter").replace("/glitterBundle/home.html", "/") + rout
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
                            //console.log("error:" + data)
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
        this.initByLocalFile = function (dataBase, rout, success, error) {
            switch (glitter.deviceType){
                case appearType.Web:
                    var map = {}
                    map.rout =  rout
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
                            //console.log("error:" + data)
                        }
                    });
                    break
                case appearType.Android:
                    try {
                        window.DataBase.initByLocalFile(dataBase, rout)
                        success()
                    } catch (e) {
                        alert (e.stack)  // this will work on chrome, FF. will no not work on safari
                        alert (e.line)  // this will work on safari but not on IPhone
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
                    window.webkit.messageHandlers.initByLocalFile.postMessage(JSON.stringify(imap));
                    break
            }

        }
        this.init = function (rout, success, error) {
            switch (glitter.deviceType){
                case appearType.Web:
                    var map = {}
                    map.rout = window.location.pathname.replace("/Glitter", "Glitter").replace("/glitterBundle/home.html", "/") + rout
                    map.dataBase = "Glitter"
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
                            //console.log("error:" + data)
                        }
                    });
                    break
                case appearType.Android:
                    try {
                        window.DataBase.init(rout)
                        success()
                    } catch (e) {
                        alert(e)
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
                        dataBase: rout,
                        callback:id
                    }
                    window.webkit.messageHandlers.initDatabase.postMessage(JSON.stringify(imap));
                    break
            }

        }
    }
}