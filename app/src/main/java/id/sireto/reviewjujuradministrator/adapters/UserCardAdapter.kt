package id.sireto.reviewjujuradministrator.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.sireto.reviewjujuradministrator.dashboard.DashboardActivity
import id.sireto.reviewjujuradministrator.dashboard.UserDetailsActivity
import id.sireto.reviewjujuradministrator.databinding.UsersCardBinding
import id.sireto.reviewjujuradministrator.models.UserResponse
import java.io.Serializable

class UserCardAdapter(private val activity: DashboardActivity) : RecyclerView.Adapter<UserCardAdapter.UserCardViewHolder>() {

    var users = arrayListOf<UserResponse>()

    inner class UserCardViewHolder(private val binding: UsersCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userResponse: UserResponse) {
            binding.userCardName.text = userResponse.name
            binding.userCardEmail.text = userResponse.email
            binding.userCardWhatsappNo.text = userResponse.whatsappNo

            userResponse.unsuspendAt?.let {
                if (it.toInt() > System.currentTimeMillis()/1000) {
                    binding.userCardSuspended.visibility = View.VISIBLE
                }
            }

            binding.userCard.setOnClickListener {
                activity.startActivity(
                    Intent(
                        activity,
                        UserDetailsActivity::class.java
                    ).putExtra("user", userResponse as Serializable)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCardViewHolder =
        UserCardViewHolder(UsersCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: UserCardViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size
}