package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Path;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import project14_1.cookandroid.com.mobilewhiteboard.R;

public class Drawing extends View {
    float startX = -1, startY = -1, stopX = -1, stopY = -1; // 터치 좌표
    float oldx = -1, oldy = -1, i=0;//터치 좌표
    int id = -1, selectID =0;  //포스트잇 ID
    String p_text; // 포스트잇 text를 저장하는 변수
    EditText edt; // 동적 생성될 EditText
    WhiteboardActivity cnxt; //화이트보드 context
    WhiteboardActivity test;
    RectF setXY = new RectF(0, 0, 0, 0); // 터치좌표
    boolean et = false; //et는 end trigger를 줄인것. 그림을 그린 후 다시 그려지는 것 방지
    boolean addPostit = false; // 포스트잇 추가 여부 저장 변수
    long PressTime;

    private Path drawPath;
    public static Paint drawPaint, canvasPaint;
    public static int paintColor = 0xFF000000;
    private Canvas drawCanvas;
    public static Bitmap canvasBitmap;
    Bitmap bitmap = null;

    int dragCount = 0;
    int index = -1;

    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    ); //포스트잇이 그려지는 Layout 선언

    public static ArrayList<PictureHistory> pictures = new ArrayList<>(); //그림을 저장하는 배열
    public static ArrayList<TextHistory> textHistories = new ArrayList<>();
    public static HashMap<EditText,Integer> postIt_hashTable = new HashMap<EditText, Integer>();
    public static  ArrayList<EditText> edtList = new ArrayList<>(); //포스트잇의 EditText를 저장하는 배열

    public Drawing(Context context, AttributeSet attrs) {
        super(context, attrs);
        cnxt = (WhiteboardActivity) context; // EditText 동적 생성을 위한 context 선언
        test = (WhiteboardActivity) context;
        setupDrawing();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 사진 그리기
        for (int i = 0; i < pictures.size(); i++) {
            PictureHistory picture = pictures.get(i);
            int nh = (int) (picture.getBitmap().getHeight() * (1024.0 / picture.getBitmap().getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(picture.getBitmap(), 1024, nh, true);
            canvas.drawBitmap(scaled, null, picture.getSetXY(), null);
            scaled.recycle();
        }
        if (et == true) {
            WhiteboardActivity.type = 0;
            et = false;
            startX = -1;
            startY = -1;
            stopX = -1;
            stopY = -1;
        }else if (et == false && (stopX >= 0 && stopY >= 0)){
            setXY = new RectF(stopX, stopY, startX, startY);
            setXY.sort();

            bitmap = WhiteboardActivity.bitmap;
            if (bitmap != null) {
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                canvas.drawBitmap(scaled, null, setXY, null);
                scaled.recycle();
            }
        }else { }
        // 글 쓰기
        for(TextHistory textHistory : textHistories) {
            textHistory.drawCanvas(canvas);
        }

        //포스트잇 그리기
        if(addPostit == true){
            for(int i=0; i<id+1; i++){

                i = id;

                Toast.makeText(this.getContext(), "test, id= "+id, Toast.LENGTH_SHORT).show();
                edt = new EditText(cnxt);
                edt.setId(id);
                edt.setText(p_text); //ptet= ""


                edtList.add(edt);

                edt.setBackgroundResource(R.drawable.postit3);

                edtList.get(id).setLayoutParams(lp);
                ((RelativeLayout) this.getParent()).addView(edtList.get(id));



                edt.setHint("내용을 입력하시오");
                edt.setPadding(50,50,10,10);
                edt.setScaleX(0.6f);
                edt.setScaleY(0.6f);
                edt.setGravity(10);
                edt.setX(oldx);
                edt.setY(oldy);
                addPostit= false;
                // edt.setOnLongClickListener(mLongClickListener);
                int j=0;
                for(int k= -1; k<id; k++){
                    j = k;
                    j++;

                    //edtList.get(j).setOnLongClickListener(mLongClickListener);
                    edtList.get(j).setOnTouchListener(mTouchListener);
                }

                edt.setFocusable(false);
            }

        }
        // 손글씨 그리기
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (WhiteboardActivity.type) {
            case 1:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 터치한 영역에 있는 텍스트박스를 찾는다
                        index = -1;
                        dragCount = 0;
                        for(int i=0; i<textHistories.size(); i++){
                            if(textHistories.get(i).isClicked(event.getX(), event.getY())){
                                index = i;
                                break;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(dragCount>10 && index != -1){
                            // 10번 이상 move가 찍히면 드래그였다고 판단하고 처음 터치한 영역에 있는 텍스트를 현재 좌표로 이동시킨다.
                            textHistories.get(index).setPosition(event.getX(), event.getY());
                            invalidate();
                        }else{
                            dragCount++;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(index == -1){
                            Toast.makeText(this.getContext(), "선택한 영역에 텍스트박스가 없습니다.", Toast.LENGTH_SHORT).show();
                        } else if(dragCount<=10){
                            // 텍스트의 내용을 수정시킨다.
                            final WhiteboardActivity activity = (WhiteboardActivity) this.getContext();
                            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                            alert.setTitle("텍스트 수정");
                            // Create TextView
                            final EditText input = new EditText(activity);
                            input.setText(textHistories.get(index).getText().toString());
                            alert.setView(input);
                            alert.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    textHistories.get(index).editText(input.getText().toString());
                                    invalidate();
                                    //activity.drawing.invalidate();
                                }
                            });
                            alert.show();
                        }
                        break;
                }
                break;
            case 2:
                float touchX = event.getX();
                float touchY = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        drawPath.moveTo(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        drawPath.lineTo(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_UP:
                        drawPath.lineTo(touchX, touchY);
                        drawCanvas.drawPath(drawPath, drawPaint);
                        drawPath.reset();

                        break;
                    default:
                        return false;
                }
                break;
            case 3:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        et = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        stopX = event.getX();
                        stopY = event.getY();
                        et = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        PictureHistory picture = new PictureHistory(WhiteboardActivity.bitmap, startX, startY, stopX, stopY);
                        pictures.add(picture);

                        et = true;
                        break;
                }
                break;
            case 4:
                String test;

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        index = -1;
                        float X = event.getX();
                        float Y = event.getY();
                        oldx = X;
                        oldy = Y;

                        break;

                    case MotionEvent.ACTION_MOVE:

                        break;

                    case MotionEvent.ACTION_UP:


                        AlertDialog.Builder alert = new AlertDialog.Builder(cnxt);
                        alert.setTitle("텍스트 입력");
                        // Create TextView
                        final EditText input = new EditText(cnxt);
                        alert.setView(input);


                        alert.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                addPostit = true;
                                p_text = input.getText().toString();
                                invalidate();
                                id++;
                            }
                        });

                        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });

                        alert.show();

                        break;
                }
                break;
            default:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 터치한 영역에 있는 사진을 찾는다
                        index = -1;
                        dragCount = 0;
                        for(int i=0; i<pictures.size(); i++){
                            if(pictures.get(i).isClicked(event.getX(), event.getY())){
                                index = i;
                                pictures.get(i).makeGapX(event.getX());
                                pictures.get(i).makeGapY(event.getY());
                                PressTime = System.currentTimeMillis();
                                break;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (dragCount > 5 && index != -1) {
                            pictures.get(index).setPosition(event.getX(), event.getY());
                            invalidate();
                        }else {
                            dragCount++;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(!(index == -1)){
                            Toast.makeText(this.getContext(), "선택한 영역에 사진이 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                break;
        }
        invalidate();
        return true;
    }

    View.OnTouchListener mTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event){

            float X = event.getX();
            float Y = event.getY();

            switch (event.getAction()){

                case MotionEvent.ACTION_UP :

                    if(dragCount <= 5){

                        AlertDialog.Builder alert = new AlertDialog.Builder(cnxt);
                        alert.setTitle("수정하시겠습니까?");
                        final EditText input = new EditText(cnxt);
                        alert.setView(input);
                        // Create TextView

                        alert.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                p_text = input.getText().toString();
                                edtList.get(selectID).setText(p_text);


                            }
                        });

                        alert.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                ((RelativeLayout) getParent()).removeView(edtList.get(selectID));
                            }
                        });
                        alert.show();
                    }


                    break;

                case MotionEvent.ACTION_MOVE :

                    Log.i("B1", "MOVE" + X + "," + Y+","+v.getId());
                    //edtList.get(selectID).setX(X);
                    //edtList.get(selectID).setY(Y);

                    if (dragCount > 5 && selectID != -1) {

                        edtList.get(selectID).setX(event.getX());
                        edtList.get(selectID).setY(event.getY());

                        invalidate();
                    } else {

                        dragCount++;
                    }


                    break;

                case MotionEvent.ACTION_DOWN :

                    selectID = v.getId();
                    dragCount = 0;

                    Log.i("C1", "DOWN" + X + "," + Y+","+v.getId());

                    break;
            }
            return  false;

        }
    };

    private void setupDrawing(){
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Drawing.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                canvasBitmap = Bitmap.createBitmap(Drawing.this.getWidth(), Drawing.this.getHeight(), Bitmap.Config.ARGB_8888);
                drawCanvas = new Canvas(canvasBitmap);
            }
        });

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

}