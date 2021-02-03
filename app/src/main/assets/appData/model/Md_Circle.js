
function getCircle(result) {
    var map = {}
    map['request'] = 'checkCircle'
    map['language'] = navigator.language
    glitter.publicBeans.postRequest(map, function (data) {
        var json = JSON.parse(data)
        result(json)
    }, function (data) {
        result('error')
    })
}