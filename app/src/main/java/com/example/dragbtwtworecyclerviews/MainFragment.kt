package com.example.dragbtwtworecyclerviews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author Burwei
 */
class MainFragment : Fragment(), MyRecyclerviewAdaptor.OnClickListener {

    private lateinit var myRecyclerviewLeft: RecyclerView
    private lateinit var myRecyclerviewAdaptorLeft: MyRecyclerviewAdaptor
    private lateinit var myViewManagerLeft: RecyclerView.LayoutManager
    private lateinit var myRecyclerviewRight: RecyclerView
    private lateinit var myRecyclerviewAdaptorRight: MyRecyclerviewAdaptor
    private lateinit var myViewManagerRight: RecyclerView.LayoutManager
    private val dragListener = MyDragListener()
    private val testDataLeft = mutableListOf<Any>("cat", "dog", "rabbit", "horse", "elephant", "eagle", "bear", "cow", "chicken", "dear")
    private val testDataRight = mutableListOf<Any>("fish", "jellyfish", "whale", "turtle", "seahorse", "coral", "octopus", "frog", "screw", "starfish")


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myViewManagerLeft = LinearLayoutManager(activity)
        myRecyclerviewAdaptorLeft = MyRecyclerviewAdaptor()
        myRecyclerviewAdaptorLeft.setClickListener(this)
        myRecyclerviewAdaptorLeft.setDragListener(dragListener)
        myViewManagerRight = LinearLayoutManager(activity)
        myRecyclerviewAdaptorRight = MyRecyclerviewAdaptor()
        myRecyclerviewAdaptorRight.setClickListener(this)
        myRecyclerviewAdaptorRight.setDragListener(dragListener)
        var rootView = inflater
                .inflate(R.layout.fragment_main, container, false) as View
        myRecyclerviewLeft = rootView.findViewById<RecyclerView>(R.id.recyclerviewLeft)
                .apply {
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    setHasFixedSize(true)

                    // use a linear layout manager
                    layoutManager = myViewManagerLeft

                    // specify an viewAdapter (see also next example)
                    adapter = myRecyclerviewAdaptorLeft
                }
        myRecyclerviewRight = rootView.findViewById<RecyclerView>(R.id.recyclerviewRight)
                .apply {
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    setHasFixedSize(true)

                    // use a linear layout manager
                    layoutManager = myViewManagerRight

                    // specify an viewAdapter (see also next example)
                    adapter = myRecyclerviewAdaptorRight
                }
        return rootView
    }

    override fun onStart() {
        super.onStart()
        myRecyclerviewAdaptorLeft.setData(testDataLeft)
        myRecyclerviewAdaptorRight.setData(testDataRight)
    }

    override fun recyclerviewClick(name: String) {
        println("get name: $name")
    }
}