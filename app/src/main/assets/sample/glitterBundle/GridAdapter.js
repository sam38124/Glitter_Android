"use strict";
class GridAdapter{
    constructor(view,data,viewBinding) {
        this.layout=`grid-template-columns: repeat(2, 1fr);
        grid-auto-rows: auto;
        display: grid;
        width: 100%;
        overflow-x: hidden;
        grid-gap: 10px;`
        //所有資料
        this.dataList=data
        //View更新載入
        this.viewBinding = viewBinding
        //頁面更新
        this.updatePage=function (){

        }
        //偏移量
        this.offsetY=0
        //現在的位置
        this.currentPosition=0
        //通知資料變化
        this.notifyDataSetChange=function (){}
    }
}