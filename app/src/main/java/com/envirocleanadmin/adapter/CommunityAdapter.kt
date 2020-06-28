package com.envirocleanadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.envirocleanadmin.base.BaseBindingAdapter
import com.envirocleanadmin.base.BaseBindingViewHolder
import com.envirocleanadmin.data.response.ListOfCommunityResponse
import com.envirocleanadmin.databinding.ItemRowCommunityBinding
import com.envirocleanadmin.databinding.LoadMoreProgressBinding


/**
 * Created by imobdev on 23/3/20
 */
class CommunityAdapter : BaseBindingAdapter<ListOfCommunityResponse.Result?>() {


    override fun bind(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewDataBinding {
        return  ItemRowCommunityBinding.inflate(inflater, parent, false)
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder, position: Int) {

        when (getItemViewType(position)) {
            VIEW_TYPE_ITEM -> {
                val binding = holder.binding as ItemRowCommunityBinding
                val item = items[position]
                item?.let {
                   binding.tvCommunitiesName.text=item.commName
                   binding.tvCommunitiesArea.text=""+item.areaCount+" area under this community"
                }
            }

        }

    }

    fun filter(string: String) {
        items = ArrayList(allItems.filter { it!!.commName!!.toLowerCase().contains(string.toLowerCase()) })
        notifyDataSetChanged()
    }
}