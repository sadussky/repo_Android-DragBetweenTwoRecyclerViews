package com.example.dragbtwtworecyclerviews

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt


class MyRecyclerviewAdaptor(private val recyclerviewName:String) : RecyclerView.Adapter<MyRecyclerviewAdaptor.MyViewHolder>() {

    // onclick listener interface
    private var clickListener: OnClickListener? = null
    private var myDataset = listOf<String>()

    interface OnClickListener {
        fun recyclerviewClick(name: String)
    }

    fun setClickListener(parentFragment: OnClickListener) {
        clickListener = parentFragment
    }

    fun setData(data: List<String>) {
        myDataset = data
        notifyDataSetChanged()
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutAnimal: ConstraintLayout = itemView.findViewById(R.id.layoutAnimal)
        val txtAnimalName: TextView = itemView.findViewById(R.id.txtAnimalName)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): MyRecyclerviewAdaptor.MyViewHolder {
        // create a new view
        val item = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_animal, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(item)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name = myDataset[position]
        var touchedX = 0f
        var touchedY = 0f
        holder.txtAnimalName.text = name
        holder.layoutAnimal.setOnClickListener {
            clickListener?.recyclerviewClick(name)
        }
        holder.layoutAnimal.tag = "$recyclerviewName/$position/$name"
        holder.layoutAnimal.setOnDragListener(MyDragListener())
        holder.layoutAnimal.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    touchedX = event.x
                    touchedY = event.y
                }
            }
            return@setOnTouchListener false  // leave the touch event to other listeners
        }
        holder.layoutAnimal.setOnLongClickListener {
            val item = ClipData.Item(it.tag as? CharSequence)
            val dragData = ClipData(
                    it.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item)
            val myShadow = MyDragShadowBuilder(it, touchedX.roundToInt(),touchedY.roundToInt())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(
                        dragData,
                        myShadow,
                        null,
                        0
                )
            } else {
                it.startDrag(
                        dragData,
                        myShadow,
                        null,
                        0
                )
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        clickListener = null
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    fun clear() {
        myDataset = mutableListOf<String>()
    }
}


class MyItemTouchHelperCallback : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return if (recyclerView.adapter != null) {
            recyclerView.adapter!!.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            println("onMove")
            true
        } else {
            false
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // do nothing
    }
}


class MyDragShadowBuilder(v: View, private val touchedX:Int, private val touchedY:Int) : View.DragShadowBuilder(v) {

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        super.onProvideShadowMetrics(size, touch)
        touch.set(touchedX,touchedY)
    }

    override fun onDrawShadow(canvas: Canvas) {
        super.onDrawShadow(canvas)
        canvas.drawColor(0x22000000)
    }
}


class MyDragListener : View.OnDragListener {

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        when (event?.action) {
            DragEvent.ACTION_DRAG_ENTERED -> {
                println("drag enter, v?.tag=${v?.tag}")
            }
            DragEvent.ACTION_DROP -> {
                println("drop, v?.tag=${v?.tag}")
            }
        }
        return true // pass the event to other listener
    }
}