package com.jianzhi.glitter

class JavaScriptInterFace(val functionName: String, val function: (request: RequestFunction) -> Unit) {
    init {
        GlitterActivity.addJavacScriptInterFace(this)
    }
}

class RequestFunction(
    val receiveValue: MutableMap<String, Any>,
    var responseValue: MutableMap<String, Any> = mutableMapOf(),
    var fin: () -> Unit = {},
    var calb:() -> Unit = {},
    var callBackID:Int = 0
) {
    //運行結束時的回調
    fun finish() {
        fin()
    }
    //運行CallBack的回調
     fun callBack(){
        calb()
     }
}