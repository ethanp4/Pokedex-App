package com.example.pokedex2

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokedex2.network.Pokemon
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter


@Composable
fun PokedexApp() {
    Scaffold(
        modifier = Modifier
    ) { innerPadding ->
        PokemonList(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun PokemonList(
    viewModel: PokemonViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val pokemonList = viewModel.pokemonList.observeAsState(initial = emptyList())

    if (pokemonList.value.isEmpty()) {
        Text("Loading..")
    } else {
        PaginatedLazyColumn(
            items = pokemonList,
            loadMoreItems = viewModel.getPokemon(),
            listState = TODO(),
            buffer = TODO(),
            isLoading = TODO(),
            modifier = TODO()
        )
    }
}

@Composable
fun PokemonItem(pokemon: Pokemon) {
    Card (modifier = Modifier.fillMaxWidth(), onClick = { Log.d("Click", "${pokemon.name} was clicked")}) {
        Text(text = pokemon.name)
    }
}
//https://medium.com/@ramadan123sayed/pagination-in-lazycolumn-in-jetpack-compose-29ba92d52632
@Composable
fun PaginatedLazyColumn(
    items: State<List<Pokemon>>,  // Using PersistentList for efficient state management
    loadMoreItems: () -> Unit,  // Function to load more items
    listState: LazyListState,  // Track the scroll state of the LazyColumn
    buffer: Int = 2,  // Buffer to load more items when we get near the end
    isLoading: Boolean,  // Track if items are being loaded
    modifier: Modifier = Modifier
) {
    // Derived state to determine when to load more items
    val shouldLoadMore = remember {
        derivedStateOf {
            // Get the total number of items in the list
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            // Get the index of the last visible item
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            // Check if we have scrolled near the end of the list and more items should be loaded
            lastVisibleItemIndex >= (totalItemsCount - buffer) && !isLoading
        }
    }

// Launch a coroutine to load more items when shouldLoadMore becomes true
    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .filter { it }  // Ensure that we load more items only when needed
            .collect {
                loadMoreItems()
            }
    }
    // LazyColumn to display the list of items
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),  // Add padding for better visual spacing
        state = listState  // Pass the scroll state
    ) {
        // Render each item in the list using a unique key
        itemsIndexed(items, key = { _, item -> item }) { index, item ->
            Text(text = item, modifier = Modifier.padding(8.dp))  // Add padding to each item
        }

//            // Check if we've reached the end of the list
//            if (index == items.lastIndex && !isLoading) {
//                loadMoreItems()
//            }

        // Show a loading indicator at the bottom when items are being loaded
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()  // Display a circular loading indicator
                }
            }
        }
    }
}

