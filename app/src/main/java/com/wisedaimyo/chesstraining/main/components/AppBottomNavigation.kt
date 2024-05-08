package com.wisedaimyo.chesstraining.main.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wisedaimyo.chesstraining.NavItem
import com.wisedaimyo.chesstraining.R


@Composable
fun AppBottomNavigation(
    navController: NavController
) {
    val navItems = listOf(NavItem.Home, NavItem.Training, NavItem.Course, NavItem.Settings)

    BottomNavigation(
        backgroundColor = colorResource(id = R.color.primary)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        navItems.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute==item.navRoute,
                selectedContentColor = colorResource(id = R.color.text),
                unselectedContentColor = colorResource(id = R.color.text).copy(alpha = 0.5F),
                onClick = {
                          navController.navigate(item.navRoute)
                },
                label = { Text(text = stringResource(id = item.title), color = colorResource(id = R.color.text)) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.navRoute) })
        }
    }
}