package project14_1.cookandroid.com.mobilewhiteboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import project14_1.cookandroid.com.mobilewhiteboard.TeamManage.ManageTeamActivity;
import project14_1.cookandroid.com.mobilewhiteboard.Whiteboard.WhiteboardActivity;

import static project14_1.cookandroid.com.mobilewhiteboard.MainActivity.IPaddress;
/**
 * Created by com on 2018-01-21.
 */
public class TeamChoiceActivity extends AppCompatActivity {

    DAO dao = new DAO();

    protected void onCreate(Bundle savedIntanteState){
        super.onCreate(savedIntanteState);
        setContentView(R.layout.activity_teamchoice);

        final RadioGroup rg_teamChoice = (RadioGroup) findViewById(R.id.rg_teamChoice);
        RadioButton rb_WB = (RadioButton) findViewById(R.id.rb_WB);
        RadioButton rb_task = (RadioButton) findViewById(R.id.rb_task);
        ImageButton ibtn_tName_1 = (ImageButton) findViewById(R.id.ibtn_tName_1);
        ImageButton ibtn_tName_2 = (ImageButton) findViewById(R.id.ibtn_tName_2);
        ImageButton ibtn_t_manage = (ImageButton) findViewById(R.id.ibtn_t_manage);

        String serverURL = "http://" + IPaddress + "/android_db_api/get_team_list.php";
        String postParameters = "id="+MainActivity.id;
        dao.DAO(serverURL,postParameters);

        Toast.makeText(this, "result is "+dao.myJSON, Toast.LENGTH_SHORT).show();
        ibtn_tName_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (rg_teamChoice.getCheckedRadioButtonId()){
                    case R.id.rb_WB:
                        Intent intent = new Intent(getApplicationContext(),WhiteboardActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.rb_task:
                        Intent intent1= new Intent(getApplicationContext(),TeamTaskActivity.class);
                        startActivity(intent1);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "기능을 선택하시오", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ibtn_t_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), ManageTeamActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainViewActivity.class);
        startActivity(intent);
    }
}
