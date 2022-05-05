package com.imuve.cristian.imuve;



import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.support.v4.view.MotionEventCompat;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;


class DoubleTapGestureDetector extends GestureDetector.SimpleOnGestureListener {

    public MyGLSurfaceView myGLSurfaceView = null;
    public MainActivity mainActivity = null;
    public DWGViewerActivity dwgViewerActivity = null;


    @Override
    public boolean onDoubleTap(MotionEvent ev ) {
        // ("TAG", "Double Tap Detected ...");
        if (myGLSurfaceView != null && mainActivity != null) {

            myGLSurfaceView.stop_ACTION_UP = true;

            final int pointerIndex = MotionEventCompat.findPointerIndex(ev, myGLSurfaceView.mActivePointerId);

            // if (pointerIndex >= 0) {
            float centerX = myGLSurfaceView.mLastTouchX; // MotionEventCompat.getX(ev, pointerIndex);
            float centerY = myGLSurfaceView.mLastTouchY; // MotionEventCompat.getY(ev, pointerIndex);
            float scale = 3.0f;

            // renderer.isTransacting = false;
            if (Constants.getDrawAccess(2000)) {

                float ratioX = (centerX) / (Constants.ScreenWX);
                float ratioY = (Constants.ScreenWY - centerY) / (Constants.ScreenWY);
                float dWX = mainActivity.GetCamera(2) * ratioX;
                float dWY = mainActivity.GetCamera(3) * ratioY;

                float cameraCX = mainActivity.GetCamera(0) + dWX;
                float cameraCY = mainActivity.GetCamera(1) + dWY;

                float cameraWX = mainActivity.GetCamera(2) / scale;
                float cameraWY = mainActivity.GetCamera(3) / scale;
                float cameraX = cameraCX - cameraWX / 2.0f;
                float cameraY = cameraCY - cameraWY / 2.0f;

                mainActivity.SetCamera(cameraX, cameraY, cameraWX, cameraWY);
                mainActivity.WriteCamera();
            }

            myGLSurfaceView.requestRender();

            myGLSurfaceView.mDoubleTapped = true;

            return true;
        }

        return false;
    }
}




class MyGLSurfaceView extends GLSurfaceView {

    private float scale = 1f;
    private float centerX, centerY, cameraCX, cameraCY;
    private ScaleGestureDetector mScaleDetector;
    private boolean mScaleStarted = false;

    private GestureDetector mGesDetect;

    public MainActivity mainActivity;
    public MyRenderer renderer;
    public DWGViewerActivity dwgViewerActivity;

