package hellhound.flamingoplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class LaunchActivity extends AppCompatActivity {

    private static final String TAG = "Launch";
    private static final int STORAGE_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ImageView icon = (ImageView) findViewById(R.id.launch_icon);

        GlideApp.with(getApplicationContext()).load(R.mipmap.flamingo_launcher_rounded).into(icon);

        if((ContextCompat.checkSelfPermission(LaunchActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            Log.i(TAG, "Permission granted");
            //All permissions granted, launching player
            updateDB();
            launchMainActivity();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    //All permissions granted, launching player
                    updateDB();
                    launchMainActivity();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
                    builder.setTitle("Please, accept permissions!")
                            .setMessage("Application needs these permissions to operate properly!")
                            .setCancelable(false)
                            .setNegativeButton("ОК",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;

        }
    }


    private void launchMainActivity(){

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }


    private void updateDB(){
        DBHelper db = DBHelper.getInstance(getApplicationContext());

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.YEAR};
        Cursor cursor = null;

        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = getContentResolver().query(uri, projection, selection, null, null);
            if( cursor != null){
                cursor.moveToFirst();

                while( !cursor.isAfterLast() ){
                    String artistName = cursor.getString(1);
                    String albumName = cursor.getString(3);
                    int albumYear = cursor.getInt(6);
                    ArtistItem artist = new ArtistItem(artistName);
                    long artistId = db.addArtist(artist);
                    AlbumItem album = new AlbumItem(albumName);
                    album.setArtistId(artistId);
                    album.setReleaseYear(albumYear);
                    db.addAlbum(album);

                    cursor.moveToNext();
                }

            }

        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }finally{
            if( cursor != null){
                cursor.close();
            }
        }
        db.close();

    }
}
