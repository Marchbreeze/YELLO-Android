package com.example.data.repository

import com.example.data.datasource.RecommendDataSource
import com.example.data.model.request.recommend.toRequestDto
import com.example.domain.entity.RecommendAddModel
import com.example.domain.entity.RecommendModel
import com.example.domain.entity.RequestRecommendKakaoModel
import com.example.domain.repository.RecommendRepository
import javax.inject.Inject

class RecommendRepositoryImpl @Inject constructor(
    private val recommendDataSource: RecommendDataSource
) : RecommendRepository {

    override suspend fun postToGetKakaoFriendList(
        page: Int, request: RequestRecommendKakaoModel
    ): RecommendModel? {
        return recommendDataSource.postToGetKakaoListData(
            page, request.toRequestDto()
        ).data?.toRecommendModel()
    }

    override suspend fun getSchoolFriendList(page: Int): RecommendModel? {
        return recommendDataSource.getSchoolListData(
            page
        ).data?.toRecommendModel()
    }

    override suspend fun postFriendAdd(friendId: Long): RecommendAddModel {
        return recommendDataSource.postFriendAdd(friendId).toRecommendAddModel()
    }

}