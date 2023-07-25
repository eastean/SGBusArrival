package com.buffkatarina.busarrival.ui.fragments.bus_timings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buffkatarina.busarrival.R
import com.buffkatarina.busarrival.data.entities.BusTimings
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


class BusTimingsAdapter(
    private val busStopCode: Int,
    private val data: BusTimings,
    private val favouritesHandler: FavouritesHandler,
    private val favouriteBusServices: List<String>) :
    RecyclerView.Adapter<BusTimingsAdapter.BusArrivalViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusArrivalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bus_timings_recyclerview_row, parent, false)
        val viewHolder = BusArrivalViewHolder(view)
       setUpListener(viewHolder)
        return viewHolder
    }

    private fun setUpListener(holder: BusArrivalViewHolder) {
        val serviceNo = holder.serviceNoHolder
        val favourite = holder.favourite
        favourite.setOnClickListener {
            //Removes bus service from database when an activated button is tapped
            // Deactivates the button afterwards - shows up as uncolored star on display
            if (favourite.isActivated) {
                favouritesHandler.removeFavouriteBusService(busStopCode, serviceNo.text as String)
                favourite.isActivated = false
            }
            //Add bus service from database when an deactivated button is tapped
            // Activates the button afterwards - shows up as colored star on display
            else {
                favouritesHandler.addFavouriteBusService(busStopCode, serviceNo.text as String)
                favourite.isActivated = true
            }

        }
    }
    override fun onBindViewHolder(holder: BusArrivalViewHolder, position: Int) {
        val currentItem = data.services[position]
        holder.serviceNoHolder.text = currentItem.serviceNo

        //Activate buttons that have are already favourited
        holder.favourite.isActivated = currentItem.serviceNo in favouriteBusServices

        //Initialise tuple of estimated arrival time of bus 1/2/3: text view of bus 1/2/3
        val pairs = listOf(
            Pair(currentItem.nextBus.estimatedArrival, holder.nextBus),
            Pair(currentItem.nextBus2.estimatedArrival, holder.nextBus2),
            Pair(currentItem.nextBus3.estimatedArrival, holder.nextBus3)
        )
        for (pair in pairs) {
            //If queried estimated arrival timing for a bus an empty string, text view displays '-'
            if (pair.first.isEmpty()) {
                pair.second.text = "-"
            }
            else{
                val difference = timeDifference(pair.first)
                // if estimated time of arrival is less than 1 minute from current time, text displays arr
                if (difference.toInt() < 1) {
                    pair.second.text = "Arr"
                }
                else{
                    // display estimated time of arrival from current time
                    pair.second.text = difference
                }

            }
        }
    }


    override fun getItemCount() = data.services.size

    private fun timeDifference(time: String): String{
        /*
        Estimates difference between current time and given time.
        Returns the difference in MINUTE
        */
        val currentTime = LocalDateTime.now()
        val parsedTime = ZonedDateTime.parse(time).toLocalDateTime()
        return ChronoUnit.MINUTES.between(currentTime, parsedTime).toString()

    }

    class BusArrivalViewHolder(view: View): RecyclerView.ViewHolder(view){
        val serviceNoHolder: TextView = view.findViewById(R.id.serviceNo)
        val nextBus: TextView = view.findViewById(R.id.nextBus)
        val nextBus2: TextView = view.findViewById(R.id.nextBus2)
        val nextBus3: TextView = view.findViewById(R.id.nextBus3)
        val favourite: ImageButton = view.findViewById(R.id.favourite)
    }

    interface FavouritesHandler {
        /*For recycler view to be able tp call view model to remove or add favourite bus services*/
        fun addFavouriteBusService(busStopCode: Int, serviceNo: String)

        fun removeFavouriteBusService(busStopCode: Int, serviceNo: String)
    }

}


