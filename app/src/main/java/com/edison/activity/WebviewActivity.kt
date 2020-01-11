package com.edison.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.edison.R
import kotlinx.android.synthetic.main.webview_layout.*

class WebviewActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_layout)

        val actionbar = supportActionBar

        //set back button
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        var url = intent.extras?.getString("url")
        webview!!.loadUrl(url)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}