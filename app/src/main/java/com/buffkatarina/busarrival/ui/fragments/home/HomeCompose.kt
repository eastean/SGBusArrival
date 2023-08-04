package com.buffkatarina.busarrival.ui.fragments.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.MutableLiveData
import com.buffkatarina.busarrival.data.entities.BusTimings
import com.buffkatarina.busarrival.data.entities.FavouriteBusServicesWithDescription
import com.buffkatarina.busarrival.model.ActivityViewModel
import com.buffkatarina.busarrival.ui.fragments.home.favourites.Favourites

@Composable
fun HomeFragment(
    databaseBuildState: Boolean,
    dialogState:Boolean,
    favouriteTimings: MutableLiveData<MutableList<BusTimings>>,
    viewModel: ActivityViewModel

    ){

//Only show dialog on app launch
    if (!dialogState) {
        Dialog(databaseBuildState, viewModel::setDialogState)
    }
    if (databaseBuildState) {
        Column {
//          MapView(modifier = Modifier.weight(1f))
            val favourites by viewModel.getAllFavouriteBusServices().observeAsState()
            val timings by favouriteTimings.observeAsState()
            if (favourites != null && timings != null) {
                if (favourites!!.size == timings!!.size) {
                    Favourites(
                        modifier = Modifier.weight(1f),
                        (favourites to timings) as Pair<List<FavouriteBusServicesWithDescription>, MutableList<BusTimings>>,
                        viewModel::removeFavouriteBusService)
                }

            }
        }
    }
}