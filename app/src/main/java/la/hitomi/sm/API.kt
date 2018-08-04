package la.hitomi.sm

import retrofit2.Call
import retrofit2.http.*

/**
* Created by Kinetic on 2018-06-02.
*/

interface API {

    @GET("/place")
    fun getPlace() :  Call<ArrayList<LocationRepo>>

}