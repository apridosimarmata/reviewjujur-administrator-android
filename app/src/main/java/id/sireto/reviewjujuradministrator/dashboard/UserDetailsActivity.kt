package id.sireto.reviewjujuradministrator.dashboard

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujuradministrator.adapters.ReviewCardAdapter
import id.sireto.reviewjujuradministrator.databinding.ActivityUserDetailsBinding
import id.sireto.reviewjujuradministrator.login.LoginActivity
import id.sireto.reviewjujuradministrator.models.BaseResponse
import id.sireto.reviewjujuradministrator.models.Meta
import id.sireto.reviewjujuradministrator.models.SuspendUserRequest
import id.sireto.reviewjujuradministrator.models.UserResponse
import id.sireto.reviewjujuradministrator.services.api.ApiClient
import id.sireto.reviewjujuradministrator.services.api.ApiService
import id.sireto.reviewjujuradministrator.utils.Auth
import id.sireto.reviewjujuradministrator.utils.Converter
import id.sireto.reviewjujuradministrator.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.lang.Exception

class UserDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var reviewsRecyclerView : RecyclerView
    private lateinit var reviewsAdapter : ReviewCardAdapter
    private var response = BaseResponse()
    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit
    private lateinit var user: UserResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)

        user = intent.getSerializableExtra("user") as UserResponse

        binding.userDetailsName.text = user.name

        getUserReviews("0")

        user.unsuspendAt?.let {
            if (it.toInt() < System.currentTimeMillis()/1000) {
                binding.userDetailsSuspend.visibility = View.VISIBLE
            }
        }

        setupUserReviewsRecyclerView()
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners(){
        binding.userDetailsBack.setOnClickListener{
            onBackPressed()
        }

        binding.userDetailsLoadMoreReviews.setOnClickListener{
            try {
                reviewsAdapter.reviews.last().createdAt.let {
                    getUserReviews(it)
                }
            }catch (e : Exception){

            }
        }

        binding.userDetailsSuspend.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Suspend pengguna ini?")
                .setCancelable(false)
                .setPositiveButton("Ya") { dialog, _ ->
                    suspendUser()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun suspendUser(){
        val token = Auth.getToken(this)
        lifecycleScope.launch(Dispatchers.Main){

            val progress = ProgressDialog(this@UserDetailsActivity)
            progress.setMessage("Tunggu sebentar ...")
            progress.show()

            val suspendUser = lifecycleScope.async {
                response = try {
                    apiService.suspendUser(token!!, SuspendUserRequest(user.uid)).body()!!
                } catch (e: Exception){
                    Log.d("eeee", e.toString())
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            suspendUser.await()

            if (response.meta.code == 200){
                UI.snackbarTop(binding.userDetailsLoadMoreReviews, "${user.name} telah di-suspend untuk 1 hari")
                binding.userDetailsSuspend.visibility = View.INVISIBLE
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.userDetailsLoadMoreReviews)
            }

            progress.dismiss()
        }
    }

    private fun getUserReviews(createdAt : String){
        val accessToken = Auth.getToken(this)
        lifecycleScope.launch(Dispatchers.Main){
            val getReviews = lifecycleScope.async {
                response = try {
                    apiService.getUserReviews(user.uid ,accessToken!!, createdAt).body()!!
                }catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            getReviews.await()

            if (response.meta.code == 200){
                (response.result as ArrayList<*>).map {
                    reviewsAdapter.reviews.add(Converter.anyToReviewResponse(it as LinkedTreeMap<*, *>))
                }
                if((response.result as ArrayList<*>).size == 0){
                    UI.snackbarTop(binding.userDetailsLoadMoreReviews, "Tidak ada review")
                }
                reviewsAdapter.notifyDataSetChanged()
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.userDetailsLoadMoreReviews)
            }
        }
    }

    private fun setupUserReviewsRecyclerView(){
        reviewsRecyclerView = binding.userReviewsRecylcerview
        reviewsAdapter = ReviewCardAdapter(apiService, lifecycleScope)
        reviewsRecyclerView.adapter = reviewsAdapter
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        reviewsRecyclerView.layoutManager = layoutManager
    }
}