    public boolean stop_ACTION_UP = false;
    public boolean mDoubleTapped = false;


    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
        if (w>0 || h>0) {
            // this.setMinimumHeight(h);
            // this.setMinimumWidth(w);
            // holder.setFixedSize(w,h);
            // renderer.onSurfaceChanged(renderer.gl, w, h);
            mainActivity.SetScreen(w,h);
            requestRender();
        }
    }


    public MyGLSurfaceView(Context context){
        super(context);

        mainActivity = (MainActivity)MainActivity.getClassInstance();

        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new MyRenderer(context);

        setRenderer(renderer);

        // Render Mode
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


        // this.requestRender();


        // Gestione scale detector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());



        // Gestione double tap
        DoubleTapGestureDetector doubleTapGestureDetector = new DoubleTapGestureDetector();
        mGesDetect = new GestureDetector( context,doubleTapGestureDetector );

        doubleTapGestureDetector.mainActivity = mainActivity;
        doubleTapGestureDetector.myGLSurfaceView = this;
        doubleTapGestureDetector.dwgViewerActivity = dwgViewerActivity;

    }






    // SearchMode = 2    -> Exact search
    // SearchMode = 1    -> Free search
    // SearchMode = 0    -> Exact key search
    public int searchForTxt(String text, String keyType, int SearchMode) {


        if (!Constants.getDrawAccess(3000)) {
            return 0;
        }

        boolean bRestartSearch = true;
        if (mainActivity.lastSearchText != null) {
            if (mainActivity.lastSearchKey != null) {
                if (mainActivity.lastSearchText.compareToIgnoreCase(text) == 0) {
                    if (mainActivity.lastSearchKey.compareToIgnoreCase(keyType) == 0) {
                        bRestartSearch = false;
                    }
                }
            }
        }

        mainActivity.lastSearchText = text;
        mainActivity.lastSearchKey = keyType;

        if (bRestartSearch) {
            mainActivity.lastClickCurItem = 0;
        } else {
            mainActivity.lastClickCurItem++;
            if (mainActivity.lastClickCurItem >= mainActivity.lastClickCount) mainActivity.lastClickCurItem = 0;
        }

        mainActivity.lastClickCount = 0;
        mainActivity.lastClickOn = false;


        int resSearch = mainActivity.FindDwgText(text, SearchMode);


        // DEBUG
        // Forzatura risultato
        if (1==0) {
            mainActivity.lastClickOn = true;
            mainActivity.lastClickCurItem = 0;
            mainActivity.lastClickCount = 1;
            mainActivity.lastClickX[mainActivity.lastClickCurItem] = 1000;
            mainActivity.lastClickY[mainActivity.lastClickCurItem] = 1000;
            mainActivity.lastClickWH[mainActivity.lastClickCurItem] = 800;
            mainActivity.lastClickHT[mainActivity.lastClickCurItem] = 600;
        }



        // Options == 0	->	Exact Key search
        // Options == 1	->	Free search
        // Options == 2	->	Exact search
        if (resSearch > 0) {

            // TEST
            // if (1==1) return 0;

            if (mainActivity.lastClickOn) {
                // Do action if found

                mainActivity.lastClickOn = false;

                if (mainActivity.lastClickCount > 0) {

                    String message = "Trovato 1 elemento";
                    if (mainActivity.lastClickCount > 1) {
                        message = "Elemento " + (mainActivity.lastClickCurItem+1) + "/" + mainActivity.lastClickCount + "";
                    }


                    DialogBox.ShowMessage(message, mainActivity.getBaseContext(), 0);

                    if (mainActivity.lastClickCurItem >= mainActivity.lastClickCount) mainActivity.lastClickCurItem = 0;

                    float cameraX = mainActivity.lastClickX[mainActivity.lastClickCurItem];
                    float cameraY = mainActivity.lastClickY[mainActivity.lastClickCurItem];
                    float cameraWX = mainActivity.lastClickWH[mainActivity.lastClickCurItem];
                    float cameraWY = mainActivity.lastClickHT[mainActivity.lastClickCurItem];

                    cameraX += cameraWX / 2.0f;
                    cameraY += cameraWY / 2.0f;

                    if (keyType != null && keyType.compareToIgnoreCase("OGGETTO") == 0) {
                        cameraWX = cameraWY * APPData.DRAW_DETECT_OBJECT_ZOOM;
                    } else if (keyType != null && keyType.compareToIgnoreCase("VANO") == 0) {
                        cameraWX = cameraWY * APPData.DRAW_DETECT_ZONE_ZOOM;
                    } else {
                        cameraWX = cameraWY * (APPData.DRAW_DETECT_OBJECT_ZOOM+APPData.DRAW_DETECT_ZONE_ZOOM) / 2.0f;
                    }
                    cameraWY = cameraWX * (float)Constants.ScreenWY/(float)Constants.ScreenWX;
                    cameraX -= cameraWX / 2.0;
                    cameraY -= cameraWY / 2.0;

                    mainActivity.SetCamera(cameraX, cameraY, cameraWX, cameraWY);
                    mainActivity.WriteCamera();
                    requestRender();
                }


                return mainActivity.lastClickCount;
            }
        }
        return 0;
    }







    public int zoomExtens() {
        int ct = 0;

        if (!Constants.getDrawAccess(3000)) {
            return 0;
        }

        ct = mainActivity.SetCamera( 0.0f, 0.0f, -1.0f, -1.0f);
        mainActivity.WriteCamera();
        return 0;
    }




    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.005f, Math.min(scale, 200.0f));
            return true;
        }
    }








    // The ‘active pointer’ is the one currently moving our object.
    private boolean mPanStarted = false;
    private boolean mScaleDone = false;
    public int mActivePointerId = -1;
    float mLastTouchX, mLastTouchY, mTouchX, mTouchY;

    int INVALID_POINTER_ID  = -1;


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        if (mGesDetect.onTouchEvent(ev)) {
            return true;
        }
        if (mScaleDetector.onTouchEvent(ev)) {
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {

            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;

                mScaleStarted = false;
                mScaleDone = false;
                mPanStarted = false;
                mDoubleTapped = false;
                scale = 1.0f;

                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);

                mainActivity.PopCamera();

                renderer.isTransacting = true;

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                if (mActivePointerId >= 0) {
                    final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                    if (pointerIndex >= 0) {
                        mTouchX = MotionEventCompat.getX(ev, pointerIndex);
                        mTouchY = MotionEventCompat.getY(ev, pointerIndex);

                        float dx = mTouchX - mLastTouchX;
                        float dy = mTouchY - mLastTouchY;

                        renderer.isTransacting = true;

                        if (!mScaleDetector.isInProgress()) {
                            if (Math.abs(dx) > 5.0f || Math.abs(dy) > 5.0f) {
                                mPanStarted = true;
                                if (!Constants.DRAW_BUSY) {
                                    mainActivity.PushCamera();
                                    mainActivity.Pan(dx, dy, true);
                                    requestRender();
                                }
                            }
                        } else {
                            if (mScaleStarted == false) {
                                mScaleStarted = true;
                                // mainActivity.PopCamera();

                                // centerX = (mLastTouchX+mTouchX)/2.0f;
                                // centerY = (mLastTouchY+mTouchY)/2.0f;

                                centerX = mLastTouchX; // + dx/2.0f;
                                centerY = mLastTouchY; // + dy/2.0f;

                                float ratioX = (centerX) / (Constants.ScreenWX);
                                float ratioY = (Constants.ScreenWY - centerY) / (Constants.ScreenWY);
                                float dWX = mainActivity.GetCamera(2) * ratioX;
                                float dWY = mainActivity.GetCamera(3) * ratioY;

                                cameraCX = mainActivity.GetCamera(0) + dWX;
                                cameraCY = mainActivity.GetCamera(1) + dWY;
                            }

                            if (!Constants.DRAW_BUSY) {
                                mainActivity.PushCamera();
                                mainActivity.Zoom(cameraCX, cameraCY, scale, true);
                                requestRender();
                            }
                        }
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                try {
                    Timer timer = new Timer();
                    timer.schedule (new TimerTask() { public void run() { process_ACTION_UP(); } }, 150);
                } catch (Exception e) {
                    e.getMessage();
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                renderer.isTransacting = false;
                if (!Constants.DRAW_BUSY) {
                    requestRender();
                }
                break;
            }



            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (mActivePointerId >= 0) {
                    final float x = MotionEventCompat.getX(ev, mActivePointerId);
                    final float y = MotionEventCompat.getY(ev, mActivePointerId);

                    // Remember where we started (for dragging)
                    mLastTouchX = x;
                    mLastTouchY = y;
                    if (mScaleStarted) mScaleDone = true;
                }

                mainActivity.PopCamera();
                mScaleStarted = false;
                mPanStarted = false;
                scale = 1.0f;

                renderer.isTransacting = false;
                if (Constants.getDrawAccess(2000)) {
                    mainActivity.WriteCamera();
                }
                requestRender();

                break;
            }
        }
        return true;
    }



    public void process_ACTION_UP () {

        try {

            if (stop_ACTION_UP) {
                stop_ACTION_UP = false;
            } else {
                float dx = mTouchX - mLastTouchX;
                float dy = mTouchY - mLastTouchY;

                if (!mPanStarted && !mScaleStarted && !mScaleDone && !mDoubleTapped) {
                    // Click

                    if (Constants.getDrawAccess(2000)) {

                        // Rewind
                        mainActivity.lastClickCount = 0;
                        mainActivity.lastClickCurItem = 0;

                        if (mainActivity.onClickIntToDwg(mLastTouchX, Constants.ScreenWY - mLastTouchY, 0, true) > 0) {
                            if (mainActivity.lastClickOn) {
                                mainActivity.lastClickOn = false;

                                Handler mainHandler = new Handler(mainActivity.context.getMainLooper());
                                Runnable myRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        mainActivity.doClickOnObject("-1", mainActivity.lastClickKey[mainActivity.lastClickCurItem], DWGViewerActivity.mGLView.getContext(), dwgViewerActivity);
                                    }
                                };
                                mainHandler.post(myRunnable);
                            }
                        }
                    }
                }


                if (Constants.getDrawAccess(2000)) {
                    mainActivity.PopCamera();
                    mainActivity.WriteCamera();
                }

                mScaleDone = false;
                mScaleStarted = false;
                mPanStarted = false;
                scale = 1.0f;

                renderer.isTransacting = false;

                requestRender();

            }

            mActivePointerId = INVALID_POINTER_ID;

        } catch (Exception e) {
            e.getMessage();
        }
    }




}





