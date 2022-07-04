package id.sireto.reviewjujuradministrator.adapters

import android.annotation.SuppressLint
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujuradministrator.databinding.ReviewCardBinding
import id.sireto.reviewjujuradministrator.models.BaseResponse
import id.sireto.reviewjujuradministrator.models.BusinessResponse
import id.sireto.reviewjujuradministrator.models.Meta
import id.sireto.reviewjujuradministrator.models.ReviewResponse
import id.sireto.reviewjujuradministrator.services.api.ApiService
import id.sireto.reviewjujuradministrator.utils.Converter
import id.sireto.reviewjujuradministrator.utils.UI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ReviewCardAdapter(
    private val apiService: ApiService,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope
    ) : RecyclerView.Adapter<ReviewCardAdapter.ReviewCardViewHolder>() {

    var reviews = arrayListOf<ReviewResponse>()

    inner class ReviewCardViewHolder(private val binding: ReviewCardBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SimpleDateFormat")
        fun bind(reviewResponse: ReviewResponse){
            val timestamp: Long = reviewResponse.createdAt.toInt().toLong()
            val timeD = Date(timestamp * 1000)
            val sdf = SimpleDateFormat("EE dd/MM/yyyy HH:mm:ss")

            val time = sdf.format(timeD)

            var business = BusinessResponse()
            var response = BaseResponse()

            lifecycleCoroutineScope.launch(Dispatchers.Main){
                val getBusiness = lifecycleCoroutineScope.async {
                    response = try {
                        apiService.getBusinessByUid(reviewResponse.businessUid).body()!!
                    }catch (e: Exception){
                        BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                    }
                }

                getBusiness.await()

                if (response.meta.code == 200){
                    business = Converter.anyToBusinessResponse(response.result as LinkedTreeMap<*, *>)
                }else{
                    UI.showSnackbarByResponseCode(response.meta, binding.reviewCardBusinessImage)
                }

                Glide.with(binding.reviewCardBusinessImage)
                    .load(Constants.CDN + business.photo + ".png")
                    .circleCrop()
                    .into(binding.reviewCardBusinessImage)
                binding.reviewCardBusinessName.text = Html.fromHtml("<u>${business.name}</u>")
            }

            binding.reviewCardCreatedAt.text = time
            binding.reviewCardStatus.text = reviewResponse.status
            binding.reviewCardText.text = reviewResponse.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewCardViewHolder =
        ReviewCardViewHolder(ReviewCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ReviewCardViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size
}