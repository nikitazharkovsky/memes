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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
//        val textView: TextView = root.findViewById(R.id.text_dashboard)
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        val progressBar = root.findViewById<ProgressBar>(R.id.dashboardProgressBar)
        progressBar.visibility = View.VISIBLE

        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_list)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = mLayoutManager

        InitialLoadMemes(recyclerView, root, progressBar)

        val swipeContainer = root.findViewById(R.id.swipeContainer) as SwipeRefreshLayout
        swipeContainer.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            LoadMemes(recyclerView, root, progressBar, swipeContainer)
        })
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        return root

    }

    private fun InitialLoadMemes(
        recyclerView: RecyclerView,
        root: View,
        progressBar: ProgressBar
    ) {
        NetworkService.getInstance()
                .jsonApi
                .memes()
                .enqueue(object : Callback<List<MemDto>> {
                    override fun onResponse(call: Call<List<MemDto>>, response: Response<List<MemDto>>) {
                        println(response.code())
                        val errorLoadMemesTV = root.findViewById<TextView>(R.id.errorLoadMemesTV)
                        val retryTV = root.findViewById<TextView>(R.id.retryTV)
                        if (response.code() != 200) {
                            errorLoadMemesTV.text = getText(R.string.can_not_load_memes)
                            retryTV.text = getText(R.string.try_again)
                        } else {
                            val memes = response.body()!!
                            val adapter = DataAdapter(activity, memes.toMutableList())
                            recyclerView.adapter = adapter
                            errorLoadMemesTV.text = ""
                            retryTV.text = ""
                        }
                        progressBar.visibility = View.GONE
                    }

                    override fun onFailure(call: Call<List<MemDto>>, t: Throwable) {
                        progressBar.visibility = View.GONE
                    }
                })
    }

    private fun LoadMemes(
        recyclerView: RecyclerView,
        root: View,
        progressBar: ProgressBar,
        swipeContainer: SwipeRefreshLayout?
    ) {
        NetworkService.getInstance()
            .jsonApi
            .memes()
            .enqueue(object : Callback<List<MemDto>> {
                override fun onResponse(call: Call<List<MemDto>>, response: Response<List<MemDto>>) {
                    println(response.code())
                    val errorLoadMemesTV = root.findViewById<TextView>(R.id.errorLoadMemesTV)
                    val retryTV = root.findViewById<TextView>(R.id.retryTV)
                    if (response.code() != 200) {
                        val errorLoadMemes = getText(R.string.can_not_load_memes)
                        val retry = getText(R.string.try_again)
                        showSnackBar("$errorLoadMemes \n $retry")
                    } else {
                        val memes = response.body()!!
                        val adapter = recyclerView.adapter as DataAdapter;
                        adapter.addAll(memes)
                        errorLoadMemesTV.text = ""
                        retryTV.text = ""
                    }
                    progressBar.visibility = View.GONE
                    swipeContainer?.setRefreshing(false);
                }

                override fun onFailure(call: Call<List<MemDto>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    val errorLoadMemes = getText(R.string.can_not_load_memes)
                    val retry = getText(R.string.try_again)
                    showSnackBar("$errorLoadMemes \n $retry")
                    swipeContainer?.setRefreshing(false);
                }

                private fun showSnackBar(str: String) {
                    val snackbar = Snackbar.make(
                        root,
                        str,
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.view.setBackgroundResource(R.color.errorBackground)
                    snackbar.show()
                }
            })
    }
}