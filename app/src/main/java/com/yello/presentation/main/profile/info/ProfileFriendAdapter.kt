package com.yello.presentation.main.profile.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entity.ProfileUserModel
import com.example.domain.entity.RecommendModel
import com.yello.databinding.ItemFriendsListBinding

class ProfileFriendAdapter(private val itemClick: (ProfileUserModel, Int) -> (Unit)) :
    RecyclerView.Adapter<ProfileFriendViewHolder>() {

    private var itemList = mutableListOf<ProfileUserModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFriendViewHolder {
        val inflater by lazy { LayoutInflater.from(parent.context) }
        val binding: ItemFriendsListBinding =
            ItemFriendsListBinding.inflate(inflater, parent, false)
        return ProfileFriendViewHolder(binding, itemClick)
    }

    override fun onBindViewHolder(holder: ProfileFriendViewHolder, position: Int) {
        holder.onBind(itemList[position], position)
    }

    override fun getItemCount(): Int = itemList.size

    fun addItemList(newItems: List<ProfileUserModel>) {
        this.itemList.addAll(newItems)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }
}