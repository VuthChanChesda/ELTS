package com.elts

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ArticleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_article_detail)

        val toolbar = findViewById<Toolbar>(R.id.articleToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "" // optional: no title if you want a clean look

        toolbar.post {
            val navIconView = toolbar.getChildAt(0)
            navIconView?.translationY = 70f  // move down by 10 pixels
        }


        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val description = intent.getStringExtra("description")
        val date = intent.getStringExtra("date")

        findViewById<TextView>(R.id.articleTitle).text = title
        findViewById<TextView>(R.id.articleContent).text = content
        findViewById<TextView>(R.id.articleDescription).text = description
        findViewById<TextView>(R.id.articleDate).text = date
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // close activity and go back
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


}