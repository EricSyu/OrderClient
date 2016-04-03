package com.example.ykk.orderclient;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ExpandableListView listView = (ExpandableListView)findViewById(R.id.mExpandableListView);

        //準備一級清單中顯示的資料:2個一級清單,分別顯示"group1"和"group2"
        List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
        Map<String, String> group1 = new HashMap<String, String>();
        group1.put("group", "漢堡類");
        Map<String, String> group2 = new HashMap<String, String>();
        group2.put("group", "蛋餅類");
        groups.add(group1);
        groups.add(group2);


        //準備第一個一級清單中的二級清單資料:兩個二級清單,分別顯示"childData1"和"childData2"
        List<Map<String, String>> child1 = new ArrayList<Map<String, String>>();
        Map<String, String> child1Data1 = new HashMap<String, String>();
        child1Data1.put("child", "火腿蛋堡 $30");
        Map<String, String> child1Data2 = new HashMap<String, String>();
        child1Data2.put("child", "培根蛋堡 $30");
        child1.add(child1Data1);
        child1.add(child1Data2);


        //準備第二個一級清單中的二級清單資料:一個二級清單,顯示"child2Data1"
        List<Map<String, String>> child2 = new ArrayList<Map<String, String>>();
        Map<String, String> child2Data1 = new HashMap<String, String>();
        child2Data1.put("child", "原味蛋餅 $15");
        child2.add(child2Data1);
        //用一個list物件保存所有的二級清單資料
        List<List<Map<String, String>>> childs = new ArrayList<List<Map<String, String>>>();
        childs.add(child1);
        childs.add(child2);

        ExpandableAdapter viewAdapter = new ExpandableAdapter(this, groups, childs);
        listView.setAdapter(viewAdapter);


        initViews();
        setListensers();
    }

    private void initViews(){


    }

    private void setListensers(){

    }

    //自訂的ExpandListAdapter
    class ExpandableAdapter extends BaseExpandableListAdapter
    {
        private Context context;
        List<Map<String, String>> groups;
        List<List<Map<String, String>>> childs;

        /*
        * 構造函數:
        * 參數1:context物件
        * 參數2:一級清單資料來源
        * 參數3:二級清單資料來源
        */
        public ExpandableAdapter(Context context, List<Map<String, String>> groups, List<List<Map<String, String>>> childs)
        {
            this.groups = groups;
            this.childs = childs;
            this.context = context;
        }

        public Object getChild(int groupPosition, int childPosition)
        {
            return childs.get(groupPosition).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }

        //獲取二級清單的View物件
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                                 ViewGroup parent)
        {
            @SuppressWarnings("unchecked")
            String text = ((Map<String, String>) getChild(groupPosition, childPosition)).get("child");
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //獲取二級清單對應的佈局檔, 並將其各元素設置相應的屬性
            LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.child, null);
            TextView tv = (TextView) linearLayout.findViewById(R.id.child_tv);
            tv.setText(text);
            //ImageView imageView = (ImageView)linearLayout.findViewById(R.id.child_iv);
            //imageView.setImageResource(R.drawable.icon);

            return linearLayout;
        }

        public int getChildrenCount(int groupPosition)
        {
            return childs.get(groupPosition).size();
        }

        public Object getGroup(int groupPosition)
        {
            return groups.get(groupPosition);
        }

        public int getGroupCount()
        {
            return groups.size();
        }

        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }

        //獲取一級清單View物件
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
        {
            String text = groups.get(groupPosition).get("group");
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //獲取一級清單佈局檔,設置相應元素屬性
            LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.group, null);
            TextView textView = (TextView)linearLayout.findViewById(R.id.group_tv);
            textView.setText(text);

            return linearLayout;
        }

        public boolean hasStableIds()
        {
            return false;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return false;
        }

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
        switch (item.getItemId()){
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
        switch (item.getItemId()){
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
