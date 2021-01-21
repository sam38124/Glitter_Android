function onCreate() {
    glitter.addScript('../PublicBeans.js')
    var userData=glitter.getPro('Md_User')
    console.log(userData)
    if(userData!=='NA'&&userData!==undefined){
        glitter.setHome('page/Page_Home.html', 'Page_Home.html', '{}')
    }else{
        glitter.setHome('page/Page_Sign_In.html', 'Page_Sign_In', '{}')
    }
    //set drawer
    glitter.setNavigation('frag/NavaGation.html', {})

}

