package com.theshoremedia.modules.home.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.databinding.FragmentManualFactCheckingBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.utils.KeyBoardManager
import com.theshoremedia.utils.ToastUtils
import com.theshoremedia.utils.extensions.makeVisible

/**
 * A simple [Fragment] subclass.
 */
class ManualFactCheckingFragment : BaseFragment() {
    private lateinit var binding: FragmentManualFactCheckingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manual_fact_checking,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        binding.tvInvestigate.setOnClickListener {
            if (binding.etClaim.text.toString().isEmpty()) {
                ToastUtils.makeToast(mContext, getString(R.string.err_empty_claim))
                return@setOnClickListener
            }
            val direction =
                HomeFragmentDirections.actionToSearchResult(binding.etClaim.text.toString())
            getNavController().navigate(direction)
        }


        KeyBoardManager.keyboardVisibilityListener(mContext as MainActivity, this) {
            binding.llServiceStatus.makeVisible(isVisible = !it)

            //scroll to last view
            val lastChild: View =
                binding.scrollView.getChildAt(binding.scrollView.childCount - 1)
            val bottom: Int = lastChild.bottom + binding.scrollView.paddingBottom
            val sy: Int = binding.scrollView.scrollY
            val sh: Int = binding.scrollView.height
            val delta = bottom - (sy + sh)

            binding.scrollView.smoothScrollBy(0, delta)
        }
    }


}
