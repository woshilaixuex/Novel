package com.test.novel.view.bookStore

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.novel.R
import com.test.novel.databinding.RandomBookItemBinding
import com.test.novel.databinding.RandomViewBinding
import com.test.novel.databinding.RankBookItemBinding
import com.test.novel.databinding.RankViewBinding
import com.test.novel.model.BookBrief
import com.test.novel.model.vo.BookVo
import com.test.novel.utils.SizeUtils
import com.test.novel.view.customView.GridSpacingItemDecoration
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json

class BookStoreAdapter(
    private val onBookClick: (BookVo) -> Unit,
    private val onDataLoaded:()->Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var rankList: List<BookVo> = emptyList()
    private var randomList: List<BookVo> = emptyList()
    private val rankViewAdapter = RankViewAdapter(onBookClick)
    private val randomViewAdapter = RandomViewAdapter(onBookClick)

    companion object {
        const val TYPE_RANK = 0
        const val TYPE_RANDOM = 1
    }
    // 排行视图Holder
    inner class RankViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RankViewBinding.bind(view)
        val recommend = binding.recommendRank
        val hot = binding.hotRank
        val recommendText = binding.recommendRankText
        val hotText = binding.hotRankText
        val rankView = binding.rankBookList
    }
    // 推荐视图Holder
    inner class RandomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RandomViewBinding.bind(view)
        val randomView = binding.randomBookList
    }
    // 排行适配器
    inner class RankViewAdapter(
        private val onBookClick: (BookVo) -> Unit
    ): RecyclerView.Adapter<RankViewAdapter.RankViewItemHolder>() {

        private var rankList: List<BookVo> = emptyList()

        inner class RankViewItemHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val binding = RankBookItemBinding.bind(view)
            val main = view
            val bookCover = binding.bookCover
            val bookTitle = binding.bookName
            val rank = binding.rank
            val type = binding.type
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewItemHolder {
            return RankViewItemHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.rank_book_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RankViewItemHolder, position: Int) {
            holder.main.setOnClickListener {
                onBookClick(rankList[position])
            }
            val book = rankList[position]
            holder.bookTitle.text = book.title
            holder.rank.text = String.format("%d", position + 1)
            if (position < 3) {
                holder.rank.setTextColor(Color.parseColor("#cfa570"))
            }
            //去除两边的括号
            holder.type.text = book.categories.joinToString(" ") { it }
            Glide.with(holder.itemView.context)
                .load(book.coverUrl)
                .placeholder(R.drawable.cover1)
                .into(holder.bookCover)
        }

        override fun getItemCount(): Int {
            return rankList.size
        }

        fun updateData(newList: List<BookVo>) {
            val oldSize = rankList.size
            rankList = newList
            if (oldSize > 0 && newList.isEmpty()) {
                notifyItemRangeRemoved(0, oldSize) // 移除所有item
            } else if (oldSize == 0 && newList.isNotEmpty()) {
                notifyItemRangeInserted(0, newList.size) // 插入新item
            } else {
                notifyItemRangeChanged(0, maxOf(oldSize, newList.size)) // 更新变化
            }
        }
    }
    // 推荐适配器
    inner class RandomViewAdapter(
        onBookClick: (BookVo) -> Unit
    ) : RecyclerView.Adapter<RandomViewAdapter.RandomViewItemHolder>() {
//        init {
//            viewModel.bookStoreState.onEach { state ->
//                updateData(state.recommend)
//            }.launchIn(fragment.viewLifecycleOwner.lifecycleScope)
//        }

        private var randomList: List<BookVo> = emptyList()

        inner class RandomViewItemHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val binding = RandomBookItemBinding.bind(view)
            val bookCover = binding.bookCover
            val bookTitle = binding.bookName
            val type = binding.bookType
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomViewItemHolder {
            return RandomViewItemHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.random_book_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RandomViewItemHolder, position: Int) {
            val book = randomList[position]
            holder.bookTitle.text = book.title
            holder.type.text = book.author
        }

        override fun getItemCount(): Int {
            return randomList.size
        }

        fun updateData(newList: List<BookVo>) {
            Log.e("TAG", "updateData: $newList")
            val oldSize = randomList.size
            randomList = newList
            if (oldSize > 0 && newList.isEmpty()) {
                notifyItemRangeRemoved(0, oldSize) // 移除所有item
            } else if (oldSize == 0 && newList.isNotEmpty()) {
                notifyItemRangeInserted(0, newList.size) // 插入新item
            } else {
                notifyItemRangeChanged(0, maxOf(oldSize, newList.size)) // 更新变化
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_RANK
            1 -> TYPE_RANDOM
            else -> TYPE_RANK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_RANK -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.rank_view, parent, false)
                RankViewHolder(view)
            }

            TYPE_RANDOM -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.random_view, parent, false)
                RandomViewHolder(view)
            }

            else -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.rank_view, parent, false)
                RankViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RankViewHolder -> {
                holder.rankView.adapter = rankViewAdapter
                // 其他逻辑
            }
            is RandomViewHolder -> {
                holder.randomView.adapter = randomViewAdapter
                val spacing = SizeUtils.dp2px(8f) // 间距大小，单位为 px
                val includeEdge = true // 是否包括边缘间距
                holder.randomView.addItemDecoration(
                    GridSpacingItemDecoration(2, spacing, spacing, includeEdge)
                )
            }
        }
    }


    override fun getItemCount(): Int {
        return 2
    }
    // 数据更新方法
    fun updateRankList(newList: List<BookVo>) {
        if (newList != rankList) {
            rankList = newList
            rankViewAdapter.updateData(newList)
            onDataLoaded() // 通知数据加载完成
        }
    }
    fun updateRandomList(newList: List<BookVo>) {
        if (newList != randomList) {
            randomList = newList
            randomViewAdapter.updateData(newList)
        }
    }

    // 清理资源
    fun clear() {
        val rankSize = rankList.size
        val randomSize = randomList.size
        rankList = emptyList()
        randomList = emptyList()
        if (rankSize > 0) {
            rankViewAdapter.updateData(rankList)
        }
        if (randomSize > 0) {
            randomViewAdapter.updateData(randomList)
        }
    }

}