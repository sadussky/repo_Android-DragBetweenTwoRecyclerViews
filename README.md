# Android-DragBetweenTwoRecyclerViews
This is a demo of drag and drop items between two RecyclerViews.

## demo
<img src="https://imgur.com/qrn0Hbd.gif" width="40%">

## Usage
1. Copy the MyDraggableRecyclerviewAdaptor.kt file to your application.
2. Make your RecyclerViewAdaptor class implements the MyDraggableRecyclerviewAdaptor interface.
3. Call setDrag() in onBindViewHolder() and onAttachedToRecyclerView()
4. Set drag listener and all other RecyclerView's settings in your activity/fragment.
5. Done.

## Use drag-drop mechanism instead of ItemTouchHelper
Since ItemTouchHelper can only drag-drop within the same RecyclerView,
it's not suitable for this task.
Drag-drop mechanism in the other hand can deal with this issue.
It's still a little bit complicated to use drag-drop mechanism to achieve
the goal of drag and drop items between two RecyclerViews, but this is the
best we can get from Android right now.
The drag-drop mechanism's doc: https://developer.android.com/guide/topics/ui/drag-drop

## Not only two
It's only two RecyclerViews in this demo, but the design is not only for
two RecyclerViews. It could be as many RecyclerViews as you want, but I haven't test it yet XD
