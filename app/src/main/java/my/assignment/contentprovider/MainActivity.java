package my.assignment.contentprovider;

import android.content.ContentProviderOperation;

import android.content.pm.PackageManager;

import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    EditText numtxt,nametxt;

    private static final int PERMISSIONS_REQUEST_WRITE_CONTTACTS =10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numtxt=(EditText) findViewById(R.id.numtxt);
        nametxt=(EditText)findViewById(R.id.nametxt);

    }
    public void OnClickAddContact(View view){


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.WRITE_CONTACTS},PERMISSIONS_REQUEST_WRITE_CONTTACTS);
        }else {

            AddContact();
        }

    }

    public void AddContact(){
        String name=nametxt.getText().toString().trim();
        String phone=numtxt.getText().toString().trim();
        if(name.length()==0 || phone.length()==0){
            Toast.makeText(this,"Please enter values",Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
        int contactIndex = cpo.size();

        cpo.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());

        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, cpo);
            Toast.makeText(this, "Contact Added.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("Add Contact", e.getMessage());
        }
        nametxt.setText("");
        numtxt.setText("");


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        if(requestCode==PERMISSIONS_REQUEST_WRITE_CONTTACTS){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                AddContact();
            }
        }
    }
}
