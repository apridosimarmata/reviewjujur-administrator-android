package id.sireto.reviewjujuradministrator.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import id.sireto.reviewjujuradministrator.R
import id.sireto.reviewjujuradministrator.databinding.ActivityLoginBinding
import id.sireto.reviewjujuradministrator.services.api.ApiClient
import id.sireto.reviewjujuradministrator.services.api.ApiService
import retrofit2.Retrofit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit

    private lateinit var whatsAppFragment: WhatsAppFragment
    private lateinit var codeVerificationFragment: CodeVerificationFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        whatsAppFragment = WhatsAppFragment(apiService, this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.login_frameLayout, whatsAppFragment)
            .commit()

        setContentView(binding.root)
    }
}