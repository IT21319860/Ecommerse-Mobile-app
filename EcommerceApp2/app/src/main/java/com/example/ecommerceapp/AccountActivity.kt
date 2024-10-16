package com.example.ecommerceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.security.cert.CertificateException
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class AccountActivity : AppCompatActivity() {
    private lateinit var accountStatusText: TextView
    private lateinit var deactivateButton: Button
    private lateinit var feedbackBtn : Button

    private val BASE_URL = "https://10.0.2.2:7047"
    private lateinit var userId: String // Email of the user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        // Retrieve the email passed from LoginActivity
        userId = intent.getStringExtra("user_id") ?:""  // Get the id from intent

        accountStatusText = findViewById(R.id.Status)
        deactivateButton = findViewById(R.id.deactivate)
        feedbackBtn = findViewById(R.id.feedback)

        deactivateButton.setOnClickListener {
            if (userId.isNotEmpty()) {
                deactivateAccount(userId)
            } else {
                Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show()
            }
        }
        feedbackBtn.setOnClickListener{
            val intent = Intent(this, FeedbackActivity::class.java)
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

    private fun deactivateAccount(userId: String) {

        val client = getUnsafeOkHttpClient()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(EcommerceService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.deactivateAccount(userId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        accountStatusText.text = "Account Deactivated"
                        Toast.makeText(
                            this@AccountActivity,
                            "Account deactivated",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@AccountActivity,
                            "Failed to deactivate",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AccountActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
