package com.zharkovsky.memes.services

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zharkovsky.memes.R
import com.zharkovsky.memes.models.MemDto

internal class DataAdapter(
        private val context: Context?,
        private val memes: MutableList<MemDto>
) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mem = memes[position]
        holder.titleView.text = mem.title
        holder.likeView.isSelected = mem.isFavorite
        holder.likeView.setOnClickListener {
            it.isSelected = !it.isSelected
        }
        Glide.with(context!!).load(mem.photoUrl).into(holder.imageView)
    }

    fun addAll(newMemes: List<MemDto>) {
        memes.addAll(newMemes)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return memes.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<View>(R.id.mem_image) as ImageView
        val titleView = view.findViewById<View>(R.id.mem_title) as TextView
        val likeView = view.findViewById<View>(R.id.mem_like) as ImageView
    }
}