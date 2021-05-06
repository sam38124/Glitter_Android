function listLoading() {

}
class Dialog {

    constructor() {
        //RecycleView加載動畫
        this.listLoading=function (frag) {
            $('#' + frag).append(`<div id="loadingView" style="display:flex;flex-direction:column;align-items:center;justify-content:center;height: 100px; width: 100vw;text-align: center;vertical-align: middle;">
     <img src="../img/loading.gif"  background="transparent"  speed="1"  style="position:relative;width: 70px; height: 70px;" >
    <h1 style="font-size: 13px;color: gray;margin-top: 0;">請稍候...</h1>
</div>`)
        }
        //關閉動畫加載
        this.closeListLoading=function(){
            document.getElementById('loadingView').remove()
        }
    }
}