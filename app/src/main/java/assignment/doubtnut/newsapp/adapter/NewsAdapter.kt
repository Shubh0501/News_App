package assignment.doubtnut.newsapp.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import assignment.doubtnut.newsapp.dataModels.DataItems
import assignment.doubtnut.newsapp.R
import assignment.doubtnut.newsapp.ui.ShowNewsComplete
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_show_news_complete.*
import kotlinx.android.synthetic.main.news_list_item.view.*
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.lang.Exception

class ListItemsAdapter (val context: Context, var listItems : MutableList<DataItems>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //Variables to determine which type of card to add
    val ITEM_CARD = 0
    val LOADING_CARD = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == ITEM_CARD) {
            var view: View = LayoutInflater.from(context).inflate(R.layout.news_list_item, parent, false)
            return MyViewHolder(view);
        }
        else{
            var view = LayoutInflater.from(context).inflate(R.layout.news_list_loading, parent, false);
            return MyLoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size;
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val news = listItems[position]
        if (holder is MyViewHolder)
            holder.updateNews(news);
    }

    override fun getItemViewType(position: Int): Int {
        if(listItems[position].title == null){
            return LOADING_CARD
        }
        return ITEM_CARD
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun updateNews(news : DataItems?) {
            //Updating the data in the card view
            itemView.titleTextView.text = news!!.title
            if(news.urlToImage!= null) {
                Picasso.get().load(Uri.parse(news.urlToImage)).fit()
                    .into(itemView.imageViewContainer, object : Callback {
                        override fun onSuccess() {
                            itemView.progressBarSmallImage.visibility = View.GONE
                            itemView.imageNotLoadedError.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            var drawable: Drawable? = itemView.imageViewContainer.drawable
                            var notFilled = drawable == null
                            if (!notFilled && drawable is BitmapDrawable) {
                                notFilled = drawable.bitmap == null
                            }
                            if (notFilled) {
                                itemView.progressBarSmallImage.visibility = View.GONE
                                itemView.imageNotLoadedError.visibility = View.VISIBLE
                            }
                        }
                    })
            }
            else{
                itemView.imageViewContainer.setImageBitmap(null)
                itemView.progressBarSmallImage.visibility = View.GONE
                itemView.imageNotLoadedError.visibility = View.VISIBLE
            }
            //Listener for onClick event on any card available
            itemView.setOnClickListener {
                var position = adapterPosition
                var news = listItems[position]
                val intent = Intent(context, ShowNewsComplete::class.java)
                intent.putExtra("News Details", news)
                context.startActivity(intent)
            }
        }
    }

    //Loading CardView Class
    inner class MyLoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    //Function to get saved data in case of no connection
    fun getData() {
        var root : File = File(context.filesDir, "newslist")
        var fileInputStream = FileInputStream(root)
        var stream = ObjectInputStream(fileInputStream)
        listItems = stream.readObject() as MutableList<DataItems>
        stream.close()

    }
}