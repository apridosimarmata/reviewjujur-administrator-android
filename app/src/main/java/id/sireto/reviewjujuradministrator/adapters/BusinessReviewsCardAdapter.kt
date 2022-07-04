package id.sireto.reviewjujuradministrator.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.sireto.reviewjujuradministrator.databinding.BusinessReviewCardBinding
import id.sireto.reviewjujuradministrator.models.ReviewResponse
import java.text.SimpleDateFormat
import java.util.*

class BusinessReviewsCardAdapter : RecyclerView.Adapter<BusinessReviewsCardAdapter.BusinessReviewCardViewHolder>() {

    var reviews = arrayListOf<ReviewResponse>()

    inner class BusinessReviewCardViewHolder(private val binding: BusinessReviewCardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(reviewResponse: ReviewResponse){

            val timestamp: Long = reviewResponse.createdAt.toLong()
            val timeD = Date(timestamp * 1000)
            val sdf = SimpleDateFormat("EE dd/MM/yyyy HH:mm:ss")

            val time = sdf.format(timeD)

            binding.businessReviewCardCreatedAt.text = "$time"
            binding.businessReviewCardText.text = reviewResponse.text
            binding.businessReviewCardScore.text = reviewResponse.score.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BusinessReviewCardViewHolder =
        BusinessReviewCardViewHolder(BusinessReviewCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BusinessReviewCardViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size
}