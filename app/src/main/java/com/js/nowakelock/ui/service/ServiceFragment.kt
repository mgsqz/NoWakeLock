package com.js.nowakelock.ui.service

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.js.nowakelock.R
import com.js.nowakelock.base.cache
import com.js.nowakelock.base.menuGone
import com.js.nowakelock.data.db.entity.Service
import com.js.nowakelock.databinding.FragmentServiceBinding
import com.js.nowakelock.ui.databding.RecycleAdapter
import com.js.nowakelock.ui.mainActivity.MainViewModel
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import org.koin.android.ext.android.inject

class ServiceFragment : Fragment() {

    private lateinit var packageName: String
    private val viewModel by inject<ServiceViewModel> { parametersOf(packageName) }

    private lateinit var binding: FragmentServiceBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: RecycleAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        packageName = arguments?.getString("packageName") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServiceBinding.inflate(inflater, container, false)
        context ?: return binding.root //if already create

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        //set recyclerview
        val handler = ServiceHandler(viewModel)
        adapter = RecycleAdapter(R.layout.item_service, handler)
        binding.list.adapter = adapter

        setItemDecoration(binding.list)

        //set SwipeRefresh
        setSwipeRefreshLayout(binding.refresh)

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //adapter
        subscribeUi()
        //status
        subscribeStatus()
    }

    /**adapter subscribe data */
    private fun subscribeUi() {
        val observer = Observer<List<Service>> { albinos ->
            loadList(albinos, mainViewModel.status.value)
        }
        viewModel.list.observe(viewLifecycleOwner, observer)
    }

    private fun subscribeStatus() {
        val observer = Observer<cache> { status ->
            loadList(viewModel.list.value, status)
        }
        mainViewModel.status.observe(viewLifecycleOwner, observer)
    }

    private fun loadList(services: List<Service>?, cache: cache?) {
        if (services != null && cache != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                adapter.submitList(viewModel.list(services, cache))
            }
        }
    }

    private fun setItemDecoration(recyclerView: RecyclerView) = recyclerView.addItemDecoration(
        DividerItemDecoration(
            recyclerView.context,
            DividerItemDecoration.VERTICAL
        )
    )

    //SwipeRefresh
    private fun setSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        //
        swipeRefreshLayout.setDistanceToTriggerSync(300)
        //color
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE)
        //binding
        swipeRefreshLayout.setOnRefreshListener {
//            viewModel.syncWakeLocks()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    //set toolbar menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuGone(menu, setOf(R.id.menu_filter_user, R.id.menu_filter_system, R.id.menu_filter_all))
        menuGone(menu, setOf(R.id.menu_sort_countime))
        super.onCreateOptionsMenu(menu, inflater)
    }
}