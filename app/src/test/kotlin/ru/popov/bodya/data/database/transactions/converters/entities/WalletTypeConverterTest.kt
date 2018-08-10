package ru.popov.bodya.data.database.transactions.converters.entities

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import ru.popov.bodya.domain.transactions.models.WalletType

/**
 *  @author popovbodya
 */
class WalletTypeConverterTest {

    private lateinit var walletTypeConverter: WalletTypeConverter

    @Before
    fun setUp() {
        walletTypeConverter = WalletTypeConverter()
    }

    @Test
    fun testFromType() {
        assertThat(walletTypeConverter.fromType(WalletType.CASH), `is`("CASH"))
        assertThat(walletTypeConverter.fromType(WalletType.CREDIT_CARD), `is`("CREDIT_CARD"))
        assertThat(walletTypeConverter.fromType(WalletType.BANK_ACCOUNT), `is`("BANK_ACCOUNT"))
    }

    @Test
    fun testToWallet() {
        assertThat(walletTypeConverter.toWallet("CASH"), `is`(WalletType.CASH))
        assertThat(walletTypeConverter.toWallet("CREDIT_CARD"), `is`(WalletType.CREDIT_CARD))
        assertThat(walletTypeConverter.toWallet("BANK_ACCOUNT"), `is`(WalletType.BANK_ACCOUNT))
    }
}