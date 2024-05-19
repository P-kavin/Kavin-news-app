package com.example.myapplication.ReadnewActivity

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.myapplication.DatasAndValues.NAConstants.PUBLISHEDAT
import com.example.myapplication.NetworkManager.NABroadCastReciever
import com.example.myapplication.NewslistActiviy.NANewsListActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.NaActivityReadnewsBinding
import java.text.SimpleDateFormat
class NaReadnewsActivity : AppCompatActivity(),NABroadCastReciever.ConnectivityReceiverListener {
    lateinit var readNewsBinding: NaActivityReadnewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readNewsBinding=NaActivityReadnewsBinding.inflate(layoutInflater)
        setContentView(readNewsBinding.root)
        registerReceiver(
            NABroadCastReciever(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        onClickListner()
        getNewsFromAdapter()
    }

    private fun getNewsFromAdapter() {
        val apiDateFormat =  SimpleDateFormat( "yyyy-MM-dd'T'hh:mm:ss'Z'")
        val requiredDateFormat = SimpleDateFormat("MMM dd")
        val publishedAt = requiredDateFormat.format(apiDateFormat.parse(intent.getStringExtra("PublishedAt")))

        var imageUrl=intent.getStringExtra("Image")
        Glide.with(this).load(imageUrl).placeholder(R.drawable.networkerror).into(readNewsBinding.ivNewsImage)
        readNewsBinding.tvShortNews.text=intent.getStringExtra("Title")
        readNewsBinding.tvPublishedAt.text= PUBLISHEDAT +" "+publishedAt
        var author=intent.getStringExtra("Author")
        var source=intent.getStringExtra("Source")
        if(author==null) {
            readNewsBinding.tvPublisher.text =source
        }
        else  readNewsBinding.tvPublisher.text=author+","+source
        readNewsBinding.tvFullNews.text=intent.getStringExtra("Content")
    }



    private fun onClickListner() {
        readNewsBinding.ivBackArrow.setOnClickListener { navigateToNewsListActivity() }
    }

    private fun navigateToNewsListActivity() {
        val intent = Intent(this, NANewsListActivity::class.java).also { finish() }
        startActivity(intent)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            readNewsBinding.tvNetworkBanner.visibility= View.VISIBLE
        } else {
            readNewsBinding.tvNetworkBanner.visibility= View.INVISIBLE
        }
    }
    override fun onResume() {
        super.onResume()
        NABroadCastReciever.connectivityReceiverListener = this
    }
}