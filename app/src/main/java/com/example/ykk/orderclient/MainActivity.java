package com.example.ykk.orderclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    static int port = 6000;
    static String Server_IP = "192.168.1.106";

    String[] categories = {"漢堡類", "蛋餅類"};
    static String[] hamburger = {"火腿蛋堡 $30", "培根蛋堡 $30"};
    String[] omelet = {"原味蛋餅 $30"};

    ArrayList<Dish> MenuList = new ArrayList<>();
    LinkedList<Dish> OrderDishList = new LinkedList<>();
    int tablenum = 0;

    private ExpandableListView elv;
    private ExpandableAdapter viewAdapter;

    //already order
    private ArrayAdapter<String> listAdapter;

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


        setMenu();
        initViews();
        setListeners();
    }

    private void setMenu(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(Server_IP, port);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    int menu_num = 0;

                    bw.write(1);
                    bw.flush();

                    menu_num = br.read();
                    for(int i = 0; i < menu_num; i++){
                        String dish_name = br.readLine();
                        int dish_price = br.read();
                        Dish dish = new Dish(dish_name);
                        dish.setPrice(dish_price);
                        MenuList.add(dish);
                    }
                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
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
            child.add(child1Data);//child2Data??????????????????????????????????
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

            final String dish_name = ((Map<String, String>) viewAdapter.getChild(groupPosition, childPosition)).get("child");
            final EditText edtNum = (EditText) diav.findViewById(R.id.edt_ordernum);
            final TextView tvNum = (TextView) diav.findViewById(R.id.tv_ordernum);

            for (int i = 0; i < OrderDishList.size(); i++) {
                if (OrderDishList.get(i).getName() == dish_name) {
                    tvNum.setText(String.valueOf(OrderDishList.get(i).getNum()));
                }
            }

            AlertDialog.Builder dishdialog = new AlertDialog.Builder(MainActivity.this);
            dishdialog.setTitle(dish_name);
            dishdialog.setView(diav);
            dishdialog.setNegativeButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean inListfalg = false;
                    try {
                        for (int i = 0; i < OrderDishList.size(); i++) {//這是不是可以else

                            if (OrderDishList.get(i).getName() == dish_name) {
                                int num = OrderDishList.get(i).getNum();
                                num += Integer.valueOf(edtNum.getText().toString());
                                OrderDishList.get(i).setNum(num);
                                inListfalg = true;
                            }
                        }
                        if (!inListfalg) {
                            Dish OD = new Dish(dish_name);
                            OD.setNum(Integer.valueOf(edtNum.getText().toString()));
                            OrderDishList.add(OD);
                        }
                        for (int i = 0; i < OrderDishList.size(); i++) {
                            Log.i(TAG, "Dish in the List : " + OrderDishList.get(i).getName() + " " +
                                    String.valueOf(OrderDishList.get(i).getNum()) + "\n");
                        }
                    } catch (Exception obj) {
                        Toast.makeText(MainActivity.this, R.string.toast_ordererror, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dishdialog.setPositiveButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

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
            case R.id.action_settable:
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View diav = inflater.inflate(R.layout.dia_tablenum, null);
                final TextView tvNum = (TextView) diav.findViewById(R.id.tv_tablenum);
                final EditText edtNum = (EditText) diav.findViewById(R.id.edt_tablenum);

                AlertDialog.Builder tabledialog = new AlertDialog.Builder(MainActivity.this);
                tvNum.setText(String.valueOf(tablenum));
                tabledialog.setView(diav);
                tabledialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            tablenum = Integer.valueOf(edtNum.getText().toString());
                            Toast.makeText(MainActivity.this, "目前桌號為: " + String.valueOf(tablenum), Toast.LENGTH_SHORT).show();
                        } catch (Exception obj) {
                            Toast.makeText(MainActivity.this, R.string.toast_tableerror, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                tabledialog.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                tabledialog.show();
                break;
            case R.id.action_bell:
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket(Server_IP, port);
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                            bw.write(3);
                            bw.flush();
                            bw.write(tablenum);
                            bw.flush();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                break;

            case R.id.action_order:
                if (OrderDishList.size() == 0) {
                    Toast.makeText(MainActivity.this, R.string.toast_no_order, Toast.LENGTH_SHORT).show();
                } else {
                    alreadyOrder();
                }
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


    private void alreadyOrder() {
        LinearLayout linearLayoutMain = new LinearLayout(this);//new a layout
        linearLayoutMain.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(this);//get current context
        listView.setFadingEdgeLength(0);

        ArrayList<String> orderList = new ArrayList<>();

        for (int i = 0; i < OrderDishList.size(); i++) {
            //price =
            orderList.add(OrderDishList.get(i).getName() + " x" + String.valueOf(OrderDishList.get(i).getNum()) + " = $");
        }


        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, orderList);
        listView.setAdapter(listAdapter);

        linearLayoutMain.addView(listView);//add listView into current context

        final AlertDialog alreadyDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_already_title).setView(linearLayoutMain)//add into dialog
                .setNegativeButton(getResources().getString(R.string.dialog_already_sent), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface alreadyDialog, int which) {
                        // ++++ 送出
                    }
                })
                .setPositiveButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface alreadyDialog, int which) {
                        alreadyDialog.cancel();
                    }
                }).create();

        alreadyDialog.show();
    }
}

