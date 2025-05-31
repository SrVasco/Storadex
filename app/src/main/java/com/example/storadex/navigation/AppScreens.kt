package com.example.storadex.navigation

sealed class AppScreens(val route: String) {
    object Login  : AppScreens("login")
    object Home   : AppScreens("home")
    object Album  : AppScreens("album")
    object Cards  : AppScreens("cards")
    // puedes añadir más pantallas aquí (p.e. Social, Settings…)
}