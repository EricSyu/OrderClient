package com.example.ykk.orderclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    String[] categories = {"漢堡類", "蛋餅類"};
    static String[] hamburger = {"", "培根蛋堡 $30"};
    String[] omelet = {"原味蛋餅 $30"};

    private ExpandableListView elv;
    private ExpandableAdapter viewAdapter;

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

        ClientHandler SocketConnect = new ClientHandler();
        SocketConnect.start();
        try {
            SocketConnect.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        initViews();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews() {

        List<Map<String, String>> groups = new ArrayList<>();
        List<List<Map<String, String>>> childs = new ArrayList<>();

        for (int i = 0; i < categories.length; i++) {
            Map<String, String> group = new HashMap<>();
            group.put("group", categories[i]);
            groups.add(group);
        }

        List<Map<String, String>> child = new ArrayList<>();
        for (int i = 0; i < hamburger.length; i++) {
            Map<String, String> child1Data = new HashMap<>();
            child1Data.put("child", hamburger[i]);
            child.add(child1Data);
        }
        childs.add(child);

        child = new ArrayList<>();
        for (int i = 0; i < omelet.length; i++) {
            Map<String, String> child1Data = new HashMap<>();
            child1Data.put("child", omelet[i]);
            child.add(child1Data);
        }
        childs.add(child);

        elv = (ExpandableListView) findViewById(R.id.mExpandableListView);
        viewAdapter = new ExpandableAdapter(this, groups, childs);
        elv.setAdapter(viewAdapter);
    }

    private void setListeners() {
        elv.setOnChildClickListener(choose_dish);
    }

    private ExpandableListView.OnChildClickListener choose_dish = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            final View diav = inflater.inflate(R.layout.dia_dish, null);

            AlertDialog.Builder dishdialog = new AlertDialog.Builder(MainActivity.this);
            dishdialog.setTitle(((Map<String, String>) viewAdapter.getChild(groupPosition, childPosition)).get("child"));
            dishdialog.setView(diav);
            dishdialog.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "xxx", Toast.LENGTH_SHORT).show();
                }
            });
            dishdialog.show();

            return false;
        }
    };

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
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Socket s = new Socket("192.168.1.106", 6000);
                            DataOutputStream dos = null;
                            dos = new DataOutputStream(ClientHandler.Client_socket.getOutputStream());
                            dos.writeUTF("服務鈴");
                            dos.flush();
                            dos.close();
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                Toast.makeText(this, "服務鈴", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_settings:
                initViews();
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
