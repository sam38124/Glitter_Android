"use strict";

class Md_Message {
    constructor(account, content) {
        //帳號
        this.account = account
        //內容
        this.content = content
    }
}

class Md_Reply_Message_InterFace {
    constructor() {
        //回覆訊息
        this.replyMessage = function (dat, result) {
            var map = {}
            map['request'] = 'insertMessage'
            map['language'] = navigator.language
            map['id'] = gBundle.id
            map['data'] = JSON.stringify(dat)
            glitter.publicBeans.postRequest(map, function (data) {
                var json = JSON.parse(data)
                result(json.result)
            }, function (data) {
                result('error')
            })
        }

        //按讚
        this.postLike=function (id,table, result) {
            var map = {}
            map['request'] = 'postRLike'
            map['language'] = navigator.language
            map['id'] = id
            map['table']=table
            map['account'] =  glitter.md_user.account
            glitter.publicBeans.postRequest(map, function (data) {
                var json = JSON.parse(data)
                result(json)
            }, function (data) {
                result('error')
            })
        }

        //取得訊息
        this.getMessage=function (id, result) {
            var map = {}
            map['request'] = 'getMessage'
            map['language'] = navigator.language
            map['table'] = id
            map['id'] = "-1"
            glitter.publicBeans.postRequest(map, function (data) {
                result(JSON.parse(data))
            }, function (data) {
                result('error')
            })
        }
    }
}
var messageInterFace=new Md_Reply_Message_InterFace()
