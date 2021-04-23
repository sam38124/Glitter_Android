package com.jianzhi.glitter

class JavaScriptInterFace(val functionName:String,val function:(request:RequestFunction) -> Unit){
    init {
        GlitterActivity.addJavacScriptInterFace(this)
    }
}

data class RequestFunction(val receiveValue:MutableMap<String,Any>, var responseValue:MutableMap<String,Any> = mutableMapOf(),var finish:() -> Unit = {})