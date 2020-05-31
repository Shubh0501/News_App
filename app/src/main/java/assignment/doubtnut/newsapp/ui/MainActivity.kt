package assignment.doubtnut.newsapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import assignment.doubtnut.newsapp.adapter.ListItemsAdapter
import assignment.doubtnut.newsapp.dataModels.DataItems
import assignment.doubtnut.newsapp.retrofitInterface.GetNewsData
import assignment.doubtnut.newsapp.dataModels.NewsResponseData
import assignment.doubtnut.newsapp.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    //Defining some global variables
    lateinit var nullList : DataItems
    var isLoading = false
    lateinit var list : MutableList<DataItems>
    lateinit var completeList : MutableList<NewsResponseData>
    lateinit var retrofit : Retrofit
    var pageNumber = 1
    var allDataDownloaded = false
    var offlineSavedData = false
    var toastAllDataDownloadedShown = false
    //Global variables complete

    //onCreate activity function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar as Toolbar?)

        //Recycler View layout Connection
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        listOfNewsContainer.layoutManager = layoutManager
        list = mutableListOf<DataItems>()
        completeList = mutableListOf<NewsResponseData>()
        //Recycler View Layout Connected

        //Retrofit Initializer
        retrofit = Retrofit.Builder().baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        //Retrofit Initialized

        var adapter : ListItemsAdapter

        //Initializing Null List
        nullList = DataItems(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        //Null List Initialized

        //Check if Internet is Available
        if(isInternetAvailable()){
            list.add(nullList)

            //Recycler View Adapter connection
            adapter =
                ListItemsAdapter(this, list)
            listOfNewsContainer.adapter = adapter
            //Recycler View Adapter Connected

            //Scroll Listener
            scrollListener(adapter, layoutManager)
        }
        else{
            //Toast.makeText(applicationContext, "Not Connected to Internet!", Toast.LENGTH_LONG).show()
            getSavedData()
            //Recycler View Adapter connection
            adapter =
                ListItemsAdapter(this, list)
            listOfNewsContainer.adapter = adapter
            //Recycler View Adapter Connected

            //Scroll Listener
            scrollListener(adapter, layoutManager)
        }

    }
    //Function onCreate Complete

    //Function to check if Internet connection is available or not
    fun isInternetAvailable() : Boolean {
        if(applicationContext == null){
            return false
        }
        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //Check for versions greater or equal to Marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val networkState = manager.activeNetwork ?: return false
            val networkActiveType = manager.getNetworkCapabilities(networkState) ?: return false
            return networkActiveType.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkActiveType.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    networkActiveType.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }

        //Check for versions less than MarshMallow
        else{
            manager.run {
                if(this.activeNetworkInfo == null){
                    return false
                }
                if(this.activeNetworkInfo.isConnectedOrConnecting){
                    return true;
                }
            }
        }
        return false;
    }

    //Function to get Data from the API
    fun getData(adapter: ListItemsAdapter) {
        //Retrofit Initializer
        retrofit.newBuilder().baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        //Retrofit Initialized
        if(isInternetAvailable()) {
            if (list.size == 0 || list[list.size - 1].title != null) {
                list.add(nullList)
                adapter.notifyDataSetChanged()
                listOfNewsContainer.smoothScrollToPosition(0)
                listOfNewsContainer.layoutManager?.smoothScrollToPosition(listOfNewsContainer, null, 0)
            }
        }

        var apiData = retrofit.create(GetNewsData::class.java)
        var NEWSAPI : String = getString(R.string.news_api_key)
        var url : String = "/v2/top-headlines?country=in&pageSize=20&page=$pageNumber&apiKey=$NEWSAPI"
        apiData.getData(url)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ 
                setData(it, adapter)
                pageNumber = pageNumber + 1
                isLoading = false;
            }, {
                Log.w("Message", it.fillInStackTrace())
                Toast.makeText(applicationContext, "Unable to connect to the internet!", Toast.LENGTH_LONG).show()
                isLoading = false;
            })
    }
    //Function getData Complete

    //Function to insert data in the list of the recycler view
    @SuppressLint("SimpleDateFormat")
    fun setData(data: NewsResponseData, adapter: ListItemsAdapter) {
        //Removing null from the last position in the list
        if(list.size != 0) {
            list.removeAt(list.size - 1)
        }
        if(offlineSavedData) {
            offlineSavedData = false
            list.clear()
        }
        //Checking the status of the result
        var status = data.status

        //If status is ok
        if(status.equals("ok")) {
            var totalResults = data.totalResults
            var articles = data.articles

            //If all data available are downloaded
            if((pageNumber-1)*20 >= totalResults){
                allDataDownloaded = true
                isLoading = true
            }

            //Filling data in the list connected to recycler view
            for (article in articles) {
                var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                var simpleDateOutputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                var date = simpleDateFormat.parse(article.publishedAt)
                var newDate = simpleDateOutputFormat.format(date)
                var tempList = DataItems(
                    article.title,
                    newDate,
                    article.source.get("name"),
                    article.author,
                    article.description,
                    article.url,
                    article.urlToImage,
                    article.content
                )
                list.add(tempList)
            }

            //Adding null value at the end if all data is not downloaded
            if (!allDataDownloaded) {
                list.add(nullList)
            }

            //Function to save the data locally
            saveData()

            //Notifying adapter of the changes made
            adapter.notifyDataSetChanged()
        }

        //If status is error
        else{
            Toast.makeText(applicationContext, data.code, Toast.LENGTH_LONG).show()
        }
    }
    //Function setData complete

    //Function to save the data locally for future use
    fun saveData() {
        var root : File = File(applicationContext.filesDir, "newslist")
        var fileOutputStream = FileOutputStream(root)
        var stream : ObjectOutputStream = ObjectOutputStream(fileOutputStream)
        stream.writeObject(list)
        stream.close()
    }

    //Function to get the data saved locally if available
    fun getSavedData(){
        var root : File = File(applicationContext.filesDir, "newslist")
        if(!root.exists()){
            return
        }
        offlineSavedData = true
        var fileInputStream = FileInputStream(root)
        var stream = ObjectInputStream(fileInputStream)
        list = stream.readObject() as MutableList<DataItems>
        stream.close()
    }

    //Function to check scroll in the recycler view and get more data if available from API
    fun scrollListener(adapter: ListItemsAdapter, layoutManager: LinearLayoutManager) {
        listOfNewsContainer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!isLoading && !allDataDownloaded){
                    if((layoutManager != null && list.size != 0 && layoutManager.findLastVisibleItemPosition() == list.size - 1)){
                        getData(adapter)
                        isLoading = true
                    }
                }
                if(allDataDownloaded && !toastAllDataDownloadedShown){
                    if((layoutManager != null && list.size != 0 && layoutManager.findLastVisibleItemPosition() == list.size - 1)){
                        Toast.makeText(applicationContext, "That's all for now!", Toast.LENGTH_LONG).show();
                        toastAllDataDownloadedShown = true
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                    if(list.size == 0){
                        getData(adapter)
                        isLoading = true
                    }

            }
        })
    }
    //Function scrollListener Complete
}