//package lorry.folder.items.memogamma.bubble
//
//import android.view.MotionEvent
//import androidx.compose.ui.graphics.Path
//import androidx.lifecycle.ViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//
//class StylusViewModel : ViewModel() {
//    private var _stylusState = MutableStateFlow(StylusState())
//    val stylusState: StateFlow<StylusState> = _stylusState
//    
//    private var currentPath = mutableListOf<DrawPoint>()
//
//    private fun requestRendering(stylusState: StylusState) {
//        // Updates the stylusState, which triggers a flow.
//        _stylusState.value = stylusState
//    }
//    
//    private fun createPath(): Path {
//        val path = Path()
//
//        for (point in currentPath) {
//            if (point.type == DrawPointType.START) {
//                path.moveTo(point.x, point.y)
//            } else {
//                path.lineTo(point.x, point.y)
//            }
//        }
//        return path
//    }
//
//    fun processMotionEvent(motionEvent: MotionEvent): Boolean {
//        when (motionEvent.actionMasked) {
//            MotionEvent.ACTION_DOWN -> {
//                currentPath.add(
//                    DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.START)
//                )
//            }
//            MotionEvent.ACTION_MOVE -> {
//                currentPath.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))
//            }
//            MotionEvent.ACTION_UP -> {
//                currentPath.add(DrawPoint(motionEvent.x, motionEvent.y, DrawPointType.LINE))
//            }
//            MotionEvent.ACTION_CANCEL -> {
//                // Unwanted touch detected.
//                cancelLastStroke()
//            }
//            else -> return false
//        }
//
//        requestRendering(
//            StylusState(
//                tilt = motionEvent.getAxisValue(MotionEvent.AXIS_TILT),
//                pressure = motionEvent.pressure,
//                orientation = motionEvent.orientation,
//                path = createPath()
//            )
//        )
//        
//        return true
//    }
//
//    private fun cancelLastStroke(): Nothing {
//        TODO("Not yet implemented")
//    }
//
//
//}