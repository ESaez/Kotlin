package com.edison.activity

import android.content.Context
import android.graphics.*
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var dataModelList: ArrayList<ModelCard>? = null
    private var adapter: SwipeAdapter? = null
    private var p = Paint()
    private var dataSetList: List<Hit>? = ArrayList()
    private var deletedItems: List<String> = ArrayList()

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
                retrieveData()
                recyclerView?.invalidate()

            }

            retrieveData()
        }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    private fun retrieveData() {
        val service = ServiceVolley()
        val apiController = APIController(service)

        val path = "search_by_date?"
        val params = JSONObject()
        params.put("query", "android")

        apiController.get(path, params) { response ->
            Log.d("val", response.toString())
            var pased = Gson().fromJson(response.toString(), Data::class.java)
            dataSetList = pased.hits

            dataModelList = populateList()
            adapter = SwipeAdapter(this, dataModelList!!)
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            enableSwipe()
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
                        deletedItems.toMutableList().add(title as String)
                        adapter!!.removeItem(position)
                        // showing snack bar with Undo option
                        val snackbar = Snackbar.make(
                            window.decorView.rootView,
                            " eliminado de Recyclerview!",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.setAction("Deshacer") {
                            // undo is selected, restore the deleted item
                            adapter!!.restoreItem(deletedModel, position)
                        }
                        snackbar.setActionTextColor(Color.YELLOW)
                        snackbar.show()
                    } else {
                        val deletedModel = dataModelList!![position]
                        deletedItems.toMutableList().add(title as String)
                        adapter!!.removeItem(position)
                        // showing snack bar with Undo option
                        val snackbar = Snackbar.make(
                            window.decorView.rootView,
                            " eliminado de Recyclerview!",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.setAction("Deshacer") {
                            // undo is selected, restore the deleted item
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
                                android.R.drawable.ic_delete
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
                                android.R.drawable.ic_delete
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
                } else if(it.story_title == null) {
                    imageModel.title = it.story_title
                }else{
                    imageModel.title = it._highlightResult?.story_title?.value
                }
                imageModel.author = it.author
                imageModel.createdAt = it.created_at
                list.add(imageModel)
            }
        }

        return list
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
