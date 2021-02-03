"use strict";
class Serialization {
    constructor() {
        //創建序列化資料庫
        this.create=function (name,success,error) {
            try {
                glitter.dataBase.exSql("Glitter","CREATE TABLE   IF NOT EXISTS `"+name+"` (\n" +
                    "    id   INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name VARCHAR UNIQUE,\n" +
                    "    data      VARCHAR\n" +
                    ");\n",success,error)
            }catch (e) {
                error()
            }
        }
        //儲存序列化檔案
            this.storeObject=function (obj,name,rout,success,error) {
            try {
                this.create(rout,function (data) {
                    glitter.dataBase.exSql("Glitter","insert or replace into `"+rout+"` (name,data) values"+"('"+name+"','"+JSON.stringify(obj)+"')",success,error)
                },error)
            }catch (e) {
                error()
            }
        }
        //取的序列化檔案
        this.getObject=function (name,rout,success,error) {
            try {
                this.create(rout,function (data) {
                    glitter.dataBase.query("Glitter","select data from `"+rout+"` where name='"+name+"'",function (data) {
                        console.log('getObject--'+data)
                     success(JSON.parse(data[0].data))
                    },error)
                },error)
            }catch (e) {
                error()
            }
        }
        //刪除序列化物件
        this.deleteObject=function(name,rout,success,error){
            try {
                glitter.dataBase.exSql("delete from `"+rout+"` where name='"+name+"'",success,error)
            }catch (e) {
                error()
            }
        }
        //列出此路徑序列化物件
        this.listObject=function(rout,limit,success,error){
            try {
                this.create(rout,function (data) {
                    glitter.dataBase.query("Glitter","select * from `"+rout+((limit===0) ?"`":("` limit 0,"+limit)),success,error)
                },error)
            }catch (e) {
                error()
            }
        }
        //列出此路徑序列化物件
        this.deleteRout=function(rout,success,error){
            try {
                this.create(rout,function (data) {
                    glitter.dataBase.query("Glitter","DROP TABLE "+rout,success,error)
                },error)
            }catch (e) {
                error()
            }
        }
    }
}