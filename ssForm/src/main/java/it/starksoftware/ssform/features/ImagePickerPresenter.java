package it.starksoftware.ssform.features;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import it.starksoftware.ssform.R;
import it.starksoftware.ssform.features.camera.CameraModule;
import it.starksoftware.ssform.features.camera.DefaultCameraModule;
import it.starksoftware.ssform.features.camera.OnImageReadyListener;
import it.starksoftware.ssform.features.common.BasePresenter;
import it.starksoftware.ssform.features.common.ImageLoaderListener;
import it.starksoftware.ssform.model.Folder;
import it.starksoftware.ssform.model.Image;

import java.io.File;
import java.util.List;

public class ImagePickerPresenter extends BasePresenter<ImagePickerView> {

    private ImageLoader imageLoader;
    private CameraModule cameraModule = new DefaultCameraModule();
    private Handler handler = new Handler(Looper.getMainLooper());

    ImagePickerPresenter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    void abortLoad() {
        imageLoader.abortLoadImages();
    }

    void loadImages(boolean isFolderMode) {
        if (!isViewAttached()) return;

        getView().showLoading(true);
        imageLoader.loadDeviceImages(isFolderMode, new ImageLoaderListener() {
            @Override
            public void onImageLoaded(final List<Image> images, final List<Folder> folders) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewAttached()) {
                            getView().showFetchCompleted(images, folders);

                            if (folders != null) {
                                if (folders.isEmpty()) {
                                    getView().showEmpty();
                                } else {
                                    getView().showLoading(false);
                                }
                            } else {
                                if (images.isEmpty()) {
                                    getView().showEmpty();
                                } else {
                                    getView().showLoading(false);
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailed(final Throwable throwable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewAttached()) {
                            getView().showError(throwable);
                        }
                    }
                });
            }
        });
    }

    void onDoneSelectImages(List<Image> selectedImages) {
        if (selectedImages != null && selectedImages.size() > 0) {

            for (int i = 0; i < selectedImages.size(); i++) {
                Image image = selectedImages.get(i);
                File file = new File(image.getPath());
                if (!file.exists()) {
                    selectedImages.remove(i);
                    i--;
                }
            }
            getView().finishPickImages(selectedImages);
        }
    }

    void captureImage(Activity activity, ImagePickerConfig config, int requestCode) {
        Context context = activity.getApplicationContext();
        Intent intent = cameraModule.getCameraIntent(activity, config);
        if (intent == null) {
            Toast.makeText(context, context.getString(R.string.ef_error_create_image_file), Toast.LENGTH_LONG).show();
            return;
        }
        activity.startActivityForResult(intent, requestCode);
    }

    void finishCaptureImage(Context context, Intent data, final ImagePickerConfig config) {
        cameraModule.getImage(context, data, new OnImageReadyListener() {
            @Override
            public void onImageReady(List<Image> images) {
                if (config.isReturnAfterFirst()) {
                    getView().finishPickImages(images);
                } else {
                    getView().showCapturedImage();
                }
            }
        });
    }
}
