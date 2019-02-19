package mx.edu.cicese.albumdecomidas;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Mario on 11/5/2016.
 */
public class PrincipalActivity extends Activity {

    Button imagenDesayuno;
    Button imagenRefrigerio1;
    Button imagenComida;
    Button imagenRefrigerio2;
    Button imagenCena;
    Button imagenRefrigerio3;
    ArrayList<File> list;
    TextView tvFecha;
    Calendar fecha;
    int semanaNumber;
    String semanaString;
    boolean[] valores;
    String[] archivos, archivos2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        //list = imageReader(Environment.getExternalStorageDirectory());

        tvFecha = (TextView) findViewById(R.id.textViewFecha);



        imagenDesayuno = (Button) findViewById(R.id.botonDesayuno);
        imagenRefrigerio1 = (Button) findViewById(R.id.botonRegrigerio1);
        imagenComida = (Button) findViewById(R.id.botonComida);
        imagenRefrigerio2 = (Button) findViewById(R.id.botonRegrigerio2);
        imagenCena = (Button) findViewById(R.id.botonCena);
        imagenRefrigerio3 = (Button) findViewById(R.id.botonRegrigerio3);

        valores = new boolean[6];
        valores[0] = false; valores[1] = false; valores[2] = false;
        valores[3] = false; valores[4] = false; valores[5] = false;

        archivos = new String[6];
        archivos[0] = ""; archivos[1] = ""; archivos[2] = "";
        archivos[3] = ""; archivos[4] = ""; archivos[5] = "";

        archivos2 = new String[6];
        archivos2[0] = ""; archivos2[1] = ""; archivos2[2] = "";
        archivos2[3] = ""; archivos2[4] = ""; archivos2[5] = "";

        fecha = Calendar.getInstance();

        setFechaLabel();

        fillImages();

        //alarma();
    }

