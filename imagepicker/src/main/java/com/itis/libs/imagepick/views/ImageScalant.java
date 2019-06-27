package com.itis.libs.imagepick.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


import com.itis.libs.imagepick.R;
import com.itis.libs.imagepick.utils.ImageUtilities;
import com.itis.libs.imagepick.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.Random;

import androidx.appcompat.widget.AppCompatImageView;

public class ImageScalant extends AppCompatImageView implements Runnable {


    private boolean showing;

    private boolean showPreview = true;

    double aspectRatio;
    private Thread timer;
    /**
     * The bitmap captured by this Rectangle.
     */
    Bitmap capturedBitmap;
    TargetImageBox targetImageBox;

    CapturedImageBox capturedImageBox;
    boolean started;
    int xDelta;
    int yDelta;

    int $xDelta;
    int $yDelta;


    private boolean showGrid;

    /**
     * @param activity The activity that hosts the View.
     * @param image    The image that is drawn on the View.
     */
    public ImageScalant(final Activity activity, Bitmap image) {
        super(activity);

        load(image);
    }

    public ImageScalant(Context context) {
        super(context);

    }

    public ImageScalant(Context context, AttributeSet set) {
        super(context, set);
        load(null);
    }

    public ImageScalant(Context context, AttributeSet set, int a) {
        super(context, set, a);

        load(null);

    }

    public void load(Bitmap image) {
        setImageBitmap(image);

        if (timer != null && timer.isAlive()) {
            timer.interrupt();
            timer = null;
        }

        timer = new Thread(this);
        timer.start();


    }


    enum DRAG_MODE {
        RESIZE_TOP_LEFT, RESIZE_TOP_RIGHT, RESIZE_BOTTOM_LEFT, RESIZE_BOTTOM_RIGHT, RESIZE_TOP_BORDER, RESIZE_BOTTOM_BORDER,
        RESIZE_LEFT_BORDER, RESIZE_RIGHT_BORDER, STATIC, DRAG_TARGET_OBJECT, DRAG_CAPTURED_OBJECT;
    }

    DRAG_MODE dragMode = DRAG_MODE.STATIC;

