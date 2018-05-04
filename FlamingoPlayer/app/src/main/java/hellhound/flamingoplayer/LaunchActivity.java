package hellhound.flamingoplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import de.umass.lastfm.Album;
import de.umass.lastfm.Caller;
import de.umass.lastfm.ImageSize;
import wseemann.media.FFmpegMediaMetadataRetriever;

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
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(LaunchActivity.this, Manifest.permission.INTERNET) ==
                        PackageManager.PERMISSION_GRANTED)){
            Log.i(TAG, "Permission granted");
            createPicsFolder();
            //All permissions granted, launching player
            updateDB();
            launchMainActivity();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET},
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
                    createPicsFolder();
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
        db.getWritableDatabase();
        db.close();
        db.showTables();

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
                    String trackName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    int albumYear = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
                    int length = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    int trackNumber = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    Log.i(TAG, path);
                    Log.i(TAG, String.valueOf(albumYear));

                    //Adding artist
                    ArtistItem artist = new ArtistItem(artistName);
                    long artistId = db.addArtist(artist);

                    //Adding album and cover
                    AlbumItem album = new AlbumItem(albumName);
                    album.setArtistId(artistId);
                    album.setReleaseYear(albumYear);
                    String coverName = albumName + "_" + artistName;
                    coverName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" +
                            getResources().getString(R.string.app_name) + "/" + coverName + ".png";
                    boolean flag = coverExists(coverName);
                    long coverId;
                    if (flag){
                        coverId = db.addCover(coverName);
                    } else {
                        if (saveCover(coverName, path, albumName, artistName)){
                            coverId = db.addCover(coverName);
                        } else {
                            coverId = -1;
                        }

                    }
                    album.setCoverId(coverId);
                    long albumId = db.addAlbum(album);

                    //Adding track
                    TrackItem track = new TrackItem(trackName);
                    track.setTrackNumber(trackNumber);
                    track.setLength(length);
                    track.setArtistId(artistId);
                    track.setAlbumId(albumId);
                    track.setPath(path);
                    db.addTrack(track);
                    cursor.moveToNext();
                    Log.i(TAG, "--------------------------------------------");
                }

            }

        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }finally{
            if( cursor != null){
                cursor.close();
            }
        }

    }


    private boolean createPicsFolder(){
        boolean res = false;
        File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), getResources().getString(R.string.app_name));
        if(!imageRoot.exists()){
            res = imageRoot.mkdirs();
            Log.i(TAG, "Created directory");
        } else {
            Log.i(TAG, "Directory already exists");
            Log.i(TAG, imageRoot.getAbsolutePath());
        }
        return res;
    }

    private boolean coverExists(String name){
        File file = new File(name);
        Log.i(TAG, file.getAbsolutePath());
        if (file.exists()){
            Log.i(TAG, "Cover exists");
            return true;
        }
        else {
            Log.i(TAG, "Cover does not exist");
            return false;
        }
    }

    private boolean saveCover(String name, String path, String albumName, String artistName){
        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        mmr.setDataSource(path);
        byte [] cover = mmr.getEmbeddedPicture();
        if (cover != null){
            Log.i(TAG, "Cover length is " + String.valueOf(cover.length));
            Bitmap bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.length);

            FileOutputStream out = null;
            try {
                Log.i(TAG, name);
                out = new FileOutputStream(name);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mmr.release();
            return true;
        } else {
            Log.i(TAG, "No cover found");
            return false;
        }

    }
}