//    public void alarma() {
//        Intent intent = new Intent(this, MyAlarmBroadcastReceiver.class);
//        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        long recurring = (24 * 60 * 60 * 1000);  // in milliseconds
//        Calendar fec = Calendar.getInstance();
//        fec.set(Calendar.HOUR_OF_DAY, 10);
//        fec.set(Calendar.MINUTE,0);
//        fec.set(Calendar.SECOND,0);
//        //fec.set(Calendar.MINUTE, 39);
//        am.setRepeating(AlarmManager.RTC, fec.getTimeInMillis(), recurring, sender);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode > 0 && resultCode>1){
            removeImages();
            fillImages();
        }
    }


    public void setFechaLabel(){
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int mes = fecha.get(Calendar.MONTH);
        int anio = fecha.get(Calendar.YEAR);

        semanaNumber = fecha.get(Calendar.DAY_OF_WEEK);

        switch (semanaNumber){
            case Calendar.MONDAY: semanaString = "LUNES"; break;
            case Calendar.TUESDAY: semanaString = "MARTES"; break;
            case Calendar.WEDNESDAY: semanaString = "MIÉRCOLES"; break;
            case Calendar.THURSDAY: semanaString = "JUEVES"; break;
            case Calendar.FRIDAY: semanaString = "VIERNES"; break;
            case Calendar.SATURDAY: semanaString = "SÁBADO"; break;
            case Calendar.SUNDAY: semanaString = "DOMINGO"; break;
            default: semanaString = ""; break;
        }

        tvFecha.setText(semanaString + " " + dia + "/" + (mes+1));
    }

    public void fillImages(){
        String fechaString = new SimpleDateFormat("yyyy-MM-dd").format(fecha.getTime());

        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/NutriAlbum");

        if (!dir.exists()){
            dir.mkdir();
        }

        String[] nombreArchivos = dir.list();
        if (!(nombreArchivos == null)) {

            for (String nombreArchivo : nombreArchivos) {
                File dire = new File(dir.getAbsoluteFile() + File.separator + nombreArchivo);

                if (nombreArchivo.equalsIgnoreCase(fechaString)){
                    if (dire.exists()) {
                        File[] nomArchivos = dire.listFiles();
                        for (File nomArchivo : nomArchivos) {
                            if (nomArchivo.getName().startsWith("IMG")){
                                fillImage(nomArchivo);
                            }
                        }
                    }
                }

            }

        }



    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 0; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 0; }
        return 0;
    }

    public void fillImage(File archivo){
        String archivoName = archivo.getName();

        String subcadena = archivoName.substring(4);

        String hora = archivoName.substring(archivoName.length()-10, archivoName.length()-8);
        String minutos = archivoName.substring(archivoName.length()-8, archivoName.length()-6);

        if (subcadena.startsWith("DES")){


            Bitmap d = new BitmapDrawable(this.getResources() , archivo.getAbsolutePath()).getBitmap();
            int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
            //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
            Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                    nh, true);
            d = null;
            Bitmap adjustedBitmap = null;
            try{
                ExifInterface exif = new ExifInterface(archivo.getPath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);

                Matrix matrix = new Matrix();
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                adjustedBitmap = Bitmap.createBitmap(scaled, 0, 0, 512, nh, matrix, true);
                scaled = null;
                //img.setImageBitmap(adjustedBitmap);
            }catch (IOException e) {

            }
            Drawable d2 = null;
            Bitmap adjustedBitmap3 = null;
            if(valores[0]){
                archivos2[0] = archivo.getAbsolutePath();

                File archi = new File(archivos[0]);

                Bitmap d3 = new BitmapDrawable(this.getResources() , archi.getAbsolutePath()).getBitmap();
                int nh3 = (int) ( d3.getHeight() * (512.0 / d3.getWidth()) );
                //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
                Bitmap scaled3 = Bitmap.createScaledBitmap(d3, 512,
                        nh3, true);
                d3 = null;

                try{
                    ExifInterface exif = new ExifInterface(archi.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);

                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                    adjustedBitmap3 = Bitmap.createBitmap(scaled3, 0, 0, 512, nh3, matrix, true);
                    scaled3 = null;

                    //img.setImageBitmap(adjustedBitmap);
                }catch (IOException e) {

                }

                Bitmap bm = combineImages(adjustedBitmap, adjustedBitmap3);

                d2 = new BitmapDrawable(getResources(), bm);

            }else{
                archivos[0] = archivo.getAbsolutePath();

                d2 = new BitmapDrawable(getResources(), adjustedBitmap);
            }



            imagenDesayuno.setBackground(d2);
            imagenDesayuno.setText("DESAYUNO" + "\n" +hora + ":" + minutos);
            valores[0] = true;

        }

        if (subcadena.startsWith("REF1")){

            Bitmap d = new BitmapDrawable(this.getResources() , archivo.getAbsolutePath()).getBitmap();
            int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
            //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
            Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                    nh, true);
            d = null;
            Bitmap adjustedBitmap = null;
            try{
                ExifInterface exif = new ExifInterface(archivo.getPath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);

                Matrix matrix = new Matrix();
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                adjustedBitmap = Bitmap.createBitmap(scaled, 0, 0, 512, nh, matrix, true);
                scaled = null;
                //img.setImageBitmap(adjustedBitmap);
            }catch (IOException e) {

            }
            Drawable d2 = null;
            Bitmap adjustedBitmap3 = null;
            if(valores[1]){
                archivos2[1] = archivo.getAbsolutePath();

                File archi = new File(archivos[1]);

                Bitmap d3 = new BitmapDrawable(this.getResources() , archi.getAbsolutePath()).getBitmap();
                int nh3 = (int) ( d3.getHeight() * (512.0 / d3.getWidth()) );
                //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
                Bitmap scaled3 = Bitmap.createScaledBitmap(d3, 512,
                        nh3, true);
                d3 = null;

                try{
                    ExifInterface exif = new ExifInterface(archi.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);

                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                    adjustedBitmap3 = Bitmap.createBitmap(scaled3, 0, 0, 512, nh3, matrix, true);
                    scaled3 = null;

                    //img.setImageBitmap(adjustedBitmap);
                }catch (IOException e) {

                }

                Bitmap bm = combineImages(adjustedBitmap, adjustedBitmap3);

                d2 = new BitmapDrawable(getResources(), bm);

            }else{
                archivos[1] = archivo.getAbsolutePath();

                d2 = new BitmapDrawable(getResources(), adjustedBitmap);
            }

            //Drawable d2 = new BitmapDrawable(getResources(), scaled);
            imagenRefrigerio1.setBackground(d2);
            imagenRefrigerio1.setText("REFRIGERIO 1" + "\n" +hora + ":" + minutos);
            valores[1] = true;
        }

        if (subcadena.startsWith("COM")){

            Bitmap d = new BitmapDrawable(this.getResources() , archivo.getAbsolutePath()).getBitmap();
            int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
            //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
            Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                    nh, true);
            d = null;
            Bitmap adjustedBitmap = null;
            try{
                ExifInterface exif = new ExifInterface(archivo.getPath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);

                Matrix matrix = new Matrix();
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                adjustedBitmap = Bitmap.createBitmap(scaled, 0, 0, 512, nh, matrix, true);
                scaled = null;
                //img.setImageBitmap(adjustedBitmap);
            }catch (IOException e) {

            }
            Drawable d2 = null;
            Bitmap adjustedBitmap3 = null;
            if(valores[2]){
                archivos2[2] = archivo.getAbsolutePath();

                File archi = new File(archivos[2]);

                Bitmap d3 = new BitmapDrawable(this.getResources() , archi.getAbsolutePath()).getBitmap();
                int nh3 = (int) ( d3.getHeight() * (512.0 / d3.getWidth()) );
                //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
                Bitmap scaled3 = Bitmap.createScaledBitmap(d3, 512,
                        nh3, true);
                d3 = null;

                try{
                    ExifInterface exif = new ExifInterface(archi.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);

                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                    adjustedBitmap3 = Bitmap.createBitmap(scaled3, 0, 0, 512, nh3, matrix, true);
                    scaled3 = null;

                    //img.setImageBitmap(adjustedBitmap);
                }catch (IOException e) {

                }

                Bitmap bm = combineImages(adjustedBitmap, adjustedBitmap3);

                d2 = new BitmapDrawable(getResources(), bm);

            }else{
                archivos[2] = archivo.getAbsolutePath();

                d2 = new BitmapDrawable(getResources(), adjustedBitmap);
            }
            imagenComida.setBackground(d2);
            imagenComida.setText("COMIDA" + "\n" +hora + ":" + minutos);
            valores[2] = true;
        }

        if (subcadena.startsWith("REF2")){

            Bitmap d = new BitmapDrawable(this.getResources() , archivo.getAbsolutePath()).getBitmap();
            int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
            //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
            Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                    nh, true);
            d = null;
            Bitmap adjustedBitmap = null;
            try{
                ExifInterface exif = new ExifInterface(archivo.getPath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);

                Matrix matrix = new Matrix();
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                adjustedBitmap = Bitmap.createBitmap(scaled, 0, 0, 512, nh, matrix, true);
                scaled = null;
                //img.setImageBitmap(adjustedBitmap);
            }catch (IOException e) {

            }
            Drawable d2 = null;
            Bitmap adjustedBitmap3 = null;
            if(valores[3]){
                archivos2[3] = archivo.getAbsolutePath();

                File archi = new File(archivos[3]);

                Bitmap d3 = new BitmapDrawable(this.getResources() , archi.getAbsolutePath()).getBitmap();
                int nh3 = (int) ( d3.getHeight() * (512.0 / d3.getWidth()) );
                //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
                Bitmap scaled3 = Bitmap.createScaledBitmap(d3, 512,
                        nh3, true);
                d3 = null;

                try{
                    ExifInterface exif = new ExifInterface(archi.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);

                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                    adjustedBitmap3 = Bitmap.createBitmap(scaled3, 0, 0, 512, nh3, matrix, true);
                    scaled3 = null;

                    //img.setImageBitmap(adjustedBitmap);
                }catch (IOException e) {

                }

                Bitmap bm = combineImages(adjustedBitmap, adjustedBitmap3);

                d2 = new BitmapDrawable(getResources(), bm);

            }else{
                archivos[3] = archivo.getAbsolutePath();

                d2 = new BitmapDrawable(getResources(), adjustedBitmap);
            }
            imagenRefrigerio2.setBackground(d2);
            imagenRefrigerio2.setText("REFRIGERIO 2" + "\n" +hora + ":" + minutos);
            valores[3] = true;
        }

        if (subcadena.startsWith("CEN")){

            Bitmap d = new BitmapDrawable(this.getResources() , archivo.getAbsolutePath()).getBitmap();
            int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
            //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
            Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                    nh, true);
            d = null;
            Bitmap adjustedBitmap = null;
            try{
                ExifInterface exif = new ExifInterface(archivo.getPath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);

                Matrix matrix = new Matrix();
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                adjustedBitmap = Bitmap.createBitmap(scaled, 0, 0, 512, nh, matrix, true);
                scaled = null;
                //img.setImageBitmap(adjustedBitmap);
            }catch (IOException e) {

            }
            Drawable d2 = null;
            Bitmap adjustedBitmap3 = null;
            if(valores[4]){
                archivos2[4] = archivo.getAbsolutePath();

                File archi = new File(archivos[4]);

                Bitmap d3 = new BitmapDrawable(this.getResources() , archi.getAbsolutePath()).getBitmap();
                int nh3 = (int) ( d3.getHeight() * (512.0 / d3.getWidth()) );
                //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
                Bitmap scaled3 = Bitmap.createScaledBitmap(d3, 512,
                        nh3, true);
                d3 = null;

                try{
                    ExifInterface exif = new ExifInterface(archi.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);

                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                    adjustedBitmap3 = Bitmap.createBitmap(scaled3, 0, 0, 512, nh3, matrix, true);
                    scaled3 = null;

                    //img.setImageBitmap(adjustedBitmap);
                }catch (IOException e) {

                }

                Bitmap bm = combineImages(adjustedBitmap, adjustedBitmap3);

                d2 = new BitmapDrawable(getResources(), bm);

            }else{
                archivos[4] = archivo.getAbsolutePath();

                d2 = new BitmapDrawable(getResources(), adjustedBitmap);
            }
            imagenCena.setBackground(d2);
            imagenCena.setText("CENA" + "\n" +hora + ":" + minutos);
            valores[4] = true;
        }

        if (subcadena.startsWith("REF3")){

            Bitmap d = new BitmapDrawable(this.getResources() , archivo.getAbsolutePath()).getBitmap();
            int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
            //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
            Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                    nh, true);
            d = null;
            Bitmap adjustedBitmap = null;
            try{
                ExifInterface exif = new ExifInterface(archivo.getPath());
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifToDegrees(rotation);

                Matrix matrix = new Matrix();
                if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                adjustedBitmap = Bitmap.createBitmap(scaled, 0, 0, 512, nh, matrix, true);
                scaled = null;
                //img.setImageBitmap(adjustedBitmap);
            }catch (IOException e) {

            }
            Drawable d2 = null;
            Bitmap adjustedBitmap3 = null;
            if(valores[5]){
                archivos2[5] = archivo.getAbsolutePath();

                File archi = new File(archivos[5]);

                Bitmap d3 = new BitmapDrawable(this.getResources() , archi.getAbsolutePath()).getBitmap();
                int nh3 = (int) ( d3.getHeight() * (512.0 / d3.getWidth()) );
                //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
                Bitmap scaled3 = Bitmap.createScaledBitmap(d3, 512,
                        nh3, true);
                d3 = null;

                try{
                    ExifInterface exif = new ExifInterface(archi.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);

                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                    adjustedBitmap3 = Bitmap.createBitmap(scaled3, 0, 0, 512, nh3, matrix, true);
                    scaled3 = null;

                    //img.setImageBitmap(adjustedBitmap);
                }catch (IOException e) {

                }

                Bitmap bm = combineImages(adjustedBitmap, adjustedBitmap3);

                d2 = new BitmapDrawable(getResources(), bm);

            }else{
                archivos[5] = archivo.getAbsolutePath();

                d2 = new BitmapDrawable(getResources(), adjustedBitmap);
            }
            imagenRefrigerio3.setBackground(d2);
            imagenRefrigerio3.setText("REFRIGERIO 3" + "\n" + hora + ":" + minutos);
            valores[5] = true;
        }
    }

    /*public void nuevasImagenes(View view){
        Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
        insertDesayunoImage();
        insertRegrigerio1Image();
        insertComidaImage();
        insertRefrigerio2Image();
        insertCenaImage();
        insertRefrigerio3Image();
    }*/

    public void clickDesayuno(View view){
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("MEAL", 1);
        intent.putExtra("DIA", fecha.get(Calendar.DAY_OF_MONTH));
        intent.putExtra("MES", fecha.get(Calendar.MONTH));
        intent.putExtra("ANO", fecha.get(Calendar.YEAR));
        intent.putExtra("TIENE", false);
        intent.putExtra("ARCHIVO", archivos[0]);
        intent.putExtra("ARCHIVO2", archivos2[0]);
        startActivityForResult(intent,1);
    }

    public void clickRefrigerio1(View view){
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("MEAL", 2);
        intent.putExtra("DIA", fecha.get(Calendar.DAY_OF_MONTH));
        intent.putExtra("MES", fecha.get(Calendar.MONTH));
        intent.putExtra("ANO", fecha.get(Calendar.YEAR));
        intent.putExtra("TIENE", false);
        intent.putExtra("ARCHIVO", archivos[1]);
        intent.putExtra("ARCHIVO2", archivos2[1]);
        startActivityForResult(intent,2);
    }

    public void clickComida(View view){
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("MEAL", 3);
        intent.putExtra("DIA", fecha.get(Calendar.DAY_OF_MONTH));
        intent.putExtra("MES", fecha.get(Calendar.MONTH));
        intent.putExtra("ANO", fecha.get(Calendar.YEAR));
        intent.putExtra("TIENE", false);
        intent.putExtra("ARCHIVO", archivos[2]);
        intent.putExtra("ARCHIVO2", archivos2[2]);
        startActivityForResult(intent,3);
    }

    public void clickRefrigerio2(View view){
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("MEAL", 4);
        intent.putExtra("DIA", fecha.get(Calendar.DAY_OF_MONTH));
        intent.putExtra("MES", fecha.get(Calendar.MONTH));
        intent.putExtra("ANO", fecha.get(Calendar.YEAR));
        intent.putExtra("TIENE", false);
        intent.putExtra("ARCHIVO", archivos[3]);
        intent.putExtra("ARCHIVO2", archivos2[3]);
        startActivityForResult(intent,4);
    }

    public void clickCena(View view){
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("MEAL", 5);
        intent.putExtra("DIA", fecha.get(Calendar.DAY_OF_MONTH));
        intent.putExtra("MES", fecha.get(Calendar.MONTH));
        intent.putExtra("ANO", fecha.get(Calendar.YEAR));
        intent.putExtra("TIENE", false);
        intent.putExtra("ARCHIVO", archivos[4]);
        intent.putExtra("ARCHIVO2", archivos2[4]);
        startActivityForResult(intent,5);
    }

    public void clickRefrigerio3(View view){
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("MEAL", 6);
        intent.putExtra("DIA", fecha.get(Calendar.DAY_OF_MONTH));
        intent.putExtra("MES", fecha.get(Calendar.MONTH));
        intent.putExtra("ANO", fecha.get(Calendar.YEAR));
        intent.putExtra("TIENE", false);
        intent.putExtra("ARCHIVO", archivos[5]);
        intent.putExtra("ARCHIVO2", archivos2[5]);
        startActivityForResult(intent,6);
    }

   /* public void insertDesayunoImage(){

//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//        int height = metrics.heightPixels;
//        int width = metrics.widthPixels;

        Bitmap d = new BitmapDrawable(this.getResources() , list.get(0).getAbsolutePath()).getBitmap();
        int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
        //Toast.makeText(PrincipalActivity.this, imagenDesayuno.getWidth() + "", Toast.LENGTH_LONG).show();
        Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                nh, true);
        imagenDesayuno.setScaleType(ImageView.ScaleType.FIT_XY);
        imagenDesayuno.setImageBitmap(scaled);

    }

    public void insertRegrigerio1Image(){


        Bitmap d = new BitmapDrawable(this.getResources() , list.get(1).getAbsolutePath()).getBitmap();
        int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                nh, true);
        imagenRefrigerio1.setScaleType(ImageView.ScaleType.FIT_XY);
        imagenRefrigerio1.setImageBitmap(scaled);

    }

    public void insertComidaImage(){


        Bitmap d = new BitmapDrawable(this.getResources() , list.get(2).getAbsolutePath()).getBitmap();
        int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                nh, true);
        imagenComida.setScaleType(ImageView.ScaleType.FIT_XY);
        imagenComida.setImageBitmap(scaled);

    }

    public void insertRefrigerio2Image(){


        Bitmap d = new BitmapDrawable(this.getResources() , list.get(3).getAbsolutePath()).getBitmap();
        int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                nh, true);
        imagenRefrigerio2.setScaleType(ImageView.ScaleType.FIT_XY);
        imagenRefrigerio2.setImageBitmap(scaled);
    }

    public void insertCenaImage(){


        Bitmap d = new BitmapDrawable(this.getResources() , list.get(4).getAbsolutePath()).getBitmap();
        int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                nh, true);
        imagenCena.setScaleType(ImageView.ScaleType.FIT_XY);
        imagenCena.setImageBitmap(scaled);

    }

    public void insertRefrigerio3Image(){


        Bitmap d = new BitmapDrawable(this.getResources() , list.get(5).getAbsolutePath()).getBitmap();
        int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(d, 512,
                nh, true);
        imagenRefrigerio3.setScaleType(ImageView.ScaleType.FIT_XY);
        imagenRefrigerio3.setImageBitmap(scaled);

    }*/

   /* ArrayList<File> imageReader(File root){
        ArrayList<File> a = new ArrayList<>();

        File files[] = root.listFiles();
        for (int i = 0; i< files.length; i++){

            if(files[i].isDirectory()){
                a.addAll(imageReader(files[i]));
            }else{
                if(files[i].getName().endsWith(".jpg")){
                    if (a.size() >= 6){
                        break;
                    }
                    a.add(files[i]);
                }
            }
        }

        return a;
    }*/

    public void removeImages(){
        imagenDesayuno.setBackgroundColor(Color.parseColor("#08a5ed"));
        imagenDesayuno.setText("DESAYUNO");
        valores[0] = false;
        imagenRefrigerio1.setBackgroundColor(Color.parseColor("#08a5ed"));
        imagenRefrigerio1.setText("REFRIGERIO 1");
        valores[1] = false;
        imagenComida.setBackgroundColor(Color.parseColor("#08a5ed"));
        imagenComida.setText("COMIDA");
        valores[2] = false;
        imagenRefrigerio2.setBackgroundColor(Color.parseColor("#08a5ed"));
        imagenRefrigerio2.setText("REFRIGERIO 2");
        valores[3] = false;
        imagenCena.setBackgroundColor(Color.parseColor("#08a5ed"));
        imagenCena.setText("CENA");
        valores[4] = false;
        imagenRefrigerio3.setBackgroundColor(Color.parseColor("#08a5ed"));
        imagenRefrigerio3.setText("REFRIGERIO 3");
        valores[5] = false;

        archivos[0] = ""; archivos[1] = ""; archivos[2] = "";
        archivos[3] = ""; archivos[4] = ""; archivos[5] = "";

        archivos2[0] = ""; archivos2[1] = ""; archivos2[2] = "";
        archivos2[3] = ""; archivos2[4] = ""; archivos2[5] = "";
        //imagenCena.setImageResource(R.drawable.imgcena);
        //imagenRefrigerio3.setImageResource(R.drawable.imgrefrigerio3);
    }
    public void siguiente(View view){
        fecha.add(Calendar.DATE,1);
        setFechaLabel();
        removeImages();
        fillImages();
    }

    public void atras(View view){
        fecha.add(Calendar.DATE,-1);
        setFechaLabel();
        removeImages();
        fillImages();
    }

    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

//        if(c.getWidth() > s.getWidth()) {
//            width = c.getWidth() + s.getWidth();
//            height = c.getHeight();
//        } else {
//            width = s.getWidth() + s.getWidth();
//            height = c.getHeight();
//        }

        width = c.getWidth();
        height = c.getHeight() + s.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        //comboImage.drawBitmap(s, c.getWidth(), 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(), null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
        //String tmpImg = String.valueOf(System.currentTimeMillis()) + ".jpg";
//    File archivoCombinado = getOutputMediaFile();
//    OutputStream os = null;
//    try {
//      os = new FileOutputStream(archivoCombinado.getAbsolutePath());
//      cs.compress(Bitmap.CompressFormat.JPEG, 100, os);
//    } catch(IOException e) {
//      Log.e("combineImages", "problem combining images", e);
//    }

        return cs;
    }
}
