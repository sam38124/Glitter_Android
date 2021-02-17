[![](https://jitpack.io/v/sam38124/Glitter_Android.svg)](https://jitpack.io/#sam38124/Glitter_Android)
[![Platform](https://img.shields.io/badge/platform-%20Android%20-brightgreen.svg)](https://github.com/sam38124)
[![characteristic](https://img.shields.io/badge/特點-%20輕量級%20%7C%20簡單易用%20%20%7C%20穩定%20-brightgreen.svg)](https://github.com/sam38124)
# Glitter
 This is the h5 hybrid development framework, which allows you to achieve cross-platform development of android, ios, and web, which allows you to greatly shorten the development time and retain the scalability of native development~

## Create by
<p align="center"><img width = "500"  src="https://github.com/sam38124/JzFrameWork/blob/master/App%20icon/squarestudio.png?raw=tru"><a name="Use"></a></p>

## List
* [Configuration](#Import)
* [Quick Start](#Use)
* [Add your own script bridge](#All)
* [About me](#About)

<a name="Import"></a>
##  Configuration

### Android


> Support jcenter。 <br/>

   1.Add  into your build.gradle 
```kotlin
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

   2.Add into your dependencies

```kotlin
dependencies {
implementation 'com.github.sam38124:Glitter_Android:version'}
```

  3.Display the glitter page use by fragment

```kotlin
GlitterPage(PublicBeans.fileRoot+"MainActivity.html","MainActivity"),"MainActivity")
//is Fragment
```

### IOS

> Support swiftPackage <br/>

### Web

```javascript
 <script src="Glitter.js"></script> 
```

<a name="Use"></a>
## Quick Start

  1.Set up your glitter root page like below (necessary)
```html
<!DOCTYPE html>
<meta name="viewport" content="width=device-width">
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<style>
    body{
        margin: 0;
        padding: 0;
        overflow-y: hidden;
        width: 100vw;
        height: 100vh;
        overflow-x: hidden;
        white-space: nowrap;
    }
    iframe{
        width: 100vw;
        height: 100vh;
        border-width: 0;
        padding: 0;
        margin: 0;
        white-space: nowrap;
        position: fixed;
    }
    html{
        white-space: nowrap;
        margin: 0;
        padding: 0;
    }
</style>
<script src="Glitter.js"></script>
<script src="jslib/jquery.js"></script>
<body>

</body>
</html>

<script>
    //set appear type
    glitter.type=appearType.Web
    //Set up your first page
    glitter.setHome('Page/Page_Sign_In.html','Page_Sign_In','{}')
</script>
```

<a name="All"></a>
## Add your own script bridge
Android
```kotlin
GlitterPage.addJsInterFace(arrayOf(JsInterFace(sampleClass(),"tagname"))
```
<a name="About"></a>
# About me
#### <font color="#0000dd"> Work for: </font><br /> 
+ ##### <font color="#660000">【Orange Electronic】橙的電子-Deputy Head of R&D </font><br /> 
+ ##### <font color="#660000">【Square Studio】四方工作室-CEO </font><br />
#### <font color="#0000dd"> Main skill: </font><br /> 
+ ##### Full stack development(Kotlin,Java,Swift,Javascript,Objective-C,C#)
+ ##### Android and IOS(4 years)<br/>  
+ ##### Jsp(2 years)<br/> 
+ ##### Javascript and Jquery and Ktor(1 years)<br /> 
#### <font color="#0000dd"> Contact information: </font><br /> 
+  ##### line:sam38124<br /> 

+  ##### gmail:sam38124@gmail.com
