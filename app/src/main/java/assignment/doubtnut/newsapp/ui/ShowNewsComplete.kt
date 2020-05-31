package assignment.doubtnut.newsapp.ui

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import assignment.doubtnut.newsapp.dataModels.DataItems
import assignment.doubtnut.newsapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_show_news_complete.*
import kotlinx.android.synthetic.main.news_list_item.view.*
import kotlinx.android.synthetic.main.news_list_loading.*
import java.lang.Exception

class ShowNewsComplete : AppCompatActivity() {

    //onCreate Activity function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_news_complete)
        setSupportActionBar(toolbar2 as Toolbar?)

        //Getting data of the clicked card from the intent
        var news : DataItems = intent.extras?.get("News Details") as DataItems

        //Downloading and setting the image
        if(news.urlToImage != null){
            Picasso.get().load(Uri.parse(news.urlToImage)).fit().into(imageContainer, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    imageLoadingProgressBar.visibility = View.GONE
                    errorLoadingImage.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    var drawable : Drawable? = imageContainer.drawable
                    var notFilled = drawable == null
                    if(!notFilled && drawable is BitmapDrawable){
                        notFilled = drawable.bitmap == null
                    }
                    if(notFilled) {
                        imageLoadingProgressBar.visibility = View.GONE
                        errorLoadingImage.visibility = View.VISIBLE
                    }
                }

            })
        }
        //If no image URL found
        else{
            imageContainer.setImageBitmap(null)
            imageLoadingProgressBar.visibility = View.GONE
            errorLoadingImage.visibility = View.VISIBLE
        }

        //Setting the data in the text views
        titleTextContainer.text = news.title
        authorNameContainer.text = news.authorName
        publicationDateContainer.text = news.publishedAt
        sourceNameContainer.text = news.sourceName
        descriptionContainer.text = news.description
        if(news.content != null){
            contentContainer.text = news.content
        }
        else{
            contentContainer.visibility = View.GONE
        }

        //Setting the click listener for showing the complete article in browser
        readMoreContainer.setOnClickListener {
            if(news.url != null) {
                var urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
                startActivity(urlIntent)
            }
            else{
                Toast.makeText(applicationContext, "This is the full content", Toast.LENGTH_LONG).show()
            }
        }

    }

}