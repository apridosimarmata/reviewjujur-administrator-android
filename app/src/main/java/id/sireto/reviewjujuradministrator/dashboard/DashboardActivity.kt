package id.sireto.reviewjujuradministrator.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import id.sireto.reviewjujuradministrator.R
import id.sireto.reviewjujuradministrator.databinding.ActivityDashboardBinding
import id.sireto.reviewjujuradministrator.services.api.ApiClient
import id.sireto.reviewjujuradministrator.services.api.ApiService
import retrofit2.Retrofit

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    private lateinit var apiService: ApiService
    private lateinit var retrofit: Retrofit

    companion object{
        val TAB_TITLES = arrayOf(
            "Users",
            "Businesses",
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)

        retrofit = ApiClient.getApiClient()
        apiService = retrofit.create(ApiService::class.java)
        val usersFragment = UsersFragment(apiService)
        val businessesFragment = BusinessesFragment(apiService)

        val tabLayout = binding.dashboardTabs
        val adapter = DashboardActivitySectionAdapter(this, usersFragment, businessesFragment)
        binding.dashboardPager.adapter = adapter

        TabLayoutMediator(tabLayout, binding.dashboardPager){ tab, position ->
            tab.text = TAB_TITLES[position]
        }.attach()
        changeTabsFont()

        setContentView(binding.root)
    }

    private fun changeTabsFont() {

        val typeface = ResourcesCompat.getFont(this, R.font.montserrat_bold)

        val vg = binding.dashboardTabs.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (i in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.typeface = typeface
                }
            }
        }
    }

}

class DashboardActivitySectionAdapter(
    activity : AppCompatActivity,
    private val usersFragment: UsersFragment,
    private val businessesFragment: BusinessesFragment) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> usersFragment
            1 -> businessesFragment
            else -> Fragment()
        }
    }
}