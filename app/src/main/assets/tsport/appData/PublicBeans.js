"use strict";
class PublicBeans {
    constructor() {
        //api路徑
        this.apiRoot = 'http://192.168.43.219:80/Api';
        //資料請求標頭
        this.postRequest = function postRequest(map, success, error) {
            console.log('startRequest' + JSON.stringify(map))
            $.ajax({
                type: "POST",
                url: publicBeans.apiRoot,
                data: '' + JSON.stringify(map),
                timeout: 5000,
                success: function (data) {
                    console.log('repsonse' + data)
                    success(data)
                },
                error: function (data) {
                    console.log('error' + data)
                    error(data)
                }
            });
        }
    }
}

var publicBeans = new PublicBeans()

