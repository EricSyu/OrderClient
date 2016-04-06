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
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    String[] categories = {"漢堡類", "蛋餅類"};
    static String[] hamburger = {"火腿蛋堡 $30", "培根蛋堡 $30"};
    String[] omelet = {"原味蛋餅 $30"};
    //int[] hamPrice = {30, 30};
    //int[] omePrice = {30};
    LinkedList<OrderDish> OrderDishList = new LinkedList<>();

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

//        ClientHandler SocketConnect = new ClientHandler();
//        SocketConnect.start();
//        try {
//            SocketConnect.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

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
            final EditText edtNum = (EditText)diav.findViewById(R.id.edt_ordernum);

            AlertDialog.Builder dishdialog = new AlertDialog.Builder(MainActivity.this);
            dishdialog.setTitle(dish_name);
            dishdialog.setView(diav);
            dishdialog.setNegativeButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean inListfalg = false;
                    try{
                        for (int i = 0; i < OrderDishList.size(); i++) {//這是不是可以else
                            if (OrderDishList.get(i).getName() == dish_name) {
                                int num = OrderDishList.get(i).getNum();
                                num += Integer.valueOf(edtNum.getText().toString());
                                OrderDishList.get(i).setNum(num);
                                inListfalg = true;
                            }
                        }
                        if (!inListfalg) {
                            OrderDish OD = new OrderDish(dish_name, Integer.valueOf(edtNum.getText().toString()));
                            OrderDishList.add(OD);
                        }
                        for (int i = 0; i < OrderDishList.size(); i++) {
                            Log.i(TAG, "Dish in the List : " + OrderDishList.get(i).getName() + " " +
                                    String.valueOf(OrderDishList.get(i).getNum()) + "\n");
                        }
                    } catch (Exception obj){
                        Toast.makeText(MainActivity.this, R.string.toast_no_count, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dishdialog.setPositiveButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener(){
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
            case R.id.action_bell:
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Socket s = new Socket("192.168.1.106", 6000);
                            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
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
//                if (!ClientHandler.Client_socket.isConnected()) {
//                    ClientHandler SocketConnect = new ClientHandler();
//                    SocketConnect.start();
//                }
//                if (ClientHandler.Client_socket.isConnected()) {// 確認socket在連通狀態下
//                    try {
//                        DataOutputStream dos = new DataOutputStream(ClientHandler.Client_socket.getOutputStream());
//                        dos.writeUTF("服務鈴");
//                        dos.flush();
//                        dos.close();
//                        ClientHandler.Client_socket.close();
//
//                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ClientHandler.Client_socket.getOutputStream()));// 寫入的Buffer
//                        bw.write("服務鈴1");
//                        bw.flush();
//
//                        Toast.makeText(this, "服務鈴", Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
                break;

            case R.id.action_order:
                if(OrderDishList.size() == 0){
                    Toast.makeText(MainActivity.this, R.string.toast_no_order , Toast.LENGTH_SHORT).show();
                }else {
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


    private void alreadyOrder(){
        LinearLayout linearLayoutMain = new LinearLayout(this);//new a layout
        linearLayoutMain.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(this);//get current context
        listView.setFadingEdgeLength(0);

        ArrayList<String> orderList = new ArrayList<>();

        for (int i = 0; i < OrderDishList.size(); i++) {
            //price =
            orderList.add(OrderDishList.get(i).getName() + " x" + String.valueOf(OrderDishList.get(i).getNum()) + " = $" );
        }


        listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,orderList);
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
                .setPositiveButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface alreadyDialog, int which) {
                        alreadyDialog.cancel();
                    }
                }).create();

        alreadyDialog.show();
    }
}

