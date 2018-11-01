package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by com on 2018-05-02.
 */

public class TaskDrawing extends View{

    TeamTaskActivity cnxt;

    public TaskDrawing(Context context, AttributeSet attrs) {
        super(context, attrs);
        cnxt = (TeamTaskActivity) context; // EditText 동적 생성을 위한 context 선언
        //setupDrawing();
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //포스트잇 그리기
       /* if(addPostit == true){
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

        }*/
    }
}