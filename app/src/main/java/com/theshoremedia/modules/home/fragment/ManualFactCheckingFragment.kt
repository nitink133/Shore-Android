package com.theshoremedia.modules.home.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.databinding.FragmentManualFactCheckingBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.utils.KeyBoardManager
import com.theshoremedia.utils.ToastUtils
import com.theshoremedia.utils.extensions.makeVisible
import com.theshoremedia.utils.permissions.AccessibilityPermissionsUtils

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

    override fun onResume() {
        super.onResume()
        AccessibilityPermissionsUtils.verifyPermission(mContext!!) { isEnabled ->
            updateShieldStatus(isEnabled)
        }
    }

    private fun updateShieldStatus(isEnabled: Boolean) {
        binding.llServiceStatus.backgroundTintList =
            ContextCompat.getColorStateList(
                mContext!!,
                if (isEnabled) R.color.bg_success_green else R.color.bg_error_red
            )
        binding.tvAccServiceStatus.text =
            getString(
                if (isEnabled) R.string.success_whatsapp_real_time_fake_news_detection_is_activated_now
                else R.string.error_whatsapp_real_time_fake_news_detection_is_not_activated_now
            )
        binding.tvAccServiceStatus.textAlignment =
            if (isEnabled) View.TEXT_ALIGNMENT_CENTER else View.TEXT_ALIGNMENT_TEXT_START
        binding.btnActivate.makeVisible(isVisible = !isEnabled)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        binding.tvInvestigate.setOnClickListener {
            if (binding.etClaim.text.toString().isEmpty()) {
                ToastUtils.makeToast(mContext, getString(R.string.err_empty_claim))
                return@setOnClickListener
            }
            KeyBoardManager.hideKeyboard(requireActivity())
            val direction =
                HomeFragmentDirections.actionToSearch(binding.etClaim.text.toString())
            getNavController().navigate(direction)
        }

        binding.btnActivate.setOnClickListener {
            AccessibilityPermissionsUtils.checkPermission(mContext = mContext!!) { isEnabled ->
                updateShieldStatus(isEnabled)
            }
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
