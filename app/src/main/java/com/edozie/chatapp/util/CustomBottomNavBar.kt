package com.edozie.chatapp.util

import com.edozie.chatapp.R

sealed class CustomBottomNavBar(
    val route: String,
    val label: String,
    val icon: Int
) {

    object Chats : CustomBottomNavBar("chats", "Chats", R.drawable.chats_ic)
    object Profile : CustomBottomNavBar("profile", "Profile", R.drawable.profile_ic)
}
