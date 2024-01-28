package com.example.domain.entity

data class ProfileUserModel(
    val userId: Long,
    val name: String,
    val profileImageUrl: String,
    val group: String,
    var yelloId: String,
    val gender: String,
    val email: String,
    val yelloCount: Int,
    val friendCount: Int,
    val point: Int,
    val groupType: String,
    val groupName: String,
    val subGroupName: String,
    val groupAdmissionYear: Int
) {
    constructor() : this(0, "", "","","", "", "", 0, 0, 0, "", "", "", 0)
}