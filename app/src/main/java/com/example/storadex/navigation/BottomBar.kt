package com.example.storadex.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.storadex.navigation.AppScreens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(AppScreens.Home.route, Icons.Default.Home, "Home")
    object Album : BottomNavItem(AppScreens.Album.route, Icons.Default.Collections, "Álbum")
    object Social : BottomNavItem("social", Icons.Default.Group, "Social")
    object Menu  : BottomNavItem("menu", Icons.Default.MoreVert, "Más")
}

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Album,
        BottomNavItem.Social,
        BottomNavItem.Menu
    )

    var menuVisible by remember { mutableStateOf(false) }
    var buttonPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    // Animación de entrada
    val offsetX by animateDpAsState(
        targetValue = if (menuVisible) 0.dp else 100.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Barra inferior
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavigationBar {
                items.forEach { item ->
                    if (item != BottomNavItem.Menu) {
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = navController.currentBackStackEntryAsState().value?.destination?.hierarchy?.any {
                                it.route == item.route
                            } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        )
                    } else {
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = false,
                            onClick = { menuVisible = true },
                            modifier = Modifier.onGloballyPositioned {
                                buttonPosition = it.positionInParent()
                            }
                        )
                    }
                }
            }
        }

        // Menú desplegable fuera de la barra para que no se recorte
        if (menuVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { menuVisible = false } // Fondo para cerrar
            )

            Card(
                modifier = Modifier
                    .width(200.dp)
                    .offset(
                        x = with(density) { buttonPosition.x.toDp() - 60.dp + offsetX },
                        y = with(density) { buttonPosition.y.toDp() - 130.dp }
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.BottomStart), // Alinea relativo al contenedor
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Acerca de",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { menuVisible = false }
                            .padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Ajustes",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { menuVisible = false }
                            .padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Cerrar sesión",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // 👇 Aquí agregas el cierre de sesión real
                                FirebaseAuth.getInstance().signOut()

                                // 👇 Y navegas al login (si quieres cerrar el menú antes, hazlo antes del signOut)
                                navController.navigate(AppScreens.Login.route) {
                                    popUpTo(0) { inclusive = true } // Limpia el back stack
                                }
                            }
                            .padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }
    }
}