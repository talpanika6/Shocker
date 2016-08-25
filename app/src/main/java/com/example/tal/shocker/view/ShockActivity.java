package com.example.tal.shocker.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tal.shocker.R;
import com.example.tal.shocker.Utility.AutoFitTextureView;
import com.example.tal.shocker.model.PhotoShare;
import com.example.tal.shocker.model.UserPhoto;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.hardware.camera2.CameraCharacteristics.*;

public class ShockActivity extends AppCompatActivity {

    private Toolbar shockLayout;
    private PhotoShare photoShare;
    ProgressDialog dialog;
    private TextView timer,userName;
    private ImageView bigImage;
    private FloatingActionButton send;
    private Activity act=this;
    private ParseFile photoFile;
    UserPhoto photoShock;
    MediaPlayer mp;
    int fabId;
    String  objId;


    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * A {@link android.hardware.camera2.CaptureRequest.Builder} for camera preview.
     */
    private CaptureRequest.Builder mPreviewBuilder;

    /**
     * A {@link android.hardware.camera2.CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mPreviewSession;

    /**
     * A reference to the opened {@link android.hardware.camera2.CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * {@link android.view.TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link android.view.TextureView}.
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture,
                                              int width, int height) {
            configureTransform(width, height);
            startPreview();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture,
                                                int width, int height) {
            configureTransform(width, height);
            startPreview();
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

    };

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * True if the app is currently trying to open camera
     */
    private boolean mOpeningCamera;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private CameraDevice.StateCallback mStateListener = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraDevice = cameraDevice;
            startPreview();
            mOpeningCamera = false;
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
            mOpeningCamera = false;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = act;
            if (activity!=null) {
                //Toast.makeText(activity,"Error camera cant open",Toast.LENGTH_LONG).show();
                activity.finish();
            }
            mOpeningCamera = false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shock);

        //get  ObjectId from intent
        Intent intent=getIntent();
        objId=intent.getStringExtra("ShockId");

        //Toolbar
        shockLayout=(Toolbar)findViewById(R.id.shockLayout);
        setSupportActionBar(shockLayout);

        //timer text
        timer=(TextView)findViewById(R.id.timer);
        //fab
        send=(FloatingActionButton)findViewById(R.id.send_shock);
        fabId=R.id.send_shock;

        //send
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Done();
            }
        });

        bigImage=(ImageView)findViewById(R.id.shock_photo_big);

        //Toolbar
        ///set backlistener
        shockLayout.setTitle(getString(R.string.title_activity_shock));
        //   photoLayout.setTitleTextColor(R.color.white);
        shockLayout.setNavigationIcon(getDrawable(R.drawable.ic_arrow_back_white_18dp));
        shockLayout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //cameraview
        mTextureView = (AutoFitTextureView) findViewById(R.id.texture_shock);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        //sound
        mp = MediaPlayer.create(this, R.raw.button_9);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.reset();
                mp.release();
                mp = null;
            }

        });


        //new object for shock photo
         photoShock=new UserPhoto();


        //startQuery
        startQuery();

    }


    public void enterReveal(int id) {
        // previously invisible view
        final View myView = findViewById(id);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
    }

    public  void exitReveal(int id) {
        // previously visible view
        final View myView = findViewById(id);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }
    private void startQuery()
    {


        ParseQuery query = new ParseQuery("PhotoShare");
        query.getInBackground(objId, new GetCallback<PhotoShare>() {
            public void done(PhotoShare object, ParseException e) {
                if (e == null) {
                    photoShare = object;
                    settingView();
                } else {
                    Toast.makeText(getApplicationContext(), "no photo found....", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //put the view from the query
    public void settingView()
    {

        ParseUser user= null;
        UserPhoto photo=null;


        try {
            user = photoShare.getSender().fetchIfNeeded();
            photo=photoShare.getUserPhoto().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (photo!=null) {
            //set Title Image
            //  title.setText(photo.getTitle());

            try {
                user = photo.getAuthor().fetchIfNeeded();

            } catch (ParseException e) {
                e.printStackTrace();
            }

            exitReveal(fabId);
            //getting big photo
            ParseFile photoFile = photo.getParseFile("photo");
            if (photoFile != null) {

                Uri uri = Uri.parse(photoFile.getUrl());
                Context context = bigImage.getContext();
                Picasso.with(context)
                        .load(uri)
                        .error(R.drawable.ic_highlight_remove_black_24dp)
                        .fit()
                        .into(bigImage, new Callback() {

                            @Override
                            public void onSuccess() {
                                bigImage.setVisibility(View.VISIBLE);
                                startTimer();


                            }

                            @Override
                            public void onError() {

                                bigImage.setVisibility(View.INVISIBLE);
                            }
                        });
                /*
                bigImage.setParseFile(photoFile);
                bigImage.loadInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {

                         startTimer();
                    }
                });*/
            }
        }
        else
            Toast.makeText(this, "null", Toast.LENGTH_LONG).show();




    }


    @Override
    public void onResume()
    {
        super.onResume();
        openCamera();
      //  exitReveal(fabId);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
       // exitReveal(fabId);
    }



    /**
     * Opens a {@link CameraDevice}. The result is listened by `mStateListener`.
     */
    private void openCamera() {
        final Activity activity = act;
        if (null == activity || activity.isFinishing() || mOpeningCamera) {
            return;
        }
//        capture.setEnabled(true);
        mOpeningCamera = true;
        int index=0,i=0;
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {

                // To get a list of available sizes of camera preview, we retrieve an instance of
                // StreamConfigurationMap from CameraCharacteristics
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                //we want the front camera
                if (characteristics.get(LENS_FACING) == LENS_FACING_BACK)
                    index = i;
                i++;
            }

            String cameraId =manager.getCameraIdList()[index];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[index];

            //get front-back camera
            //   backCam=CameraCharacteristics.LENS_FACING_BACK;
            // frontCam=CameraCharacteristics.LENS_FACING_FRONT;

            // We fit the aspect ratio of TextureView to the size of preview we picked.
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            // We are opening the camera with a listener. When it is ready, onOpened of
            // mStateListener is called.

            manager.openCamera(cameraId, mStateListener, null);
        } catch (CameraAccessException e) {
            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            activity.finish();
        } catch (NullPointerException e) {
            // C

        }
    }

    /**
     * Starts the camera preview.
     */
    private void startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            // When the session is ready, we start displaying the preview.
                            mPreviewSession = cameraCaptureSession;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                            Activity activity = act;
                            if (null != activity) {
                                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            // The camera preview can be run in a background thread. This is a Handler for camera
            // preview.
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            Handler backgroundHandler = new Handler(thread.getLooper());

            // Finally, we start displaying the camera preview.
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fills in parameters of the {@link CaptureRequest.Builder}.
     *
     * @param builder The builder for a {@link CaptureRequest}
     */
    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        // In this sample, we just let the camera device pick the automatic settings.
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        //  builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.LENS_FACING_FRONT);
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in openCamera and
     * also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = act;
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }


    /**
     * Takes a picture.
     */
    private void takePicture() {
        try {
            final Activity activity =act;
            if (null == activity || null == mCameraDevice) {
                return;
            }
            CameraManager manager =
                    (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

            // Pick the best JPEG size that can be captured with this CameraDevice.
            CameraCharacteristics characteristics =
                    manager.getCameraCharacteristics(mCameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics
                        .get(SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            // We use an ImageReader to get a JPEG from CameraDevice.
            // Here, we create a new ImageReader and prepare its Surface as an output from camera.
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));

            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            setUpCaptureRequestBuilder(captureBuilder);

            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            String NameOfFolder = "/Shocker";
            String NameOfFile   = "image_";
            String CurrentDateAndTime= getCurrentDateAndTime();

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), NameOfFolder);

            if(!dir.exists()){
                dir.mkdirs();
            }


            final  File file = new File(dir, NameOfFile +CurrentDateAndTime+ ".jpg");


            // Output file
            //  final File file = new File(getActivity().getExternalFilesDir(null),NameOfFile +CurrentDateAndTime+ ".jpg");

            // This listener is called when a image is ready in ImageReader
            ImageReader.OnImageAvailableListener readerListener =
                    new ImageReader.OnImageAvailableListener() {
                        @Override
                        public void onImageAvailable(ImageReader reader) {
                            Image image = null;
                            try {
                                image = reader.acquireLatestImage();

                                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                                byte[] bytes = new byte[buffer.capacity()];
                                buffer.get(bytes);
                                save(bytes);
                                savePhotoToModel(bytes);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (image != null) {
                                    image.close();
                                }
                            }
                        }

                        private void save(byte[] bytes) throws IOException {
                            OutputStream output = null;
                            try {
                                output = new FileOutputStream(file);
                                output.write(bytes);
                            } finally {
                                if (null != output) {
                                    output.close();
                                }
                            }
                        }
                    };


            // We create a Handler since we want to handle the result JPEG in a background thread
            HandlerThread thread = new HandlerThread("CameraPicture");
            thread.start();
            final Handler backgroundHandler = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener, backgroundHandler);

            // This listener is called when the capture is completed.
            // Note that the JPEG data is not available in this listener, but in the
            // ImageReader.OnImageAvailableListener we created above.
            final CameraCaptureSession.CaptureCallback captureListener =
                    new CameraCaptureSession.CaptureCallback () {

                        @Override
                        public void onCaptureCompleted(CameraCaptureSession session,
                                                       CaptureRequest request,
                                                       TotalCaptureResult result) {
                            // Toast.makeText(activity, "Saved: " + file, Toast.LENGTH_SHORT).show();
                            String uri= Uri.fromFile(file).toString();
                             photoShock.setUri(uri);
                            // We restart the preview when the capture is completed
                            //  startPreview();

                        }

                    };



            // Finally, we can start a new CameraCaptureSession to take a picture.
            mCameraDevice.createCaptureSession(outputSurfaces,  new CameraCaptureSession.StateCallback()
                    {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            try {
                                session.capture(captureBuilder.build(), captureListener,
                                        backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                        }
                    }, backgroundHandler
            );

        } catch (CameraAccessException e) {
            Toast.makeText(this,"Error Taking Picture" , Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }


    }

    private void savePhotoToModel(byte[] data) {


        // Resize photo from camera byte array
        Bitmap userImage = BitmapFactory.decodeByteArray(data, 0, data.length);

        Bitmap userImageScaled = Bitmap.createScaledBitmap(userImage,480, 640, false);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        userImageScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        byte[] scaledData = bos.toByteArray();
        // Save the scaled image to Parse
        photoFile = new ParseFile("user_photo_shock.jpg", scaledData);
        photoShock.setPhotoFile(photoFile);

        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(act,
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    public void Done()
    {

        //save photo shock
        photoShock.setAuthor(ParseUser.getCurrentUser());
        photoShock.setIsSocked(true);
        photoShock.setTitle("photo shock");

        photoShock.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if(e==null){

                }else
                Toast.makeText(act ,"Error saving: PhotoShock" + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        ///update to true
        photoShare.setReadBy(true);
        photoShare.setUserPhotoShock(photoShock);

        // / Save the PhotoShare and return
        photoShare.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ///s

                } else {
                    Toast.makeText(act
                            ,
                            "Error saving: PhotoShare" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

        Toast.makeText(ShockActivity.this, "Sending...", Toast.LENGTH_LONG).show();
        startIntent();
    }

    private void startIntent()
    {
        // Start and intent for the Profile activity

        Intent intent = new Intent(ShockActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        exitReveal(fabId);
    }


    public void startTimer()
    {
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText(""+(millisUntilFinished / 1000));

            }

            public void onFinish() {
                timer.setText("");

                //sound on when enable it
              if(SettingsActivity.soundOn)
                   mp.start();

               enterReveal(fabId);
                takePicture();
            }
        }.start();
    }



    private String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        exitReveal(fabId);

    }

}
