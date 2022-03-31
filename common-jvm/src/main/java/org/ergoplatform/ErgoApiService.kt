package org.ergoplatform

import org.ergoplatform.api.OkHttpSingleton
import org.ergoplatform.explorer.client.DefaultApi
import org.ergoplatform.explorer.client.model.OutputInfo
import org.ergoplatform.explorer.client.model.TokenInfo
import org.ergoplatform.explorer.client.model.TotalBalance
import org.ergoplatform.persistance.PreferencesProvider
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ErgoApi {
    fun getTotalBalanceForAddress(publicAddress: String): Call<TotalBalance>
    fun getBoxInformation(boxId: String): Call<OutputInfo>
    fun getTokenInformation(tokenId: String): Call<TokenInfo>
}

class ErgoApiService(val defaultApi: DefaultApi) : ErgoApi {

    override fun getTotalBalanceForAddress(publicAddress: String): Call<TotalBalance> =
        defaultApi.getApiV1AddressesP1BalanceTotal(publicAddress)

    override fun getBoxInformation(boxId: String): Call<OutputInfo> =
        defaultApi.getApiV1BoxesP1(boxId)

    override fun getTokenInformation(tokenId: String): Call<TokenInfo> =
        defaultApi.getApiV1TokensP1(tokenId)

    companion object {
        private var ergoApiService: ErgoApiService? = null

        fun getOrInit(preferences: PreferencesProvider): ErgoApiService {
            if (ergoApiService == null) {

                val retrofit = Retrofit.Builder()
                    .baseUrl(preferences.prefExplorerApiUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpSingleton.getInstance())
                    .build()

                val defaultApi = retrofit.create(DefaultApi::class.java)
                ergoApiService = ErgoApiService(defaultApi)
            }
            return ergoApiService!!
        }


        fun resetApiService() {
            ergoApiService = null
        }


    }
}