package com.example.comp3000;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import java.util.concurrent.Executors;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;

public class PuzzleObjectDetector {
    @androidx.camera.core.ExperimentalGetImage
    public static void start(PuzzlesActivity activity) {
        com.google.common.util.concurrent.ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(activity);

        SharedPreferences puzzleObject = activity.getSharedPreferences("puzzle", Context.MODE_PRIVATE);
        String targetObject = puzzleObject.getString("targetObject", "table");

        TextView targetObjectView = activity.findViewById(R.id.targetObjectView);
        targetObjectView.setText("Find a: " + targetObject);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                PreviewView previewView = activity.findViewById(R.id.cameraPreview);
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), imageProxy -> {
                    Log.d("ObjectDetection", "Frame received");
                    InputImage inputImage = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                    ObjectDetector detector = ObjectDetection.getClient(ObjectDetectorOptions.DEFAULT_OPTIONS);
                    detector.process(inputImage).addOnSuccessListener(detectedObjects -> {
                        Log.d("ObjectDetection", "Objects detected: " + detectedObjects.size());
                        Log.d("ObjectDetection", "isEmpty check: " + detectedObjects.isEmpty());

                        try {
                            if (!detectedObjects.isEmpty()) {
                                Log.d("ObjectDetection", "INSIDE IF BLOCK");
                                boolean foundTargetObject = false;
                                for (com.google.mlkit.vision.objects.DetectedObject obj : detectedObjects) {
                                    if (obj.getLabels().isEmpty()) continue;
                                    String label = obj.getLabels().get(0).getText();
                                    Log.d("ObjectDetection", "Detected: " + label);
                                    if (label.equals(targetObject)) {
                                        foundTargetObject = true;
                                        break;
                                    }
                                }

                                if (foundTargetObject) {
                                    SharedPreferences prefs = activity.getSharedPreferences("puzzle", Context.MODE_PRIVATE);
                                    puzzleObject.edit().putBoolean("puzzleCompleted", true).apply();
                                    Log.d("ObjectDetection", "puzzleCompleted set to true");

                                    EditText answerInput = activity.findViewById(R.id.answerInput);
                                    answerInput.setText("144");

                                    Log.d("ObjectDetection", "About to call completePuzzle, activity is: " + activity);
                                    try {
                                        activity.completePuzzle();
                                        Log.d("ObjectDetection", "completePuzzle() called successfully");
                                    } catch (Exception completePuzzleException) {
                                        Log.e("ObjectDetection", "Error calling completePuzzle: " + completePuzzleException.getMessage());
                                        completePuzzleException.printStackTrace();
                                    }
                                }

                                puzzleObject.edit().putBoolean("puzzleCompleted", true).apply();
                                Log.d("ObjectDetection", "puzzleCompleted set to true");

                                //this is a proof of concept to show that yes, once an object is
                                // detected the value gets set to 144 and the alarms stops
                                EditText answerInput = activity.findViewById(R.id.answerInput);
                                answerInput.setText("144");

                                Log.d("ObjectDetection", "About to call completePuzzle, activity is: " + activity);
                                try {
                                    activity.completePuzzle();
                                    Log.d("ObjectDetection", "completePuzzle() called successfully");
                                } catch (Exception completePuzzleException) {
                                    Log.e("ObjectDetection", "Error calling completePuzzle: " + completePuzzleException.getMessage());
                                    completePuzzleException.printStackTrace();
                                }

                            }
                        } catch (Exception e) {
                            Log.e("ObjectDetection", "Exception in if block: " + e.getMessage());
                            e.printStackTrace();
                        }

                        imageProxy.close();
                    }).addOnFailureListener(e -> {
                        Log.e("ObjectDetection", "Detection failed: " + e.getMessage());
                        imageProxy.close();
                    });
                });

                    cameraProvider.bindToLifecycle(activity, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(activity));
    }
}