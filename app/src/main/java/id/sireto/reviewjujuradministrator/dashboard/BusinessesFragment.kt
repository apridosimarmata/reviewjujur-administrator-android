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
import id.sireto.reviewjujuradministrator.R
import id.sireto.reviewjujuradministrator.adapters.BusinessCardAdapter
import id.sireto.reviewjujuradministrator.adapters.UserCardAdapter
import id.sireto.reviewjujuradministrator.databinding.FragmentBusinessesBinding
import id.sireto.reviewjujuradministrator.models.BaseResponse
import id.sireto.reviewjujuradministrator.models.Meta
import id.sireto.reviewjujuradministrator.services.api.ApiService
import id.sireto.reviewjujuradministrator.utils.Auth
import id.sireto.reviewjujuradministrator.utils.Converter
import id.sireto.reviewjujuradministrator.utils.UI
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BusinessesFragment(private val apiService: ApiService) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentBusinessesBinding
    private lateinit var rvBusinesses : RecyclerView
    private lateinit var rvBusinessesAdapter: BusinessCardAdapter
    private lateinit var query: Observable<String>
    private var page = 1
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
        binding = FragmentBusinessesBinding.inflate(layoutInflater)
        query = createTextChangeObservable()
        setupListeners()
        setupBusinessesRecyclerView()
        return binding.root
    }

    private fun setupBusinessesRecyclerView(){
        rvBusinesses = binding.businessesRecyclerView
        rvBusinessesAdapter = BusinessCardAdapter(requireActivity() as DashboardActivity)
        rvBusinesses.adapter = rvBusinessesAdapter
        rvBusinesses.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    @SuppressLint("CheckResult")
    private fun setupListeners(){
        query
            .debounce(300, TimeUnit.MILLISECONDS)
            .subscribe {
                if(it.length > 2){
                    searchBusiness(it,true)
                    page = 1
                }
            }
        binding.businessesLoadMore.setOnClickListener{
            searchBusiness(binding.businessesSearchQuery.text.toString(),false)
        }
    }

    private fun searchBusiness(query: String, clear: Boolean){

        if(clear){
            rvBusinessesAdapter.businesses.clear()
            lifecycleScope.launch(Dispatchers.Main){
                rvBusinessesAdapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch(Dispatchers.Main){
            val search = lifecycleScope.async {
                response = try {
                    apiService.searchBusiness(query, page).body()!!
                } catch (e: Exception){
                    BaseResponse(Meta(code = 0, message = "Error : ${e.cause}"), null)
                }
            }

            search.await()

            if (response.meta.code == 200){
                if(((response.result as LinkedTreeMap<*, *>)["rows"] as List<*>?)?.size == null){
                    UI.snackbarTop(binding.businessesLoadMore, "Tidak ada data")
                }else{
                    ((response.result as LinkedTreeMap<*, *>)["rows"] as List<*>?)?.map {
                        rvBusinessesAdapter.businesses.add(Converter.anyToBusinessResponse(it as LinkedTreeMap<*, *>))
                        rvBusinessesAdapter.notifyDataSetChanged()
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
            binding.businessesSearchQuery.addTextChangedListener(textWatcher)

            // 6
            emitter.setCancellable {
                binding.businessesSearchQuery.removeTextChangedListener(textWatcher)
            }
        }
    }

}