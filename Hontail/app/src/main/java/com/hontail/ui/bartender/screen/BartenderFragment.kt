package com.hontail.ui.bartender.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R.*
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.Cocktail
import com.hontail.databinding.FragmentBatenderBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.bartender.adapter.BartenderAdapter
import com.hontail.util.CommonUtils

class BartenderFragment : BaseFragment<FragmentBatenderBinding>(
    FragmentBatenderBinding::bind,
    layout.fragment_batender
) {
    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var bartenderAdapter: BartenderAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(state = true)
        activityViewModel.receiveBartenderGreeting()

        observeBartender()
        initToolbar()
        initAdapter()
        initEvent()
    }

    // 툴바 설정
    private fun initToolbar() {
        binding.apply {
            toolbarBartender.apply {
                setNavigationIcon(drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack("bartenderFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    // ViewModel Observe 등록
    private fun observeBartender() {

        binding.apply {

            // 메시지 리스트
            activityViewModel.messages.observe(viewLifecycleOwner)  { messages ->
                bartenderAdapter.updateMessages(messages)
                scrollToLastMessage()
            }
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            bartenderAdapter = BartenderAdapter(mainActivity, emptyList())

            recyclerViewBartender.adapter = bartenderAdapter
            recyclerViewBartender.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    // 이벤트들
    private fun initEvent() {
        binding.apply {

            // 음성 녹음 이벤트
            imageViewBartenderVoice.setOnClickListener {
                val dialog = BartenderDialogFragment(CommonUtils.BartenderRecordMode.READY)
                dialog.show(parentFragmentManager, "BartenderDialog")
            }

            // 보내기 이벤트
            imageViewBartenderSend.setOnClickListener {

                val messageText = editTextBartenderMessage.text.toString()

                if(messageText.isNotEmpty()) {
                    activityViewModel.sendMessageToBartender(messageText)
                    editTextBartenderMessage.text.clear()
                }
            }

            bartenderAdapter.bartenderListener = object : BartenderAdapter.ItemOnClickListener {

                // 칵테일 상세 화면으로 가기.
                override fun onClickCocktailImage(cocktailId: Int) {
                    activityViewModel.setCocktailId(cocktailId)
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                }
            }
        }
    }

    // 키보드 올라오면 맨 마지막 아이템으로 가기.
    private fun scrollToLastMessage() {
        binding.apply {
            recyclerViewBartender.postDelayed({
                if(bartenderAdapter.itemCount > 0) {
                    recyclerViewBartender.smoothScrollToPosition(bartenderAdapter.itemCount - 1)
                }
            }, 100)
        }
    }

}

data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val timestamp: String,
    val cocktail: Cocktail? = null
)