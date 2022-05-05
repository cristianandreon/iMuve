package com.imuve.cristian.imuve;


import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.android.texample.GLText;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class MyRenderer implements Renderer {

    public boolean isTransacting = false;

    private float TEXT_BASE_SIZE = 100.0f;

    public GLText glText;
    public int curTab = 0;

    private Context context;                           // Context (from Activity)
    private int width = 100;                           // Updated to the Current Width + Height in onSurfaceChanged()
    private int height = 100;
    private float cameraX, cameraY, cameraWX, cameraWY;

    public GL10 gl;
    public boolean isBusy = false;

    private MainActivity mainActivity;


    public MyRenderer(Context context) {
        super();
        this.context = context;
        this.mainActivity = (MainActivity) MainActivity.getClassInstance();
        this.mainActivity.myRenderer = this;

        // Coordinate telecamera
        cameraX = mainActivity.GetCamera(0);
        cameraY = mainActivity.GetCamera(1);
        cameraWX = mainActivity.GetCamera(2);
        cameraWY = mainActivity.GetCamera(3);


        /////////////////////////////////
        // Operazioni 'First time'
        //
        try {
            mainActivity.DrawDwg(curTab, (int)0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDrawFrame(GL10 gl) {

        if (Constants.DRAW_BUSY)
            return;

        myDrawFrame(gl);

    }


    synchronized private void myDrawFrame(GL10 gl) {

        try {

            Constants.DRAW_BUSY = true;

            if (gl == null) gl = this.gl;


            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            gl.glClearDepthf(1.0f);
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();

            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();

            // Disegno del DWG
            int res = mainActivity.onDrawDwg(curTab, isTransacting);

            gl.glFlush();
            gl.glFinish();


        } catch (Exception e) {
            if (e != null) {
                e.getMessage();
                e.printStackTrace();
            }
        }


        Constants.DRAW_BUSY = false;
    }

    synchronized


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        this.gl = gl;
        this.mainActivity = (MainActivity) MainActivity.getClassInstance();

        gl.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);

        onPrepareText(gl);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        IntBuffer i = IntBuffer.allocate(2);

        gl.glGetIntegerv(GL10.GL_ALIASED_LINE_WIDTH_RANGE, i);
        int aliasedLineSizeMin = i.get(0);
        int aliasedLineSizeMax = i.get(1);


        gl.glViewport(0, 0, width, height);

        // make adjustments for screen ratio
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
        gl.glLoadIdentity();                        // reset the matrix to its default state
        // gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);  // apply the projection matrix

        // Set Ortho Projection (Left,Right,Bottom,Top,Front,Back)
        // gl.glOrthof(0, width, 0, height, 1.0f, -1.0f);


        this.width = width;
        this.height = height;

        if (width > 0) Constants.ScreenWX = width;
        if (height > 0) Constants.ScreenWY = height;
    }


    public void onPrepareText(GL10 gl) {
        MainActivity mainActivity = (MainActivity) MainActivity.getClassInstance();
        glText = new GLText(gl, mainActivity.getAssets());
        glText.load("Roboto-Regular.ttf", (int) TEXT_BASE_SIZE, 10, 10, true);
    }




    void onDrawText(String text, int horiz_alignment, int vert_alignment, float x, float y, float ht, float ang, float r, float g, float b, float a, char isMtext) {

        //////////////////////////
        //////////// DEBUG
        //
        // if (1==1) return;

        try {

            if (isMtext == 1) {
                int i, j, n = text.length();
                String out_text = "", format_str = "";
                for (i = 0; i < n; i++) {
                    if (text.charAt(i) == '\\') {
                        i++;
                        switch (text.charAt(i)) {
                            case 'L':
                                break;
                            case 'O':
                                break;
                            case 'o':
                                break;
                            case 'f':
                            case 'F':
                                j = i;
                                while (text.charAt(i) != ';') i++;
                                format_str = text.substring(j + 1, i);
                                // "Arial|b0|i0|c0|p34"
                                String[] fmt_array = format_str.split("\\|");
                                for (String fmt : fmt_array) {
                                    if (fmt.length() > 0) {
                                        if (fmt.charAt(0) == 'p' || fmt.charAt(0) == 'p') {
                                            float text_ht = Float.parseFloat(fmt.substring(1));
                                            if (text_ht > 0.0) ht = text_ht;
                                        }
                                    }
                                }
                                break;
                            case 'w':
                            case 'W':
                                j = i;
                                while (text.charAt(i) != ';') i++;
                                format_str = text.substring(j + 1, i);
                                break;
                            case 'h':
                            case 'H':
                                j = i;
                                while (text.charAt(i) != ';') i++;
                                format_str = text.substring(j + 1, i);
                                break;
                            default:
                                break;
                        }
                    } else if (text.charAt(i) == '{') {
                    } else if (text.charAt(i) == '}') {
                        if (text.charAt(i + 1) == '*') {
                            i++;
                        }
                    } else {
                        out_text += text.charAt(i);
                    }
                }

                if (out_text.length() == 0) {
                    return;
                } else if (out_text.length() == 1) {
                    if (out_text.charAt(0) == ' ') {
                        return;
                    }
                }

                text = out_text;

                // text = text.replaceAll("\L","");

                /*
                Code	Function
                \L	Start underline
                \l	Stop underline
                \O	Start overstrike
                \o	Stop overstrike
                \K	Start strike-through
                \k	Stop strike-through
                \P	New paragraph (new line)
                \pxi	Control codes for bullets, numbered paragraphs and columns
                \X	Paragraph wrap on the dimension line (only in dimensions)
                \Q	Slanting (obliquing) text by angle - e.g. \Q30;
                \H	Text height - e.g. \H3x;
                \W	Text width - e.g. \W0.8x;
                \F	Font selection

                e.g. \Fgdt;o - GDT-tolerance
                e.g. \Fkroeger|b0|i0|c238|p10 - font Kroeger, non-bold, non-italic, codepage 238, pitch 10

                \S	Stacking, fractions

                e.g. \SA^B:
                A
                        B
                e.g. \SX/Y:
                X
                        Y
                e.g. \S1#4:
                ¼

                \A	Alignment

                \A0; = bottom
                \A1; = center
                \A2; = top

                \C	Color change

                \C1; = red
                \C2; = yellow
                \C3; = green
                \C4; = cyan
                \C5; = blue
                \C6; = magenta
                \C7; = white

                \T	Tracking, char.spacing - e.g. \T2;
                \~	Non-wrapping space, hard space
                {}	Braces - define the text area influenced by the code
                \	Escape character - e.g. \\ = "\", \{ = "{"" +
                */

            } else {
                if (text.endsWith("*")) {
                    text = text.substring(0, text.length() - 1);
                }
            }

            if (ht > 0.0) {
                if (cameraWY / ht < 2.0f) {
                    return;
                }
            }

            if (ht > 0.0f) {
                float x_gap = 0.0f, y_gap = 0.0f;

                if (horiz_alignment == 1) {
                }

                if (isMtext == 1) {
                    y_gap = -ht;
                } else {
                    if (vert_alignment == 1) {
                        y_gap = -ht;
                    } else if (vert_alignment == 1) {
                        y_gap = -ht / 2.0f;
                    } else {
                        y_gap = 0;
                    }
                }

                if (gl != null) {
                    gl.glMatrixMode(GL10.GL_MODELVIEW);
                    gl.glLoadIdentity();

                    // gl.glMatrixMode( GL10.GL_PROJECTION );

                    gl.glEnable(GL10.GL_TEXTURE_2D);


                    int[] last_GL_LIGHTING = new int[1];
                    gl.glGetIntegerv(GL10.GL_LIGHTING, last_GL_LIGHTING, 0);
                    int[] last_GL_BLEND = new int[1];
                    gl.glGetIntegerv(GL10.GL_BLEND, last_GL_BLEND, 0);
                    int[] last_GL_DEPTH_TEST = new int[1];
                    gl.glGetIntegerv(GL10.GL_DEPTH_TEST, last_GL_DEPTH_TEST, 0);


                    gl.glEnable(GL10.GL_BLEND);
                    // gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                    // gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ZERO);
                    // gl.glBlendFunc(GL10.GL_DST_COLOR, GL10.GL_ONE);
                    // gl.glBlendFunc(GL10.GL_DST_COLOR, GL10.GL_ONE_MINUS_SRC_ALPHA);
                    // gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_SRC_ALPHA);
                    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_DST_ALPHA);


                    gl.glDisable(gl.GL_DEPTH_TEST);
                    gl.glDisable(gl.GL_LIGHTING);
                    gl.glDisableClientState(gl.GL_COLOR_ARRAY);


                    gl.glPushMatrix();

                    gl.glTranslatef(x + x_gap, y + y_gap, 0.0f);
                    if (ang != 0.0f) {
                        gl.glRotatef(ang * 180.0f / 3.14159265354f, 0.0f, 0.0f, 1.0f);
                    }

                    float text_scale = ht / TEXT_BASE_SIZE;

                    glText.setScale(text_scale);

                    // glText.setAngle (ang);

                    glText.begin(r, g, b, a);
                    glText.draw(text, 0.0f, 0.0f);
                    glText.end();


                    gl.glPopMatrix();

                    gl.glDisable(GL10.GL_TEXTURE_2D);


                    //////////////////////////////////
                    // Ripristino comne precedente
                    //
                    if (last_GL_LIGHTING[0] != 0) {
                        gl.glEnable(gl.GL_LIGHTING);
                    } else {
                        gl.glDisable(gl.GL_LIGHTING);
                    }

                    if (last_GL_BLEND[0] != 0) {
                        gl.glEnable(gl.GL_BLEND);
                    } else {
                        gl.glDisable(gl.GL_BLEND);
                    }


                    if (last_GL_DEPTH_TEST[0] != 0) {
                        gl.glEnable(gl.GL_DEPTH_TEST);
                    } else {
                        gl.glDisable(gl.GL_DEPTH_TEST);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

}