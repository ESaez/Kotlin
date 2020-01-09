package com.edison.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.edison.R
import com.edison.controller.APIController
import com.edison.impl.ServiceVolley
import com.edison.model.Data
import com.google.gson.Gson
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        retrieveData()
    }

    private fun retrieveData() {
        val service = ServiceVolley()
        val apiController = APIController(service)

        val path = "search_by_date?"
        val params = JSONObject()
        params.put("query", "android")

        apiController.get(path, params) { response ->
            var pased = Gson().fromJson(response.toString(), Data::class.java)
        }
    }
}
