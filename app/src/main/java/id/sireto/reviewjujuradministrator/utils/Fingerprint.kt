package id.sireto.reviewjujur.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.ContentResolver
import android.content.Context
import android.location.LocationManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigInteger
import java.security.MessageDigest

object Fingerprint {

    @SuppressLint("HardwareIds")
    fun getAndroidID(contentResolver: ContentResolver) : String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

    fun getExternalStorageCapacity(context: Context): Int {
        return try{
            if(Environment.getExternalStorageState() == Environment.MEDIA_REMOVED
                || Environment.getExternalStorageState() == Environment.MEDIA_UNKNOWN){
                0
            }else{
                var totalSpace = 0
                ContextCompat.getExternalFilesDirs(context, null).map {
                    totalSpace = it.totalSpace.toInt()
                }
                totalSpace
            }
        }catch (e : Exception){
            0
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getCurrentWallpaper(context: Context) : String {
        val wallpaperManager = WallpaperManager.getInstance(context)

        var wallpaperInfo = ""
        wallpaperManager.wallpaperInfo?.toString()?.let {
            wallpaperInfo = it
        }

        val md = MessageDigest.getInstance("MD5")

        return BigInteger(1, md.digest(wallpaperInfo.toByteArray())).toString(16).padStart(32, '0')
    }

    fun getRingtone(activity: Activity) : String {
        return RingtoneManager(activity).getRingtone(0).getTitle(activity.applicationContext) ?: "0"
    }

    fun getRingtoneList(activity: Activity) : List<String>{
        val manager = RingtoneManager(activity)
        manager.setType(RingtoneManager.TYPE_RINGTONE)
        val cursor = manager.cursor
        val ringtones = arrayListOf<String>()
        while(cursor.moveToNext()){
            ringtones.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX))
        }
        return ringtones
    }

    fun getKernelInformation() : String{
        val process: Process = Runtime.getRuntime().exec("uname -a")
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        return bufferedReader.readLines().toString()
    }

    fun getScreenTimeout(contentResolver: ContentResolver) : Int =
        Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)

    fun getInputMethods(activity: Activity) : List<String>{
        val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val inputMethods = arrayListOf<String>()
        manager.enabledInputMethodList.map {
            inputMethods.add(it.serviceName)
        }
        return inputMethods
    }

    fun getPasswordInputIsShown(contentResolver: ContentResolver) : String =
        Settings.System.getString(contentResolver, Settings.System.TEXT_SHOW_PASSWORD) ?: "0"

    fun getWifiSleepingPolicy(contentResolver: ContentResolver) : String{
        return Settings.Global.getString(contentResolver, Settings.Global.WIFI_SLEEP_POLICY)
    }

    fun getLocationProviders(activity: Activity) : List<String>{
        val locationManager = activity.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER).toString()
        val gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER).toString()
        return listOf(gpsProvider, networkProvider)
    }

}