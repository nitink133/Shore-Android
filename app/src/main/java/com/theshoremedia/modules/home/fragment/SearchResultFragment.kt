package com.theshoremedia.modules.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.database.helper.FactCheckHistoryDatabaseHelper
import com.theshoremedia.databinding.FragmentSearchResultBinding
import com.theshoremedia.modules.base.BaseFragment
import com.theshoremedia.modules.floatingview.credibility_checker.model.ValidateNewsReqModel
import com.theshoremedia.modules.home.fragment.SearchResultFragmentArgs.fromBundle
import com.theshoremedia.retrofit.API
import com.theshoremedia.retrofit.model.GenericResponseModel
import com.theshoremedia.utils.Log
import com.theshoremedia.utils.ObjectUtils
import com.theshoremedia.utils.extensions.makeVisible

class SearchResultFragment : BaseFragment() {
    private lateinit var binding: FragmentSearchResultBinding
    private val claim: String by lazy {
        fromBundle(arguments!!).claim
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search_result, container, false)
        return binding.root
    }


    private fun callValidateNews(claim: String) {

        binding.lvSearching.makeVisible(isVisible = true)
        binding.llContentView.visibility = View.INVISIBLE

        val reqModel = ValidateNewsReqModel(query = claim)

        //First, We'll check if forwardedMessage stored in local database or not. If yes, then we'll return it from there. Else, will hit API
        FactCheckHistoryDatabaseHelper.instance?.getNews(reqModel.query) {
            //Here it == null states, that searched message is not stored in local database. Thus, we'll hit API for further verification.
            if (it == null) {
                Log.d(
                    "Nitin",
                    "Did not find the search query in local database, thus we're hitting API for further verification"
                )
                callAPIAsDataNotInLocal(reqModel)
                return@getNews
            }

            Log.d("Nitin", "Searched query has been found in local database.")
            reqModel.isProcessed = true

            moveToArticle(isPopCurrent = true, factCheckHistoryModel = it)
        }

    }

    //@method is used for calling news validation API
    private fun callAPIAsDataNotInLocal(reqModel: ValidateNewsReqModel) {
        reqModel.isProcessing = true
        API.callValidateNews(mContext = mContext, requestBody = reqModel) {
            Log.d("Nitin", "validateNews API response ${ObjectUtils.toString(it)}")
            if (it !is GenericResponseModel<*>) {
                return@callValidateNews
            }
            FactCheckHistoryDatabaseHelper.instance?.insertNews(it.data as FactCheckHistoryModel)
            moveToArticle(factCheckHistoryModel = it.data as FactCheckHistoryModel)
        }
    }


    private fun moveToArticle(
        isPopCurrent: Boolean = false,
        factCheckHistoryModel: FactCheckHistoryModel
    ) {
        val direction =
            HomeFragmentDirections.actionToFavArticle(factCheckHistoryModel)
        getNavController().navigate(direction)
        if (isPopCurrent)
            getNavController().popBackStack()
    }

    override fun onPageRefreshListener(data: Bundle?) {
        super.onPageRefreshListener(data)
        (mContext as MainActivity).setTitle(getString(R.string.search_result))
    }
}
