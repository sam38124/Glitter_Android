"use strict";
class Serialization {
    constructor() {
        //創建序列化資料庫
        this.create=function (name,success,error) {
            try {
                glitter.dataBase.exSql("Serialization",`
                        CREATE TABLE   IF NOT EXISTS \`${name}\` ( 
                       id   INTEGER PRIMARY KEY AUTOINCREMENT, 
                       name VARCHAR UNIQUE, 
                        data      VARCHAR
                    );`,success,error)
            }catch (e) {
                console.log(e)
                error()
            }
        }
        //儲存序列化檔案
            this.storeObject=function (obj,name,rout,success,error) {
            try {
                this.create(rout,function (data) {
                    glitter.dataBase.exSql("Serialization","insert or replace into `"+rout+"` (name,data) values"+"('"+name+"','"+JSON.stringify(obj)+"')",success,error)
                },error)
            }catch (e) {
                error()
            }
        }
        //取的序列化檔案
        this.getObject=function (name,rout,success,error) {
            try {
                let that=this
                glitter.dataBase.init(rout,function (){
                    that.create(rout,function (data) {
                        glitter.dataBase.query("Serialization",`select data from \`${rout}\` where name='${name}'`,function (data) {
                            try {
                                console.log("getObject"+data[0])
                                if(data.length===0){
                                    success(undefined)
                                }else {
                                    if(data[0].data!==undefined){
                                        success(JSON.parse(data[0].data))
                                    }else{
                                        success(undefined)
                                    }

                                }
                            }catch (e){
                                alert (e.stack)  // this will work on chrome, FF. will no not work on safari
                                alert (e.line)  // this will work on safari but not on IPhone
                                console.log("getObject:error-"+JSON.stringify(data))
                                success(undefined)
                            }
                        },error)
                    },error)
                },error)
            }catch (e) {
                console.log(e)
                error()
            }
        }
        //刪除序列化物件
        this.deleteObject=function(name,rout,success,error){
            try {
                glitter.dataBase.exSql("Serialization","delete from `"+rout+"` where name='"+name+"'",success,error)
            }catch (e) {
                error()
            }
        }
        //列出此路徑序列化物件
        this.listObject=function(rout,limit,success,error){
            try {
                this.create(rout,function (data) {
                    glitter.dataBase.query("Serialization","select * from `"+rout+((limit===0) ?"`":("` limit 0,"+limit)),success,error)
                },error)
            }catch (e) {
                error()
            }
        }
        //列出此路徑序列化物件
        this.deleteRout=function(rout,success,error){
            try {
                this.create(rout,function (data) {
                    glitter.dataBase.query("Serialization","DROP TABLE "+rout,success,error)
                },error)
            }catch (e) {
                error()
            }
        }
    }
}