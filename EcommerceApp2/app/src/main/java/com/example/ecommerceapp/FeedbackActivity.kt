package com.example.ecommerceapp

import okhttp3.OkHttpClient
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class FeedbackActivity : AppCompatActivity() {

    private lateinit var vendorNameInput: EditText
    private lateinit var rankInput: EditText
    private lateinit var commentInput: EditText
    private lateinit var submitButton: Button
    private lateinit var ecommerceService: EcommerceService

    private val BASE_URL = "https://10.0.2.2:7047"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)  // Replace with your layout file name

        vendorNameInput = findViewById(R.id.vendorName)
        rankInput = findViewById(R.id.rank)
        commentInput = findViewById(R.id.comment)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            submitFeedback()
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

    private fun submitFeedback() {
        // Create an OkHttpClient that skips SSL validation
        val client = getUnsafeOkHttpClient()

        // Retrofit setup using the unsafe OkHttp client
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)  // Replace with your actual API URL
            .client(client)  // Use the unsafe OkHttp client here
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        ecommerceService = retrofit.create(EcommerceService::class.java)

        val vendorName = vendorNameInput.text.toString()
        val rank = rankInput.text.toString()
        val comment = commentInput.text.toString()

        // Validate input
        if (vendorName.isEmpty() || rank.isEmpty() || comment.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val feedback = Feedback(vendorName, rank, comment)

        // Coroutine for network call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ecommerceService.submitFeedback(feedback)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FeedbackActivity, "Feedback submitted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@FeedbackActivity, "Failed to submit feedback: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FeedbackActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
