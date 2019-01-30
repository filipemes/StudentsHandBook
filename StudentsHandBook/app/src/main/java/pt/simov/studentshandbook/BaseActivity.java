package pt.simov.studentshandbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity  extends AppCompatActivity {


    private int indexActivity=-1;

    /**
     * This method allows to init
     */
    public void initNaviationView(int index) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        if (bottomNavigationView != null) {
            //First thing to do is select first menu option
            if(index>=0) {
                menu.getItem(index).setChecked(true);
                indexActivity = index;
            }else{
                menu.setGroupCheckable(0, false, true);
            }
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            if(indexActivity!=item.getItemId())
                            selectActivity(item);
                            return false;
                        }
                    });
        }
    }

    /**
     * This method allows to select a new activity by id of MenuItem
     *
     * @param item
     */
    public void selectActivity(MenuItem item) {
        Intent intent=null;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent=new Intent(this,MainMenuActivity.class);
                startActivity(intent);
                break;
            case R.id.action_schedule:
                intent=new Intent(this,ScheduleActivity.class);
                startActivity(intent);
                break;
            case R.id.action_account:
                intent=new Intent(this,AccountActivity.class);
                startActivity(intent);
                break;
        }
    }
}
