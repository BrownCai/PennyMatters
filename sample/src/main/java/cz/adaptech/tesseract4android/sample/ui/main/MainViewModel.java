package cz.adaptech.tesseract4android.sample.ui.main;

import android.app.Application;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.util.Locale;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private final TessBaseAPI tessApi;

    private final MutableLiveData<Boolean> processing = new MutableLiveData<>(false);

    private final MutableLiveData<String> progress = new MutableLiveData<>();

    private final MutableLiveData<String> result = new MutableLiveData<>();

    private boolean tessInit;

    private volatile boolean stopped;

    private volatile boolean tessProcessing;

    private volatile boolean recycleAfterProcessing;

    private final Object recycleLock = new Object();

    public MainViewModel(@NonNull Application application) {
        super(application);

        tessApi = new TessBaseAPI(progressValues -> {
            progress.postValue("Progress: " + progressValues.getPercent() + " %");
        });

        // Show Tesseract version and library flavor at startup
        progress.setValue(String.format(Locale.ENGLISH, "Tesseract %s (%s)",
                tessApi.getVersion(), tessApi.getLibraryFlavor()));
    }

    @Override
    protected void onCleared() {
        synchronized (recycleLock) {
            if (tessProcessing) {
                recycleAfterProcessing = true;
                tessApi.stop();
            } else {
                tessApi.recycle();
            }
        }
    }

    public void initTesseract(@NonNull String dataPath, @NonNull String language, int engineMode) {
        Log.i(TAG, "Initializing Tesseract with: dataPath = [" + dataPath + "], " +
                "language = [" + language + "], engineMode = [" + engineMode + "]");
        try {
            tessInit = tessApi.init(dataPath, language, engineMode);
        } catch (IllegalArgumentException e) {
            tessInit = false;
            Log.e(TAG, "Cannot initialize Tesseract:", e);
        }
    }

    public void recognizeImage(@NonNull File imagePath) {
        if (!tessInit) {
            Log.e(TAG, "recognizeImage: Tesseract is not initialized");
            return;
        }
        if (tessProcessing) {
            Log.e(TAG, "recognizeImage: Processing is in progress");
            return;
        }
        tessProcessing = true;

        result.setValue("");
        processing.setValue(true);
        progress.setValue("Processing...");
        stopped = false;


        new Thread(() -> {
            tessApi.setImage(imagePath);
            long startTime = SystemClock.uptimeMillis();

            tessApi.getHOCRText(0);

            String text = tessApi.getUTF8Text();


            tessApi.clear();


            result.postValue(text);
            processing.postValue(false);
            if (stopped) {
                progress.postValue("Stopped.");
            } else {
                long duration = SystemClock.uptimeMillis() - startTime;
                progress.postValue(String.format(Locale.ENGLISH,
                        "Completed in %.3fs.", (duration / 1000f)));
            }

            synchronized (recycleLock) {
                tessProcessing = false;

                // Recycle the instance here if the view model is already destroyed
                if (recycleAfterProcessing) {
                    tessApi.recycle();
                }
            }
        }).start();
    }

    public void stop() {
        if (!tessProcessing) {
            return;
        }
        progress.setValue("Stopping...");
        stopped = true;
        tessApi.stop();
    }

    public boolean isInitialized() {
        return tessInit;
    }

    @NonNull
    public LiveData<Boolean> getProcessing() {
        return processing;
    }

    @NonNull
    public LiveData<String> getProgress() {
        return progress;
    }

    @NonNull
    public LiveData<String> getResult() {
        return result;
    }
}