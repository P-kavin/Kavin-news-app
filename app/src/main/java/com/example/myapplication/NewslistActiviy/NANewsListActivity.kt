package com.example.myapplication.NewslistActiviy

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.NAAdapterClass
import com.example.myapplication.DatasAndValues.NAConstants.GOOD_NETWORK
import com.example.myapplication.DatasAndValues.NANewsListData
import com.example.myapplication.DatasAndValues.NANewsListDataItem
import com.example.myapplication.NetworkManager.NABroadCastReciever
import com.example.myapplication.R
import com.example.myapplication.databinding.NaActivityNewslistBinding
import kotlin.jvm.internal.Intrinsics

class NANewsListActivity : AppCompatActivity(), NABroadCastReciever.ConnectivityReceiverListener {
    private lateinit var homeAdapter: NAAdapterClass
    private lateinit var newsListBinding: NaActivityNewslistBinding
    private val newsListViewModel: NANewsListViewModel by lazy {
        ViewModelProvider(this)[NANewsListViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("e","Create")
        newsListBinding = NaActivityNewslistBinding.inflate(layoutInflater)
        setContentView(newsListBinding.root)
        registerReceiver(
            NABroadCastReciever(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        newsListBinding.ivDescendingList.visibility = View.GONE
        onClickListener()
        onSearchListener()
        onScrollListener()
    }


    var isScrolling = false
    var isLoading = false

    private fun onScrollListener() {
        newsListBinding.rvNewsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition == newsListViewModel.listSizes - 1) {
                    if (isScrolling) {
                        newsListViewModel.pageNumber += 1
                        Log.d("pg", newsListViewModel.pageNumber.toString())
                        isScrolling = false
                        requestApiForNewsList()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })
    }

    private fun onSearchListener() {
        newsListBinding.etSearchNews.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("ml", newText.toString())
                newsListViewModel.getSearchedListFromDatabase(newText)
                displaySearchedNews()
                if (newText!!.isEmpty()) {
                    if (network == true) {
                        requestApiForNewsList()
                    } else {
                        newsListViewModel.getBackToNewsList()
                        displaySearchedNews()
                    }
                }
                return false
            }
        })
    }

    private fun displaySearchedNews() {
        newsListViewModel.searchNewsLiveData.observe(this, Observer { searchedNews ->
            if (searchedNews.size != 0) {
                newsListViewModel.searchedList = searchedNews
                homeAdapter = NAAdapterClass(searchedNews)
                newsListBinding.rvNewsList.adapter = homeAdapter
                newsListBinding.tvHomeInfo.text = ""
                newsListBinding.rvNewsList.layoutManager = GridLayoutManager(this, 1)
               // newsListBinding.rvNewsList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            } else {
                newsListViewModel.searchedList.clear()
                newsListBinding.tvHomeInfo.text = getString(R.string.Info)
            }
        })
    }

    private fun requestApiForNewsList() {
        newsListViewModel.getNewsListFromServer(newsListViewModel.newsHeadLines)
        getNewsList()
    }

    private fun onClickListener() {
        newsListBinding.ivAscendingList.setOnClickListener {
            changeOrderToAscending()
        }
        newsListBinding.ivDescendingList.setOnClickListener {
            changeOrderTodescending()
        }
        newsListBinding.ivFilterList.setOnClickListener { showPopupMenu(newsListBinding.ivFilterList) }
        newsListBinding.slPullToRefresh.setOnRefreshListener { reloadNewsList() }
    }

    private fun reloadNewsList() {
        if (network) {
            newsListViewModel.deletNewsListIndataBase()
            newsListViewModel.currentList == true
            newsListViewModel.pageNumber = 1
            requestApiForNewsList()
            newsListBinding.slPullToRefresh.isRefreshing = false
        } else newsListBinding.slPullToRefresh.isRefreshing = false
    }

    private fun changeOrderToAscending() {
        newsListViewModel.ChangeListOrder = "Ascending"
        getNewsListFromDatabase()
        newsListBinding.ivAscendingList.visibility = View.GONE
        newsListBinding.ivDescendingList.visibility = View.VISIBLE
    }

    private fun changeOrderTodescending() {
        newsListViewModel.ChangeListOrder = "Descending"
        getNewsListFromDatabase()
        newsListBinding.ivDescendingList.visibility = View.GONE
        newsListBinding.ivAscendingList.visibility = View.VISIBLE
    }

    private fun showPopupMenu(ivFilterList: ImageView) {
        val popup = PopupMenu(this, ivFilterList)
        popup.inflate(R.menu.menu_filter_list)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            when (it.itemId) {
                R.id.economy -> Toast.makeText(this, "Economy", Toast.LENGTH_SHORT).show().also {
                    filterBy("economy")
                }
                R.id.history -> Toast.makeText(this, "History", Toast.LENGTH_SHORT).show().also {
                    filterBy("history")
                }
                R.id.science -> Toast.makeText(this, "Science", Toast.LENGTH_SHORT).show().also {
                    filterBy("science")
                }
                R.id.space -> Toast.makeText(this, "Space", Toast.LENGTH_SHORT).show().also {
                    filterBy("space")
                }
                R.id.sports -> Toast.makeText(this, "Sports", Toast.LENGTH_SHORT).show().also {
                    filterBy("sports")
                }
            }
            true
        })
        popup.show()
    }

    private fun filterBy(newsType: String) {
        newsListViewModel.currentList = true
        newsListViewModel.pageNumber = 1
        newsListViewModel.newsHeadLines = newsType
        if (network == false) getNewsListFromDatabase()
        else requestApiForNewsList()
    }

    private fun getNewsList() {
        newsListViewModel.isLoadingLiveData.observe(this, Observer {
            if (it == true) {
                showProgressBar()
            } else {
                newsListViewModel.networkErrorLivedata.observe(this, Observer { networkResult ->
                    if (networkResult == GOOD_NETWORK) {
                        fetchNewsList()
                    } else {
                        showNetWorkError()
                    }
                })
                hideProgressBar()
            }
        })

    }

    private fun fetchNewsList() {
        newsListViewModel.newsListLivedata.observe(this, Observer { newsListResult ->
            when (newsListResult) {
                newsListResult -> addListToExistingList(newsListResult)
                else -> newsListBinding.tvHomeInfo.text = getString(R.string.Info)
            }
        })
    }

    private fun addListToExistingList(newsListResult: NANewsListData) {
        if (newsListViewModel.pageNumber > 1) {
            newsListViewModel.newList =
                ArrayList(newsListResult.articles.sortedByDescending { it.publishedAt })
            newsListViewModel.oldList.addAll(newsListViewModel.newList.sortedBy { it?.pageNo }
                .sortedByDescending { it?.publishedAt })
            homeAdapter.updateNewsList(newsListViewModel.newList)
            newsListViewModel.listSizes = newsListViewModel.oldList.size
            isScrolling = true
        } else {
            if (newsListViewModel.currentList == true) {
                newsListViewModel.oldList =
                    ArrayList(newsListResult.articles.sortedBy { it.pageNo }
                        .sortedByDescending { it.publishedAt })
                sendToAdapter(newsListViewModel.oldList)
                newsListViewModel.currentList == false
            } else {
                newsListViewModel.newList =
                    ArrayList(newsListResult.articles.sortedBy { it.pageNo }
                        .sortedByDescending { it.publishedAt })
                sendToAdapter(newsListViewModel.newList)
            }
        }
    }

    private fun sendToAdapter(
        newList: ArrayList<NANewsListDataItem?>
    ) {
        newsListViewModel.listSizes = newList.size
        homeAdapter = NAAdapterClass(newList)
        newsListBinding.rvNewsList.adapter = homeAdapter
        newsListBinding.tvHomeInfo.text = ""
        newsListBinding.rvNewsList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
       // newsListBinding.rvNewsList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        isScrolling = true
    }


    private fun showNetWorkError() {
        Toast.makeText(this, getString(R.string.badNetwork), Toast.LENGTH_SHORT).show()
    }

    var network = false
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            network = false
            newsListBinding.tvNetworkBanner.visibility = View.VISIBLE
            getNewsListFromDatabase()

        } else {
            network = true
            newsListBinding.tvNetworkBanner.visibility = View.INVISIBLE
            requestApiForNewsList()
        }
    }

    private fun getNewsListFromDatabase() {
        newsListViewModel.getOfflineNews(newsListViewModel.newsHeadLines)
        showOfflineNews()
    }

    private fun showOfflineNews() {
        newsListViewModel.offLineLivedata.observe(this, Observer { newsListResult ->
            newsListViewModel.offlineList = newsListResult
            if (newsListViewModel.ChangeListOrder == "Descending") {
                newsListViewModel.offlineList =
                    ArrayList(newsListViewModel.offlineList.sortedBy { it?.pageNo }
                        .sortedByDescending { it?.publishedAt })
            } else {
                newsListViewModel.offlineList =
                    ArrayList(newsListViewModel.offlineList.sortedBy { it?.pageNo }
                        .sortedBy { it?.publishedAt })
            }
            homeAdapter = NAAdapterClass(newsListViewModel.offlineList)
            newsListBinding.rvNewsList.adapter = homeAdapter
            newsListBinding.rvNewsList.layoutManager = GridLayoutManager(this, 1)
            if (newsListResult.size != 0) {
                newsListBinding.tvHomeInfo.text = ""
            } else {
                newsListBinding.tvHomeInfo.text = getString(R.string.Info)
            }
        })
    }

    private fun hideProgressBar() {
        newsListBinding.pbPagination.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar() {
        newsListBinding.pbPagination.visibility = View.VISIBLE
        isLoading = true
    }

    override fun onStart() {
        super.onStart()
        Log.d("t","Start")
    }
    override fun onResume() {
        super.onResume()
        Log.d("e","Resume")
        NABroadCastReciever.connectivityReceiverListener = this
    }

    override fun onPause() {
        super.onPause()
        Log.d("w","Pause")
    }
    override fun onRestart() {
        super.onRestart()
        Log.d("q","Restart")
    }

    override fun onStop() {
        super.onStop()
        Log.d("u","Stop")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("y","Destroy")
    }


}
