package com.kaushiksamba.contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


public class ContactAdapter extends ArrayAdapter<EachContact>
{
    private Context context;
    List<EachContact> values;
    public ContactAdapter(Context context,List<EachContact> values)
    {
        super(context, R.layout.single_contact,values);
        this.context = context;
        this.values = values;
    }

    static class ViewHolder
    {
        TextView textView;
        ImageView imageView;
        ImageButton imageButton;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        Log.d("getview pos ", String.valueOf(position));
        ViewHolder holder;
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_contact,null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.name_contact);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_contact);
            holder.imageButton = (ImageButton) convertView.findViewById(R.id.remove_button);
            convertView.setTag(holder);
        }
            else
            {
                holder = (ViewHolder) convertView.getTag();
                if(holder.textView.getText().toString().equals(values.get(position).getName())) return convertView;
            }
        final String contact_name_string = values.get(position).getName();
        holder.textView.setText(contact_name_string);
        final String path = values.get(position).getImg_url();


        new AsyncTask<ViewHolder, Void, Bitmap>()
        {
            private ViewHolder v;
            @Override
            protected Bitmap doInBackground(ViewHolder... params)
            {
                v = params[0];
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_name) + File.separator + path);
                try {
                    File file = new File(uri.getPath());
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] data = new byte[(int) file.length()];
                    fileInputStream.read(data);
                    fileInputStream.close();
                    return BitmapFactory.decodeByteArray(data,0,data.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap)
            {
                super.onPostExecute(bitmap);
                    if(bitmap!=null)
                    {
                        v.imageView.setImageBitmap(bitmap);
                        Log.d("Bitmap set", v.textView.getText().toString());
                    }
                        else
                        {
                            int id = getContext().getResources().getIdentifier("doge","drawable",getContext().getPackageName());
                            v.imageView.setImageResource(id);
                            Log.d("Bitmap not set", v.textView.getText().toString());

                        }
            }
        }.execute(holder);

        holder.imageButton = (ImageButton) convertView.findViewById(R.id.remove_button);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler dbh = new DatabaseHandler(getContext());
                dbh.deleteContact(new EachContact(contact_name_string, "dummyURL"));
                dbh.close();
                Toast.makeText(getContext(), contact_name_string + " has been deleted", Toast.LENGTH_SHORT).show();
                values.remove(position);
                ContactAdapter.this.notifyDataSetChanged();
            }
        });
        return convertView;
    }
}
