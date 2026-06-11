package com.example.foodboxd.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodboxd.R
import com.example.foodboxd.ui.theme.FoodboxdTheme

/**
 * Pantalla de inicio de sesión conectada al backend (`/api/users/login`).
 * Valida credenciales reales, muestra estado de carga y errores, y al
 * autenticar correctamente invoca [onLoginSuccess].
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    viewModel: AuthViewModel = viewModel(),
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.success) {
        if (uiState.success) onLoginSuccess()
    }

    LoginForm(
        modifier = modifier,
        email = email,
        password = password,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onEmailChange = { email = it; if (uiState.error != null) viewModel.consumeError() },
        onPasswordChange = { password = it; if (uiState.error != null) viewModel.consumeError() },
        onSignInClick = { viewModel.login(email, password) },
        onForgotPasswordClick = {
            // El backend no expone un endpoint de recuperación de contraseña.
            Toast.makeText(
                context,
                "La recuperación de contraseña no está disponible por ahora.",
                Toast.LENGTH_LONG
            ).show()
            onForgotPasswordClick()
        },
        onSignUpClick = onSignUpClick,
    )
}

@Composable
private fun LoginForm(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.login_email_label)) },
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.login_password_label)) },
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        text = stringResource(
                            if (passwordVisible) R.string.login_hide_password
                            else R.string.login_show_password
                        ),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        if (error != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(stringResource(R.string.login_forgot_password))
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onSignInClick,
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp),
                )
            } else {
                Text(
                    text = stringResource(R.string.login_sign_in),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.login_divider),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.login_no_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = onSignUpClick) {
                Text(
                    text = stringResource(R.string.login_sign_up),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    FoodboxdTheme {
        Scaffold { padding ->
            LoginForm(
                modifier = Modifier.padding(padding),
                email = "ana@foodboxd.com",
                password = "secreto",
                isLoading = false,
                error = null,
                onEmailChange = {},
                onPasswordChange = {},
                onSignInClick = {},
                onForgotPasswordClick = {},
                onSignUpClick = {},
            )
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenDarkPreview() {
    FoodboxdTheme {
        Scaffold { padding ->
            LoginForm(
                modifier = Modifier.padding(padding),
                email = "",
                password = "",
                isLoading = false,
                error = "Credenciales inválidas",
                onEmailChange = {},
                onPasswordChange = {},
                onSignInClick = {},
                onForgotPasswordClick = {},
                onSignUpClick = {},
            )
        }
    }
}
