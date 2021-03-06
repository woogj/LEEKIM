package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Path;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import project14_1.cookandroid.com.mobilewhiteboard.MainActivity;
import project14_1.cookandroid.com.mobilewhiteboard.R;

import static android.content.ContentValues.TAG;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class Drawing extends View {
    float startX = -1, startY = -1, stopX = -1, stopY = -1, oldx = -1, oldy = -1, i=0;//터치 좌표
    int id = -1, selectID =0;  //포스트잇 ID
    String p_text; // 포스트잇 text를 저장하는 변수
    EditText edt; // 동적 생성될 EditText
    WhiteboardActivity cnxt; //화이트보드 context
    WhiteboardActivity test;
    RectF setXY = new RectF(0, 0, 0, 0); // 터치좌표
    boolean et = false; //et는 end trigger를 줄인것. 그림을 그린 후 다시 그려지는 것 방지
    boolean addPostit = false; // 포스트잇 추가 여부 저장 변수

    private Path drawPath1, drawPath2;
    public static Paint drawPaint1, drawPaint2, canvasPaint;
    //public static int paintColor = 0xFF000000;
    private Canvas drawCanvas;
    public static Bitmap canvasBitmap;
    Bitmap bitmap = null, DownIMG = null;

    int dragCount = 0;
    int index = -1;
    Boolean Press = false,
            Start = true,
            OtherSigI = false,
            OtherSigP = false;
    Long timer;

    int MaxBufferSize = 1 * 1024 * 1024;

    String Paths = null;

    String mJsonString;
    private static final String TAG_JSON="data", TAG_CONTENT_PATH="content_path", TAG_CONTENT_TYPE="content_type", TAG_CONTENTX="contentX", TAG_CONTENTY="contentY", TAG_CONTENT_WIDTH="content_width", TAG_CONTENT_HEIGHT = "content_height";

    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
    ); //포스트잇이 그려지는 Layout 선언

    public static HashMap<EditText,Integer> postIt_hashTable = new HashMap<EditText, Integer>();
    public static ArrayList<EditText> edtList = new ArrayList<>(); //포스트잇의 EditText를 저장하는 배열

    public static ArrayList<ContentHistory> All = new ArrayList<ContentHistory>(); //그림과 글을 저장하는 배열 [배열명 수정 필요]
    public static ArrayList<ContentHistory> List = new ArrayList<ContentHistory>();
    public static ArrayList<ContentHistory> New = new ArrayList<ContentHistory>();
    public static ArrayList<ContentHistory> Draw = new ArrayList<ContentHistory>();

    public Drawing(Context context, AttributeSet attrs) {
        super(context, attrs);
        cnxt = (WhiteboardActivity) context; // EditText 동적 생성을 위한 context 선언
        test = (WhiteboardActivity) context;
        setupDrawing();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("TAG", All.toString());
        Long now = System.currentTimeMillis();
        for (int i = 0; i < Draw.size(); i++) {
            ContentHistory map = Draw.get(i);
            if (map.getType().equals("Picture")) {
                int nh = (int) (map.getBitmap().getHeight() * (512.0 / map.getBitmap().getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(map.getBitmap(), 512, nh, true);
                canvas.drawBitmap(scaled, null, map.getSetXY(), null);
                scaled.recycle();
            }
        }
        for (int i = 0; i < All.size(); i++) {
            if (Press && index == i) {

            } else {
                //배열에 있는 것들을 그린다.
                ContentHistory ch = All.get(i);
                if (ch.getType().equals("Text")) {
                    //글 그리기
                    ch.drawCanvas(canvas);
                } else if (ch.getType().equals("Drawing")) {
                    //손글씨 그리기
                    if (Paths != null && ch.getSig() == "Y") {
                        ch.setCoord(Paths);
                        ch.setSig("N");
                    } else if (ch.getCoord().equals("null") && ch.getSig() == "N") {
                        if(!OtherSigP) {
                            //경로가 비어있을 경우
                            load_paths task1 = new load_paths();
                            task1.execute("http://" + MainActivity.IPaddress + "/android_db_api/" + ch.getPath());
                            ch.setSig("Y");
                            OtherSigP = true;
                        }
                    } else if (ch.getCoord().startsWith("{")) {
                        //경로가 json형식으로 저장되있는 경우 내용을 배열에 넣는다.
                        Log.d("TAG", Paths);
                        showPaths(i);
                    } else if (!ch.getCoord().equals("null") && Paths == null){
                        //배열에 있는 정보로 그림 그리기
                        String[] coordA = ch.getCoord().split("/"), coordB;
                        float touchX = 0, touchY = 0;
                        for (int j = 0; j < coordA.length; j++) {
                            coordB = coordA[j].split(",");
                            for (int k = 0; k < coordB.length; k++) {
                                if (k == 0) {
                                    touchX = Float.parseFloat(coordB[0]);
                                } else if (k == 1) {
                                    touchY = Float.parseFloat(coordB[1]);
                                }
                            }
                            if (j == 0) {
                                drawPath1.moveTo(touchX, touchY);
                            } else {
                                drawPath1.lineTo(touchX, touchY);
                            }
                        }
                        drawPaint1.setColor(ch.getColor());
                        drawCanvas.drawPath(drawPath1, drawPaint1);
                        drawPath1.reset();
                    }else { }
                } else if (ch.getType().equals("Picture")) {
                    // 사진 그리기
                    if (DownIMG != null && ch.getSig() == "Y") {
                        ch.setBitmap(DownIMG);
                        All.add(i, ch);
                        DownIMG = null;
                        ch.setSig("N");
                    } else if (ch.checkBitmap().equals("null") && ch.getSig() == "N") {
                        if (!OtherSigI) {
                            load_image task2 = new load_image();
                            task2.execute("http://" + MainActivity.IPaddress + "/android_db_api/" + ch.getPath());
                            ch.setSig("Y");
                            OtherSigI = true;
                        }
                    } else if (!ch.checkBitmap().equals("null")) {
                        int nh1 = (int) (ch.getBitmap().getHeight() * (512.0 / ch.getBitmap().getWidth()));
                        Bitmap scaled1 = Bitmap.createScaledBitmap(ch.getBitmap(), 512, nh1, true);
                        canvas.drawBitmap(scaled1, null, ch.getSetXY(), null);
                        scaled1.recycle();
                        OtherSigI = false;
                    }else { }
                } else {
                    Log.d(TAG, "error");
                }
            }
            if (et == true) {
                WhiteboardActivity.type = 0;
                et = false;
                startX = -1;
                startY = -1;
                stopX = -1;
                stopY = -1;
            } else if (et == false && (stopX >= 0 && stopY >= 0)) {
                setXY = new RectF(stopX, stopY, startX, startY);
                setXY.sort();

                bitmap = WhiteboardActivity.bitmap;
                if (bitmap != null) {
                    int nh2 = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                    Bitmap scaled2 = Bitmap.createScaledBitmap(bitmap, 512, nh2, true);
                    canvas.drawBitmap(scaled2, null, setXY, null);
                    scaled2.recycle();
                }
            } else {
            }
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
        //손글씨 바로 그릴 때
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath2, drawPaint2);
        if(Start) {
            LoopInvalidate li = new LoopInvalidate();
            RenewThread rt = new RenewThread();
            li.start();
            rt.start();
            Start = false;
        }
        //Log.d("TAG", All.toString()); //배열 확인용
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (WhiteboardActivity.type) {
            case 1:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        ContentHistory map = new ContentHistory(WhiteboardActivity.data, event.getX(),event.getY());
                        Draw.add(map);
                        GetData task = new GetData();
                        if(task.getStatus()== AsyncTask.Status.RUNNING){
                            task.cancel(true);
                        }
                        task.execute(map.getText(), "Text", Float.toString(map.getX()), Float.toString(map.getY()), Float.toString(map.getWidth()), Float.toString(map.getHeight()));
                        Draw.clear();
                        WhiteboardActivity.type = 0;
                        break;
                }
                break;
            case 2:
                float touchX = event.getX();
                float touchY = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        drawPath2.moveTo(touchX, touchY);
                        Paths = "{\r\n\t\"data\" : [\r\n\t\t{\r\n\t\t\t\"coord\": \"" + (int)touchX +","+ (int)touchY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        drawPath2.lineTo(touchX, touchY);
                        Paths += "/" + (int)touchX +","+ (int)touchY;
                        break;
                    case MotionEvent.ACTION_UP:
                        Paths += "\",\r\n\t\t\t\"color\": \"" + drawPaint2.getColor() + "\"\r\n\t\t}\r\n\t]\r\n}";
                        drawPath2.lineTo(touchX, touchY);
                        drawCanvas.drawPath(drawPath2, drawPaint2);
                        drawPath2.reset();
                        ContentHistory map = new ContentHistory(Paths, drawPaint2.getColor(),0,0,0,0);
                        Draw.add(map);
                        GetData task = new GetData();
                        if(task.getStatus()== AsyncTask.Status.RUNNING){
                            task.cancel(true);
                        }
                        task.execute(map.getPath(), "Drawing");
                        Draw.clear();
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
                        ContentHistory map = new ContentHistory(WhiteboardActivity.absolutePath, WhiteboardActivity.bitmap, startX, startY, stopX, stopY);
                        Draw.add(map);
                        GetData task = new GetData();
                        if(task.getStatus()== AsyncTask.Status.RUNNING){
                            task.cancel(true);
                        }
                        task.execute(WhiteboardActivity.absolutePath, "Picture", Float.toString(map.getX()) , Float.toString(map.getY()), Float.toString(map.getWidth()), Float.toString(map.getHeight()));
                        Draw.clear();
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
                LongClickEvent lce;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 터치한 영역에 있는 것을 찾는다
                        Press = true;
                        index = -1;
                        dragCount = 0;
                        for(int i=0; i<All.size(); i++){
                            System.out.println(All.get(i).isClicked(event.getX(), event.getY()));
                            if(All.get(i).isClicked(event.getX(), event.getY())){
                                index = i;
                                Draw.add(All.get(i));
                                Draw.get(0).makeGapX(event.getX());
                                Draw.get(0).makeGapY(event.getY());

                                lce = new LongClickEvent();
                                lce.execute(Draw.get(0));
                                i = All.size();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (dragCount > 5 && index != -1) {
                            Draw.get(0).setPosition(event.getX(), event.getY());
                        }else {
                            dragCount++;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Press = false;
                        if(index == -1){

                        }else if(dragCount > 5){
                            switch (Draw.get(0).getType()) {
                                case "Text" :
                                    GetData PutText = new GetData();
                                    if(PutText.getStatus()== AsyncTask.Status.RUNNING){
                                        PutText.cancel(true);
                                    }
                                    PutText.execute(Draw.get(0).getText(), Draw.get(0).getText(),"Text", Float.toString(Draw.get(0).getX()) , Float.toString(Draw.get(0).getY()), Float.toString(Draw.get(0).getWidth()), Float.toString(Draw.get(0).getHeight()));
                                    break;
                                case "Picture" :
                                    GetData PutPicture= new GetData();
                                    if(PutPicture.getStatus()== AsyncTask.Status.RUNNING){
                                        PutPicture.cancel(true);
                                    }
                                    PutPicture.execute(Draw.get(0).getPath(), Draw.get(0).getPath(),"Picture", Float.toString(Draw.get(0).getX()) , Float.toString(Draw.get(0).getY()), Float.toString(Draw.get(0).getWidth()), Float.toString(Draw.get(0).getHeight()));
                                    break;
                            }
                        }else if (Draw.get(0).getType().equals("Text")) {
                            // 텍스트의 내용을 수정시킨다.
                            final WhiteboardActivity activity = (WhiteboardActivity) this.getContext();
                            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                            alert.setTitle("텍스트 수정");
                            // Create TextView
                            final EditText input = new EditText(activity);
                            WhiteboardActivity.data = Draw.get(0).getText();
                            input.setText(Draw.get(0).getText().toString());
                            alert.setView(input);
                            alert.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Draw.get(0).editText(input.getText().toString());
                                    GetData SetText = new GetData();
                                    if(SetText.getStatus()== AsyncTask.Status.RUNNING){
                                        SetText.cancel(true);
                                    }
                                    SetText.execute(WhiteboardActivity.data, Draw.get(0).getText(),"Text", Float.toString(Draw.get(0).getX()) , Float.toString(Draw.get(0).getY()), Float.toString(Draw.get(0).getWidth()), Float.toString(Draw.get(0).getHeight()));
                                }
                            });
                            alert.show();
                        }else {

                        }
                        Press = false;
                        index = -1;
                        Draw.clear();
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

        drawPath1 = new Path();
        drawPath2 = new Path();
        drawPaint1 = new Paint();
        drawPaint2 = new Paint();
        //drawPaint.setColor(paintColor);
        drawPaint1.setAntiAlias(true);
        drawPaint1.setStrokeWidth(30);
        drawPaint1.setStyle(Paint.Style.STROKE);
        drawPaint1.setStrokeJoin(Paint.Join.ROUND);
        drawPaint1.setStrokeCap(Paint.Cap.ROUND);
        drawPaint2.setAntiAlias(true);
        drawPaint2.setStrokeWidth(30);
        drawPaint2.setStyle(Paint.Style.STROKE);
        drawPaint2.setStrokeJoin(Paint.Join.ROUND);
        drawPaint2.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    //JSON 디코드
    private void showPaths(int j){
        try {
            mJsonString = Paths;
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);

                ContentHistory map = All.get(j);
                map.setCoord(item.getString("coord"));
                map.setColor(Integer.parseInt(item.getString("color")));
                All.add(j, map);
            }
            OtherSigP = false;
            Paths = null;
        } catch (JSONException e) {
            Log.d(TAG, "showPaths : ", e);
        }
    }

    //백그라운드
    private class showResult extends AsyncTask<Void,Void,Boolean> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... work) {
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                New = All;
                List.clear();

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    if (item.getString(TAG_CONTENT_TYPE).equals("Picture")) {
                        ContentHistory map = new ContentHistory(item.getString(TAG_CONTENT_PATH), item.getString(TAG_CONTENTX), item.getString(TAG_CONTENTY), item.getString(TAG_CONTENT_WIDTH), item.getString(TAG_CONTENT_HEIGHT));
                        List.add(map);
                    }else if (item.getString(TAG_CONTENT_TYPE).equals("Text")) {
                        ContentHistory map = new ContentHistory(item.getString(TAG_CONTENT_PATH), Float.parseFloat(item.getString(TAG_CONTENTX)), Float.parseFloat(item.getString(TAG_CONTENTY)));
                        List.add(map);
                    }else if (item.getString(TAG_CONTENT_TYPE).equals("Drawing")) {
                        ContentHistory map = new ContentHistory(item.getString(TAG_CONTENT_PATH), 0, Float.parseFloat(item.getString(TAG_CONTENTX)), Float.parseFloat(item.getString(TAG_CONTENTY)), Float.parseFloat(item.getString(TAG_CONTENT_WIDTH)), Float.parseFloat(item.getString(TAG_CONTENT_HEIGHT)));
                        List.add(map);
                    }else {

                    }
                }
                //중복배열 삭제
                ArrayList<Integer> overlap = new ArrayList<>();
                for (int i = 0; i < List.size(); i++) {
                    for (int j = i+1; j < List.size(); j++) {
                        if (List.get(i).equals(List.get(j))) {
                            overlap.add(j);
                        }
                    }
                }
                for (int i = 0; i < overlap.size(); i++) {
                    List.remove(overlap.get(i));
                }


                for (int i = 0; i < List.size(); i++) {
                    for (int j = i; j < New.size(); j++) {
                        if (List.get(i).getPath().equals(New.get(j).getPath())) {
                            if ((List.get(i).getX() == New.get(j).getX()) && (List.get(i).getY() == New.get(j).getY())) {
                                if (i != j) {
                                    New.add(i, New.get(j));
                                    New.remove(j + 1);
                                }
                            } else {
                                if (i != j) {
                                    New.add(i, New.get(j));
                                    New.remove(j + 1);
                                }
                                New.get(i).setX(List.get(i).getX());
                                New.get(i).setY(List.get(i).getY());
                                if (New.get(i).getType().equals("Picture")) {
                                    New.get(i).setXY(new RectF(List.get(i).getX(), List.get(i).getY(), List.get(i).getX() + List.get(i).getWidth(), List.get(i).getY() + List.get(i).getHeight()));
                                }
                            }
                        } else { }
                    }
                }

                if (New.size() == List.size()) {
                    for (int i = 0; i < List.size(); i++) {
                        if (!(New.get(i).getPath().equals(List.get(i).getPath()))) {
                            New.add(List.get(i));
                            New.remove(i);
                        }
                    }
                }else if (New.size() > List.size()) {
                    for (int i = 0; i < New.size(); i++) {
                        if (i > List.size() - 1) {
                            New.remove(i);
                        }else if (!(New.get(i).getPath().equals(List.get(i).getPath()))) {
                            New.remove(i);
                            New.add(List.get(i));
                        }
                    }
                }else if (List.size() > New.size()) {
                    for (int i = 0; i < List.size(); i++) {
                        if (i > New.size() - 1) {
                            New.add(List.get(i));
                        }else if (!(New.get(i).getPath().equals(List.get(i).getPath()))) {
                            New.add(List.get(i));
                            New.remove(i);
                        }
                    }
                }else { }

                All = New;
            } catch (JSONException e) {
                Log.d(TAG, "showResult : ", e);
            }
            invalidate();

            return false;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
        }
    }

    private class load_image extends AsyncTask<String,Integer,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoOutput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                DownIMG = BitmapFactory.decodeStream(is);

                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return DownIMG;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

    private class load_paths extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(String... params) {
            String searchKeyword1 = params[0];

            String serverURL = "http://" + MainActivity.IPaddress + "/android_db_api/whiteboardJSONload.php";
            String postParameters = "content_path=" + searchKeyword1;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = conn.getResponseCode();
                Log.d(TAG, "response code Coord - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                conn.disconnect();

                Paths = sb.toString().trim();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private class LongClickEvent extends AsyncTask<ContentHistory,String,Boolean> {
        ContentHistory cont;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(ContentHistory... Time) {
            cont = Time[0];
            long now = System.currentTimeMillis(), past = now;
            while (1000 >= now - past){
                now = System.currentTimeMillis();
            }
            if (dragCount <= 5 && Press) {
                Log.d("TAG", "롱클릭 Y");
                return true;
            }else {
                Log.d("TAG", "롱클릭 N");
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(String... params) {

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            Press = false;
            dragCount = 0;
            index = -1;

            if (result) {
                AlertDialog.Builder alert = new AlertDialog.Builder(cnxt);
                alert.setTitle("삭제 하시겠습니까?");

                alert.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                //HW
                alert.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //DB에서 삭제
                        RemoveData rd = new RemoveData();
                        rd.execute(cont.getPath(), cont.getType(), Float.toString(cont.getX()), Float.toString(cont.getY()));
                    }
                });

                alert.show();
            }
        }
    }

    private class LoopInvalidate extends Thread {
        public void run() {
            while (true) {
                invalidate();

                long now = System.currentTimeMillis(), past = now;
                while (500 >= now - past){ //HW 시간초 조절 필요
                    now = System.currentTimeMillis();
                }
            }
        }
    }

    private class RenewThread extends Thread {
        public void run() {
            while (true) {
                GetDataThread gdt = new GetDataThread();
                gdt.start();

                long now = System.currentTimeMillis(), past = now;
                while (500 >= now - past){ //HW 시간초 조절 필요
                    now = System.currentTimeMillis();
                }
            }
        }
    }

    private class GetDataThread extends Thread {
        public void run() {
            String errorString = null, result = null;

            String serverURL = "";
            String postParameters = "";

            HttpURLConnection conn = null;
            DataOutputStream dos = null;

            String lineEnd = "\r\n";
            String twoHypens = "--";
            String boundary = "*****";

            serverURL = "http://" + MainActivity.IPaddress + "/android_db_api/whiteboardDBload.php";
            postParameters = "whiteboardID=" + "1" + "&teamID=" + "1";
            // 1 부분은 변수로 변경 필요

            try {
                URL url = new URL(serverURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = conn.getResponseCode();
                Log.d(TAG, "response code All - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                conn.disconnect();

                result = sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
            }
            if (result == null){
                All.clear();
                drawPaint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            } else if (result.startsWith("{")) {
                mJsonString = result;
                showResult sr = new showResult();
                if(sr.getStatus()== AsyncTask.Status.RUNNING){
                    sr.cancel(true);
                }
                sr.execute();
            } else { }
        }
    }

    private class RemoveData extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            if (result == null){
                Toast.makeText(Drawing.this.getContext(), errorString, Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(Drawing.this.getContext(), result, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = "";
            String postParameters = "";

            HttpURLConnection conn = null;
            DataOutputStream dos = null;

            String lineEnd = "\r\n";
            String twoHypens = "--";
            String boundary = "*****";
            if (params.length == 4) {
                String searchKeyword1 = params[0];
                String searchKeyword2 = params[1];
                String searchKeyword3 = params[2];
                String searchKeyword4 = params[3];

                serverURL = "http://" + MainActivity.IPaddress + "/android_db_api/whiteboardDBdelete.php";
                postParameters = "content_path=" + searchKeyword1 + "&content_type=" + searchKeyword2 + "&contentX=" + searchKeyword3 + "&contentY=" + searchKeyword4;

                try {
                    URL url = new URL(serverURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    int responseStatusCode = conn.getResponseCode();
                    Log.d(TAG, "response code Text - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = conn.getInputStream();
                    } else {
                        inputStream = conn.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    conn.disconnect();

                    return sb.toString().trim();
                } catch (Exception e) {
                    Log.d(TAG, "InsertData: Error ", e);
                    errorString = e.toString();
                }
            }
            return null;
        }
    }



    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            if (result == null){
                Toast.makeText(Drawing.this.getContext(), errorString, Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(Drawing.this.getContext(), result, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = "";
            String postParameters = "";

            HttpURLConnection conn = null;
            DataOutputStream dos = null;

            String lineEnd = "\r\n";
            String twoHypens = "--";
            String boundary = "*****";
            if (params.length == 7) {
                String searchKeyword1 = params[0];
                String searchKeyword2 = params[1];
                String searchKeyword3 = params[2];
                String searchKeyword4 = params[3];
                String searchKeyword5 = params[4];
                String searchKeyword6 = params[5];
                String searchKeyword7 = params[6];

                serverURL = "http://" + MainActivity.IPaddress + "/android_db_api/whiteboardDBupdate.php";
                postParameters = "content_path=" + searchKeyword1 + "&new_content_path=" + searchKeyword2 + "&content_type=" + searchKeyword3 + "&contentX=" + searchKeyword4 + "&contentY=" + searchKeyword5 + "&content_width=" + searchKeyword6 + "&content_height=" + searchKeyword7;

                try {
                    URL url = new URL(serverURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    int responseStatusCode = conn.getResponseCode();
                    Log.d(TAG, "response code Text - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = conn.getInputStream();
                    } else {
                        inputStream = conn.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    conn.disconnect();

                    return sb.toString().trim();
                } catch (Exception e) {
                    Log.d(TAG, "InsertData: Error ", e);
                    errorString = e.toString();

                    return null;
                }
            } else if (params.length == 6) {
                String searchKeyword1 = params[0];
                String searchKeyword2 = params[1];
                String searchKeyword3 = params[2];
                String searchKeyword4 = params[3];
                String searchKeyword5 = params[4];
                String searchKeyword6 = params[5];

                if (params[1].equals("Picture")) {
                    serverURL = "http://" + MainActivity.IPaddress + "/android_db_api/whiteboardIMGsave.php";

                    File imgFile = new File(searchKeyword1);
                    if (imgFile.exists()) {
                        //이미지 파일이 있으면.
                        try {
                            FileInputStream FIS = new FileInputStream(imgFile);

                            URL url = new URL(serverURL);

                            conn = (HttpURLConnection) url.openConnection();

                            // connection setting....
                            conn.setDoInput(true); // Allow Inputs
                            conn.setDoOutput(true); // Allow Outputs
                            conn.setUseCaches(false); // Don't use a Cached Copy
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Connection", "Keep-Alive");
                            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                            dos = new DataOutputStream(conn.getOutputStream());
                            //데이터 쓰기 시작
                            dos.writeBytes(twoHypens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_img\";filename=\""
                                    + searchKeyword1 + "\"" + lineEnd);

                            dos.writeBytes(lineEnd);

                            int byteAvaiable = FIS.available();

                            int bufferSize = Math.min(byteAvaiable, MaxBufferSize);
                            byte[] buffer = new byte[bufferSize];

                            int byteRead = FIS.read(buffer, 0, bufferSize);

                            while (byteRead > 0) {
                                dos.write(buffer, 0, bufferSize);
                                byteAvaiable = FIS.available();
                                bufferSize = Math.min(byteAvaiable, MaxBufferSize);
                                byteRead = FIS.read(buffer, 0, bufferSize);
                            }

                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHypens + boundary + lineEnd);

                            //data 쓰기 완료.
                            int responseStatusCode = conn.getResponseCode();
                            Log.d(TAG, "response code IMG - " + responseStatusCode);

                            InputStream inputStream;
                            if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                                inputStream = conn.getInputStream();
                            } else {
                                inputStream = conn.getErrorStream();
                            }

                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                            StringBuilder sb = new StringBuilder();
                            String line;

                            while ((line = bufferedReader.readLine()) != null) {
                                sb.append(line);
                            }

                            bufferedReader.close();

                            FIS.close();

                            dos.flush();
                            dos.close();
                            searchKeyword1 = sb.toString().trim();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    } else {
                        Toast.makeText(Drawing.this.getContext(), "이미지 파일이 없습니다.", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                } else {
                }

                serverURL = "http://" + MainActivity.IPaddress + "/android_db_api/whiteboardDBsave.php";
                postParameters = "userID=" + MainActivity.id + "&content_path=" + searchKeyword1 + "&content_type=" + searchKeyword2 + "&contentX=" + searchKeyword3 + "&contentY=" + searchKeyword4 + "&content_width=" + searchKeyword5 + "&content_height=" + searchKeyword6;
                try {
                    URL url = new URL(serverURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    int responseStatusCode = conn.getResponseCode();
                    Log.d(TAG, "response code All Type - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = conn.getInputStream();
                    } else {
                        inputStream = conn.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    conn.disconnect();

                    return sb.toString().trim();
                } catch (Exception e) {
                    Log.d(TAG, "InsertData: Error ", e);
                    errorString = e.toString();

                    return null;
                }
            }else if (params.length == 2) {
                String searchKeyword1 = params[0];
                String searchKeyword2 = params[1];

                serverURL = "http://" + MainActivity.IPaddress + "/android_db_api/whiteboardJSONsave.php";
                postParameters = "content_path=" + searchKeyword1;
                try {
                    URL url = new URL(serverURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    int responseStatusCode = conn.getResponseCode();
                    Log.d(TAG, "response code JSON - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = conn.getInputStream();
                    } else {
                        inputStream = conn.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    conn.disconnect();

                    searchKeyword1 = sb.toString().trim();
                } catch (Exception e) {
                    Log.d(TAG, "InsertData: Error ", e);
                    errorString = e.toString();

                    return null;
                }

                serverURL = "http://" + MainActivity.IPaddress + "/android_db_api/whiteboardDBsave.php";
                postParameters = "userID=" + MainActivity.id + "&content_path=" + searchKeyword1 + "&content_type=" + searchKeyword2 + "&contentX=" + 0 + "&contentY=" + 0 + "&content_width=" + 0 + "&content_height=" + 0 ;
                try {
                    URL url = new URL(serverURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    int responseStatusCode = conn.getResponseCode();
                    Log.d(TAG, "response code Drawing - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = conn.getInputStream();
                    } else {
                        inputStream = conn.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    conn.disconnect();

                    return sb.toString().trim();
                } catch (Exception e) {
                    Log.d(TAG, "InsertData: Error ", e);
                    errorString = e.toString();

                    return null;
                }
            } else {
                return null;
            }
        }
    }
}