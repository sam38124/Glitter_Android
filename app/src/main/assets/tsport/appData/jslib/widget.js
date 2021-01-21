function listLoading() {

}
class Dialog {

    constructor() {
        //RecycleView加載動畫
        this.listLoading=function (frag) {
            $('#' + frag).append("<div id=\"loadingView\" style=\"height: 100px; width: 100vw;text-align: center;vertical-align: middle;\">\n" +
                "    <lottie-player src=\"../lottieFile/loading.json\"  background=\"transparent\"  speed=\"1\"  style=\"position:relative;width: 50px; height: 50px;transform: translateX(-50%);left: 50%;\"  loop  autoplay></lottie-player>\n" +
                "    <br>\n" +
                "    <h1 style=\"font-size: 13px;color: gray;margin-top: -20px;\">請稍候...</h1>\n" +
                "</div>")
        }
        //關閉動畫加載
        this.closeListLoading=function(){
            document.getElementById('loadingView').remove()
        }
    }
}