package ru.netology.homework_2_resources.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
/*import android.widget.ListAdapter*/
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.homework_2_resources.databinding.CardPostBinding
import ru.netology.homework_2_resources.dto.Post

/* даем алиас (альтернитивное имя) OnLikeClickListener */
typealias OnLikeClickListener = (Post) -> Unit /* что это за синтаксис такой?? */
typealias OnShareClickListener = (Post) -> Unit

class PostAdapter(
    private val onLikeClicked: OnLikeClickListener, /* private val onLikeClicked: (Post) -> Unit, */
    private val onShareClicked: OnShareClickListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        Log.d("PostAdapter", "Создался ViewHolder")
        return PostViewHolder(binding = binding, onLikeClicked = onLikeClicked, onShareClicked = onShareClicked)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Log.d("PostAdapter", "произошла привязка ViewHolder к данным (onBindViewHolder) $position")
        holder.bind(getItem(position))
    }


}