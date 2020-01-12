package com.edison.activity

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edison.R
import com.edison.adapter.SwipeAdapter
import com.edison.controller.APIController
import com.edison.impl.ServiceVolley
import com.edison.model.Data
import com.edison.model.Hit
import com.edison.model.ModelCard
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var dataModelList: ArrayList<ModelCard>? = null
    private var adapter: SwipeAdapter? = null
    private var p = Paint()
    private var dataSetList: List<Hit>? = ArrayList()
    private var deletedItems: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rv) as RecyclerView

        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
        itemsswipetorefresh.setColorSchemeColors(Color.WHITE)

        if (isOnline(this)) {
            itemsswipetorefresh.setOnRefreshListener {

                dataSetList = null
                ModelCard().deleteAll()
                retrieveData()
                recyclerView?.invalidate()

            }

            retrieveData()
        } else {
            retreieveDatafromRealm()
        }

    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun retreieveDatafromRealm() {
        dataModelList = ArrayList(ModelCard().getAll())
        adapter = SwipeAdapter(this, dataModelList!!)
        recyclerView!!.adapter = adapter
        recyclerView!!.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        enableSwipe()
        adapter?.onItemClick = { model ->

            var url = model.story_url

            val intent = Intent(this, WebviewActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)

        }

    }


    private fun retrieveData() {
        val service = ServiceVolley()
        val apiController = APIController(service)

        val path = "search_by_date?"
        val params = JSONObject()
        params.put("query", "android")

        apiController.get(path, params) { response ->
            Log.d("val", response.toString())
//            ModelCard().deleteAll()
            var pased = Gson().fromJson(response.toString(), Data::class.java)
            dataSetList = pased.hits

            dataModelList = populateList()
            adapter = SwipeAdapter(this, dataModelList!!)
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            enableSwipe()
            adapter?.onItemClick = { model ->

                var url = model.story_url

                val intent = Intent(this, WebviewActivity::class.java)
                intent.putExtra("url", url)
                startActivity(intent)

            }

            itemsswipetorefresh.isRefreshing = false
        }
    }

    private fun enableSwipe() {
        val simpleItemTouchCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val title = (viewHolder as SwipeAdapter.MyViewHolder).tview_title_.text

                    if (direction == ItemTouchHelper.LEFT) {
                        val deletedModel = dataModelList!![position]
                        deletedItems.add(title.toString())
                        adapter!!.removeItem(position)
                        ModelCard().deleteWhere(title.toString())

                        val snackbar = Snackbar.make(
                            window.decorView.rootView,
                            " eliminado de Recyclerview!",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.setAction("Deshacer") {
                            adapter!!.restoreItem(deletedModel, position)
                        }
                        snackbar.setActionTextColor(Color.YELLOW)
                        snackbar.show()
                    } else {
                        val deletedModel = dataModelList!![position]
                        deletedItems.add(title.toString())
                        ModelCard().deleteWhere(title.toString())
                        adapter!!.removeItem(position)
                        val snackbar = Snackbar.make(
                            window.decorView.rootView,
                            " eliminado de Recyclerview!",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.setAction("Deshacer") {
                            adapter!!.restoreItem(deletedModel, position)
                        }
                        snackbar.setActionTextColor(Color.YELLOW)
                        snackbar.show()
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    val icon: Bitmap
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                        val itemView = viewHolder.itemView
                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3

                        if (dX > 0) {
                            p.color = Color.parseColor("#388E3C")
                            val background =
                                RectF(
                                    itemView.left.toFloat(),
                                    itemView.top.toFloat(),
                                    dX,
                                    itemView.bottom.toFloat()
                                )
                            c.drawRect(background, p)
                            icon = BitmapFactory.decodeResource(
                                resources,
                                R.drawable.ic_delete
                            )
                            val icon_dest = RectF(
                                itemView.left.toFloat() + width,
                                itemView.top.toFloat() + width,
                                itemView.left.toFloat() + 2 * width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)
                        } else {
                            p.color = Color.parseColor("#D32F2F")
                            val background = RectF(
                                itemView.right.toFloat() + dX,
                                itemView.top.toFloat(),
                                itemView.right.toFloat(),
                                itemView.bottom.toFloat()
                            )
                            c.drawRect(background, p)
                            icon = BitmapFactory.decodeResource(
                                resources,
                                R.drawable.ic_delete
                            )
                            val icon_dest = RectF(
                                itemView.right.toFloat() - 2 * width,
                                itemView.top.toFloat() + width,
                                itemView.right.toFloat() - width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)
                        }
                    }
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun populateList(): ArrayList<ModelCard> {

        val list = ArrayList<ModelCard>()

        dataSetList?.forEach {

            if (checkDeletedItems(it._highlightResult?.title?.value) && checkDeletedItems(it.story_title)) {
                val imageModel = ModelCard()
                if (it.story_title == null) {
                    imageModel.title = it._highlightResult?.title?.value
                } else if (it.story_title == null) {
                    imageModel.title = it.story_title
                } else {
                    imageModel.title = it._highlightResult?.story_title?.value
                }
                imageModel.author = it.author
                imageModel.createdAt = toLocalTime(it.created_at!!)
                imageModel.story_url = it.story_url
                imageModel.additem(imageModel)

                list.add(imageModel)
            }
        }

        return list
    }

    fun toLocalTime(date: String): String {

        var utc: TimeZone = TimeZone.getTimeZone("UTC")
        var sourceFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        var destFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        sourceFormat.timeZone = utc
        var convertedDate: Date = sourceFormat.parse(date)
        var dayName = ""

        if (DateUtils.isToday(convertedDate.time)) {
            dayName = "Today"
        } else {
            var sdf_ = SimpleDateFormat("EEEE")
            dayName = sdf_.format(convertedDate)
        }

        return dayName + " " + destFormat.format(convertedDate)
    }


    fun checkDeletedItems(value: String?): Boolean {

        if (deletedItems.size > 0 && value != null) {
            deletedItems.forEach {

                if (value == it)
                    return false
            }
        }

        return true
    }

}
