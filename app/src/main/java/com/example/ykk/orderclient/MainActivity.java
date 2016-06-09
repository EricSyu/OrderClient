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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    static String Server_IP = "172.20.10.2";
    static int port = 1212;

    public  ArrayList<Dish> MenuList = new ArrayList<Dish>();
    LinkedList<Dish> OrderDishList = new LinkedList<>();
    int TableNum = 0;
    boolean OrderFlag = true;

    //Menu ListView
//    private ListView menuListView;
//    private MyMenuListAdapter myMenuListAdapter;

    //Expandable
    private ExpandableListView elv ;
    private ExpandableAdapter myExpandableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        setListeners();
        testCast();

        SetMenu thread = new SetMenu(Server_IP, port, mHandler, MenuList);
        thread.start();
    }
    private void testCast() {
        Dish d1 = new Dish("滷肉飯", 30, 0);
        Dish d2 = new Dish("雞肉飯", 35, 0);
        Dish d3 = new Dish("貢丸湯", 20, 0);
        MenuList.add(d1);
        MenuList.add(d2);
        MenuList.add(d3);


    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    myExpandableAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initViews() {
        //menuListView = (ListView) findViewById(R.id.list_view);
        //myMenuListAdapter = new MyMenuListAdapter(this, MenuList);/////
        //menuListView.setAdapter(myMenuListAdapter);

        elv = (ExpandableListView) findViewById(R.id.list_view);

        /*  限制只展開一組  */
        elv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < elv.getCount(); i++) {
                    if (groupPosition != i) {
                        elv.collapseGroup(i);
                    }
                }
            }
        });
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
                Log.e("click","click");
                return false;
            }
        });

        // 準備一級清單中顯示的資料:2個一級清單,分別顯示"group1"和"group2"
        List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
        Map<String, String> group1 = new HashMap<String, String>();
        group1.put("group", "分類一");
        Map<String, String> group2 = new HashMap<String, String>();
        group2.put("group", "分類二");
        groups.add(group1);
        groups.add(group2);

        myExpandableAdapter = new ExpandableAdapter(this, MenuList, groups);
        elv.setAdapter(myExpandableAdapter);
    }

    private void setListeners() {
        elv.setOnChildClickListener(choose_dish);//.setOnItemClickListener(choose_dish);
        //menuListView.setOnItemClickListener(choose_dish);
    }

    ////////////////////////////////////////////////////////////
    /*  onItemClick
    按下餐點顯示圖片
    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
    final View diav = inflater.inflate(R.layout.dia_information, null);

    final Dish GetDish = (Dish) myMenuListAdapter.getItem(position);
    final String dish_name = GetDish.getName();
    final int dish_price = GetDish.getPrice();
    final TextView diaTitle = (TextView) diav.findViewById(R.id.information_title);
    final TextView tvPrice = (TextView) diav.findViewById(R.id.show_price);
    final ImageView picture = (ImageView) diav.findViewById(R.id.img_dish);

    diaTitle.setText(dish_name);
    tvPrice.setText(String.valueOf(dish_price));
    ***Image.setImageResource();

    final AlertDialog.Builder dishDialog = new AlertDialog.Builder(MainActivity.this);
    dishDialog.setView(diav);
    final AlertDialog dialog = dishDialog.show();
    */
    ////////////////////////////////////////////////////////////
   private ExpandableListView.OnChildClickListener choose_dish = new ExpandableListView.OnChildClickListener() {
       // private AdapterView.OnItemClickListener  choose_dish = new AdapterView.OnItemClickListener() {
       @Override
       public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
           final Dish GetDish = (Dish) myExpandableAdapter.getChild(groupPosition,childPosition);
           Toast.makeText(MainActivity.this, "點到", Toast.LENGTH_SHORT).show();
           Log.e("ccc","cc");
           //Toast.makeText(MainActivity.this, itemData.get(title.get(groupPosition)).get(childPosition)+" click", 0).show();

       // @Override
        //public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            /*final View diav = inflater.inflate(R.layout.dia_order_dish, null);

            //final Dish GetDish = (Dish) myMenuListAdapter.getItem(position);


            final String dish_name = GetDish.getName();
            final int dish_price = GetDish.getPrice();
            final TextView diaTitle = (TextView) diav.findViewById(R.id.dialog_title);
            final EditText edtNum = (EditText) diav.findViewById(R.id.edt_ordernum);
            final TextView tvOrderNum = (TextView) diav.findViewById(R.id.tv_ordernum);
            final TextView tvDishPrice = (TextView) diav.findViewById(R.id.tv_unitPrice);
            final RadioGroup rgroup = (RadioGroup) diav.findViewById(R.id.rgroup);
            final Button okBtn = (Button) diav.findViewById(R.id.button_ok);
            final Button cancelBtn = (Button) diav.findViewById(R.id.button_cancel);

            rgroup.setOnCheckedChangeListener(RGlistener);

            for (int i = 0; i < OrderDishList.size(); i++) {
                if (OrderDishList.get(i).getName() == dish_name) {
                    tvOrderNum.setText(String.valueOf(OrderDishList.get(i).getAmount()));
                }
            }

            diaTitle.setText(dish_name);
            Log.e("dishPrice", ""+dish_price);
            tvDishPrice.setText(String.valueOf(dish_price));

            final AlertDialog.Builder dishDialog = new AlertDialog.Builder(MainActivity.this);
            dishDialog.setView(diav);
            final AlertDialog dialog = dishDialog.show();
            /*okBtn.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(TAG, "in");
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
                    dialog.cancel();
                }
            });*/

            /*cancelBtn.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });*/
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
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View diav = inflater.inflate(R.layout.dia_bell, null);

                final Button okBtn = (Button) diav.findViewById(R.id.button_table_ok);
                final Button cancelBtn = (Button) diav.findViewById(R.id.button_table_cancel);

                final AlertDialog.Builder bellDilog = new AlertDialog.Builder(MainActivity.this);
                bellDilog.setView(diav);
                final AlertDialog dialog = bellDilog.show();

                okBtn.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TableNum == 0) {
                            Toast.makeText(MainActivity.this, R.string.toast_no_tablenum, Toast.LENGTH_SHORT).show();
                            setTableNumDialog();
                        } else {
                            SendBell thread = new SendBell(Server_IP, port, TableNum);
                            thread.start();
                        }
                        dialog.cancel();
                    }
                });
                cancelBtn.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
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
        final Button okBtn = (Button) diav.findViewById(R.id.button_table_ok);
        final Button cancelBtn = (Button) diav.findViewById(R.id.button_table_cancel);

        final AlertDialog.Builder tableDialog = new AlertDialog.Builder(MainActivity.this);
            tvTableNum.setText(String.valueOf(TableNum));
        tableDialog.setView(diav);
        final AlertDialog dialog = tableDialog.show();

        okBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    TableNum = Integer.valueOf(edtNum.getText().toString());
                    Toast.makeText(MainActivity.this, "您目前的桌號為: " + TableNum , Toast.LENGTH_SHORT).show();
                } catch (Exception obj) {
                    Toast.makeText(MainActivity.this, R.string.toast_no_tablenum, Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();
            }
        });
        cancelBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
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
