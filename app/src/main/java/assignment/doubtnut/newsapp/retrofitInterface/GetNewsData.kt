package assignment.doubtnut.newsapp.retrofitInterface

import assignment.doubtnut.newsapp.dataModels.NewsResponseData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface GetNewsData {

    @GET
    fun getData(@Url url: String) : Observable<NewsResponseData>

}