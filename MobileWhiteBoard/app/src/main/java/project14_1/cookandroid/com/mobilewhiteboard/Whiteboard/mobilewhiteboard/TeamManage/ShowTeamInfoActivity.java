package project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard.TeamManage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import project14_1.cookandroid.com.mobilewhiteboard.R;
import project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.mobilewhiteboard.MainActivity;

import static project14_1.cookandroid.com.mobilewhiteboard.MainActivity.IPaddress;
import static project14_1.cookandroid.com.mobilewhiteboard.TeamManage.ManageTeamActivity.teamtype;

public class ShowTeamInfoActivity extends AppCompatActivity{

    TextView tvTeamName, tvTeamMaster, tvTeamMember;
    EditText edtTeamObject, edtTeamSummary;
    Button btnLeaveTeam, btInviteTeam, btnChangeTeam, btnDeleteTeam;
    Toolbar toolbarResult;
    String TeamMaster, changeObject, changeSummary, teamID, postURL, postParams;
    boolean ChangeButton=false;
    TeamDAO postDB = new TeamDAO();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_teamsearch);

        Intent intent = getIntent();


        toolbarResult =(Toolbar) findViewById(R.id.toolbarResult);

        tvTeamName = (TextView) findViewById(R.id.tvTeamName);
        tvTeamMember = (TextView) findViewById(R.id.tvTeamMember);
        edtTeamObject = (EditText) findViewById(R.id.edtTeamObject);
        edtTeamSummary = (EditText) findViewById(R.id.edtTeamSummary);
        tvTeamMaster = (TextView) findViewById(R.id.tvTeamMaster);

        btnLeaveTeam = (Button) findViewById(R.id.btnLeaveTeam);
        btInviteTeam = (Button) findViewById(R.id.btInviteTeam);
        btnChangeTeam = (Button) findViewById(R.id.btnChangeTeam);
        btnDeleteTeam = (Button) findViewById(R.id.btnDeleteTeam);

        edtTeamObject.setFocusableInTouchMode(false);
        edtTeamSummary.setFocusableInTouchMode(false);
        //editText.setFocusableInTouchMode(false);
        //
        setSupportActionBar(toolbarResult);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        actionBar.setHomeAsUpIndicator(R.drawable.back2); //뒤로가기 버튼을 본인이 만든 아이콘으로 하기 위해 필요

        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_toolbar, null);

        actionBar.setCustomView(actionbar);

        TeamMaster = intent.getStringExtra("masterID");

        Toast.makeText(getApplicationContext(),"masterID is "+TeamMaster+"and login id is "+ MainActivity.id,Toast.LENGTH_LONG).show();
        if(TeamMaster.toString().equals(MainActivity.id)){
            btInviteTeam.setEnabled(true);
            btnChangeTeam.setEnabled(true);
            btnDeleteTeam.setEnabled(true);
        }

        if(teamtype == 1) {
            tvTeamName.setText(intent.getStringExtra("teamName"));
            edtTeamObject.setText(intent.getStringExtra("object"));
            edtTeamSummary.setText(intent.getStringExtra("summary"));
            tvTeamMaster.setText(intent.getStringExtra("masterID"));
            teamID = intent.getStringExtra("teamID");

        }

        btnChangeTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ChangeButton == false){

                    ChangeButton = true;
                    // 정보 수정 상태
                    edtTeamObject.setFocusableInTouchMode(true);
                    edtTeamSummary.setFocusableInTouchMode(true);

                    btnChangeTeam.setText("수정 완료?");
                    //colorChangeButton
                    btnChangeTeam.setBackgroundResource(R.color.colorChangeButton);


                }else{
                    ChangeButton = false;
                    // 정보 수정 완료 상태
                    edtTeamObject.setFocusableInTouchMode(false);
                    edtTeamSummary.setFocusableInTouchMode(false); //editText에 커서가 있으면 false가 안먹힘

                    changeObject = edtTeamObject.getText().toString();
                    changeSummary=edtTeamSummary.getText().toString();
                    postURL = "http://" + IPaddress + "/android_db_api/team_updateInfo.php";
                    postParams = "teamID="+teamID+"&object="+changeObject+"&summary="+changeSummary;
                    postDB.TeamDAO(postURL,postParams);

                    btnChangeTeam.setBackgroundResource(R.color.colorOriginButton);
                    btnChangeTeam.setText("팀 정보 수정");

                    Toast.makeText(getApplicationContext(),"팀 정보가 수정되었습니다.",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnDeleteTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다이얼로그 바디
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(ShowTeamInfoActivity.this);
                // 다이얼로그 메세지
                alertdialog.setMessage("팀 삭제는 되돌릴 수 없으며, 관련 데이터는 모두 삭제됩니다.");

                // 확인버튼
                alertdialog.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // Toast.makeText(ShowTeamInfoActivity.this, "'확인'버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show();
                        postURL = "http://" + IPaddress + "/android_db_api/team_delete.php";
                        postParams = "teamID="+teamID;
                        postDB.TeamDAO(postURL,postParams);

                        onBackPressed();
                    }
                });

                // 취소버튼
                alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // Toast.makeText(ShowTeamInfoActivity.this, "'취소'버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                // 메인 다이얼로그 생성
                AlertDialog alert = alertdialog.create();
                // 아이콘 설정
                alert.setTitle("팀을 삭제하시겠습니까? ");
                // 다이얼로그 보기
                alert.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), ManageTeamActivity.class);
        startActivity(intent);
    }
}
