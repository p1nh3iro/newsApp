package com.example.newsapplication

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newsapp.network.RetrofitInstance
import com.example.newsapplication.model.Article
import com.example.newsapplication.repository.NewsRepository
import com.example.newsapplication.ui.composables.DetailScreen
import com.example.newsapplication.ui.composables.HeadlinesScreen
import com.example.newsapplication.viewmodel.NewsViewModel
import com.example.newsapplication.viewmodel.NewsViewModelFactory
import com.google.gson.Gson

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        authenticateUser()
    }

    private fun authenticateUser() {
        // TODO STORY 3
        if (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            val executor = ContextCompat.getMainExecutor(this)
            val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    // Success so the app can setup the content
                    setupContent()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    // If the user cancels the authentication or fails the fingerprint sometimes (depends on device used), close the app
                    finishAffinity()
                }
            })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.biometricAuthenticator_title))
                .setSubtitle(getString(R.string.biometricAuthenticator_description))
                .setNegativeButtonText(getString(R.string.biometricAuthenticator_cancel))
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            setupContent()
        }
    }

    private fun setupContent() {
        // Not clean code, but for the sake of simplicity, i didn't worry about this
        val api = RetrofitInstance.api
        val repo = NewsRepository(api)
        val factory = NewsViewModelFactory(repo)
        val viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "headlines") {
                // TODO STORY 1
                composable("headlines") {
                    HeadlinesScreen(viewModel) { article ->
                        val json = Uri.encode(Gson().toJson(article))
                        navController.navigate("detail/$json")
                    }
                }
                // TODO STORY 2
                composable("detail/{article}") { backStackEntry ->
                    val json = backStackEntry.arguments?.getString("article")
                    val article = Gson().fromJson(json, Article::class.java)
                    DetailScreen(article)
                }
            }
        }
    }
}