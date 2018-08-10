package ru.popov.bodya.domain.currency

import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import ru.popov.bodya.data.repositories.CurrenciesRepository
import ru.popov.bodya.domain.currency.model.Rates

/**
 * @author popovbodya
 */
class CurrencyInteractorTest {

    private companion object {
        const val USD_RATE = 0.015789
        const val EUR_RATE = 0.014321
    }

    private lateinit var currencyInteractor: CurrencyInteractor
    private lateinit var currenciesRepository: CurrenciesRepository

    @Before
    fun setUp() {
        currenciesRepository = mock(CurrenciesRepository::class.java)
        currencyInteractor = CurrencyInteractor(currenciesRepository)
    }

    @Test
    fun testGetExchangeRate() {
        val expected = createRates()
        `when`(currenciesRepository.getExchangeRateSingle()).thenReturn(Single.just(expected))
        currencyInteractor.getExchangeRate()
                .test()
                .assertValue(expected)
        verify(currenciesRepository).getExchangeRateSingle()
        verifyNoMoreInteractions(currenciesRepository)
    }

    @Test
    fun testGetCachedExchangeRate() {
        val expected = createRates()
        `when`(currenciesRepository.getCachedExchangeRateSingle()).thenReturn(Single.just(expected))
        currencyInteractor.getCachedExchangeRate()
                .test()
                .assertValue(expected)
        verify(currenciesRepository).getCachedExchangeRateSingle()
        verifyNoMoreInteractions(currenciesRepository)
    }

    @Test
    fun testGetUsdRate() {
        val rates = createRates()
        val expected = 1 / rates.usd
        val actual = currencyInteractor.getUsdRate(rates.usd)
        assertThat(actual, `is`(expected))
    }

    @Test
    fun testGetEurRate() {
        val rates = createRates()
        val expected = 1/ rates.eur
        val actual  = currencyInteractor.getEurRate(rates.eur)
        assertThat(actual, `is`(expected))
    }

    private fun createRates() = Rates(USD_RATE, EUR_RATE)
}