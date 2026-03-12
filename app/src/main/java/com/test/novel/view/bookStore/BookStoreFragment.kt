package com.test.novel.view.bookStore

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.test.novel.R
import com.test.novel.databinding.FragmentBookStoreBinding
import com.test.novel.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class BookStoreFragment : Fragment() {

    companion object {
        fun newInstance() = BookStoreFragment()
    }

    private val viewModel: BookStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.sendIntent(BookStoreIntent.DefaultData)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_book_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentBookStoreBinding.bind(view)
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.SearchBar)
        activity?.let {activity->
            WindowInsetsControllerCompat(activity.window, binding.root).apply {
                show(WindowInsetsCompat.Type.systemBars())
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            v.setPadding(0, SizeUtils.statusBarHeight, 0, SizeUtils.navigationBarHeight)
            insets
        }
        val navController = findNavController()
        val adapter = BookStoreAdapter(
            onBookClick = { book ->
                val action = BookStoreFragmentDirections.actionBookStoreFragmentToIntroductionFragment(
                    book
                )
                navController.navigate(action)
            },
            onDataLoaded = {
                binding.swipe.isRefreshing = false
                binding.loadingState.visibility = View.GONE
            }
        )
        binding.recycle.adapter = adapter
        // 监听加载状态
        viewModel.bookStoreState.onEach { state ->
            adapter.updateRankList(state.rank)
            adapter.updateRandomList(state.recommend)
            // 控制加载状态的显示
            binding.loadingState.visibility = if (state.isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
            // 控制用户交互
            binding.root.isClickable = !state.isLoading
            binding.recycle.isEnabled = !state.isLoading
            binding.swipe.isEnabled = !state.isLoading
            binding.SearchBar.isEnabled = !state.isLoading
            // 数据加载完成时隐藏刷新状态
            if (!state.isLoading && state.rank.isNotEmpty()) {
                binding.swipe.isRefreshing = false
                binding.loadingState.visibility = View.GONE
            }
        }.launchIn(lifecycleScope)
        binding.SearchBar.setOnClickListener {
            navController.navigate(R.id.SearchFragment)
        }
        binding.swipe.setOnRefreshListener {
            // 显示加载状态
            viewModel.sendIntent(BookStoreIntent.ClearData)
            binding.loadingState.visibility = View.VISIBLE
            viewModel.sendIntent(BookStoreIntent.DefaultData)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG", "onDestroy: ")
    }
}