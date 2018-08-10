package ru.popov.bodya.data.database.preferences

import android.content.SharedPreferences
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import ru.popov.bodya.data.database.currencies.entities.RatesEntity

/**
 * @author popovbodya
 */
class SharedPreferencesWrapperTest {

    private companion object {
        const val DEFAULT_RATE = "0.015807"
    }

    private lateinit var sharedPreferencesWrapper: SharedPreferencesWrapper
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        sharedPreferences = mock(SharedPreferences::class.java)
        sharedPreferencesWrapper = SharedPreferencesWrapper(sharedPreferences)
    }

    @Test
    fun testGetRatesEntity() {
        val expected = RatesEntity(0.015807, 0.015807)
        val value = `when`(sharedPreferences.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(DEFAULT_RATE)
        val actual = sharedPreferencesWrapper.getRatesEntity()
        assertThat(actual, `is`(expected))
        verify(sharedPreferences, times(2)).getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        verifyNoMoreInteractions(sharedPreferences)
    }

    @Test
    fun testSaveRatesEntity() {
        val ratesEntity = RatesEntity(0.015807, 0.015807)
        val preferencesEditor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(preferencesEditor)
        sharedPreferencesWrapper.saveRatesEntity(ratesEntity)
        verify(sharedPreferences, times(2)).edit()
        verifyNoMoreInteractions(sharedPreferences)
        verify(preferencesEditor, times(2)).putString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        verify(preferencesEditor, times(2)).apply()
        verifyNoMoreInteractions(preferencesEditor)
    }
}