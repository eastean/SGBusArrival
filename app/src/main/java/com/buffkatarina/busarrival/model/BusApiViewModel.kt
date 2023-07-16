package com.buffkatarina.busarrival.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buffkatarina.busarrival.api.BusApiRepository
import com.buffkatarina.busarrival.api.BusApiService
import com.buffkatarina.busarrival.data.db.BusArrivalDatabase
import com.buffkatarina.busarrival.data.db.BusArrivalRepository
import com.buffkatarina.busarrival.data.entities.BusTimings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BusApiViewModel(application: Application): AndroidViewModel(application) {
    /*View model that handles all the HTTP requests and parsing and building of database.*/

    private val _busTimings  = MutableLiveData<BusTimings>()
    val busTimings: LiveData<BusTimings>
        get() = _busTimings

    private val busApiRepository: BusApiRepository
    private val busArrivalRepository: BusArrivalRepository

    init {
        val dbDao = BusArrivalDatabase.getInstance(application).BusArrivalDao()
        busArrivalRepository = BusArrivalRepository(dbDao)
        val busApiInterface = BusApiService.BusApi.busApi
        busApiRepository = BusApiRepository(busApiInterface)
    }
    fun getBusTimings(busStopCode: Int?){
        /*

        Get a list of bus timings for a given bus stop code
        Updates the busTimings class variable with the result
        List of bus timings can be obtained by 'OBSERVING' the busTimings variable for changes
        * */
        viewModelScope.launch{
            try {
                _busTimings.value = busApiRepository.getBusTimings(busStopCode)
            } catch (e: Exception) {
                Log.d("Error", "${e.message}")

            }

        }
    }

    fun buildDB(){
        /*
        Loads all parsed data into the database
        */
        viewModelScope.launch(Dispatchers.IO) {

            try{
                insertBusStops()
                insertBusServices()
                insertBusRoutes()

            } catch (e: Exception){
                Log.d("GetBusStopsError", "${e.message}")
            }
        }
    }

    private suspend fun insertBusStops() {
        /*Parse all bus stops and insert into database*/
        var skipAmt = 0
        do{
            val busStops = busApiRepository.getBusStops(skipAmt)
            for (i in busStops.data){
                busArrivalRepository.insertBusStops(i)
            }
            skipAmt += 500
        } while(busApiRepository.getBusStops(skipAmt).data.isNotEmpty())

    }

    private suspend fun insertBusServices() {
        /*Parse all bus services and insert into database*/
        var skipAmt = 0
        do{
            val busServices = busApiRepository.getBusServices(skipAmt)
            for (i in busServices.data){
                busArrivalRepository.insertBusServices(i)
            }
            skipAmt += 500
        } while(busApiRepository.getBusServices(skipAmt).data.isNotEmpty())

    }

    private suspend fun insertBusRoutes() {
        /*Parse all bus routes and insert into database*/
        var skipAmt = 0
        do{
            val busRoutes = busApiRepository.getBusRoutes(skipAmt)
            for (i in busRoutes.data){
                busArrivalRepository.insertBusRoutes(i)
            }
            skipAmt += 500
        } while(busApiRepository.getBusRoutes(skipAmt).data.isNotEmpty())

    }
}