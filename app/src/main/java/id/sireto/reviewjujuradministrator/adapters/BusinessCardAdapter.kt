package id.sireto.reviewjujuradministrator.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.sireto.reviewjujur.utils.Constants
import id.sireto.reviewjujuradministrator.dashboard.BusinessDetailsActivity
import id.sireto.reviewjujuradministrator.dashboard.DashboardActivity
import id.sireto.reviewjujuradministrator.dashboard.UserDetailsActivity
import id.sireto.reviewjujuradministrator.databinding.BusinessCardBinding
import id.sireto.reviewjujuradministrator.models.BusinessResponse
import java.io.Serializable

class BusinessCardAdapter(private val activity: DashboardActivity) : RecyclerView.Adapter<BusinessCardAdapter.BusinessCardViewHolder>() {

    var businesses = arrayListOf<BusinessResponse>()

    inner class BusinessCardViewHolder(private val binding: BusinessCardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(businessResponse: BusinessResponse){
            binding.businessCardName.text = businessResponse.name
            binding.businessCardAddress.text = businessResponse.address
            binding.businessCardRating.text = if (businessResponse.reviewsCount == 0){
                "-"
            } else{
                "${(businessResponse.totalScore.toFloat()/businessResponse.reviewsCount.toFloat())} (${businessResponse.reviewsCount})"
            }

            Glide.with(binding.businessCardImage)
                .load(Constants.CDN + businessResponse.photo + ".png")
                .circleCrop()
                .into(binding.businessCardImage)

            binding.businessCard.setOnClickListener{
                activity.startActivity(Intent(activity, BusinessDetailsActivity::class.java).putExtra("business", businessResponse as Serializable))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessCardViewHolder =
        BusinessCardViewHolder(BusinessCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BusinessCardViewHolder, position: Int) {
        holder.bind(businesses[position])
    }

    override fun getItemCount(): Int = businesses.size
}