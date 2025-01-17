package com.ryankoech.krypto.feature_transaction.presentation.transaction.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryankoech.krypto.common.core.util.Resource
import com.ryankoech.krypto.common.presentation.util.ScreenState
import com.ryankoech.krypto.feature_coin_list.domain.entity.Coin
import com.ryankoech.krypto.feature_home.domain.usecase.DeleteOwnedCoinUseCase
import com.ryankoech.krypto.feature_home.domain.usecase.GetOwnedCoinUseCase
import com.ryankoech.krypto.feature_home.domain.usecase.SaveOwnedCoinUseCase
import com.ryankoech.krypto.feature_transaction.data.dto.transaction_dto.TransactionDto
import com.ryankoech.krypto.feature_transaction.data.dto.transaction_dto.TransactionType
import com.ryankoech.krypto.feature_transaction.domain.usecase.SaveCoinTransactionUseCase
import com.ryankoech.krypto.feature_transaction.presentation.transaction.viewstate.TransactionScreenViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TransactionScreenViewModel @Inject constructor(
    private val saveCoinTransactionUseCase: SaveCoinTransactionUseCase,
    private val getOwnedCoinUseCase : GetOwnedCoinUseCase,
    private val saveOwnedCoinUseCase: SaveOwnedCoinUseCase,
    private val deleteOwnedCoinUseCase: DeleteOwnedCoinUseCase,
) : ViewModel() {

    private val _viewState = mutableStateOf(TransactionScreenViewState())
    val viewState : State<TransactionScreenViewState> = _viewState

    fun getCoinAmount(coinId : String) {

        getOwnedCoinUseCase(coinId)
            .onEach { res ->
                when(res) {
                    is Resource.Error -> {
                        _viewState.value = _viewState.value.copy(
                            screenState = ScreenState.ERROR
                        )
                    }
                    is Resource.Loading -> {
                        _viewState.value = _viewState.value.copy(
                            screenState = ScreenState.LOADING
                        )
                    }
                    is Resource.Success -> {
                        _viewState.value = _viewState.value.copy(
                            screenState = ScreenState.SUCCESS,
                            ownedCoin = res.data!!
                        )

                    }
                }
            }.launchIn(viewModelScope)
    }

    fun saveCoinTransaction (transaction : TransactionDto) {

        _viewState.value = _viewState.value.copy(
            ownedCoin = _viewState.value.ownedCoin.copy(
                amount = if( transaction.transactionType == TransactionType.BUY) {
                    _viewState.value.ownedCoin.amount + transaction.amount
                }else {
                    _viewState.value.ownedCoin.amount - transaction.amount
                },
                value = transaction.currentPrice,
                change = ((transaction.currentPrice - _viewState.value.ownedCoin.value) / (_viewState.value.ownedCoin.value) * 100 ).toFloat()
            )
        )

        if(_viewState.value.ownedCoin.amount <= 0) {

            deleteOwnedCoinUseCase(_viewState.value.ownedCoin.id)
                .onEach { res ->
                    when(res) {
                        is Resource.Error -> {
                            _viewState.value = _viewState.value.copy(
                                screenState = ScreenState.ERROR
                            )
                        }
                        is Resource.Loading -> {
                            _viewState.value = _viewState.value.copy(
                                screenState = ScreenState.LOADING
                            )
                        }
                        is Resource.Success -> {
                            _viewState.value = _viewState.value.copy(
                                screenState = ScreenState.SUCCESS
                            )
                            insertTransaction(transaction)
                        }
                    }
                }.launchIn(viewModelScope)

            return

        }

        saveOwnedCoinUseCase(_viewState.value.ownedCoin)
            .onEach { res ->
                when(res) {
                    is Resource.Error -> {
                        _viewState.value = _viewState.value.copy(
                            screenState = ScreenState.ERROR
                        )
                    }
                    is Resource.Success -> {
                        _viewState.value = _viewState.value.copy(
                            screenState = ScreenState.SUCCESS
                        )
                        insertTransaction(transaction)
                    }
                    else -> {
                        _viewState.value = _viewState.value.copy(
                            screenState = ScreenState.ERROR
                        )
                    }
                }
            }.launchIn(viewModelScope)

    }

    private fun insertTransaction(transaction : TransactionDto) {
        saveCoinTransactionUseCase(transaction)
            .onEach { res ->
                when(res) {
                    is Resource.Error -> {
                        _viewState.value = _viewState.value.copy(
                            screenState = ScreenState.ERROR
                        )
                    }
                    is Resource.Loading -> {
                        _viewState.value = _viewState.value.copy(
                            screenState = ScreenState.LOADING
                        )
                    }
                    is Resource.Success -> {
                        _viewState.value = _viewState.value.copy(
                            screenState = ScreenState.SUCCESS,
                            backToHome = true,
                        )
                    }
                }
            }.launchIn(viewModelScope)

    }

}