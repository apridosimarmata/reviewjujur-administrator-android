package id.sireto.reviewjujuradministrator.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import id.sireto.reviewjujuradministrator.R
import id.sireto.reviewjujuradministrator.databinding.FragmentWhatsAppBinding
import id.sireto.reviewjujuradministrator.models.BaseResponse
import id.sireto.reviewjujuradministrator.models.Meta
import id.sireto.reviewjujuradministrator.services.api.ApiService
import id.sireto.reviewjujuradministrator.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WhatsAppFragment(private val apiService: ApiService, private val activity: LoginActivity) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentWhatsAppBinding
    private var response = BaseResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWhatsAppBinding.inflate(layoutInflater)
        setupListeners()
        setupValidators()
        return binding.root
    }

    private fun setupValidators(){
        binding.loginWhatsappNo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(p0.toString()){
                    if (this.length < 9){
                        binding.loginWhatsappNo.error = "Nomor terlalu pendek"
                    }else if (this.length > 14){
                        binding.loginWhatsappNo.error = "Nomor terlalu panjang"
                    }
                }
            }

        })
    }

    private fun setupListeners(){
        binding.loginSendCode.setOnClickListener{
            if(!binding.loginWhatsappNo.text.isNullOrEmpty() && binding.loginWhatsappNo.error.isNullOrEmpty()){
                sendVerificationCode()
            }
        }
    }

    private fun sendVerificationCode(){
        lifecycleScope.launch(Dispatchers.Main){
            val progress = ProgressDialog(requireContext())
            progress.setMessage("Mengirim kode ...")
            val requestCode = lifecycleScope.async {
                response = try{
                    apiService.requestVerificationCode(binding.loginWhatsappNo.text.toString()).body()!!
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            requestCode.await()
            progress.dismiss()

            if (response.meta.code == 200){
                UI.snackbarTop(binding.root, "Kode berhasil dikirim")
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.login_frameLayout, CodeVerificationFragment(apiService, binding.loginWhatsappNo.text.toString())).commit()
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.root)
            }
        }
    }
}