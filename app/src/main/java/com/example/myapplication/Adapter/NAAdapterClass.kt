package com.example.myapplication.Adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.DatasAndValues.NAConstants.PUBLISHEDAT
import com.example.myapplication.DatasAndValues.NANewsListDataItem
import com.example.myapplication.R
import com.example.myapplication.ReadnewActivity.NaReadnewsActivity
import com.example.myapplication.databinding.NaActivityAddNewslistBinding
import kotlinx.android.synthetic.main.na_activity_add_newslist.view.*
import java.text.SimpleDateFormat

class NAAdapterClass(naNewsListData: ArrayList<NANewsListDataItem?>) :
    RecyclerView.Adapter<NAAdapterClass.ViewHolder>() {

    private var newsList = naNewsListData

    inner class ViewHolder(var adapterBinding: NaActivityAddNewslistBinding) :
        RecyclerView.ViewHolder(adapterBinding.root) {
        fun receivedData(user: NANewsListDataItem?) {
            val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
            val requiredDateFormat = SimpleDateFormat("MMM dd")
            val publishedAt = requiredDateFormat.format(apiDateFormat.parse(user?.publishedAt))
            Glide.with(itemView).load(user?.urlToImage).placeholder(R.drawable.networkerror)
                .into(adapterBinding.ivNewsImage)
            adapterBinding.tvShortNews.text = user?.title
            adapterBinding.tvPublishedAt.text = PUBLISHEDAT + " " + publishedAt

        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NaActivityAddNewslistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("view", "one")
        val currentList = newsList[position]
        holder.receivedData(currentList)
            holder.itemView.cl_list_view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(v?.context, NaReadnewsActivity::class.java)
                intent.putExtra("Title", currentList?.title)
                intent.putExtra("Content", currentList?.content)
                intent.putExtra("Author", currentList?.author)
                intent.putExtra("PublishedAt", currentList?.publishedAt)
                intent.putExtra("Image", currentList?.urlToImage)
                intent.putExtra("Source", currentList?.source?.name)
                v?.context?.startActivity(intent)
            }

        })
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNewsList(oldList: ArrayList<NANewsListDataItem?>) {
        newsList.addAll(oldList)
        notifyDataSetChanged()
    }
}
