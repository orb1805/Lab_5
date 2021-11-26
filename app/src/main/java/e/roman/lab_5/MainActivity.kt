package e.roman.lab_5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_lab5).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    Lab5Activity::class.java
                )
            )
        }

        findViewById<Button>(R.id.btn_lab6).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    Lab6Activity::class.java
                )
            )
        }
    }
}