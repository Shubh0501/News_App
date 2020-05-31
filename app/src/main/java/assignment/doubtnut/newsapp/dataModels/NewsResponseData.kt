package assignment.doubtnut.newsapp.dataModels

class NewsResponseData {

    lateinit var status : String
    var totalResults : Int = 0
    var code : String? = null
    var message : String? = null
    var articles : List<NewsItems> = mutableListOf<NewsItems>()

}