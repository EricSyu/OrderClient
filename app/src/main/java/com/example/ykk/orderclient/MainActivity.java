package com.example.ykk.orderclient;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String[] categories = {"漢堡類", "蛋餅類"};
    String[] hamburger = {"火腿蛋堡 $30", "培根蛋堡 $30"};
    String[] omelet = {"原味蛋餅 $30"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initViews();
        setListensers();
    }

    private void initViews() {

        List<Map<String, String>> groups = new ArrayList<>();
        List<List<Map<String, String>>> childs = new ArrayList<>();

        for(int i = 0; i < categories.length; i++){
            Map<String, String> group = new HashMap<>();
            group.put("group", categories[i]);
            groups.add(group);
        }
//        //準備一級清單中顯示的資料:2個一級清單,分別顯示"group1"和"group2"
//        Map<String, String> group1 = new HashMap<>();
//        group1.put("group", "漢堡類");
//        Map<String, String> group2 = new HashMap<>();
//        group2.put("group", "蛋餅類");
//        groups.add(group1);
//        groups.add(group2);

        List<Map<String, String>> child1 = new ArrayList<>();
        for(int i = 0; i < hamburger.length; i++){
            Map<String, String> child1Data = new HashMap<>();
            child1Data.put("child", hamburger[i]);
            child1.add(child1Data);
        }
//        //準備第一個一級清單中的二級清單資料:兩個二級清單,分別顯示"childData1"和"childData2"
//        List<Map<String, String>> child1 = new ArrayList<>();
//        Map<String, String> child1Data1 = new HashMap<>();
//        child1Data1.put("child", "火腿蛋堡 $30");
//        Map<String, String> child1Data2 = new HashMap<>();
//        child1Data2.put("child", "培根蛋堡 $30");
//        child1.add(child1Data1);
//        child1.add(child1Data2);

        List<Map<String, String>> child2 = new ArrayList<>();
        for(int i = 0; i < omelet.length; i++){
            Map<String, String> child1Data = new HashMap<>();
            child1Data.put("child", omelet[i]);
            child2.add(child1Data);
        }

//        //準備第二個一級清單中的二級清單資料:一個二級清單,顯示"child2Data1"
//        List<Map<String, String>> child2 = new ArrayList<>();
//        Map<String, String> child2Data1 = new HashMap<>();
//        child2Data1.put("child", "原味蛋餅 $15");
//        child2.add(child2Data1);

        //用一個list物件保存所有的二級清單資料
        childs.add(child1);
        childs.add(child2);

        ExpandableListView elv = (ExpandableListView) findViewById(R.id.mExpandableListView);
        ExpandableAdapter viewAdapter = new ExpandableAdapter(this, groups, childs);
        elv.setAdapter(viewAdapter);
    }

    private void setListensers() {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_bell:
                break;

            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_camera:
                break;
            case R.id.nav_gallery:
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
