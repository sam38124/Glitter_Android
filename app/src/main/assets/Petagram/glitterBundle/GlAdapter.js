"use strict";
class GlAdapter {
    constructor(e, size, viewHolder) {
        this.notifyDataSetChange = function () {
            e.innerHTML = ''
            var html = ''
            for (var a = 0; a < size(); a++) {
                html += viewHolder(a)
            }
            e.innerHTML = html
        }
        this.notifyDataSetChange()
    }
}