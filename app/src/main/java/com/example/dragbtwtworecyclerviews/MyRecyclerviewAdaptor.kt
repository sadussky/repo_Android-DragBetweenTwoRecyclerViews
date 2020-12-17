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
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt


class MyRecyclerviewAdaptor : RecyclerView.Adapter<MyRecyclerviewAdaptor.MyViewHolder>() {

    // onclick listener interface
    private var clickListener: OnClickListener? = null
    private var myDataset = mutableListOf<Any>()
    private var dragListener: MyDragListener? = null

    interface OnClickListener {
        fun recyclerviewClick(name: String)
    }

    fun setClickListener(parentFragment: OnClickListener) {
        clickListener = parentFragment
    }

    fun setDragListener(listener: MyDragListener) {
        dragListener = listener
    }

    fun setData(data: MutableList<Any>) {
        myDataset = data
        notifyDataSetChanged()
    }

    fun getData(): MutableList<Any> {
        return myDataset
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setDrag(v: ConstraintLayout, position: Int) {
        var touchedX = 0f  // closure variable
        var touchedY = 0f  // closure variable
        v.visibility = View.VISIBLE
        v.setOnDragListener(dragListener)
        v.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchedX = event.x
                    touchedY = event.y
                }
            }
            return@setOnTouchListener false  // leave the touch event to other listeners
        }
        v.setOnLongClickListener {
            it.visibility = View.INVISIBLE
            val myShadow = MyDragShadowBuilder(it, touchedX.roundToInt(), touchedY.roundToInt())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(
                        null,
                        myShadow,
                        it,
                        0
                )
            } else {
                it.startDrag(
                        null,
                        myShadow,
                        it,
                        0
                )
            }
        }
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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name = myDataset[position]
        holder.txtAnimalName.text = name as String
        holder.layoutAnimal.setOnClickListener {
            clickListener?.recyclerviewClick(name)
        }
        setDrag(holder.layoutAnimal, position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.setOnDragListener(dragListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        clickListener = null
        clear()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    fun clear() {
        myDataset = mutableListOf<Any>()
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
            true
        } else {
            false
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // do nothing
    }
}


class MyDragShadowBuilder(v: View, private val touchedX: Int, private val touchedY: Int) : View.DragShadowBuilder(v) {

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        super.onProvideShadowMetrics(size, touch)
        touch.set(touchedX, touchedY)
    }

    override fun onDrawShadow(canvas: Canvas) {
        super.onDrawShadow(canvas)
        canvas.drawColor(0x22000000)
    }
}


class MyDragListener : View.OnDragListener, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Job()

    private var isStarted = false
    private var isOriginalParent = true
    private var initPositionInOriParent = 0
    private var initPositionInOtherParent = 0
    private var finalPosition = 0
    private var finalPositionInOriParent = 0
    private var finalParent: RecyclerView? = null

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        if (v == null || v is RecyclerView || v.parent == null) {
            return true
        }
        when (event?.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                val sourceView = event.localState as View
                if (!isStarted && sourceView.parent != null) {
                    val sourcePosition = (sourceView.parent as RecyclerView).getChildAdapterPosition(sourceView)
                    initPositionInOriParent = sourcePosition
                    finalPosition = sourcePosition
                    isStarted = true
                }
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                val sourceView = event.localState as View
                if (sourceView.parent == null) {
                    return true
                }
                val targetAdaptor = (v.parent as RecyclerView).adapter!! as MyRecyclerviewAdaptor
                val targetPosition = (v.parent as RecyclerView).getChildAdapterPosition(v)
                if (v.parent == sourceView.parent) {
                    if (isOriginalParent) {
                        try {
                            targetAdaptor.notifyItemMoved(finalPosition, targetPosition)
                        } catch (e: Exception) {
                            println("ignore index out of bound")
                        }
                    } else {
                        try {
                            targetAdaptor.notifyItemMoved(finalPositionInOriParent, targetPosition)
                            (finalParent?.adapter as MyRecyclerviewAdaptor?)?.getData()!!.removeAt(initPositionInOtherParent)
                            finalParent?.adapter?.notifyDataSetChanged()
                        } catch (e: Exception) {
                            println("ignore index out of bound")
                        }
                        isOriginalParent = true
                    }
                    finalPosition = targetPosition
                    finalPositionInOriParent = targetPosition
                } else {
                    if (isOriginalParent) {
                        val sourceValue = ((sourceView.parent as RecyclerView).adapter as MyRecyclerviewAdaptor).getData()[initPositionInOriParent]
                        try {
                            targetAdaptor.getData().add(targetPosition, sourceValue)
                            targetAdaptor.notifyDataSetChanged()
                            (v.parent as RecyclerView)[targetPosition].visibility = View.INVISIBLE  // not working, don't know why
                            initPositionInOtherParent = targetPosition
                        } catch (e: Exception) {
                            println("ignore index out of bound")
                        }
                        isOriginalParent = false
                        finalPosition = targetPosition
                    } else {
                        if (finalPosition != targetPosition) {
                            try {
                                targetAdaptor.notifyItemMoved(finalPosition, targetPosition)
                            } catch (e: Exception) {
                                println("ignore index out of bound")
                            }
                            finalPosition = targetPosition
                        }
                    }
                }
                finalParent = v.parent as RecyclerView
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                val sourceView = event.localState as View
                if (finalParent == null || sourceView.parent == null) {
                    return true
                }
                val sourceParent = sourceView.parent as RecyclerView
                val sourceValue = ((sourceView.parent as RecyclerView).adapter as MyRecyclerviewAdaptor).getData()[initPositionInOriParent]
                if (finalParent == sourceView.parent) {
                    (finalParent!!.adapter as MyRecyclerviewAdaptor).getData().removeAt(initPositionInOriParent)
                    (finalParent!!.adapter as MyRecyclerviewAdaptor).getData().add(finalPosition, sourceValue)
                } else {
                    (sourceParent.adapter as MyRecyclerviewAdaptor).getData().removeAt(initPositionInOriParent)
                    (finalParent!!.adapter as MyRecyclerviewAdaptor).getData().removeAt(initPositionInOtherParent)
                    (finalParent!!.adapter as MyRecyclerviewAdaptor).getData().add(finalPosition, sourceValue)
                }
                (finalParent!!.adapter as MyRecyclerviewAdaptor).notifyDataSetChanged()
                (sourceView.parent as RecyclerView?)?.adapter?.notifyDataSetChanged()
                isStarted = false
                finalParent = null
                isOriginalParent = true
            }
        }
        return true
    }
}