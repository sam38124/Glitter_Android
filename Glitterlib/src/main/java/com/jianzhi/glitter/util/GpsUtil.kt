//package com.jianzhi.glitter.util
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Geocoder
//import android.location.Location
//import android.location.LocationListener
//import android.location.LocationManager
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.jianzhi.glitter.GlitterActivity
//
//class GpsUtil(var activity: GlitterActivity) {
//    var mLocationManager: LocationManager = activity
//        .getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//    //取得定位位置
//    var lastKnownLocation: Location? = null
//        get() {
//            for (permission in arrayOf(
//                android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            )) {
//                val permissionCheck =
//                    ContextCompat.checkSelfPermission(activity, permission)
//                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                    return null
//                }
//            }
//            try {
//                if (clock.stop() > 5) {
//                    //TaskHandler.newInstance.runTaskDelay("lastKnownLocation", 5.0, runner {
//                    mLocationManager.requestLocationUpdates(
//                        "network",
//                        0.toLong(),
//                        0.toFloat(),
//                        object : LocationListener {
//                            override fun onLocationChanged(location: Location) {
//                                lastKnownLocation = location
//                            }
//
//                            override fun onStatusChanged(
//                                provider: String?,
//                                status: Int,
//                                extras: Bundle?
//                            ) {
//                            }
//
//                            override fun onProviderEnabled(provider: String) {
//                            }
//
//                            override fun onProviderDisabled(provider: String) {
//                            }
//                        });
//                    clock.Zeroing()
//                }
//                //})
//            } catch (e: Exception) {
//                Log.e("lastLocation", e.toString())
//            }
//            return mLocationManager.getLastKnownLocation("network")
//        }
//
//
//    //取得地址
//    val address: String
//        get() {
//            try {
//                val geocoder: Geocoder = Geocoder(activity)
//                val addresses = geocoder.getFromLocation(
//                    lastKnownLocation!!.latitude,
//                    lastKnownLocation!!.longitude,
//                    1
//                );
//                if (addresses != null && addresses.size > 0) {
//                    val address = addresses[0];
//                    val addressText = String.format(
//                        "%s-%s%s%s%s",
//                        address.countryName, //國家
//                        address.adminArea, //城市
//                        address.locality, //區
//                        address.thoroughfare, //路
//                        address.subThoroughfare //巷號
//                    ).replace("null", "");
//                    return addressText
//                } else {
//                    return "Unknown Address"
//                }
//            } catch (e: Exception) {
//                return "Unknown Address"
//            }
//        }
//    /**
//     * 定位判斷並回傳
//     * */
//    fun haveLocation(){
//        if(!isOpenGps()){activity.webRoot.evaluateJavascript("glitter.gpsUtil.callback.notOpen()",null)}
//        for (permission in arrayOf(
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//            android.Manifest.permission.ACCESS_FINE_LOCATION
//        )) {
//            val permissionCheck =
//                ContextCompat.checkSelfPermission(activity, permission)
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(activity, arrayOf(
//                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION
//                ), 1022)
//                activity.webRoot.evaluateJavascript("glitter.gpsUtil.callback.denied()",null)
//                return
//            }
//        }
//        activity.webRoot.evaluateJavascript("glitter.gpsUtil.callback.grant()",null)
//    }
//    /**
//     * 判斷GPS是否開啟，GPS或者AGPS開啟一個就認為是開啟的
//     */
//    fun isOpenGps(): Boolean {
//        val locationManager = mLocationManager
//        // 通過GPS衛星定位，定位級別可以精確到街（通過24顆衛星定位，在室外和空曠的地方定位準確、速度快）
//        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        // 通過WLAN或移動網路(3G/2G)確定的位置（也稱作AGPS，輔助GPS定位。主要用於在室內或遮蓋物（建築群或茂密的深林等）密集的地方定位）
//        val network =
//            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        return gps || network
//    }
//
//    /**
//     * 強制幫用戶打開GPS
//     * @param context
//     */
//    fun openGPS(context: Context?) {
//        val GPSIntent = Intent()
//        GPSIntent.setClassName(
//            "com.android.settings",
//            "com.android.settings.widget.SettingsAppWidgetProvider"
//        )
//        GPSIntent.addCategory("android.intent.category.ALTERNATIVE")
//        GPSIntent.data = Uri.parse("custom:3")
//        try {
//            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send()
//        } catch (e: PendingIntent.CanceledException) {
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * 強制關閉GPS
//     * @param context
//     */
//    @SuppressLint("WrongConstant")
//    fun closeGPS(context: Context?) {
//        val GPSIntent = Intent()
//        GPSIntent.setClassName(
//            "com.android.settings",
//            "com.android.settings.widget.SettingsAppWidgetProvider"
//        )
//        GPSIntent.addCategory("android.intent.category.ALTERNATIVE")
//        GPSIntent.data = Uri.parse("custom:3")
//        try {
//            PendingIntent.getBroadcast(context, 0, GPSIntent, 1).send()
//        } catch (e: PendingIntent.CanceledException) {
//            e.printStackTrace()
//        }
//    }
//}