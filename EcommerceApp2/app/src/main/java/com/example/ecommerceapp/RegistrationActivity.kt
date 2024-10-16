package com.example.ecommerceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.security.cert.CertificateException
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RegistrationActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var IdInput : EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerButton: Button

    private lateinit var loginRedirectText :TextView

    private val BASE_URL = "https://10.0.2.2:7047"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        emailInput = findViewById(R.id.emailInput)
        IdInput = findViewById(R.id.IdInput)
        passwordInput = findViewById(R.id.passwordInput)
        registerButton = findViewById(R.id.registerButton)

        loginRedirectText = findViewById(R.id.loginRedirectText)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val Id = IdInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && Id.isNotEmpty()) {
                createAccount(email, password, Id)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }


        loginRedirectText.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an SSL socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _: String?, _: SSLSession? -> true }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun createAccount(email: String, password: String, Id:String) {
        // Create an OkHttpClient that skips SSL validation
        val client = getUnsafeOkHttpClient()

        // Initialize Retrofit with the unsafe OkHttp client
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // Use the unsafe OkHttp client here
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(EcommerceService::class.java)
        val accountRequest = AccountRequest(email, password, Id)

        // Use Coroutine to handle network requests on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.createAccount(accountRequest)
                if (response.isSuccessful) {
                    // Switch back to the main thread to update the UI
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegistrationActivity, "Account created. Awaiting approval.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegistrationActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistrationActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
