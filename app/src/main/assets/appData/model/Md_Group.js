"use strict";

class Md_Group {
    constructor(map) {
        this.account = map.account
        this.pick = map.pick
        this.content = map.content
        this.title = map.title
        this.head = map.head
        this.lan = map.lan
        this.lon = map.lon
        this.startTime = map.startTime
        this.endTime = map.endTime
        this.joinCount = map.joinCount
        this.limit = map.limit
        this.interested = map.interested
        this.join = map.join
        this.circle = map.circle
    }
}

function getGroupData(result) {
    var map = {}
    map['request'] = 'getGroupData'
    map['language'] = navigator.language
    glitter.publicBeans.postRequest(map, function (data) {
        var json = JSON.parse(data)
        json = JSON.parse(json["data"])
        result(json)
    }, function (data) {
        result('error')
    })

}