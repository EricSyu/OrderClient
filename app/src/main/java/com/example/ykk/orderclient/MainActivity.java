package com.example.ykk.orderclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity{
        //implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    static int port = 6000;
    static String Server_IP = "192.168.1.106";

    ArrayList<Dish> MenuList = new ArrayList<>();
    LinkedList<Dish> OrderDishList = new LinkedList<>();
    int tableNum = 0;

    //Menu ListView
    private ListView menuListView;
    private String[] list = {"火腿蛋堡 $30","培根蛋堡 $30","原味蛋餅 $30" };
    private ArrayAdapter<String> menuListAdapter;

    //already order
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        menuListView = (ListView)findViewById(R.id.list_view);
        menuListAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        menuListView.setAdapter(menuListAdapter);
    }

    private void setListeners() {
        menuListView.setOnItemClickListener(choose_dish);
    }
    private AdapterView.OnItemClickListener choose_dish = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            final View diav = inflater.inflate(R.layout.dia_dish, null);

            final String dish_name = menuListAdapter.getItem(position);
            final EditText edtNum = (EditText) diav.findViewById(R.id.edt_ordernum);
            final TextView tvNum = (TextView) diav.findViewById(R.id.tv_ordernum);

            for (int i = 0; i < OrderDishList.size(); i++) {
                if (OrderDishList.get(i).getName() == dish_name) {
                    tvNum.setText(String.valueOf(OrderDishList.get(i).getNum()));
                }
            }

            AlertDialog.Builder dishDialog = new AlertDialog.Builder(MainActivity.this);
            dishDialog.setTitle(dish_name);
            dishDialog.setView(diav);
            dishDialog.setNegativeButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean inListFlag = false;
                    try {
                        for (int i = 0; i < OrderDishList.size(); i++) {//這是不是可以else

                            if (OrderDishList.get(i).getName() == dish_name) {
                                int num = OrderDishList.get(i).getNum();
                                num += Integer.valueOf(edtNum.getText().toString());
                                OrderDishList.get(i).setNum(num);
                                inListFlag = true;
                            }
                        }
                        if (!inListFlag) {
                            Dish OD = new Dish(dish_name);
                            OD.setNum(Integer.valueOf(edtNum.getText().toString()));
                            OrderDishList.add(OD);
                        }
                        for (int i = 0; i < OrderDishList.size(); i++) {
                            Log.i(TAG, "Dish in the List : " + OrderDishList.get(i).getName() + " " +
                                    String.valueOf(OrderDishList.get(i).getNum()) + "\n");
                        }
                    } catch (Exception obj) {
                        Toast.makeText(MainActivity.this, R.string.toast_orderError, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dishDialog.setPositiveButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dishDialog.show();
        }
    };


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
                final TextView tvNum = (TextView) diav.findViewById(R.id.tv_tableNum);
                final EditText edtNum = (EditText) diav.findViewById(R.id.edt_tableNum);

                AlertDialog.Builder tableDialog = new AlertDialog.Builder(MainActivity.this);
                tvNum.setText(String.valueOf(tableNum));
                tableDialog.setView(diav);
                tableDialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            tableNum = Integer.valueOf(edtNum.getText().toString());
                            Toast.makeText(MainActivity.this, R.string.table_num + String.valueOf(tableNum), Toast.LENGTH_SHORT).show();
                        } catch (Exception obj) {
                            Toast.makeText(MainActivity.this, R.string.toast_tableError, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                tableDialog.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                tableDialog.show();
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
                            bw.write(tableNum);
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

