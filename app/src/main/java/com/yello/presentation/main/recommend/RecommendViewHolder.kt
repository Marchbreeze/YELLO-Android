package com.yello.presentation.main.recommend

import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.domain.entity.RecommendModel
import com.yello.databinding.ItemRecommendListBinding

class RecommendViewHolder(val binding: ItemRecommendListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(item: RecommendModel) {
        binding.tvRecommendItemName.text = item.name
        binding.tvRecommendItemSchool.text = item.school
        item.thumbnail?.let { thumbnail ->
            binding.ivRecommendItemThumbnail.load(thumbnail) {
                transformations(CircleCropTransformation())
            }
        }
    }
}