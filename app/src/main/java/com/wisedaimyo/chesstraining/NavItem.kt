package com.wisedaimyo.chesstraining

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.wisedaimyo.chesstraining.main.data.models.NAV_COURSE
import com.wisedaimyo.chesstraining.main.data.models.NAV_HOME
import com.wisedaimyo.chesstraining.main.data.models.NAV_SETTINGS
import com.wisedaimyo.chesstraining.main.data.models.NAV_TRAINING

sealed class NavItem(
    @StringRes val title: Int,
    val icon: ImageVector,
    val navRoute: String
) {
    object Home: NavItem(title = R.string.home, Icons.Default.Home, NAV_HOME)
    object Training: NavItem(title = R.string.training, Icons.Default.DateRange, NAV_TRAINING)
    object Course: NavItem(title = R.string.courses, Icons.Default.Face, NAV_COURSE)
    object Settings: NavItem(title = R.string.settings, Icons.Default.Settings, NAV_SETTINGS)
}