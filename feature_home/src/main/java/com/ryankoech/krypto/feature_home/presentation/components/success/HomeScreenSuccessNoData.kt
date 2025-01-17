package com.ryankoech.krypto.feature_home.presentation.components.success

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ryankoech.krypto.common.presentation.ErrorScreen
import com.ryankoech.krypto.common.presentation.theme.KryptoTheme
import com.ryankoech.krypto.feature_home.R

const val TEST_TAG_HOME_SCREEN_SUCCESS_NO_DATA = "test_tag_home_screen_success_no_data"

@Composable
fun HomeScreenSuccessNoData(
    modifier: Modifier = Modifier,
    navigateToChooseAsset : () -> Unit
){
    ErrorScreen(
        modifier = modifier,
        onButtonClick = navigateToChooseAsset,
        messageText = stringResource(R.string.home_screen_success_not_data_message),
        buttonText = stringResource(R.string.home_screen_success_no_data_action),
        res = R.drawable.astronaut
    )
}

@Preview
@Composable
fun HomeScreenSuccessNoDataPreview() {

    KryptoTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            HomeScreenSuccessNoData(Modifier, {})

        }
    }

}