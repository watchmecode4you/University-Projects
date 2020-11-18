package com.example.owner.googlemapsgoogleplaces.DemoMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.owner.googlemapsgoogleplaces.DemoMap.DatabaseManipulation.SQLHelper;
import com.example.owner.googlemapsgoogleplaces.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class new_delivery extends AppCompatActivity {

    ArrayList<String> customers = new ArrayList<String>();

    ArrayList<String> building_customer = new ArrayList<String>();

    ArrayList<String> id_customer = new ArrayList<String>();

    CustomAdapter customAdapter ;
    Button add_customer ;
    SQLHelper sqlHelper = new SQLHelper(this);
    SQLiteDatabase dbRead ;

    public String corresponding_building ;
    public String corresponding_customer ;
    public int corresponding_id ;

    Cursor cursor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_delivery);

        ListView listView = (ListView)findViewById(R.id.ListView);

        showListViewElements();

            //setting and applying the custom Adapter
            customAdapter = new CustomAdapter(new_delivery.this, customers, building_customer, id_customer);

            //initializing the listView
            listView.setAdapter(customAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    final TextView buildingName = (TextView) view.findViewById(R.id.textView2_description);
                    corresponding_building = buildingName.getText().toString();

                    Intent intent = new Intent(new_delivery.this, MapActivity.class);
                    intent.putExtra("value", corresponding_building);
                    Log.i("new_delivery", "onItemClick: " + corresponding_building);
                    startActivity(intent);
                }
            });


            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(new_delivery.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_options, null);
                    Button deleteCustomer = (Button) mView.findViewById(R.id.deleteCustomerBtn);
                    Button editCustomer = (Button) mView.findViewById(R.id.editCustomerBtn);

                    final TextView customerName = (TextView) view.findViewById(R.id.textView2_description);
                    corresponding_customer = customerName.getText().toString();

                    final TextView customerId = (TextView) view.findViewById(R.id.textView3_idCustomer);
                    corresponding_id = Integer.parseInt(customerId.getText().toString());

                    Log.i("new_delivery", "onItemLongClick: " + corresponding_id);

                    deleteCustomer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SQLiteDatabase dbDelete;
                            dbDelete = sqlHelper.openDataBase();
                            sqlHelper.deleteCustomer(dbDelete, corresponding_id);
                            Toast.makeText(new_delivery.this, "Customer has been deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(new_delivery.this, new_delivery.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    editCustomer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(new_delivery.this);
                            View mView = getLayoutInflater().inflate(R.layout.dialog_edit, null);

                            final EditText fnameTxt = (EditText) mView.findViewById(R.id.FName);
                            final EditText lnameTxt = (EditText) mView.findViewById(R.id.LName);
                            final EditText floorTxt = (EditText) mView.findViewById(R.id.Floor);
                            final Button editCustomer = (Button) mView.findViewById(R.id.editButton);
                            final Button cancelCustomer = (Button) mView.findViewById(R.id.cancelButton);

                            SQLiteDatabase dbShow;
                            dbShow = sqlHelper.openDataBase();

                            try {
                                String query = "Select FName , LName , Floor from customers where CustID ='" + corresponding_id + "'";
                                Log.i("new_delivert", "onClick: " + corresponding_id);
                                cursor = dbShow.rawQuery(query, null);
                                cursor.moveToFirst();
                                Log.i("new_delivert", "onClick: " + cursor.getString(0));
                                fnameTxt.setText(cursor.getString(0));
                                lnameTxt.setText(cursor.getString(1));
                                floorTxt.setText(cursor.getString(2));

                            } catch (SQLException ex) {
                                Log.e("SQLHELPER: ", ex.getMessage());
                            } finally {
                                cursor.close();
                                dbShow.close();
                            }

                            editCustomer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SQLiteDatabase dbEdit;
                                    dbEdit = sqlHelper.openDataBase();

                                    String first_name = fnameTxt.getText().toString();
                                    String last_name = lnameTxt.getText().toString();
                                    int floor = Integer.parseInt(floorTxt.getText().toString());

                                    sqlHelper.editCustomer(dbEdit, corresponding_id, first_name, last_name, floor);

                                    Toast.makeText(new_delivery.this, "Customer edited", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(new_delivery.this, new_delivery.class);
                                    startActivity(intent);
                                    finish();

                                }
                            });

                            cancelCustomer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent intent = new Intent(new_delivery.this, new_delivery.class);
                                    startActivity(intent);
                                    finish();

                                }
                            });

                            mBuilder.setView(mView);
                            AlertDialog dialog = mBuilder.create();
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();

                        }
                    });

                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    return true;
                }
            });

        //initializing the add new customer button that sends us to the mapActivity to designate the new customer's location
        add_customer = (Button) findViewById(R.id.add_customer);
        add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean addCustomer = true ;
                Intent intent = new Intent(new_delivery.this, MapActivity.class);
                intent.putExtra("addCustomer",addCustomer);
                startActivity(intent);
            }
        });


    }

    class CustomAdapter extends BaseAdapter{

        Context context;
        ArrayList<String> customers;
        ArrayList<String> building_customers;
        ArrayList<String> id_customers;

        public CustomAdapter(new_delivery newDelivery, ArrayList<String> customers,ArrayList<String> building_customers,ArrayList<String> id_customers) {

            this.context = newDelivery;
            this.customers = customers;
            this.building_customers = building_customers ;
            this.id_customers = id_customers ;
        }

        @Override
        public int getCount() {
            return customers.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
                view = getLayoutInflater().inflate(R.layout.customlayout, null);

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                TextView customer = (TextView) view.findViewById(R.id.textView_CustName);
                TextView description = (TextView) view.findViewById(R.id.textView2_description);
                TextView id = (TextView) view.findViewById(R.id.textView3_idCustomer);

                String buildName=sqlHelper.getBuildName(Integer.valueOf(id_customers.get(i)));
                Log.e("BuildingName:",buildName.toString());
                customer.setText(customers.get(i));
                description.setText(buildName);
                id.setText(id_customers.get(i));

                return view;

        }
    }


    //Getting the ListView values from the Database.
    public void showListViewElements(){
        dbRead = sqlHelper.openDataBase();
        customers = sqlHelper.showAllCustomers(dbRead);
        //building_customer = sqlHelper.showBuildings(dbRead);
        id_customer = sqlHelper.showAllCustomersIds();
        //can't close the databasae inside of each method since both methods are using the same database:
        //can't open a already close database without calling the method .openDatabase();
        //so we close it here after completing all the tasks
        dbRead.close();
    }

    /*

     */
}
