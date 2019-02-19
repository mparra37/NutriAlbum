package mx.edu.cicese.albumdecomidas;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Mario on 11/7/2016.
 */
public class PhotoActivity extends Activity {

    private static final int CAM_REQUEST = 1234;
    public ImageView img;
    private String mCurrentPhotoPath = "";
    private String mCurrentPhotoPath2 = "";
    public EditText et;
    File f;
    String timeStamp;
    File archivo;
    Bitmap imagen;
    int meal;
    String mealTag = "";
    File folderDay;
    Calendar fecha;
    Intent intent;
    Button btnAceptar, btnAgregar, btnEliminar;
    boolean tieneImagen, tiene;
    String archivoNombre, archivo2Nombre;
    TextView tvHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_layout);

        img = (ImageView) findViewById(R.id.imageView2);
        et = (EditText) findViewById(R.id.editText2);
        btnAceptar = (Button) findViewById(R.id.botonAceptar);
        btnAgregar = (Button) findViewById(R.id.btnAgregarOtraFoto);
        btnEliminar = (Button) findViewById(R.id.btnEliminar);
        tvHora = (TextView) findViewById(R.id.labelHora);

        intent = getIntent();

        meal = intent.getIntExtra("MEAL", 0);
        tiene = intent.getBooleanExtra("TIENE", false);
        fecha = Calendar.getInstance();
       fecha.set(Calendar.DAY_OF_MONTH, intent.getIntExtra("DIA", 0));
        fecha.set(Calendar.MONTH, intent.getIntExtra("MES", 0));
        fecha.set(Calendar.YEAR, intent.getIntExtra("ANO", 0));

       // tvHora.setText(fecha.get(Calendar.HOUR) + ":" + fecha.get(Calendar.MINUTE));
        setFechaLabel();

        setMeal();

        archivoNombre = intent.getStringExtra("ARCHIVO");
        archivo2Nombre = intent.getStringExtra("ARCHIVO2");

        if (archivoNombre.equalsIgnoreCase("")){
            tieneImagen = false;
            btnAgregar.setEnabled(false);
            btnEliminar.setEnabled(false);
            btnEliminar.setBackgroundColor(Color.parseColor("#8808a5ed"));
            btnAgregar.setBackgroundColor(Color.parseColor("#8808a5ed"));
            //btnAgregar.setVisibility(View.GONE);
            //btnEliminar.setVisibility(View.GONE);
            comenzarCamara();
        }else{
            archivo = new File(archivoNombre);
            mCurrentPhotoPath = archivo.getAbsolutePath();
            setFecha(archivo.getName());
            tieneImagen = true;
            leerTexto();
            if (archivo.exists()){
                btnAceptar.setText("Editar");
                Bitmap d = new BitmapDrawable(this.getResources() , archivo.getAbsolutePath()).getBitmap();
                int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
                Bitmap adjustedBitmap = null;
                try{
                    ExifInterface exif = new ExifInterface(archivo.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);

                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                    adjustedBitmap = Bitmap.createBitmap(scaled, 0, 0, 512, nh, matrix, true);

                    //img.setImageBitmap(adjustedBitmap);
                }catch (IOException e) {

                }

                img.setImageBitmap(adjustedBitmap);

            }
            if(tiene){
                btnAgregar.setEnabled(false);
                btnAgregar.setBackgroundColor(Color.parseColor("#8808a5ed"));
            }
            if(!archivo2Nombre.equals("")){
                btnAgregar.setText("Ver Siguiente");
            }
        }
    }

    public void setFechaLabel(){
        int hora = fecha.get(Calendar.HOUR_OF_DAY);
        int minutos = fecha.get(Calendar.MINUTE);

        String horaString = hora+ "";
        String minutosString = minutos + "";

        if(horaString.length()==1){
            horaString = "0" + horaString;
        }

        if(minutosString.length()==1){
            minutosString = "0" + minutosString;
        }

        tvHora.setText(horaString + ":" + minutosString);


    }

    public void setFecha(String nombreImagen){
        Calendar date = Calendar.getInstance();

        String ano = nombreImagen.substring(nombreImagen.length()-19, nombreImagen.length()-15);
        String mes = nombreImagen.substring(nombreImagen.length()-15, nombreImagen.length()-13);
        String dia = nombreImagen.substring(nombreImagen.length()-13, nombreImagen.length()-11);
        String hora = nombreImagen.substring(nombreImagen.length()-10, nombreImagen.length()-8);
        String min = nombreImagen.substring(nombreImagen.length()-8, nombreImagen.length()-6);
        String seg = nombreImagen.substring(nombreImagen.length()-6, nombreImagen.length()-4);

        date.set(Calendar.YEAR, Integer.parseInt(ano));
        date.set(Calendar.MONTH, Integer.parseInt(mes));
        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dia));
        date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora));
        date.set(Calendar.MINUTE, Integer.parseInt(min));
        date.set(Calendar.SECOND, Integer.parseInt(seg));


        tvHora.setText(hora + ":" + min);

       // return date;
    }

    public void leerTexto(){
        String newName = archivo.getName().substring(3, archivo.getName().length()-4);
        newName = "TXT" + newName + ".txt";

        File file = new File(archivo.getParent() + File.separator + newName);

//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        et.setText(text);
    }

    public void comenzarCamara(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        f = null;
        //try {
        //f = createImageFile();
        f = getOutputMediaFile();
        //} catch (IOException e) {
        //  e.printStackTrace();
        //}
        mCurrentPhotoPath = f.getAbsolutePath();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

        startActivityForResult(takePictureIntent, CAM_REQUEST);
    }

    public void comenzarCamara2(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        f = null;
        //try {
        //f = createImageFile();
        f = getOutputMediaFile();
        //} catch (IOException e) {
        //  e.printStackTrace();
        //}
        mCurrentPhotoPath2 = f.getAbsolutePath();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

        startActivityForResult(takePictureIntent, CAM_REQUEST);
    }

    public void setMeal(){
        TextView tv = (TextView) findViewById(R.id.textViewMeal);
        switch(meal){
            case 1: tv.setText("DESAYUNO"); mealTag = "DES_";
                break;
            case 2: tv.setText("REFRIGERIO 1"); mealTag = "REF1_";
                break;
            case 3: tv.setText("COMIDA"); mealTag = "COM_";
                break;
            case 4: tv.setText("REFRIGERIO 2"); mealTag = "REF2_";
                break;
            case 5: tv.setText("CENA"); mealTag = "CEN_";
                break;
            case 6: tv.setText("REFRIGERIO 3"); mealTag = "REF3_";
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i("msj", mCurrentPhotoPath);

        if(requestCode == 5 && resultCode == 7){
            setResult(7, intent);
            finish();
        }

        //File f = new File(mCurrentPhotoPath);
        if(requestCode == CAM_REQUEST && resultCode == RESULT_OK){

            //Bitmap imagen = BitmapFactory.decodeFile(archivo.getAbsolutePath());
            //imagen = (Bitmap) data.getExtras().get("data");
            //img.setImageBitmap(imagen);
            //setPic();

            File imgFile = new  File(mCurrentPhotoPath);


           /* Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                }
            }, 10000);*/
            int cont =  0;
            while(!imgFile.exists()){
                //Toast.makeText(PhotoActivity.this, cont+ "", Toast.LENGTH_SHORT).show();
                cont++;
            }

                //Toast.makeText(PhotoActivity.this, "entró", Toast.LENGTH_SHORT).show();
                // Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                // ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);
                //setPic();

                Bitmap d = new BitmapDrawable(this.getResources() , imgFile.getAbsolutePath()).getBitmap();
                int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
                 d = null;
                //img.setImageBitmap(scaled);

                //img.setImageBitmap(myBitmap);

                try{
                    ExifInterface exif = new ExifInterface(imgFile.getPath());
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int rotationInDegrees = exifToDegrees(rotation);

                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
                    Bitmap adjustedBitmap = Bitmap.createBitmap(scaled, 0, 0, 512, nh, matrix, true);

                    img.setImageBitmap(adjustedBitmap);
                }catch (IOException e) {

                }




            if (!mCurrentPhotoPath2.equals("")){
                File imgFile2 = new  File(mCurrentPhotoPath2);

                cont =  0;
                while(!imgFile2.exists()){
                    //Toast.makeText(PhotoActivity.this, cont+ "", Toast.LENGTH_SHORT).show();
                    cont++;
                }
                if(imgFile2.exists()){
                    //Toast.makeText(PhotoActivity.this, "entró", Toast.LENGTH_SHORT).show();
                    // Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    // ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);
                    //setPic();

                    Bitmap d2 = new BitmapDrawable(this.getResources() , imgFile2.getAbsolutePath()).getBitmap();
                    int nh2 = (int) ( d2.getHeight() * (512.0 / d2.getWidth()) );
                    Bitmap scaled2 = Bitmap.createScaledBitmap(d2, 512, nh2, true);
                    d2 = null;
                    Bitmap combinado = combineImages(scaled,scaled2, imgFile2.getParent());

                    int nh3 = (int) ( combinado.getHeight() * (512.0 / combinado.getWidth()) );
                    Bitmap scaled3 = Bitmap.createScaledBitmap(combinado, 512, nh3, true);

                    img.setImageBitmap(combinado);
                    //img.setImageBitmap(myBitmap);

                }else{
                    //Toast.makeText(PhotoActivity.this, "No existe el archivo", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            finish();
        }



    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 0; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 0; }
        return 0;
    }

    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/NutriAlbum");

        if (!dir.exists()){
            dir.mkdir();
        }

        String timeStamp1 = new SimpleDateFormat("yyyy-MM-dd").format(fecha.getTime());
        folderDay = new File(dir.getAbsolutePath() + "/" + timeStamp1);

        if (!folderDay.exists()){
            folderDay.mkdir();
        }
        // Create a media file name
        Calendar fechaTemp = Calendar.getInstance();
        fechaTemp.set(Calendar.DAY_OF_MONTH, fecha.get(Calendar.DAY_OF_MONTH));
        fechaTemp.set(Calendar.MONTH, fecha.get(Calendar.MONTH));
        fechaTemp.set(Calendar.YEAR, fecha.get(Calendar.YEAR));
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(fechaTemp.getTime());
        File mediaFile = new File(folderDay.getPath() + File.separator +
                "IMG_"+ mealTag+ timeStamp + ".jpg");

        return mediaFile;
    }


    /*public File getDirectorio(){
        String state;
        state = Environment.getExternalStorageState();
        File dir = null;

        if (Environment.MEDIA_MOUNTED.equals(state)){
            File root = Environment.getExternalStorageDirectory();
            dir = new File(root.getAbsolutePath() + "/NutriApp");

            if (!dir.exists()){
                dir.mkdir();
            }

            *//*File file = new File(dir, "mensaje.txt");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write("Hola qué tal!".getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*//*

        }

        return dir;
    }*/

   /* private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG" + timeStamp + "_";
        File albumF = getDirectorio();
        File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
        mCurrentPhotoPath = imageF.getAbsolutePath();
        archivo = imageF;
        return imageF;
    }*/



    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = img.getWidth();
        int targetH = img.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        img.setImageBitmap(bitmap);
        //mVideoUri = null;
        // img.setVisibility(View.VISIBLE);
        //mVideoView.setVisibility(View.INVISIBLE);
    }



    public void eliminar(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirmación");
        builder.setMessage("¿Deseas eliminar esta imagen?");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                FileOutputStream out = null;
                //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                //String imageFileName = "IMG" + timeStamp + "_";
                // File albumF = getDirectorio();

                String fileName =  "/TXT_" + mealTag + timeStamp + ".txt";

                try{
                    archivo.delete();
                    Toast.makeText(PhotoActivity.this, "Se ha eliminado la imagen", Toast.LENGTH_SHORT).show();
                    String newName = archivo.getName().substring(3, archivo.getName().length()-4);
                    newName = "TXT"+newName + ".txt";
                    File file = new File(archivo.getParent() + File.separator + newName);
                    file.delete();
                    setResult(7, intent);
                    finish();
                }catch (Exception e){
                    Toast.makeText(PhotoActivity.this, "No se pudo eliminar la imagen", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }

    public void saveFile(View view){
        FileOutputStream out = null;
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "IMG" + timeStamp + "_";
        //File albumF = getDirectorio();

        String fileName =  "/TXT_" + mealTag + timeStamp + ".txt";




//        try {
//            out = new FileOutputStream(fileName);
//            out.write();
//            //imagen.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//            // PNG is a lossless format, the compression factor (100) is ignored
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        if (tieneImagen){
            try{
                //archivo.delete();
                String newName = archivo.getName().substring(3, archivo.getName().length()-4);
                newName = "TXT"+newName + ".txt";

                File file = new File(archivo.getParent() + File.separator + newName);

                file.delete();
                //Toast.makeText(PhotoActivity.this, "Se han guardado los cambios", Toast.LENGTH_SHORT).show();
                //setResult(1, intent);
              //  finish();

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(et.getText().toString().getBytes());
                    fileOutputStream.close();
                    Toast.makeText(PhotoActivity.this, "Se han guardado los cambios", Toast.LENGTH_SHORT).show();
                    setResult(7, intent);
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                Toast.makeText(PhotoActivity.this, "No se pudo guardar los cambios", Toast.LENGTH_SHORT).show();
            }

        }

            File file2 = new File(folderDay, fileName);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                fileOutputStream.write(et.getText().toString().getBytes());
                fileOutputStream.close();
                Toast.makeText(PhotoActivity.this, "Se han guardado los cambios", Toast.LENGTH_SHORT).show();
                setResult(7, intent);
                finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



    }

    public void cancelar(View view){
        try{
            f.delete();
        }catch(Exception e){

        }
        setResult(-1, intent);
        finish();
    }

    public void nuevaFoto(View view){
//        tieneImagen= false;
//        btnAceptar.setText("Aceptar");
//        btnAgregar.setEnabled(false);
//        comenzarCamara2();
        if(archivo2Nombre.equals("")){
            Calendar fecha2 = Calendar.getInstance();

            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("MEAL", meal);
            intent.putExtra("DIA", fecha.get(Calendar.DAY_OF_MONTH));
            intent.putExtra("MES", fecha.get(Calendar.MONTH));
            intent.putExtra("ANO", fecha.get(Calendar.YEAR));
            intent.putExtra("TIENE", false);
            intent.putExtra("ARCHIVO", "");
            intent.putExtra("ARCHIVO2", "");
            startActivityForResult(intent,5);
            //finish();
        }else{
            File arc = new File(archivo2Nombre);

            if(arc.exists()){
                String nombre = arc.getName();
            }
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("MEAL", meal);
           intent.putExtra("DIA", fecha.get(Calendar.DAY_OF_MONTH));
            intent.putExtra("MES", fecha.get(Calendar.MONTH));
            intent.putExtra("ANO", fecha.get(Calendar.YEAR));
            intent.putExtra("TIENE", true);
            intent.putExtra("ARCHIVO", archivo2Nombre);
            intent.putExtra("ARCHIVO2", "");
            startActivityForResult(intent,5);
            //finish();
        }

    }

    public Bitmap combineImages(Bitmap c, Bitmap s, String loc) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

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
