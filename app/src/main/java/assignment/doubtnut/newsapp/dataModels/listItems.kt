package assignment.doubtnut.newsapp.dataModels

import java.io.Serializable

data class DataItems (
    var title: String?,
    var publishedAt: String?,
    var sourceName: String?,
    var authorName : String?,
    var description : String?,
    var url : String?,
    var urlToImage : String?,
    var content : String?
) : Serializable