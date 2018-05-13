package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.*;

/**
 * Created by com on 2018-01-21.
 */
public class TeamTaskActivity extends AppCompatActivity {

    private String TName = "LEEKIM";
    private String TSchedule="DB 연동";
    String[] spnMemberName = {"김영준", "이수빈", "이지연", "이현우"};

    ArrayList<String> pvtTask0 =new ArrayList<>();
    ArrayList<String> pvtTask1 =new ArrayList<>();
    ArrayList<String> pvtTask2 =new ArrayList<>();
    ArrayList<String> pvtTask3 =new ArrayList<>();

    TextView tvTName, tvTSchedule;
    Spinner spnMember, spnMemberAdd;
    RadioGroup rgTaskChoice;
    Button btnAddTask, btnInputTask;
    RelativeLayout rlyAllTask, rlyPrivateTask, rlyTaskAdd;
    RadioButton rbAllTask, rbPrivateTask;
    static int type = 0;

    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_task);



        tvTName = (TextView) findViewById(R.id.tvTName);
        tvTSchedule = (TextView) findViewById(R.id.tvTSchedule);
        rgTaskChoice = (RadioGroup) findViewById(R.id.rgTaskChoice);
        rbAllTask = (RadioButton) findViewById(R.id.rbAllTask);
        rbPrivateTask = (RadioButton) findViewById(R.id.rbPrivateTask);
        rlyAllTask = (RelativeLayout) findViewById(R.id.rlyALLTask);
        rlyPrivateTask = (RelativeLayout) findViewById(R.id.rlyPrivateTask);
        rlyTaskAdd = (RelativeLayout) findViewById(R.id.rlyTaskAdd);
        btnAddTask = (Button) findViewById(R.id.btnAddTask);
        btnInputTask = (Button) findViewById(R.id.btnInputTask);


        rbAllTask.setOnClickListener(radioButtonClickListener);
        rbPrivateTask.setOnClickListener(radioButtonClickListener);

        tvTName.setText(TName);
        tvTSchedule.setText(TSchedule);



         spnMember = (Spinner) findViewById(R.id.spnMember);
         spnMemberAdd = (Spinner) findViewById(R.id.spnMemberAdd);


        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.Member,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMember.setAdapter(adapter);
        spnMemberAdd.setAdapter(adapter);

        spnMember.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //각 항목 클릭시 포지션값을 토스트에 띄운다.
                Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnMemberAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //각 항목 클릭시 포지션값을 토스트에 띄운다.
                Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    RadioButton.OnClickListener radioButtonClickListener = new RadioButton.OnClickListener(){
        @Override
       public void onClick(View view) {

            switch (rgTaskChoice.getCheckedRadioButtonId()){
                case R.id.rbAllTask:
                    rlyAllTask.setVisibility(View.VISIBLE);
                    rlyPrivateTask.setVisibility(View.INVISIBLE);
                    break;
                case R.id.rbPrivateTask:
                    rlyAllTask.setVisibility(View.INVISIBLE);
                    rlyPrivateTask.setVisibility(View.VISIBLE);

                    btnAddTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            rlyPrivateTask.setVisibility(View.INVISIBLE);
                            rlyTaskAdd.setVisibility(View.VISIBLE);


                        }
                    });

                    btnInputTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            rlyTaskAdd.setVisibility(View.INVISIBLE);
                            rlyPrivateTask.setVisibility(View.VISIBLE);



                        }
                    });

                    break;
                default:
                    Toast.makeText(getApplicationContext(), "기능을 선택하시오", Toast.LENGTH_SHORT).show();
            }
        }
  };


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), TeamChoiceActivity.class);
        startActivity(intent);
    }
}
