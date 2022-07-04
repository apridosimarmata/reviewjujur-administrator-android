package id.sireto.reviewjujuradministrator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import id.sireto.reviewjujuradministrator.dashboard.DashboardActivity
import id.sireto.reviewjujuradministrator.databinding.ActivityMainBinding
import id.sireto.reviewjujuradministrator.login.LoginActivity
import id.sireto.reviewjujuradministrator.models.BaseResponse
import id.sireto.reviewjujuradministrator.models.Meta
import id.sireto.reviewjujuradministrator.services.api.ApiClient
import id.sireto.reviewjujuradministrator.services.api.ApiService
import id.sireto.reviewjujuradministrator.utils.Auth
import id.sireto.reviewjujuradministrator.utils.SharedPref
import id.sireto.reviewjujuradministrator.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var response = BaseResponse()
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPref.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        binding = ActivityMainBinding.inflate(layoutInflater)

        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        setContentView(binding.root)
        authorize()
    }

    private fun authorize(){
        lifecycleScope.launch(Dispatchers.Main) {
            val token = Auth.getToken(this@MainActivity)
            val authorize = lifecycleScope.async {
                response = try {
                    apiService.authorizeAdministrator(token!!).body()!!
                }catch (e : Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            authorize.await()
            
            if (response.meta.code == 200){
                val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                startActivity(intent)
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.splashLogo)
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            authorize.await()
        }
    }
}