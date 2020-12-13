package com.example.dragbtwtworecyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerviewAdaptor: RecyclerView.Adapter<MyRecyclerviewAdaptor.MyViewHolder>() {

    // onclick listener interface
    private var clickListener: OnClickListener? = null
    private var myDataset = listOf<String>()

    interface OnClickListener {
        fun recyclerviewClick(name: String)
    }

    fun setListener(parentFragment: OnClickListener) {
        clickListener = parentFragment
    }

    fun setData(data: List<String>){
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

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val name = myDataset[position]
        holder.txtAnimalName.text = name
        holder.layoutAnimal.setOnClickListener {
            clickListener?.recyclerviewClick(name)
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