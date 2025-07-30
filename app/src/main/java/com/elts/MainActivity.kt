package com.elts

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.elts.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()


        binding.signupTv.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {

            if(binding.emailET.text.toString().isEmpty()){
                binding.emailLayout.error="Enter Email"
            }
            else if(binding.passET.text.toString().isEmpty()){
                binding.passLayout.error="Enter Password"
            }
            else {
                auth.signInWithEmailAndPassword(binding.emailET.text.toString(),binding.passET.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this,"Login Successfully", Toast.LENGTH_SHORT).show()
                        var intent=Intent(this@MainActivity,HomeSCreenActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this,"Error Invalid Email or Password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}