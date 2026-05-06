package com.example.comp3000;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import java.util.concurrent.Executors;
import com.google.mlkit.vision.common.InputImage;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;


public class PuzzleObjectDetector {
    @androidx.camera.core.ExperimentalGetImage
    public static void start(PuzzlesActivity activity) {
        com.google.common.util.concurrent.ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(activity);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                PreviewView previewView = activity.findViewById(R.id.cameraPreview);
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), imageProxy -> {
                    InputImage inputImage = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                    ObjectDetector detector = ObjectDetection.getClient(ObjectDetectorOptions.DEFAULT_OPTIONS);
                    detector.process(inputImage).addOnSuccessListener(detectedObjects -> {
                        if (!detectedObjects.isEmpty()) {
                            SharedPreferences prefs = activity.getSharedPreferences("puzzle", Context.MODE_PRIVATE);
                            prefs.edit().putBoolean("puzzleCompleted", true).apply();
                        }
                    });
                    imageProxy.close();
                });

                cameraProvider.bindToLifecycle(activity, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(activity));
    }
}