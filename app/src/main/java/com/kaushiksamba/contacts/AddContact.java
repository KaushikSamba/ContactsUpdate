package com.kaushiksamba.contacts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;


public class AddContact extends ActionBarActivity {

    String image_path="@drawable/doge";
    String folder_path;
    int process;
    String originalname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        folder_path = intent.getStringExtra("Folder_path");
        process = intent.getIntExtra("Process",1);  //Adding process
        if(process==2) //Editing process
        {
            String name = intent.getStringExtra("Name");
            String img_url = intent.getStringExtra("img_url");
            update_edit_fields(name,img_url);
        }
    }

    private void update_edit_fields(String name, final String img_url)
    {
        EditText editText = (EditText) findViewById(R.id.name_editText);
        editText.setText(name);
        TextView textView = (TextView) findViewById(R.id.path);
        textView.setVisibility(View.INVISIBLE);
        originalname = name;
        filename = img_url;
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name) + File.separator + filename);
                try {
                    File file = new File(uri.getPath());
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] data = new byte[(int) file.length()];
                    fileInputStream.read(data);
                    fileInputStream.close();
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int PICK_IMAGE_REQUEST = 1;

    public void SelectImage(View view)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if(Build.VERSION.SDK_INT >=19) intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        final Intent finalintent = intent;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(Intent.createChooser(finalintent, "Select a picture"), PICK_IMAGE_REQUEST);
            }
        }).start();
    }

    public void SaveContact(View view)
    {
        EditText editText = (EditText) findViewById(R.id.name_editText);
        if(editText.getText().toString().length()==0) Toast.makeText(this,"Enter a contact name",Toast.LENGTH_SHORT).show();
            else
            {
                String name = editText.getText().toString();
                DatabaseHandler dbh = new DatabaseHandler(this);
                if(process==1) dbh.addContact(new EachContact(name,filename));
                    else
                    {
                            dbh.updateContact(originalname,new EachContact(name,filename));
                    }
                dbh.close();
                finish();
            }
    }

    String filename="doge";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            Uri uri = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);

                //Copying the image to the app folder
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] bytes = stream.toByteArray();

                OutputStream outputStream;
                filename = getRandomString();
                image_path = folder_path + File.separator + filename;
                File file = new File(image_path);
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                outputStream.write(bytes);
                outputStream.close();

                TextView textView = (TextView) findViewById(R.id.path);
                textView.setText(image_path);
            }
            catch (IOException e) {
                Toast.makeText(this,"Failed to get image",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }

    public String getRandomString()
    {
        char ch;
        StringBuilder sb = new StringBuilder();
        for(ch = 'a'; ch<='z'; ch++)   sb.append(ch);
        for(ch='0'; ch<='9'; ch++) sb.append(ch);
        for(ch='A'; ch<='Z'; ch++) sb.append(ch);
        char x[] = sb.toString().toCharArray();
        StringBuilder random_string = new StringBuilder();
        for(int i=0;i<7;i++)
        {
            Random random = new Random();
            random_string.append(x[random.nextInt(x.length)]);
        }
        return random_string.toString();
    }
}
