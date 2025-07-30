package com.elts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.elts.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginText.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnNext.setOnClickListener {
            val intent = Intent(this, OTPActivity::class.java)
            startActivity(intent)
        }

        binding.btnNext.setOnClickListener {
            if(binding.emailET.text.toString().isEmpty()){
                binding.emailLayout.error="Enter Email"
            }
            else if(binding.passET.text.toString().isEmpty()){
                binding.passLayout.error="Enter Password"
            }
            else if(binding.confirmPassET.text.toString().isEmpty()){
                binding.confirmPassLayout.error="Enter Password Again"
            }
            else if(!(binding.passET.text.toString().equals(binding.confirmPassET.text.toString()))){
                binding.confirmPassLayout.error="Password must be same"
            }
            else {
                var intent=Intent(this@SignUpActivity,OTPActivity::class.java)
                intent.putExtra("email",binding.emailET.text.toString())
                intent.putExtra("pass",binding.passET.text.toString())
                intent.putExtra("username",binding.nameET.text.toString())
                startActivity(intent)
            }
        }


    }
}