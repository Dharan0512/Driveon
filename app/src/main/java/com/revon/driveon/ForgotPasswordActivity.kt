package com.revon.driveon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forgot_password)

        val googleBtn =
            findViewById<MaterialButton>(R.id.googleBtn)

        googleBtn.setOnClickListener {

            startActivity(
                Intent(this, LoginActivity::class.java)
            )
        }
    }
}