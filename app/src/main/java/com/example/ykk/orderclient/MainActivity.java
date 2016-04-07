package com.example.ykk.orderclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    static String Server_IP = "172.20.10.2";
    static int port = 1212;

    public  ArrayList<Dish> MenuList = new ArrayList<Dish>();
    LinkedList<Dish> OrderDishList = new LinkedList<>();
    int TableNum = 0;
    boolean OrderFlag = true;

    //Menu ListView
    private ListView menuListView;
    private MyMenulstAdapter myMenulstAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        setListeners();

        SetMenu thread = new SetMenu(Server_IP, port, mHandler, MenuList);
        thread.start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    myMenulstAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initViews() {
        menuListView = (ListView) findViewById(R.id.list_view);
        myMenulstAdapter = new MyMenulstAdapter(this, MenuList);
        menuListView.setAdapter(myMenulstAdapter);
    }

    private void setListeners() {
        menuListView.setOnItemClickListener(choose_dish);
    }

    private AdapterView.OnItemClickListener choose_dish = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            final View diav = inflater.inflate(R.layout.dia_order_dish, null);

            final Dish GetDish = (Dish) myMenulstAdapter.getItem(position);
            final String dish_name = GetDish.getName();
            final int dish_price = GetDish.getPrice();
            final EditText edtNum = (EditText) diav.findViewById(R.id.edt_ordernum);
            final TextView tvOrderNum = (TextView) diav.findViewById(R.id.tv_ordernum);
            final RadioGroup rgroup = (RadioGroup) diav.findViewById(R.id.rgroup);

            rgroup.setOnCheckedChangeListener(RGlistener);

            for (int i = 0; i < OrderDishList.size(); i++) {
                if (OrderDishList.get(i).getName() == dish_name) {
                    tvOrderNum.setText(String.valueOf(OrderDishList.get(i).getAmount()));
                }
            }

            AlertDialog.Builder dishDialog = new AlertDialog.Builder(MainActivity.this);
            dishDialog.setTitle(dish_name);
            dishDialog.setView(diav);
            dishDialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean inListFlag = false;
                    try {
                        Log.d(TAG, String.valueOf(OrderFlag));
                        if (OrderFlag) {
                            for (int i = 0; i < OrderDishList.size(); i++) {//這是不是可以else
                                if (OrderDishList.get(i).getName() == dish_name) {
                                    int num = OrderDishList.get(i).getAmount();
                                    num += Integer.valueOf(edtNum.getText().toString());
                                    OrderDishList.get(i).setAmount(num);
                                    inListFlag = true;
                                }
                            }
                            if (!inListFlag) {
                                int amount = Integer.valueOf(edtNum.getText().toString());
                                Dish OD = new Dish(dish_name, dish_price, amount);
                                OrderDishList.add(OD);
                            }
                        } else {
                            for (int i = 0; i < OrderDishList.size(); i++) {
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
            dishDialog.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dishDialog.show();
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
                            SendBell thread = new SendBell(Server_IP, port, TableNum);
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
                //Intent intent = new Intent(MainActivity.this,CustomDialogActivity.class);
                //startActivity(intent);
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
        final TextView tvTableNum = (TextView) diav.findViewById(R.id.tv_tableNum);
        final EditText edtNum = (EditText) diav.findViewById(R.id.edt_tableNum);

        AlertDialog.Builder tabledialog = new AlertDialog.Builder(MainActivity.this);
        tvTableNum.setText(String.valueOf(TableNum));
        tabledialog.setView(diav);
        tabledialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    TableNum = Integer.valueOf(edtNum.getText().toString());
                    Toast.makeText(MainActivity.this, R.string.toast_table + TableNum , Toast.LENGTH_SHORT).show();
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

    private void alreadyOrder() {
        LinearLayout linearLayoutMain = new LinearLayout(this);//new a layout
        linearLayoutMain.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(this);//get current context
        listView.setFadingEdgeLength(0);

        ArrayList<String> orderList = new ArrayList<>();

        int price = 0;
        int totalPrice = 0;
        for (int i = 0; i < OrderDishList.size(); i++) {
            price = OrderDishList.get(i).getPrice() * OrderDishList.get(i).getAmount();
            Log.e(TAG, String.valueOf(OrderDishList.get(i).getPrice()) + "   " + String.valueOf(OrderDishList.get(i).getAmount()));
            orderList.add(OrderDishList.get(i).getName() + " x" + String.valueOf(OrderDishList.get(i).getAmount()) + " = $" + price);
            totalPrice += price;
        }
        orderList.add("總共： " + totalPrice + " 元");
/*
        listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, orderList);
            //price =
            orderList.add(OrderDishList.get(i).getName() + " x" + String.valueOf(OrderDishList.get(i).getAmount()) + " = $");
        }
*/
        ArrayAdapter<String> listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, orderList);
        listView.setAdapter(listAdapter);

        linearLayoutMain.addView(listView);//add listView into current context

        final AlertDialog.Builder alreadyDialog = new AlertDialog.Builder(this);
        final int sendPrice = totalPrice;
        alreadyDialog.setTitle(R.string.dialog_already_title);
        alreadyDialog.setView(linearLayoutMain);//add into dialog;
        alreadyDialog.setNegativeButton(getResources().getString(R.string.dialog_already_sent), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface alreadyDialog, int which) {
                if (TableNum == 0) {
                    Toast.makeText(MainActivity.this, R.string.toast_no_tablenum, Toast.LENGTH_SHORT).show();
                    setTableNumDialog();
                } else {
                    AlertDialog.Builder confirmDialog = new AlertDialog.Builder(MainActivity.this);
                    confirmDialog.setTitle(R.string.dialog_confirm_title);
                    confirmDialog.setMessage(R.string.dialog_confirm_msg);
                    confirmDialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SendOrderDish thread = new SendOrderDish(Server_IP, port, TableNum, OrderDishList, sendPrice);
                            thread.start();
                        }
                    });
                    confirmDialog.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    confirmDialog.show();
                }
            }
        });
        alreadyDialog.setPositiveButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface alreadyDialog, int which) {
                alreadyDialog.cancel();
            }
        });
        alreadyDialog.show();
    }

}
