package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard.Whiteboard;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import project14_1.cookandroid.com.mobilewhiteboard.R;
import project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard.MainActivity;

import static android.content.ContentValues.TAG;

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
            Start = true;

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

    public Drawing(Context context, AttributeSet attrs) {
        super(context, attrs);
        cnxt = (WhiteboardActivity) context; // EditText 동적 생성을 위한 context 선언
        test = (WhiteboardActivity) context;
        setupDrawing();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("TAG", All.toString());
        for (int i = 0; i < All.size(); i++) {
            //배열에 있는 것들을 그린다.
            ContentHistory map = All.get(i);
            if (map.getType().equals("Text")) {
                //글 그리기
                map.drawCanvas(canvas);
            }else if (map.getType().equals("Drawing")) {
                //손글씨 그리기
                if (map.getCoord().equals("null")) {
                    //경로가 비어있을 경우
                    load_paths task = new load_paths();
                    if(task.getStatus()== AsyncTask.Status.RUNNING){
                        task.cancel(true);
                    }
                    task.execute("http://" + MainActivity.IPaddress + "/android_db_api/" + map.getPath());
                    while (Paths == null) { }
                    map.setCoord(Paths);
                    i--;
                }else if (map.getCoord().startsWith("{")) {
                    //경로가 json형식으로 저장되있는 경우 내용을 배열에 넣는다.
                    showPaths(i);
                    while (Paths != null) { }
                    i--;
                }else {
                    //배열에 있는 정보로 그림 그리기
                    String[] coordA = map.getCoord().split("/"), coordB;
                    float touchX = 0, touchY = 0;
                    for (int j=0; j<coordA.length; j++) {
                        coordB = coordA[j].split(",");
                        for (int k=0; k<coordB.length; k++) {
                            if (k==0) {
                                touchX = Float.parseFloat(coordB[0]);
                            }else if (k==1) {
                                touchY = Float.parseFloat(coordB[1]);
                            }
                        }
                        if (j==0) {
                            drawPath1.moveTo(touchX, touchY);
                        }else {
                            drawPath1.lineTo(touchX, touchY);
                        }
                    }
                    drawPaint1.setColor(map.getColor());
                    drawCanvas.drawPath(drawPath1, drawPaint1);
                    drawPath1.reset();
                }
            }else if (map.getType().equals("Picture")) {
                // 사진 그리기
                if (map.checkBitmap().equals("null")) {
                    load_image task = new load_image();
                    if(task.getStatus()== AsyncTask.Status.RUNNING){
                        task.cancel(true);
                    }
                    task.execute("http://" + MainActivity.IPaddress + "/android_db_api/" + map.getPath());
                    while (DownIMG == null) { }
                    map.setBitmap(DownIMG);
                    All.add(i, map);
                    DownIMG = null;
                    i--;
                }else {
                    int nh = (int) (map.getBitmap().getHeight() * (512.0 / map.getBitmap().getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(map.getBitmap(), 512, nh, true);
                    canvas.drawBitmap(scaled, null, map.getSetXY(), null);
                    scaled.recycle();
                }
            }else {
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
        }else if (et == false && (stopX >= 0 && stopY >= 0)){
            setXY = new RectF(stopX, stopY, startX, startY);
            setXY.sort();

            bitmap = WhiteboardActivity.bitmap;
            if (bitmap != null) {
                int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                canvas.drawBitmap(scaled, null, setXY, null);
                scaled.recycle();
            }
        }else { }
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
            RenewThread rt = new RenewThread();
            rt.start();
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
                        All.add(map);
                        GetData task = new GetData();
                        if(task.getStatus()== AsyncTask.Status.RUNNING){
                            task.cancel(true);
                        }
                        task.execute(map.getText(), "Text", Float.toString(map.getX()), Float.toString(map.getY()), Float.toString(map.getWidth()), Float.toString(map.getHeight()));
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
                        All.add(map);
                        GetData task = new GetData();
                        if(task.getStatus()== AsyncTask.Status.RUNNING){
                            task.cancel(true);
                        }
                        task.execute(map.getPath(), "Drawing");
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
                        All.add(map);
                        GetData task = new GetData();
                        if(task.getStatus()== AsyncTask.Status.RUNNING){
                            task.cancel(true);
                        }
                        task.execute(WhiteboardActivity.absolutePath, "Picture", Float.toString(map.getX()) , Float.toString(map.getY()), Float.toString(map.getWidth()), Float.toString(map.getHeight()));
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
                                All.get(i).makeGapX(event.getX());
                                All.get(i).makeGapY(event.getY());

                                lce = new LongClickEvent();
                                if(lce.getStatus()== AsyncTask.Status.RUNNING){
                                    lce.cancel(true);
                                }
                                lce.execute(System.currentTimeMillis());
                                break;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (dragCount > 5 && index != -1) {
                            All.get(index).setPosition(event.getX(), event.getY());
                        }else {
                            dragCount++;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Press = false;
                        if(index == -1){

                        }else if(dragCount > 5){
                            switch (All.get(index).getType()) {
                                case "Text" :
                                    GetData task1 = new GetData();
                                    if(task1.getStatus()== AsyncTask.Status.RUNNING){
                                        task1.cancel(true);
                                    }
                                    task1.execute(All.get(index).getText(), All.get(index).getText(),"Text", Float.toString(All.get(index).getX()) , Float.toString(All.get(index).getY()), Float.toString(All.get(index).getWidth()), Float.toString(All.get(index).getHeight()));
                                    break;
                                case "Picture" :
                                    GetData task2 = new GetData();
                                    if(task2.getStatus()== AsyncTask.Status.RUNNING){
                                        task2.cancel(true);
                                    }
                                    task2.execute(All.get(index).getPath(), All.get(index).getPath(),"Picture", Float.toString(All.get(index).getX()) , Float.toString(All.get(index).getY()), Float.toString(All.get(index).getWidth()), Float.toString(All.get(index).getHeight()));
                                    break;
                            }
                        }else if (All.get(index).getType().equals("Text")) {
                            // 텍스트의 내용을 수정시킨다.
                            final WhiteboardActivity activity = (WhiteboardActivity) this.getContext();
                            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                            alert.setTitle("텍스트 수정");
                            // Create TextView
                            final EditText input = new EditText(activity);
                            WhiteboardActivity.data = All.get(index).getText();
                            input.setText(All.get(index).getText().toString());
                            alert.setView(input);
                            alert.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    All.get(index).editText(input.getText().toString());
                                    GetData task = new GetData();
                                    if(task.getStatus()== AsyncTask.Status.RUNNING){
                                        task.cancel(true);
                                    }
                                    task.execute(WhiteboardActivity.data, All.get(index).getText(),"Text", Float.toString(All.get(index).getX()) , Float.toString(All.get(index).getY()), Float.toString(All.get(index).getWidth()), Float.toString(All.get(index).getHeight()));
                                }
                            });
                            alert.show();
                        }else {

                        }
                        break;
                    }
                break;
        }
        invalidate();
        return true;
    }

    OnTouchListener mTouchListener = new OnTouchListener(){
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

                //HW 사진 경로 있는 것인지 확인
                if (!Start) {
                    for (int i = 0; i < List.size(); i++) {
                        for (int j = 0; j < All.size(); j++) {
                            if (List.get(i).getPath().equals(All.get(j).getPath())) {
                                if ((List.get(i).getX() == All.get(j).getX()) && (List.get(i).getY() == All.get(j).getY())) {
                                    if ((i != j) && (i < j)) {
                                        All.add(i, All.get(j));
                                        All.remove(j + 1);
                                    }
                                } else {
                                    if ((i != j) && (i < j)) {
                                        All.add(i, All.get(j));
                                        All.remove(j + 1);
                                    }
                                    switch (All.get(i).getType()) {
                                        case "Drawing":
                                            All.get(i).setWidth(List.get(i).getWidth());
                                            All.get(i).setHeight(List.get(i).getHeight());
                                            All.get(i).setBitmap(List.get(i).getBitmap());
                                            break;
                                        case "Picture":
                                            All.get(i).setWidth(List.get(i).getWidth());
                                            All.get(i).setHeight(List.get(i).getHeight());
                                            All.get(i).setCoord(List.get(i).getCoord());
                                            All.get(i).setColor(List.get(i).getColor());
                                            break;
                                    }
                                    All.get(i).setX(List.get(i).getX());
                                    All.get(i).setY(List.get(i).getY());
                                }
                            } else { }
                        }
                    }

                    if (All.size() == List.size()) {
                        for (int i = 0; i < List.size(); i++) {
                            if (!(All.get(i).equals(List.get(i)))) {
                                All.add(List.get(i));
                                All.remove(i);
                            }
                        }
                    }else if (All.size() > List.size()) {
                        for (int i = 0; i < All.size(); i++) {
                            if (i > List.size() - 1) {
                                All.remove(i);
                            }else if (!(All.get(i).equals(List.get(i)))) {
                                All.remove(i);
                                All.add(List.get(i));
                            }
                        }
                    }else if (List.size() > All.size()) {
                        for (int i = 0; i < List.size(); i++) {
                            if (i > All.size() - 1) {
                                All.add(List.get(i));
                            }else if (!(All.get(i).equals(List.get(i)))) {
                                All.add(List.get(i));
                                All.remove(i);
                            }
                        }
                    }else { }
                }else{
                    All = List;
                    Start = false;
                }
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

    private class LongClickEvent extends AsyncTask<Long,String,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Long... Time) {
            long now = System.currentTimeMillis();
            while (1500 >= now - Time[0]){
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
                        //배열에서 삭제
                        All.remove(index);
                        Log.d(TAG, All.toString());
                        //DB에서 삭제

                    }
                });

                alert.show();
            }
        }
    }

    private class RenewThread extends Thread {
        public void run() {
            while (true) {
                GetDataThread gdt = new GetDataThread();
                gdt.start();

                long now = System.currentTimeMillis(), past = now;
                while (10000 >= now - past){ //HW 시간초 조절 필요
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
                Toast.makeText(Drawing.this.getContext(), errorString, Toast.LENGTH_SHORT).show();
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