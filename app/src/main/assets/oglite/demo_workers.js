
var i = 0;
var a = 10;


function timedCount() {
    i = i + 1;
    postMessage(i);
    setTimeout("timedCount()",500);
}

function timedCount2() {
    a = a + 1;
    postMessage(a);
    setTimeout("timedCount2()",500);
}
timedCount();
timedCount2();