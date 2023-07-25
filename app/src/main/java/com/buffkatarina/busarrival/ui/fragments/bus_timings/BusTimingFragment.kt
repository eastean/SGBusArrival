package com.buffkatarina.busarrival.ui.fragments.bus_timings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buffkatarina.busarrival.R
import com.buffkatarina.busarrival.model.ActivityViewModel
import kotlinx.coroutines.launch

class BusTimingFragment: Fragment(), BusTimingsAdapter.FavouritesHandler {
    private val model: ActivityViewModel by lazy {
        ViewModelProvider(requireActivity())[ActivityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        return inflater.inflate(R.layout.bus_timings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getBusTimings(view)

    }

    private fun getBusTimings(view: View)
    /*
    Set ups recycler view
    Gets bus timings and loads them into the recycler view*/
    {

        val recyclerView = view.findViewById<RecyclerView>(R.id.busTimings_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val currentFragment = this
        val viewModel =ViewModelProvider(requireActivity())[ActivityViewModel::class.java]
        parentFragmentManager.setFragmentResultListener("busStopCodeKey",
            viewLifecycleOwner) {
                _, bundle ->
            //Gets the queried bus stop code
            val busStopCode =  bundle.getString("busStopCode")?.toInt()
            busStopCode?.let{code ->
                viewLifecycleOwner.lifecycleScope.launch {
                    //Retrieve the bus timings
                    viewModel.getBusTimings(busStopCode)
                    viewModel.getFavouriteBusService(busStopCode)

                    //Merge data from view model and load into recycler view
                    viewModel.mergeFavouriteAndTimings().observe(viewLifecycleOwner) {
                            result ->
                        val favouriteBusServices = result.first
                        val busTimings = result.second
                        if (favouriteBusServices != null && busTimings != null){
                            recyclerView.adapter = BusTimingsAdapter(code, busTimings, currentFragment, favouriteBusServices)
                        }

                    }
                }
            }

        }

    }

    override fun addFavouriteBusService(busStopCode: Int, serviceNo: String) {
        model.insertFavouriteBusService(busStopCode, serviceNo)

    }

    override fun removeFavouriteBusService(busStopCode: Int, serviceNo: String) {
        model.removeFavouriteBusService(busStopCode, serviceNo)
    }

}