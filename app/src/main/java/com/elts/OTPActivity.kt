package com.elts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.elts.api.RetrofitClient
import com.elts.databinding.ActivityOtpactivityBinding
import com.elts.models.SendOtpRequest
import com.elts.models.SendOtpResponse
import com.elts.models.VerifyOtpRequest
import com.elts.models.VerifyOtpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class OTPActivity : AppCompatActivity() {

    lateinit var binding : ActivityOtpactivityBinding
    lateinit var auth: FirebaseAuth
    var email : String=""
    var pass : String=""
    var name : String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get data from intent we got from signup
        email = intent.getStringExtra("email").toString()
        pass = intent.getStringExtra("pass").toString()
        name = intent.getStringExtra("username").toString()

        auth = FirebaseAuth.getInstance()


        binding.showEmail.setText(email.toString()) //get email from intent and show on screen

        sendOtp(email) // send otp when activity is created

        binding.tvResend.setOnClickListener {
            sendOtp(email)
        }

        //check if there any otp is entered if not set focus on first otp
        binding.otp1.doOnTextChanged { text, start, before, count ->
            if (!binding.otp1.text.toString().isEmpty()) {
                binding.otp2.requestFocus()
            }
            if (!binding.otp2.text.toString().isEmpty()) {
                binding.otp2.requestFocus()
            }
        }

        binding.otp2.doOnTextChanged { text, start, before, count ->
            if (!binding.otp2.text.toString().isEmpty()) {
                binding.otp3.requestFocus()
            } else {
                binding.otp1.requestFocus()
            }

        }
        binding.otp3.doOnTextChanged { text, start, before, count ->
            if (!binding.otp3.text.toString().isEmpty()) {
                binding.otp4.requestFocus()
            } else {
                binding.otp2.requestFocus()
            }

        }
        binding.otp4.doOnTextChanged { text, start, before, count ->
            if (!binding.otp4.text.toString().isEmpty()) {
                binding.otp5.requestFocus()
            } else {
                binding.otp3.requestFocus()
            }

        }
        binding.otp5.doOnTextChanged { text, start, before, count ->
            if (!binding.otp5.text.toString().isEmpty()) {
                binding.otp6.requestFocus()
            } else {
                binding.otp4.requestFocus()
            }

        }
        binding.otp6.doOnTextChanged { text, start, before, count ->
            if (binding.otp6.text.toString().isEmpty()) {
                binding.otp5.requestFocus()
            }
        }

        binding.button.setOnClickListener {
            val otpFields = listOf(
                binding.otp1,
                binding.otp2,
                binding.otp3,
                binding.otp4,
                binding.otp5,
                binding.otp6
            )

            if (otpFields.any { it.text.toString().isEmpty() }) {
                Toast.makeText(this@OTPActivity, "Enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val enteredOtp = otpFields.joinToString(separator = "") { it.text.toString() }

//            verifyOtp(email, enteredOtp)
            verifyOtp(email,enteredOtp,pass, name)

        }
        
    }

    private fun sendOtp(email: String) {
        val request = SendOtpRequest(email)
        RetrofitClient.api.sendOtp(request).enqueue(object : Callback<SendOtpResponse> {
            override fun onResponse(call: Call<SendOtpResponse>, response: Response<SendOtpResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        Toast.makeText(this@OTPActivity, body.message, Toast.LENGTH_SHORT).show()
                        Log.d("OTP", "OTP sent successfully: ${body.message}")
                    } else {
                        val errorMessage = body?.message ?: "Failed to send OTP"
                        Toast.makeText(this@OTPActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.e("OTP", "API responded with error: $errorMessage")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@OTPActivity, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("OTP", "Server error ${response.code()}: $errorBody")
                }
            }

            override fun onFailure(call: Call<SendOtpResponse>, t: Throwable) {
                Toast.makeText(this@OTPActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("OTP", "Network failure: ${t.localizedMessage}", t)
            }
        })
    }


    private fun verifyOtp(email: String, otp: String, password: String, username: String) {
        val request = VerifyOtpRequest(email, otp)

        RetrofitClient.api.verifyOtp(request).enqueue(object : Callback<VerifyOtpResponse> {
            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        Toast.makeText(this@OTPActivity, body.message, Toast.LENGTH_SHORT).show()

                        // Step 1: Create Firebase user
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = FirebaseAuth.getInstance().currentUser
                                    val uid = user?.uid

                                    // Step 2: Store user info in Firestore
                                    val userData = hashMapOf(
                                        "uid" to uid,
                                        "email" to email,
                                        "username" to username,
                                        "created_at" to FieldValue.serverTimestamp()
                                    )

                                    uid?.let {
                                        FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(it)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                Toast.makeText(this@OTPActivity, "User created!", Toast.LENGTH_SHORT).show()
                                                startActivity(Intent(this@OTPActivity, HomeSCreenActivity::class.java))
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(this@OTPActivity, "Firestore error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                            }
                                    }

                                } else {
                                    Toast.makeText(this@OTPActivity, "Firebase Auth Error: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    Log.e("OTP", "Firebase Auth Error: ${task.exception?.localizedMessage}")
                                }
                            }

                    } else {
                        Toast.makeText(this@OTPActivity, body?.message ?: "OTP verification failed", Toast.LENGTH_SHORT).show()
                        Log.e("OTP", "API responded with error: ${body?.message ?: "Unknown error"}")
                    }
                } else {
                    Toast.makeText(this@OTPActivity, "Server Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("OTP", "Server error ${response.code()}: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {
                Toast.makeText(this@OTPActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}