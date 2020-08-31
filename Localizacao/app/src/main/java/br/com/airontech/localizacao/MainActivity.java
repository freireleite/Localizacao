package br.com.airontech.localizacao;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String DATA_MIMETYPE = ContactsContract.Data.MIMETYPE;
    private static final Uri DATA_CONTENT_URI = ContactsContract.Data.CONTENT_URI;
    private static final String DATA_CONTACT_ID = ContactsContract.Data.CONTACT_ID;

    private static final String CONTACTS_ID = ContactsContract.Contacts._ID;
    private static final Uri CONTACTS_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

    private static final String STRUCTURED_POSTAL_CONTENT_ITEM_TYPE = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;
    private static final String STRUCTURED_POSTAL_FORMATTED_ADDRESS = ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;

    private static final  int PICK_CONTACT_REQUEST = 0;
    static  String TAG = "MapLocation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button)findViewById(R.id.btnLocalizar);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Intent intent = new Intent(Intent.ACTION_PICK, CONTACTS_CONTENT_URI );

                    startActivityForResult(intent, PICK_CONTACT_REQUEST);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST){
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(data.getData(), null,null,null,null);

            if(null != cursor && cursor.moveToFirst()){
                String id = cursor.getString(cursor.getColumnIndex(CONTACTS_ID));

                String where = DATA_CONTACT_ID + " = ? AND " + DATA_MIMETYPE + " = ?";

                String[] whereParameters = new String[]{
                        id, STRUCTURED_POSTAL_CONTENT_ITEM_TYPE
                };

                Cursor addrCur = cr.query(DATA_CONTENT_URI, null, where, whereParameters, null);

                if (null != addrCur && addrCur.moveToFirst()){
                    String formatteAddress = addrCur.getString(addrCur.getColumnIndex(STRUCTURED_POSTAL_FORMATTED_ADDRESS));

                    if (null != formatteAddress){

                        formatteAddress = formatteAddress.replace(' ', '+');

                        Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + formatteAddress));

                        startActivity(geoIntent);
                    }
                }
                if(null != addrCur)
                    addrCur.close();

            }
            if (null != cursor)
                cursor.close();

        }


    }

}