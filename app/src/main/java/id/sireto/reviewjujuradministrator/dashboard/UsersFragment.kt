package id.sireto.reviewjujuradministrator.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujuradministrator.adapters.UserCardAdapter
import id.sireto.reviewjujuradministrator.utils.Converter
import id.sireto.reviewjujuradministrator.databinding.FragmentUsersBinding
import id.sireto.reviewjujuradministrator.models.BaseResponse
import id.sireto.reviewjujuradministrator.models.Meta
import id.sireto.reviewjujuradministrator.models.UserResponse
import id.sireto.reviewjujuradministrator.services.api.ApiService
import id.sireto.reviewjujuradministrator.utils.Auth
import id.sireto.reviewjujuradministrator.utils.UI
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UsersFragment(private val apiService: ApiService) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentUsersBinding
    private lateinit var query: Observable<String>
    private var response = BaseResponse()
    private lateinit var rvUsers : RecyclerView
    private lateinit var rvUsersAdapter : UserCardAdapter
    private var page = 1

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
        binding = FragmentUsersBinding.inflate(layoutInflater)
        query = createTextChangeObservable()
        setupListeners()
        setupUsersRecyclerView()
        return binding.root
    }

    private fun setupUsersRecyclerView(){
        rvUsers = binding.usersRecyclerView
        rvUsersAdapter = UserCardAdapter(requireActivity() as DashboardActivity)
        rvUsers.adapter = rvUsersAdapter
        rvUsers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    @SuppressLint("CheckResult")
    private fun setupListeners(){
        query
            .debounce(300, TimeUnit.MILLISECONDS)
            .subscribe {
                if(it.length > 2){
                    searchUser(it,true)
                    page = 1
                }
            }
        binding.usersLoadMore.setOnClickListener{
            searchUser(binding.usersSearchQuery.text.toString(),false)
        }
    }

    private fun searchUser(query: String, clear: Boolean){

        if(clear){
            rvUsersAdapter.users.clear()
            lifecycleScope.launch(Dispatchers.Main){
                rvUsersAdapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch(Dispatchers.Main){
            val token = Auth.getToken(requireContext())
            val search = lifecycleScope.async {
                response = try {
                    apiService.searchUser(token!!, query, page).body()!!
                } catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            search.await()

            if (response.meta.code == 200){
                if(((response.result as LinkedTreeMap<*, *>)["rows"] as List<*>?)?.size == null){
                    UI.snackbarTop(binding.usersLoadMore, "Tidak ada data")
                }else{
                    ((response.result as LinkedTreeMap<*, *>)["rows"] as List<*>?)?.map {
                        rvUsersAdapter.users.add(Converter.anyToUserResponse(it as LinkedTreeMap<*, *>))
                        rvUsersAdapter.notifyDataSetChanged()
                    }
                    page += 1
                }
            }else{
                UI.showSnackbarByResponseCode(response.meta, binding.root)
            }
        }
    }

    private fun createTextChangeObservable(): Observable<String> {
        return Observable.create { emitter ->
            // 3
            val textWatcher = object : TextWatcher {

                override fun afterTextChanged(s: Editable?) = Unit

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                // 4
                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    s?.toString()?.let { emitter.onNext(it) }
                }
            }

            // 5
            binding.usersSearchQuery.addTextChangedListener(textWatcher)

            // 6
            emitter.setCancellable {
                binding.usersSearchQuery.removeTextChangedListener(textWatcher)
            }
        }
    }
}