package com.example.pokedex2

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.pokedex2.data.PokemonRepository
import com.example.pokedex2.ui.PokedexApp
import com.example.pokedex2.ui.theme.Pokedex2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pokedex2Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PokemonRepository.filesDir = filesDir
                    PokemonRepository.cacheDir = cacheDir

                    val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                    val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                    var isOnline: Boolean
                    if (networkInfo != null) {
                        isOnline = networkInfo.isConnected
                    } else {
                        isOnline = false
                    }
                    if (!isOnline) {
                        Toast.makeText(this, "No internet connection, you will be shown a list of cached entries", Toast.LENGTH_LONG).show()
                    }
                    Log.d("Network", "isOnline: $isOnline")
                    PokedexApp(isOnline = isOnline)
                }
            }
        }
    }
}
