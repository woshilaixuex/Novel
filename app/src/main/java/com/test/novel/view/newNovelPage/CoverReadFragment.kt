package com.test.novel.view.newNovelPage

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import com.test.novel.databinding.FragmentCoverReadBinding
import com.test.novel.model.vo.BookVo

/**
 * 实际用不上，这个片段使用时会以ViewBinding的形式使用
 */
class CoverReadFragment : Fragment() {


    private var _binding: FragmentCoverReadBinding? = null

    private val binding get() = _binding!!


    companion object {
        fun newInstance(bookVo: BookVo) = CoverReadFragment().apply {
            arguments = Bundle().apply {
                putParcelable("bookVo", bookVo)
            }
        }
    }
}