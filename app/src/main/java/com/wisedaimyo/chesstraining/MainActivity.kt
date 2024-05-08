package com.wisedaimyo.chesstraining

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.wisedaimyo.chesstraining.main.components.checkInitialNetworkState
import com.wisedaimyo.chesstraining.main.data.models.model.User
import com.wisedaimyo.chesstraining.main.screens.LostConnection
import com.wisedaimyo.chesstraining.main.screens.NavigationRegisterRouting
import com.wisedaimyo.chesstraining.main.screens.NavigationRouting
import com.wisedaimyo.chesstraining.main.ui.theme.ChessTrainingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @SuppressLint(
        "UnusedMaterialScaffoldPaddingParameter",
        "SuspiciousIndentation"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChessTrainingTheme {

                val context = LocalContext.current
                var isNetworkAvailable by remember { mutableStateOf(checkInitialNetworkState(context)) }
                val networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        runOnUiThread {
                            isNetworkAvailable = true
                        }
                    }

                    override fun onLost(network: Network) {
                        runOnUiThread {
                            isNetworkAvailable = false
                        }
                    }
                }

                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)

                val isUserSignedOut = viewModel.getAuthState().collectAsState().value

                LaunchedEffect(key1 = isUserSignedOut) {
                    Firebase.auth.currentUser?.let { viewModel.getCurrentUser(it.uid) }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.background)
                ) {
                      if (isNetworkAvailable) {
                          if (!isUserSignedOut && viewModel.currentUser.displayName != null) {
                              NavigationRouting()
                          } else {
                            viewModel.currentUser = User()
                            NavigationRegisterRouting(rememberNavController())
                          }
                      } else {
                          LostConnection()
                      }
                  }
            }
        }


    }
}

