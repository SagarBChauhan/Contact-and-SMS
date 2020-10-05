package com.example.pr3q4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    MatrixCursor matrixCursor;
    SimpleCursorAdapter simpleCursorAdapter;
    ListView lv_contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},0);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},0);
        matrixCursor=new MatrixCursor(new String[]{"_ID","Name","MobileNo"});

        Uri contactUri= ContactsContract.Contacts.CONTENT_URI;
        Cursor contactCursor=getContentResolver().query(contactUri,null,null,null,ContactsContract.Contacts.DISPLAY_NAME);

        if(contactCursor.moveToFirst())
        {
            do{
                Long contactId=contactCursor.getLong(contactCursor.getColumnIndex("_ID"));
                Uri dataUri=ContactsContract.Data.CONTENT_URI;
                Cursor dataCursor=getContentResolver().query(dataUri,null,ContactsContract.Data.CONTACT_ID+"="+contactId,null,null);
                String Name="";
                String Contact="";
                if (dataCursor.moveToFirst())
                {
                    Name=dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                    if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE))
                    {
                        Contact=dataCursor.getString(dataCursor.getColumnIndex("data1"));
                    }
                    matrixCursor.addRow(new Object[]{Long.toString(contactId),Name,Contact});
                }
            }while (contactCursor.moveToNext());
        }
        simpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(),R.layout.list_item,matrixCursor,new String[]{"Name","MobileNo"},new int[]{R.id.txt_Name,R.id.txt_Contact},0);
        lv_contacts.setAdapter(simpleCursorAdapter);
    }

    private void init() {
        lv_contacts=findViewById(R.id.lv_Contacts);
        registerForContextMenu(lv_contacts);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_context,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send_SMS:
                String name=matrixCursor.getString(1);
                String mobile=matrixCursor.getString(2);
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("smsto:"));
                i.putExtra("address",mobile);
                i.putExtra("sms_body","Hello "+name);
                startActivity(i);
                break;
        }
        return super.onContextItemSelected(item);
    }
}
