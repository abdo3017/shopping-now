package com.example.e_commerce.ui.base

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener


class RecyclerTouchListener(
    context: Context?,
    recycleView: RecyclerView,
    private val clicklistener: ItemLongClickListener?
) :
    OnItemTouchListener {
    private val gestureDetector: GestureDetector
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//        val child: View? = rv.findChildViewUnder(e.x, e.y)
//        if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
//            clicklistener.onLongClick(rv.getChildAdapterPosition(child), child)
//        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    init {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child: View? = recycleView.findChildViewUnder(e.x, e.y)
                if (child != null && clicklistener != null) {
                    clicklistener.onLongClick(recycleView.getChildAdapterPosition(child), child)
                }
            }
        })
    }
}