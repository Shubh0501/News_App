package assignment.doubtnut.newsapp.dataModels

data class NewsItems (
    var author : String?,
    var title : String?,
    var description : String?,
    var url : String?,
    var urlToImage : String?,
    var publishedAt : String?,
    var content : String?,
    var source : Map<String?,String?>
)