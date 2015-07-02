package com.kaushiksamba.contacts;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume()
    {
        create_app_folder();    //To create a folder to store selected images of contacts
        populate_listView();
        super.onResume();
    }

    public String folder_path;
    public void create_app_folder()
    {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) //If an SD card is present
        {
            folder_path = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name);
            File newfolder = new File(folder_path);
            newfolder.mkdirs();
        }
            else Toast.makeText(this,"Unable to create folder", Toast.LENGTH_SHORT).show();
    }

    public void populate_listView()
    {
        DatabaseHandler dbh = new DatabaseHandler(this);
        final List<EachContact> list = dbh.getAllContacts();
        int number = dbh.getContactCount();
        dbh.close();
        TextView number_of_contacts = (TextView) findViewById(R.id.number_of_contacts);
        if(number>0) number_of_contacts.setVisibility(View.INVISIBLE);
        ContactAdapter adapter = new ContactAdapter(this,list);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() //Clicking an item is to edit the data or change the image
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(),AddContact.class);
                int PROCESS_EDIT = 2;
                intent.putExtra("Process",PROCESS_EDIT);
                String name = list.get(position).getName();
                String img_url = list.get(position).getImg_url();
                intent.putExtra("Name",name);
                intent.putExtra("img_url",img_url);
                intent.putExtra("Folder_path",folder_path);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.add_contact:  Intent intent = new Intent(this,AddContact.class);
                                    int PROCESS_ADD = 1;
                                    intent.putExtra("Process",PROCESS_ADD);
                                    intent.putExtra("Folder_path", folder_path);
                                    startActivity(intent);
                                    break;
        }
        return super.onOptionsItemSelected(item);
    }

}
