package com.example.tal.shocker.fragment;

import android.app.Activity;
import android.content.Context;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tal.shocker.R;
import com.example.tal.shocker.Utility.AutoFitTextureView;
import com.example.tal.shocker.view.SettingsActivity;
import com.example.tal.shocker.view.UserPhotoActivity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

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

import static android.hardware.camera2.CameraCharacteristics.LENS_FACING;
import static android.hardware.camera2.CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_BACK;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT;


public class CameraFragment extends Fragment  {

	public static final String TAG = "CameraFragment";


	   private ParseFile photoFile;
	   private ImageButton photoButton;
       int facing=LENS_FACING_BACK;
       private  int backCam;
       private  int frontCam ;
         MediaPlayer mp;
       private ImageButton capture;

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
        Activity activity = getActivity();
        if (null != activity) {
            Toast.makeText(activity,"Error camera cant open",Toast.LENGTH_LONG).show();
            activity.finish();
        }
            mOpeningCamera = false;
   }

   };


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,	Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_camera, parent, false);

        mTextureView = (AutoFitTextureView) v.findViewById(R.id.texture);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        //sound
        mp = MediaPlayer.create(getActivity(), R.raw.button_6);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.reset();
                mp.release();
                mp = null;
            }

        });

        capture =(ImageButton) v.findViewById(R.id.picture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sound on when enable it
                if(SettingsActivity.soundOn)
                    mp.start();
             
                takePicture();
            }
        });




        v.findViewById(R.id.btn_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (facing==LENS_FACING_BACK)
                    facing=LENS_FACING_FRONT;
                 else
                    facing=LENS_FACING_BACK;

                openCamera(facing);
            }
        });
        v.findViewById(R.id.btn_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Clicked flash",Toast.LENGTH_LONG).show();
            }
        });

		return v;
	}


    /**
     * Takes a picture.
     */
    private void takePicture() {
        try {
            final Activity activity = getActivity();
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
                        .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
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
                            ((UserPhotoActivity) getActivity()).getCurrentUserPhoto().setUri(uri);
                            // We restart the preview when the capture is completed
                            //  startPreview();
                            addPhotoToUserPhotoAndReturn();
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
            Toast.makeText(getActivity(),"Error Taking Picture" , Toast.LENGTH_SHORT).show();
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
        photoFile = new ParseFile("user_photo.jpg", scaledData);
        ((UserPhotoActivity) getActivity()).getCurrentUserPhoto().setPhotoFile(
                photoFile);

        photoFile.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }

	/*
	 * Once the photo has saved successfully, we're ready to go to the
	 * PhotoInfoFragment. When we added the CameraFragment to the back stack, we
	 * named it "NewMealFragment". Now we'll pop fragments off the back stack
	 * until we reach that Fragment.
	 */
	private void addPhotoToUserPhotoAndReturn() {

	      //replace fragment and go to photo Info
         Fragment photoInfo = new PhotoInfoFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragmentContainer, photoInfo);
        transaction.addToBackStack("CameraFragment");
        transaction.commit();
	}





      @Override
        public void onResume()
    {
        super.onResume();
        openCamera(facing);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }



        /**
         * Opens a {@link CameraDevice}. The result is listened by `mStateListener`.
         */
        private void openCamera(int facing_camera) {
            final Activity activity = getActivity();
            if (null == activity || activity.isFinishing() || mOpeningCamera) {
                return;
            }
            capture.setEnabled(true);
            mOpeningCamera = true;
            int index=0,i=0;
            CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            try {
                for (String cameraId : manager.getCameraIdList()) {

                    // To get a list of available sizes of camera preview, we retrieve an instance of
                    // StreamConfigurationMap from CameraCharacteristics
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    //we want the front camera
                    if (characteristics.get(LENS_FACING) == facing_camera)
                         index = i;

                    i++;
                }

                String cameraId =manager.getCameraIdList()[index];
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[index];

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
                                Activity activity = getActivity();
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
            Activity activity = getActivity();
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
         * Switch between flash modes (ON/AUTO/OFF)
         * @param
         * @return 	void
         * @author 	ajuarez@tanukiteam.com
         */
        private void setFlashMode(){
        /*
        Camera.Parameters params = mCamera.getParameters();
        ImageButton flashButton=(ImageButton)findViewById(R.id.btn_flash);

        switch(fmi){
            case 0: //IF Flash AUTO
            {
                fmi = 1; //Flash ON
                flashButton.setImageResource(R.drawable.flash);
                break;
            }
            case 1: //IF Flash ON
            {
                fmi = 2; //Flash OFF
                flashButton.setImageResource(R.drawable.flash_disabled);
                break;
            }
            case 2: //IF Flash OFF
            {
                fmi = 0; //Flash AUTO
                flashButton.setImageResource(R.drawable.flash_auto);
                break;
            }
            default:
            {
                fmi = 0; //Flash AUTO
                flashButton.setImageResource(R.drawable.flash_auto);
                break;
            }
        }

        //Set the new parameters to the camera:
        mCamera.setParameters(params);
        */

        }


        /**
         * Switch camera (back/front)
         * @param
         * @return 	void
         * @author 	ajuarez@tanukiteam.com
         */
        private void switchCamera(){
        /*
        mCamera.stopPreview();
        mCamera.release();

        if(currentCamID == backCam){
            currentCamID = frontCam;
            flashButton.setVisibility(View.INVISIBLE);
        }
        else {
            currentCamID = backCam;
            flashButton.setVisibility(View.VISIBLE);
        }

        mCamera = Camera.open(currentCamID);

        setCameraDisplayOrientation(CameraAPIActivity.this, currentCamID, mCamera);

        try {
            mCamera.setPreviewDisplay(mPreview.getSurfaceHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
        */
        }



        private String getCurrentDateAndTime() {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String formattedDate = df.format(c.getTime());
            return formattedDate;
        }



}
