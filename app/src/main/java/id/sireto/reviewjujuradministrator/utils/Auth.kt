package id.sireto.reviewjujuradministrator.utils

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujuradministrator.models.BaseResponse
import id.sireto.reviewjujuradministrator.models.Meta
import id.sireto.reviewjujuradministrator.services.api.ApiClient
import id.sireto.reviewjujuradministrator.services.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

object Auth {

    private var retrofit = ApiClient.getApiClient()
    private var apiService = retrofit.create(ApiService::class.java)
    private var response = BaseResponse()

    fun saveToken(token : String){
        SharedPref.saveToStringSharedPref(Constants.KEY_TOKEN, token)
    }

    fun removeToken(){
        SharedPref.removeFromSharedPref(Constants.KEY_TOKEN)
    }

    suspend fun authUser(lifecycleCoroutineScope: LifecycleCoroutineScope, context: Context, callback : (result : Boolean) -> Unit) {
        val token = SharedPref.getStringFromSharedPref(Constants.KEY_TOKEN)
        lifecycleCoroutineScope.launch(Dispatchers.IO){
            val auth = lifecycleCoroutineScope.async {
                response = try {
                    //apiService.authorizeUser(token!!, refreshToken!!).body()!!
                    BaseResponse(Meta(code = 0, message = "Error : "), null)
                }catch (e : Exception) {
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }
            auth.await()
            when(response.meta.code){
                200 -> callback(true)
                410 -> {
                //
                }
                else -> callback(false)
            }
        }
    }


    fun getToken(context: Context) : String? {
        return SharedPref.getStringFromSharedPref(Constants.KEY_TOKEN)
    }

    fun getRefreshToken(context: Context) : String? {
        return SharedPref.getStringFromSharedPref(Constants.KEY_REFRESH_TOKEN)
    }
}