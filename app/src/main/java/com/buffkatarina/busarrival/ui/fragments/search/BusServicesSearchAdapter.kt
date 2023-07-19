package com.buffkatarina.busarrival.ui.fragments.search

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.buffkatarina.busarrival.R
import com.buffkatarina.busarrival.data.entities.BusServices
import com.buffkatarina.busarrival.data.entities.BusStops
import com.buffkatarina.busarrival.ui.fragments.bus_routes.BusRoutesFragment

class BusServicesSearchAdapter(private val context: Context): RecyclerView.Adapter<BusServicesSearchAdapter.SearchAdapterViewHolder>() {
    private var dataList = emptyList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    : SearchAdapterViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.bus_services_recyclerview_row, parent, false)

        return setUpViewHolder(view)
    }

    private fun setUpViewHolder(view: View): SearchAdapterViewHolder {
        val viewHolder = SearchAdapterViewHolder(view)
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        viewHolder.cardView.setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, BusRoutesFragment())
                .addToBackStack(null)
                .commit()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SearchAdapterViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.busServiceNo.text = currentItem
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


     fun updateData(newData: List<String>) {
        dataList = newData
        notifyDataSetChanged()
    }

    class SearchAdapterViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val busServiceNo: TextView = view.findViewById(R.id.busServiceNo)
        val cardView: CardView = view.findViewById(R.id.bus_services_card)

    }
}