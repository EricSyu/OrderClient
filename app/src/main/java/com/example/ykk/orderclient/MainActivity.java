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
import android.widget.RadioGroup;
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
    int TableNum = 0;
    boolean OrderFlag = true;

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

        setMenu();
        initViews();
        setListeners();
    }

    private void setMenu() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(Server_IP, port);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    int menu_num = 0;

                    bw.write(1);
                    bw.flush();

                    menu_num = br.read();
                    for (int i = 0; i < menu_num; i++) {
                        String dish_name = br.readLine();
                        int dish_price = br.read();
                        Dish dish = new Dish(dish_name, dish_price, 0);
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

            final EditText edtNum = (EditText) diav.findViewById(R.id.edt_ordernum);
            final TextView tvPrice = (TextView) diav.findViewById(R.id.tv_price);
            final TextView tvNum = (TextView) diav.findViewById(R.id.tv_ordernum);
            final RadioGroup rgroup = (RadioGroup) diav.findViewById(R.id.rgroup);

            final String dish_name = ((Map<String, String>) viewAdapter.getChild(groupPosition, childPosition)).get("child");

            rgroup.setOnCheckedChangeListener(RGlistener);

            for (int i = 0; i < MenuList.size(); i++) {
                if (MenuList.get(i).getName() == dish_name) {
                    tvPrice.setText(String.valueOf(MenuList.get(i).getPrice()));
                }
            }

            for (int i = 0; i < OrderDishList.size(); i++) {
                if (OrderDishList.get(i).getName() == dish_name) {
                    tvNum.setText(String.valueOf(OrderDishList.get(i).getAmount()));
                }
            }

            AlertDialog.Builder dishdialog = new AlertDialog.Builder(MainActivity.this);
            dishdialog.setTitle(dish_name);
            dishdialog.setView(diav);
            dishdialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean inListfalg = false;
                    try {
                        Log.d(TAG, String.valueOf(OrderFlag));
                        if (OrderFlag) {
                            for (int i = 0; i < OrderDishList.size(); i++) {//這是不是可以else
                                if (OrderDishList.get(i).getName() == dish_name) {
                                    int num = OrderDishList.get(i).getAmount();
                                    num += Integer.valueOf(edtNum.getText().toString());
                                    OrderDishList.get(i).setAmount(num);
                                    inListfalg = true;
                                }
                            }
                            if (!inListfalg) {
                                int price = 0;
                                int amount = Integer.valueOf(edtNum.getText().toString());
                                for (int i = 0; i < MenuList.size(); i++) {
                                    if (MenuList.get(i).getName() == dish_name) {
                                        price = MenuList.get(i).getPrice();
                                    }
                                }
                                Dish OD = new Dish(dish_name, price, amount);
                                OrderDishList.add(OD);
                            }
                        } else {
                            for (int i = 0; i < OrderDishList.size(); i++) {//這是不是可以else
                                if (OrderDishList.get(i).getName() == dish_name) {
                                    int num = OrderDishList.get(i).getAmount();
                                    num -= Integer.valueOf(edtNum.getText().toString());
                                    if (num < 0) {
                                        num = 0;
                                    }
                                    if (num != 0) {
                                        OrderDishList.get(i).setAmount(num);
                                    } else {
                                        OrderDishList.remove(OrderDishList.get(i));
                                    }
                                }
                            }
                            OrderFlag = true;
                        }
                        for (int i = 0; i < OrderDishList.size(); i++) {
                            Log.d(TAG, "Dish in the List : " + OrderDishList.get(i).getName() + " " +
                                    String.valueOf(OrderDishList.get(i).getAmount()) + "\n");
                        }
                    } catch (Exception obj) {
                        Toast.makeText(MainActivity.this, R.string.toast_no_amount, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dishdialog.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dishdialog.show();

            return false;
        }
    };

    public RadioGroup.OnCheckedChangeListener RGlistener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_Order:
                    OrderFlag = true;
                    Log.d(TAG, String.valueOf(OrderFlag));
                    break;
                case R.id.rb_CancelOrder:
                    OrderFlag = false;
                    Log.d(TAG, String.valueOf(OrderFlag));
                    break;
            }
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
                setTableNumDialog();
                break;

            case R.id.action_bell:
                AlertDialog.Builder belldilog = new AlertDialog.Builder(MainActivity.this);
                belldilog.setTitle(R.string.dialog_bell_title);
                belldilog.setMessage(R.string.dialog_bell_msg);
                belldilog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TableNum == 0) {
                            Toast.makeText(MainActivity.this, R.string.toast_no_tablenum, Toast.LENGTH_SHORT).show();
                            setTableNumDialog();
                        } else {
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Socket socket = new Socket(Server_IP, port);
                                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                                        bw.write(3);
                                        bw.flush();
                                        bw.write(TableNum);
                                        bw.flush();
                                        socket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            thread.start();
                        }
                    }
                });
                belldilog.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                belldilog.show();
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

    public void setTableNumDialog() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View diav = inflater.inflate(R.layout.dia_tablenum, null);
        final TextView tvNum = (TextView) diav.findViewById(R.id.tv_tablenum);
        final EditText edtNum = (EditText) diav.findViewById(R.id.edt_tablenum);

        AlertDialog.Builder tabledialog = new AlertDialog.Builder(MainActivity.this);
        tvNum.setText(String.valueOf(TableNum));
        tabledialog.setView(diav);
        tabledialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    TableNum = Integer.valueOf(edtNum.getText().toString());
                    Toast.makeText(MainActivity.this, "目前桌號為: " + String.valueOf(TableNum), Toast.LENGTH_SHORT).show();
                } catch (Exception obj) {
                    Toast.makeText(MainActivity.this, R.string.toast_no_tablenum, Toast.LENGTH_SHORT).show();
                }
            }
        });
        tabledialog.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        tabledialog.show();
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
            orderList.add(OrderDishList.get(i).getName() + " x" + String.valueOf(OrderDishList.get(i).getAmount()) + " = $");
        }

        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, orderList);
        listView.setAdapter(listAdapter);

        linearLayoutMain.addView(listView);//add listView into current context

        final AlertDialog alreadyDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_already_title).setView(linearLayoutMain)//add into dialog
                .setNegativeButton(getResources().getString(R.string.dialog_already_sent), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface alreadyDialog, int which) {
                        //送出菜單
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Socket socket = new Socket(Server_IP, port);
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                                    bw.write(2);
                                    bw.flush();
                                    bw.write(TableNum);
                                    bw.flush();
                                    bw.write(OrderDishList.size());
                                    bw.flush();
                                    for (int i = 0; i < OrderDishList.size(); i++) {
                                        bw.write(OrderDishList.get(i).getName());
                                        bw.flush();
                                        bw.write(OrderDishList.get(i).getAmount());
                                        bw.flush();
                                    }
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    }
                })
                .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface alreadyDialog, int which) {
                        alreadyDialog.cancel();
                    }
                }).create();
        alreadyDialog.show();
    }
}