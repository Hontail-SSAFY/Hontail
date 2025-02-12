package com.hontail.ui.cocktail.screen


import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentCocktailCommentBottomSheetBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.adapter.CocktailCommentAdapter
import com.hontail.ui.cocktail.viewmodel.CocktailCommentViewModel
import kotlin.math.absoluteValue

class CocktailCommentBottomSheetFragment : BaseBottomSheetFragment<FragmentCocktailCommentBottomSheetBinding>(
    FragmentCocktailCommentBottomSheetBinding::bind,
    R.layout.fragment_cocktail_comment_bottom_sheet
) {

    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailCommentViewModel by viewModels()

    private lateinit var cocktailCommentAdapter: CocktailCommentAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityViewModel.cocktailId.value?.let { viewModel.setCocktailId(it) }
        viewModel.setUserId(activityViewModel.userId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCocktailComment()
        initAdapter()
        initEvent()
    }

    // ViewModel Observe 등록
    private fun observeCocktailComment() {

        binding.apply {

            // userId
            viewModel.userId.observe(viewLifecycleOwner) { userId ->
                imageViewCocktailCommentBottomSheetSend.isEnabled = userId > 0
                imageViewCocktailCommentBottomSheetSend.alpha = if (userId > 0) 1.0f else 0.5f
            }

            // 댓글 리스트
            viewModel.comments.observe(viewLifecycleOwner) { commentList ->
                cocktailCommentAdapter.updateComments(commentList)
            }
        }
    }

    private fun initAdapter() {

        binding.apply {

            cocktailCommentAdapter = CocktailCommentAdapter(mainActivity, mutableListOf(), viewModel.userId.value)

            recyclerViewCocktailCommentBottomSheet.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = cocktailCommentAdapter
            }
        }
    }

    // event
    private fun initEvent() {

        binding.apply {

            // 댓글 전송 버튼
            imageViewCocktailCommentBottomSheetSend.setOnClickListener {

                val content = editTextCocktailCommentBottomSheetMessage.text.toString()

                if(content.isNotBlank()) {
                    viewModel.insertComment(content)
                    editTextCocktailCommentBottomSheetMessage.text.clear()
                }
            }
        }
    }
}