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
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujuradministrator.utils.Converter
import id.sireto.reviewjujuradministrator.dashboard.DashboardActivity
import id.sireto.reviewjujuradministrator.databinding.FragmentCodeVerificationBinding
import id.sireto.reviewjujuradministrator.models.BaseResponse
import id.sireto.reviewjujuradministrator.models.CodeVerificationRequest
import id.sireto.reviewjujuradministrator.models.Meta
import id.sireto.reviewjujuradministrator.services.api.ApiService
import id.sireto.reviewjujuradministrator.utils.Auth
import id.sireto.reviewjujuradministrator.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CodeVerificationFragment(private val apiService: ApiService, private val whatsappNo : String) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCodeVerificationBinding
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
        binding = FragmentCodeVerificationBinding.inflate(layoutInflater)
        setupValidators()
        setupListeners()
        return binding.root
    }

    private fun setupListeners(){
        binding.verifySubmit.setOnClickListener{
            if(binding.verifyCode.error.isNullOrEmpty()
                && !binding.verifyCode.text.toString().isNullOrEmpty()) {
                verifyCode()
            }
        }
    }

    private fun setupValidators(){
        binding.verifyCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                with(p0.toString()){
                    if (this.length != 6) {
                        binding.verifyCode.error = "Kode harus 6 digit"
                    }
                }
            }
        })
    }

    private fun verifyCode(){
        lifecycleScope.launch(Dispatchers.Main){
            val progress = ProgressDialog(requireContext())
            progress.setMessage("Tunggu sebentar ...")
            progress.show()

            val verifyCode = lifecycleScope.async {
                response = try {
                    apiService.verifyWhatsApp(
                        CodeVerificationRequest(
                            whatsappNo,
                            binding.verifyCode.text.toString()
                        )
                    ).body()!!
                }catch (e: Exception) {
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            verifyCode.await()

            if (response.meta.code == 200){
                response.meta.message?.let { UI.snackbar(binding.verifyCode, it) }
                val authenticationResponse = Converter.anyToAthenticationResponse(response.result as LinkedTreeMap<String, Any>)
                Auth.saveToken(authenticationResponse.token)
                requireActivity().finish()
                startActivity(Intent(requireContext(), DashboardActivity::class.java))
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.root)
            }
            progress.dismiss()
        }
    }
}