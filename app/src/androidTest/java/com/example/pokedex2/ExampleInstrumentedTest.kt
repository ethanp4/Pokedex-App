package com.example.pokedex2

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pokedex2.data.PokemonRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.pokedex2", appContext.packageName)
    }

    @Test
    fun testFavourite() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val repo = PokemonRepository()
        PokemonRepository.cacheDir = appContext.cacheDir
        PokemonRepository.filesDir = appContext.filesDir
        val id = 1
        repo.setPokemonFavourite(id, true)
        assert(repo.isPokemonFavourite(id))
        repo.setPokemonFavourite(id, false)
        assert(!repo.isPokemonFavourite(id))
    }
}