package com.example.domain.repository

import com.example.domain.entity.RequestServiceTokenModel
import com.example.domain.entity.ServiceTokenModel
import com.example.domain.entity.onboarding.AddFriendListModel
import com.example.domain.entity.onboarding.GroupHighSchool
import com.example.domain.entity.onboarding.GroupList
import com.example.domain.entity.onboarding.HighSchoolList
import com.example.domain.entity.onboarding.RequestAddFriendModel
import com.example.domain.entity.onboarding.SchoolList
import com.example.domain.entity.onboarding.SignupInfo
import com.example.domain.entity.onboarding.UserInfo

interface OnboardingRepository {
    suspend fun postTokenToServiceToken(
        requestServiceTokenModel: RequestServiceTokenModel,
    ): Result<ServiceTokenModel?>

    suspend fun getSchoolList(
        keyword: String,
        page: Long,
    ): Result<SchoolList?>

    suspend fun getHighSchoolList(
        keyword: String,
        page: Long,
    ): Result<HighSchoolList?>

    suspend fun getGroupList(
        page: Int,
        school: String,
        search: String,
    ): Result<GroupList?>

    suspend fun getGroupHighSchool(
        name: String,
        keyword: String,
    ): Result<GroupHighSchool?>

    suspend fun getValidYelloId(
        yelloId: String,
    ): Result<Boolean?>

    suspend fun postToGetFriendList(
        request: RequestAddFriendModel,
        page: Int,
    ): Result<AddFriendListModel?>

    suspend fun postSignup(
        signupInfo: SignupInfo,
    ): Result<UserInfo?>
}
