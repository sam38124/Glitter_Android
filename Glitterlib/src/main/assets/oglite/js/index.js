// function sleep(ms) {return new Promise(resolve => setTimeout(resolve, ms));}
// async function waitChange() {
//     while (!BleHelper.isConnected()) {
//         await sleep(1000)
//         Glitter.logE('run', 'run')
//     }
// }
let admin=Glitter.getActivityData()['admin']

JzFragment.viewInit = function () {
    Glitter.logE('adminData',''+admin)
    Glitter.logE('JzFragment', 'viewInit')
}
JzFragment.onDestroy = function () {
    Glitter.logE('JzFragment', 'onDestroy')
}
JzFragment.onResume = function () {
    Glitter.logE('JzFragment', 'onResume')
}
JzFragment.onPause = function () {
    Glitter.logE('JzFragment', 'onPause')
}