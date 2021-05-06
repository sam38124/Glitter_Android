'use strict';
class ViewPager{
    constructor() {
        this.elememtID=''
        this.setUp=function (id){
            this.elememtID=id
            scrollElement=$('#'+id).get(0)
            $('#'+id).scroll(function () {
                scrollListener()
            });
            viewPagerWidth = $('#'+id).width()
        }
        this.addPage=function (item){
                $('#'+this.elememtID).append(`
    <div style="display: inline-block;margin: 0;">
    ${item}
    </div>`)
        }
        this.scrollToPage=function (position){
            scrollElement.scrollTo({left:(position*viewPagerWidth),behavior: 'smooth'})
            this.scrollListener(position)
        }
        this.scrollListener=function (position){

        }
    }
}

var viewPager=new ViewPager()

let viewPagerWidth=0
let scrollElement=undefined
let t1 = 0;
let t2 = 0;
let timer = null; // 定时器
function scrollListener() {
    clearTimeout(timer);
    timer = setTimeout(isScrollEnd, 300);
    t1 = scrollElement.scrollLeft;
}

function isScrollEnd() {
    t2 = scrollElement.scrollLeft;
    if(t2 === t1){
        var scrollPosition=Math.round(scrollElement.scrollLeft/viewPagerWidth)
        scrollElement.scrollTo({left:(scrollPosition*viewPagerWidth),behavior: 'smooth'})
        viewPager.scrollListener(scrollPosition)
    }
}