package com.zharkovsky.memes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.zharkovsky.memes.R
import com.zharkovsky.memes.models.MemDto
import com.zharkovsky.memes.services.DataAdapter
import com.zharkovsky.memes.services.NetworkService
import com.zharkovsky.memes.viewModels.DashboardViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var root: View
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var mLayoutManager: StaggeredGridLayoutManager
    private lateinit var errorLoadMemesTV: TextView
    private lateinit var retryTV: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        initViews(inflater, container)
        initViewProperties()
        loadMemes(initial = true)
        return root
    }

    private fun initViews(inflater: LayoutInflater, container: ViewGroup?) {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        progressBar = root.findViewById(R.id.dashboardProgressBar)
        recyclerView = root.findViewById(R.id.recycler_list)
        swipeContainer = root.findViewById(R.id.swipeContainer)
        errorLoadMemesTV = root.findViewById(R.id.errorLoadMemesTV)
        retryTV = root.findViewById(R.id.retryTV)
        mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun initViewProperties() {
        progressBar.visibility = View.VISIBLE
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = mLayoutManager
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        )
        swipeContainer.setOnRefreshListener { loadMemes() }
    }

    private fun loadMemes(initial: Boolean = false) {
        NetworkService.getInstance()
                .jsonApi
                .memes()
                .enqueue(callback(initial))
    }

    private fun callback(initial: Boolean = false): Callback<List<MemDto>> {
        val errorLoadMemes = getText(R.string.can_not_load_memes)
        val retry = getText(R.string.try_again)

        return object : Callback<List<MemDto>> {
            override fun onResponse(call: Call<List<MemDto>>, response: Response<List<MemDto>>) {
                if (response.code() != 200) {
                    when {
                        initial -> {
                            errorLoadMemesTV.text = errorLoadMemes
                            retryTV.text = retry
                        }
                        else -> {
                            showSnackBar("$errorLoadMemes \n $retry")
                        }
                    }
                } else {
                    val memes = response.body()!!
                    when {
                        initial -> {
                            onInitialMemes(memes)
                        }
                        else -> {
                            onMemes(memes)
                        }
                    }
                    errorLoadMemesTV.text = ""
                    retryTV.text = ""
                }
                progressBar.visibility = View.GONE
                swipeContainer.isRefreshing = false
            }

            override fun onFailure(call: Call<List<MemDto>>, t: Throwable) {
                progressBar.visibility = View.GONE
                if (!initial) {
                    showSnackBar("$errorLoadMemes \n $retry")
                    swipeContainer.isRefreshing = false
                }
            }

            private fun onInitialMemes(memes: List<MemDto>) {
                val adapter = DataAdapter(activity, memes.toMutableList())
                recyclerView.adapter = adapter
            }

            private fun onMemes(memes: List<MemDto>) {
                val adapter = recyclerView.adapter as DataAdapter
                adapter.addAll(memes)
            }

            private fun showSnackBar(str: String) {
                val snackBar = Snackbar.make(
                        root,
                        str,
                        Snackbar.LENGTH_LONG
                )
                snackBar.view.setBackgroundResource(R.color.errorBackground)
                snackBar.show()
            }
        }
    }
}