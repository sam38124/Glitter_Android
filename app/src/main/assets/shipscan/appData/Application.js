function onCreate() {
    //set appear type
    glitter.type = appearType.Web
    //set home page
    glitter.setHome('page/OrderListPage.html', 'OrderListPage', '{}')
    //set drawer
    // glitter.setNavigation('NavaGation.html', {})

}
"use strict";
class PublicBeans {
    constructor() {
        this.apiRoot = 'http://192.168.43.219';
    }
}
var publicBeans=new PublicBeans()