    void init() {

        targetImageBox = new TargetImageBox();

        capturedImageBox = new CapturedImageBox();


        targetImageBox.setTopLeft(capturedImageBox.getX() + new Random().nextInt(30), capturedImageBox.getY() +
                capturedImageBox.getHeight() + new Random().nextInt(20));




        this.setOnTouchListener(new OnTouchListener() {


            Random r = new Random();

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int X = (int) event.getX();
                final int Y = (int) event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        $xDelta = X - capturedImageBox.topLeft.x;
                        $yDelta = Y - capturedImageBox.topLeft.y;

                        selectAction(X, Y);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (dragMode == DRAG_MODE.DRAG_TARGET_OBJECT) {//red,white----stable orange
                            capturedImageBox.setOutlineColor(Color.WHITE);
                            invalidate();
                        }

                        dragMode = DRAG_MODE.STATIC;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:

                        if (dragMode == DRAG_MODE.DRAG_TARGET_OBJECT) {
                            targetImageBox.setTopLeft(X - xDelta, Y - yDelta);
                            capturedImageBox.setOutlineColor(new int[]{Color.RED, Color.WHITE}[r.nextInt(2)]);


                        } else if (dragMode == DRAG_MODE.RESIZE_TOP_LEFT) {
                            int dw = (X - xDelta) - targetImageBox.topLeft.x;
                            int dh = (Y - yDelta) - targetImageBox.topLeft.y;
                            targetImageBox.setWidth(targetImageBox.width - dw);
                            targetImageBox.setHeight(targetImageBox.height - dh);
                            targetImageBox.setTopLeft(X - xDelta, Y - yDelta);
                        } else if (dragMode == DRAG_MODE.RESIZE_BOTTOM_LEFT) {
                            int dw = (X - xDelta) - targetImageBox.topLeft.x;
                            int dh = (Y - yDelta) - targetImageBox.topLeft.y - targetImageBox.height;

                            targetImageBox.setWidth(targetImageBox.width - dw);
                            targetImageBox.setHeight(targetImageBox.height + dh);
                            targetImageBox.setTopLeft(X - xDelta, targetImageBox.getY());
                        } else if (dragMode == DRAG_MODE.RESIZE_TOP_RIGHT) {
                            int dw = (X - xDelta) - targetImageBox.topLeft.x - targetImageBox.width;
                            int dh = (Y - yDelta) - targetImageBox.topLeft.y;

                            targetImageBox.setWidth(targetImageBox.width + dw);
                            targetImageBox.setHeight(targetImageBox.height - dh);
                            targetImageBox.setTopLeft(targetImageBox.getX(), Y - yDelta);
                        } else if (dragMode == DRAG_MODE.RESIZE_BOTTOM_RIGHT) {
                            int dw = (X - xDelta) - targetImageBox.topLeft.x - targetImageBox.width;
                            int dh = (Y - yDelta) - targetImageBox.topLeft.y - targetImageBox.height;
                     if(targetImageBox.topLeft.x+targetImageBox.width + dw <= getWidth() && targetImageBox.topLeft.y + targetImageBox.height + dh <= getHeight()) {
                         targetImageBox.setWidth(targetImageBox.width + dw);
                         targetImageBox.setHeight(targetImageBox.height + dh);
                     }
                        } else if (dragMode == DRAG_MODE.RESIZE_TOP_BORDER) {

                            int dh = (Y - yDelta) - targetImageBox.topLeft.y;

                            targetImageBox.setHeight(targetImageBox.height - dh);
                            targetImageBox.setTopLeft(targetImageBox.getX(), Y - yDelta);
                        } else if (dragMode == DRAG_MODE.RESIZE_LEFT_BORDER) {
                            int dw = (X - xDelta) - targetImageBox.topLeft.x;

                            targetImageBox.setWidth(targetImageBox.width - dw);
                            targetImageBox.setTopLeft(X - xDelta, targetImageBox.getY());
                        } else if (dragMode == DRAG_MODE.RESIZE_BOTTOM_BORDER) {
                            int dh = (Y - yDelta) - targetImageBox.topLeft.y - targetImageBox.height;

                            targetImageBox.setHeight(targetImageBox.height + dh);

                        } else if (dragMode == DRAG_MODE.RESIZE_RIGHT_BORDER) {
                            int dw = (X - xDelta) - targetImageBox.topLeft.x - targetImageBox.width;
                            targetImageBox.setWidth(targetImageBox.width + dw);
                        } else if (dragMode == DRAG_MODE.DRAG_CAPTURED_OBJECT) {//red,white----stable orange
                            capturedImageBox.setTopLeft(X - $xDelta, Y - $yDelta);
                        }

                        break;
                    default:
                        break;
                }//end switch

                postInvalidate();
                return true;
            }//end method
        });


    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setCropperBorderColor(int color) {
        this.targetImageBox.setOutlineColor(color);
    }
    public void setCropThickness(int borderThickness) {
        this.targetImageBox.setThickness(borderThickness);
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }

    public boolean isShowPreview() {
        return showPreview;
    }

    public void setShowing(boolean showing) {
        this.showing = showing;
    }

    public boolean isShowing() {
        return showing;
    }

    public void setImage(Bitmap image) {
        showing = true;
        if (image != null) {

            init();
            this.aspectRatio = (image.getWidth() * 1.0) / (image.getHeight() * 1.0);
            int x = (ImageScalant.this.getWidth() -
                    targetImageBox.width) / 2;
            int y = (ImageScalant.this.getHeight() - targetImageBox.height) / 2;

            targetImageBox.setTopLeft(x, y);


            setBackgroundColor(Color.rgb(34, 34, 34));
            setImageBitmap(image);

            int sizes[] = getBitmapDimensionsOnView(this);

            int[] offset = getImageOffset(this);

            if (targetImageBox.width > sizes[0] / 2) {
                targetImageBox.width = sizes[0] / 2;
                targetImageBox.height = targetImageBox.width;
            }
            if (targetImageBox.height > sizes[1] / 2) {
                targetImageBox.height = sizes[1] / 2;
                targetImageBox.width = targetImageBox.height;
            }

            if (targetImageBox.width < sizes[0] / 3) {
                targetImageBox.width = sizes[0] / 3;
                targetImageBox.height = targetImageBox.width;
            }
            if (targetImageBox.height < sizes[1] / 3) {
                targetImageBox.height = sizes[1] / 3;
                targetImageBox.width = targetImageBox.height;
            }

            targetImageBox.setTopLeft(offset[0] + targetImageBox.getWidth(), offset[1] + targetImageBox.getHeight());
            postInvalidate();

        } else {
            Utils.showShortToast(getContext(), "Couldn't pick the image due to errors. Please try again.");
            Activity activity = Utils.getActivity(this);
            if(activity != null) {
                activity.finish();
            }
        }

    }

    /**
     * Sets the image to be scaled from the given {@link Uri}
     *
     * @param uri The Uri of the image
     */
    public void setImage(final Uri uri) {


        try {

            //  Bitmap map = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            // setImage(map);
            InputStream stream = getContext().getContentResolver().openInputStream(uri);
            Bitmap map = ImageUtilities.decodeSampledBitmapFromStream(stream, getWidth(), true);
            setImage(map);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the image to be scaled from the given {@link Uri}
     *
     * @param f The File referencing the image
     */
    public void setImage(File f) {
        try {
            Bitmap map = ImageUtilities.decodeSampledBitmapFromFile(f, getWidth(), true);
            setImage(map);
        } catch (Exception e) {

        }
    }

    private void selectAction(int X, int Y) {


        if (targetImageBox.draggableRegionContains(X, Y)) {
            xDelta = X - targetImageBox.topLeft.x;
            yDelta = Y - targetImageBox.topLeft.y;
            dragMode = DRAG_MODE.DRAG_TARGET_OBJECT;
        } else if (targetImageBox.topLeftEdgeContains(X, Y)) {
            xDelta = X - targetImageBox.topLeft.x;
            yDelta = Y - targetImageBox.topLeft.y;
            dragMode = DRAG_MODE.RESIZE_TOP_LEFT;
        } else if (targetImageBox.bottomLeftEdgeContains(X, Y)) {
            xDelta = X - targetImageBox.topLeft.x;
            yDelta = Y - targetImageBox.topLeft.y - targetImageBox.height;
            dragMode = DRAG_MODE.RESIZE_BOTTOM_LEFT;
        } else if (targetImageBox.topRightEdgeContains(X, Y)) {
            xDelta = X - targetImageBox.topLeft.x - targetImageBox.width;
            yDelta = Y - targetImageBox.topLeft.y;

            dragMode = DRAG_MODE.RESIZE_TOP_RIGHT;
        } else if (targetImageBox.bottomRightEdgeContains(X, Y)) {
            xDelta = X - targetImageBox.topLeft.x - targetImageBox.width;
            yDelta = Y - targetImageBox.topLeft.y - targetImageBox.height;

            dragMode = DRAG_MODE.RESIZE_BOTTOM_RIGHT;
        } else if (targetImageBox.topBorderContains(X, Y)) {
            yDelta = Y - targetImageBox.topLeft.y;
            dragMode = DRAG_MODE.RESIZE_TOP_BORDER;
        } else if (targetImageBox.leftBorderContains(X, Y)) {
            xDelta = X - targetImageBox.topLeft.x;
            dragMode = DRAG_MODE.RESIZE_LEFT_BORDER;
        } else if (targetImageBox.bottomBorderContains(X, Y)) {
            yDelta = Y - targetImageBox.topLeft.y - targetImageBox.height;
            dragMode = DRAG_MODE.RESIZE_BOTTOM_BORDER;
        } else if (targetImageBox.rightBorderContains(X, Y)) {
            xDelta = X - targetImageBox.topLeft.x - targetImageBox.width;
            dragMode = DRAG_MODE.RESIZE_RIGHT_BORDER;
        } else if (capturedImageBox.contains(X, Y)) {
            dragMode = DRAG_MODE.DRAG_CAPTURED_OBJECT;
        }


    }

    public Bitmap getImage() {
        return ((BitmapDrawable) getDrawable()).getBitmap();
    }

    public boolean hasImage() {
        return ((BitmapDrawable) getDrawable()).getBitmap() != null;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
            if (started && showing) {
                    targetImageBox.draw(canvas);
                    capturedImageBox.draw(canvas);
            }
    }


    public void setCapturedBitmap(Bitmap capturedBitmap) {
        this.capturedBitmap = capturedBitmap;
    }

    public Bitmap getCapturedBitmap() {
        return capturedBitmap;
    }

    class TargetImageBox {


        /**
         * The thickness of the rectangle.
         */
        private int thickness = 8;

        private Point topLeft;
        private int width;
        private int height;

        Paint paint;


        /**
         * The thickness of the boundary
         * of this object on which it may be dragged.
         * This is the size of an imaginary square centered
         * about each edge of this object.Once a user clicks in the square,
         * he or she can drag this object.
         */
        private int draggableBoundaryThickness = 60;


        public static final int EDGE_EXTENSIONS = 24;

        private int outlineColor;

        public TargetImageBox() {
            outlineColor = Color.WHITE;
            width = height = 300;
            topLeft = new Point(1, 1);
            paint = new Paint();
            paint.setColor(outlineColor);
            paint.setStrokeWidth(thickness);

        }


        public Point getTopLeft() {
            return topLeft;
        }


        public void setTopLeft(int topX, int topY) {
            try {
                int[] offset = getImageOffset(ImageScalant.this);
                int[] img_dimensions = getBitmapDimensionsOnView(ImageScalant.this);

                int offsetX = offset[0];
                int offsetY = offset[1];


                int w = offsetX + img_dimensions[0];
                int h = offsetY + img_dimensions[1];
                this.topLeft = new Point(topX, topY);
                if (this.topLeft.x <= offsetX) {
                    this.topLeft.x = offsetX;
                }
                if (this.topLeft.x + width >= w) {
                    this.topLeft.x = w - width > 0 ? w - width : offsetX;
                }

                if (this.topLeft.y <= offsetY) {
                    this.topLeft.y = offsetY;
                }
                if (this.topLeft.y + height >= h) {
                    this.topLeft.y = h - height > 0 ? h - height : offsetY;
                }
            } catch (Exception e) {

            }
            postInvalidate();
        }

        public boolean contains(int x, int y) {
            boolean xContains = false;
            boolean yContains = false;

            if (x >= topLeft.x && x < topLeft.x + width) {
                xContains = true;
            }
            if (y >= topLeft.y && y < topLeft.y + height) {
                yContains = true;
            }
            return xContains && yContains;
        }

        public boolean topLeftEdgeContains(int x, int y) {
            Rect r = new Rect(topLeft.x - draggableBoundaryThickness / 2, topLeft.y - draggableBoundaryThickness / 2,
                    topLeft.x + draggableBoundaryThickness / 2, topLeft.y + draggableBoundaryThickness / 2);
            return r.contains(x, y);
        }

        public boolean topRightEdgeContains(int x, int y) {
            Rect r = new Rect(topLeft.x + width - draggableBoundaryThickness / 2, topLeft.y - draggableBoundaryThickness / 2,
                    topLeft.x + width + draggableBoundaryThickness / 2, topLeft.y + draggableBoundaryThickness / 2);
            return r.contains(x, y);
        }

        public boolean bottomLeftEdgeContains(int x, int y) {
            Rect r = new Rect(topLeft.x - draggableBoundaryThickness / 2, topLeft.y + height - draggableBoundaryThickness / 2,
                    topLeft.x + draggableBoundaryThickness / 2,
                    topLeft.y + height + draggableBoundaryThickness / 2);
            return r.contains(x, y);
        }

        public boolean bottomRightEdgeContains(int x, int y) {
            Rect r = new Rect(topLeft.x + width - draggableBoundaryThickness / 2, topLeft.y + height - draggableBoundaryThickness / 2,
                    topLeft.x + width + draggableBoundaryThickness / 2, topLeft.y + height + draggableBoundaryThickness / 2);
            return r.contains(x, y);
        }


        public boolean topBorderContains(int x, int y) {
            Rect r = new Rect(topLeft.x + draggableBoundaryThickness / 2, topLeft.y - draggableBoundaryThickness / 2,
                    topLeft.x + width - draggableBoundaryThickness / 2, topLeft.y + draggableBoundaryThickness / 2);
            return r.contains(x, y);
        }

        public boolean leftBorderContains(int x, int y) {
            Rect r = new Rect(topLeft.x - draggableBoundaryThickness / 2, topLeft.y + draggableBoundaryThickness / 2,
                    topLeft.x + draggableBoundaryThickness / 2,
                    topLeft.y + height - draggableBoundaryThickness / 2);
            return r.contains(x, y);
        }

        public boolean rightBorderContains(int x, int y) {
            Rect r = new Rect(topLeft.x + width - draggableBoundaryThickness / 2, topLeft.y + draggableBoundaryThickness / 2,
                    topLeft.x + width + draggableBoundaryThickness / 2,
                    topLeft.y + height - draggableBoundaryThickness / 2);
            return r.contains(x, y);
        }

        public boolean bottomBorderContains(int x, int y) {
            Rect r = new Rect(topLeft.x + draggableBoundaryThickness / 2, topLeft.y + height - draggableBoundaryThickness / 2,
                    topLeft.x + width - draggableBoundaryThickness / 2, topLeft.y + height + draggableBoundaryThickness / 2);
            return r.contains(x, y);
        }

        public boolean draggableRegionContains(int x, int y) {
            Rect r = new Rect(topLeft.x + draggableBoundaryThickness / 2, topLeft.y + draggableBoundaryThickness / 2,
                    topLeft.x + width - draggableBoundaryThickness / 2, topLeft.y + height - draggableBoundaryThickness / 2);
            return r.contains(x, y);
        }


        public int getX() {
            return topLeft.x;
        }

        public int getY() {
            return topLeft.y;
        }


        public void setWidth(int width) {
            int sizes[] = getBitmapDimensionsOnView(ImageScalant.this);
            this.width = width < sizes[0] ? width : sizes[0];
            if (this.width <= 80) {
                this.width = 80;
            }
            this.height = this.width;
            postInvalidate();
        }

        public int getWidth() {
            return width;
        }


        public void setHeight(int height) {
            int sizes[] = getBitmapDimensionsOnView(ImageScalant.this);
            this.height = height < sizes[1] ? height : sizes[1];
            if (this.height <= 80) {
                this.height = 80;
            }
            this.width = this.height;
            postInvalidate();
        }

        public int getHeight() {
            return height;
        }


        public int getOutlineColor() {
            return outlineColor;
        }

        public void setOutlineColor(int outlineColor) {
            this.outlineColor = outlineColor;
        }

        public void setThickness(int thickness) {
            this.thickness = thickness;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(thickness);
        }

        public int getThickness() {
            return thickness;
        }


        public void draw(Canvas canvas) {

            if (width < 0 || height < 0) {
                return;
            }
            try {
                Bitmap zaMap = Bitmap.createBitmap(width, height, Config.ARGB_8888);

                Canvas canv = new Canvas(zaMap);


                //	LGraphicsAndroid2D graphics = new LGraphicsAndroid2D(Bitmap.createBitmap(width,height, Conf.ARGB_8888));


                if (paint == null) {
                    paint = new Paint();
                    paint.setStrokeWidth(thickness);

                }

                int hei = thickness;

                paint.setColor(outlineColor);


                drawThickEdges:
                {
                    paint.setStyle(Paint.Style.STROKE);
                    //line going right from top left
                    //	graphics.fillRect(0, 0, EDGE_EXTENSIONS, hei);//top left-- [x,y,wid,hei]

                    canv.drawRect(0, 0, EDGE_EXTENSIONS, hei, paint);

                    //line going down from top left
                    //graphics.fillRect(0, 0, hei, EDGE_EXTENSIONS);//top left

                    canv.drawRect(0, 0, hei, EDGE_EXTENSIONS, paint);

                    //line going down from top right
                    //graphics.fillRect(width - hei, 0, hei, EDGE_EXTENSIONS);//top right

                    canv.drawRect(width - hei, 0, width, EDGE_EXTENSIONS, paint);//top right

                    //line going left from top right
                    //graphics.fillRect(width - EDGE_EXTENSIONS, 0, EDGE_EXTENSIONS, hei);
                    canv.drawRect(width - EDGE_EXTENSIONS, 0, width, hei, paint);//top right


                    //line going up from bottom left
                    //graphics.fillRect(0, height - EDGE_EXTENSIONS, hei, EDGE_EXTENSIONS);//bottom left

                    canv.drawRect(0, height - EDGE_EXTENSIONS, hei, height, paint);//bottom left

                    //line going right from bottom left
                    //	graphics.fillRect(0, height - hei, EDGE_EXTENSIONS, hei);//bottom left

                    canv.drawRect(0, height - hei, EDGE_EXTENSIONS, height, paint);//bottom left


                    //line going up from bottom right
                    //	graphics.fillRect(width - hei, height - EDGE_EXTENSIONS, hei, EDGE_EXTENSIONS);//bottom right

                    canv.drawRect(width - hei, height - EDGE_EXTENSIONS, width, height, paint);//bottom right

                    //line going left from bottom right
                    //graphics.fillRect(width - EDGE_EXTENSIONS, height - hei, EDGE_EXTENSIONS, hei);//bottom right

                    canv.drawRect(width - EDGE_EXTENSIONS, height - hei, width, height, paint);//bottom right

                }

                drawThickCenterMarkers:
                {
                    //top centre
                    int xLeft = (width - EDGE_EXTENSIONS) / 2;
                    //graphics.fillRect(xLeft, 0, EDGE_EXTENSIONS, hei);

                    canv.drawRect(xLeft, 0, xLeft + EDGE_EXTENSIONS, hei, paint);
                    //left centre
                    int top = (height - EDGE_EXTENSIONS) / 2;
                    //graphics.fillRect(0, top, hei, EDGE_EXTENSIONS);

                    canv.drawRect(0, top, hei, top + EDGE_EXTENSIONS, paint);

                    //right centre
                    top = (height - EDGE_EXTENSIONS) / 2;
                    //graphics.fillRect(width - hei, top, hei, EDGE_EXTENSIONS);

                    canv.drawRect(width - hei, top, width, top + EDGE_EXTENSIONS, paint);

                    //bottom centre
                    xLeft = (width - EDGE_EXTENSIONS) / 2;
                    //graphics.fillRect(xLeft, height - hei, EDGE_EXTENSIONS, hei);
                    canv.drawRect(xLeft, height - hei, xLeft + EDGE_EXTENSIONS, height, paint);

                }


                drawRect:
                {
                    canv.drawRect(thickness/2, thickness/2, width - thickness/2, height - thickness/2, paint);
                }

if(showGrid) {
    drawVerticalGrid:
    {

        //	graphics.setStroke(gridStroke);
//first vertical line a quarter distance from the left.
        //	graphics.fillRect(width/4,0,1,height);
       // canv.drawRect(width / 4, 0, 1 + width / 4, height, paint);
        canv.drawLine(width/4,0, width/4, height, paint);
//second vertical line half distance from the left.
        //	graphics.fillRect(width/2,0,1,height);
      //  canv.drawRect(width / 2, 0, 1 + width / 2, height, paint);
        canv.drawLine(width/2,0, width/2, height, paint);
//second vertical line three quarters distance from the left.
        //	graphics.fillRect(3*width/4,0,1,height);
      //  canv.drawRect(3 * width / 4, 0, 1 + 3 * width / 4, height, paint);
        canv.drawLine(3*width/4,0, 3*width/4, height, paint);
    }
    drawHorizontalGrid:
    {
//first horizontal line a quarter distance from the top.
        //	graphics.fillRect(0,height/4,width,1);
       // canv.drawRect(0, height / 4, width, 1 + height / 4, paint);
        canv.drawLine(0,height/4, width, height/4, paint);
//second horizontal line half distance from the top.
        //	graphics.fillRect(0,height/2,width,1);
        //canv.drawRect(0, height / 2, width, 1 + height / 2, paint);
        canv.drawLine(0,height/2, width, height/2, paint);
//second horizontal line three quarters distance from the top.
        //	graphics.fillRect(0,3*height/4,width,1);
       // canv.drawRect(0, 3 * height / 4, width, 1 + 3 * height / 4, paint);
        canv.drawLine(0,3*height/4, width, 3*height/4, paint);
    }
}


                canvas.drawBitmap(zaMap, topLeft.x, topLeft.y, paint);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }//end inner class


    class CapturedImageBox {


        Point topLeft;
        int width;
        int height;

        int outlineColor;
        int thickness;


        private Paint strokablePaint;


        public CapturedImageBox() {
            outlineColor = Color.WHITE;
            width = height = getResources().getDimensionPixelSize(R.dimen._100sdp);
            topLeft = new Point(1, 1);
            thickness = 10;

            strokablePaint = new Paint();
            strokablePaint.setStyle(Paint.Style.STROKE);
            strokablePaint.setColor(outlineColor);
            strokablePaint.setStrokeWidth(thickness);
        }


        public Point getTopLeft() {
            return topLeft;
        }


        public void setTopLeft(int topX, int topY) {
            int w = ImageScalant.this.getWidth();
            int h = ImageScalant.this.getHeight();

            this.topLeft = new Point(topX, topY);
            if (this.topLeft.x <= 1) {
                this.topLeft.x = 1;
            }
            if (this.topLeft.x + width >= w) {
                this.topLeft.x = w - width;
            }

            if (this.topLeft.y <= 1) {
                this.topLeft.y = 1;
            }
            if (this.topLeft.y + height >= h) {
                this.topLeft.y = h - height;
            }

        }

        public boolean contains(int x, int y) {
            boolean xContains = false;
            boolean yContains = false;

            if (x >= topLeft.x && x < topLeft.x + width) {
                xContains = true;
            }
            if (y >= topLeft.y && y < topLeft.y + height) {
                yContains = true;
            }
            return xContains && yContains;
        }

        public int getX() {
            return topLeft.x;
        }

        public int getY() {
            return topLeft.y;
        }


        public void setWidth(int width) {
            this.width = width < ImageScalant.this.getWidth() ? width : ImageScalant.this.getWidth();
            postInvalidate();
        }

        public int getWidth() {
            return width;
        }


        public void setHeight(int height) {
            this.height = height < ImageScalant.this.getHeight() ? height : ImageScalant.this.getHeight();
            postInvalidate();
        }

        public int getHeight() {
            return height;
        }


        public int getOutlineColor() {
            return outlineColor;
        }

        public void setOutlineColor(int outlineColor) {
            this.outlineColor = outlineColor;
        }

        public void setThickness(int thickness) {
            this.thickness = thickness;
        }

        public int getThickness() {
            return thickness;
        }


        public void draw(Canvas canvas) {

            if (width < 0 || height < 0) {
                return;
            }
            try {


                snapshot:
                {
                    int $offset[] = getImageOffset(ImageScalant.this);

                    int[] offset = getMapping(targetImageBox.topLeft.x - $offset[0], targetImageBox.topLeft.y - $offset[1], ImageScalant.this);

                    int x = offset[0];
                    int y = offset[1];

                    int size[] = getMapping(targetImageBox.width, targetImageBox.height, ImageScalant.this);

                    int wid = size[0];
                    int hei = size[1];

                    if (wid < 0 || hei < 0) {
                        capturedBitmap = Bitmap.createBitmap(20, 20, Config.ARGB_8888);
                    } else {
                        capturedBitmap = ImageUtilities.scaleImage(Bitmap.createBitmap(getImage(), x, y, wid, hei), this.width, true);
                    }
                }

                // g.drawBitmap(capturedBitmap, 0, 0,width,height);

                //    canv.drawBitmap(capturedBitmap, 0, 0, strokablePaint);


                drawRect:
                {
                    strokablePaint.setStrokeWidth(thickness);
                    strokablePaint.setColor(outlineColor);
                    //   Canvas canv = new Canvas(capturedBitmap);
                    //   canv.drawRect(0, 0, width, height, strokablePaint);
                }

                if(showPreview) {
                    canvas.drawBitmap(capturedBitmap, topLeft.x, topLeft.y, strokablePaint);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }//end inner class


    @Override
    public void run() {

        while (getWidth() <= 0 || getHeight() <= 0) {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        init();
        started = true;

        postInvalidate();
    }

    /**
     * @param view The view that has the image.
     * @return the distance of the image from the
     * left and top of the {@link ImageView}.
     */
    public static int[] getImageOffset(ImageView view) {
        try {
            Bitmap image = ((BitmapDrawable) view.getDrawable()).getBitmap();
            double viewWidth = view.getWidth();
            double viewHeight = view.getHeight();

            double viewAspect = viewWidth / viewHeight;

            double imgWidth = image.getWidth();
            double imgHeight = image.getHeight();

            double imgAspect = imgWidth / imgHeight;


            double drawLeft = 0, drawHeight = 0, drawTop = 0, drawWidth = 0;

            if (imgAspect > viewAspect) {
                drawLeft = 0;
                drawHeight = (viewAspect / imgAspect) * viewHeight;
                drawTop = (viewHeight - drawHeight) / 2;
            } else {
                drawTop = 0;
                drawWidth = (imgAspect / viewAspect) * viewWidth;
                drawLeft = (viewWidth - drawWidth) / 2;
            }

            return new int[]{(int) drawLeft, (int) drawTop};
        } catch (Exception e) {
            return new int[]{10, 10};
        }
    }

    /**
     * @param view The view that has the bitmap
     * @return the width and height that the view used
     * to draw the bitmap onto itself.
     */
    public static int[] getBitmapDimensionsOnView(ImageView view) {

        double viewWidth = view.getWidth();
        double viewHeight = view.getHeight();

        int[] offset = getImageOffset(view);


        int imgWidthApparent = (int) (viewWidth - 2 * offset[0]);

        int imgHeightApparent = (int) (viewHeight - 2 * offset[1]);


        return new int[]{imgWidthApparent, imgHeightApparent};
    }

    /**
     * @param w    An horizontal distance along the view.
     * @param h    A  vertical distance along the view.
     * @param view The view that has the image.
     * @return an array of size 2. Index 0 has the corresponding
     * horizontal distance along the image. Index 1 has the corresponding
     * vertical distance along the image.
     */
    public static int[] getMapping(int w, int h, ImageView view) {
        try {
            Bitmap image = ((BitmapDrawable) view.getDrawable()).getBitmap();

            double imgWidth = image.getWidth();
            double imgHeight = image.getHeight();

            int dimensions[] = getBitmapDimensionsOnView(view);


            double widthRatio = (dimensions[0] * 1.0) / (imgWidth);
            double heightRatio = (dimensions[1] * 1.0) / (imgHeight);

            return new int[]{(int) (w / widthRatio), (int) (h / heightRatio)};
        } catch (Exception e) {
            return new int[]{1, 1};
        }
    }


}





