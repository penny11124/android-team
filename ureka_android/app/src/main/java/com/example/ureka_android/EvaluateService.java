package com.example.ureka_android;

public class EvaluateService {
    private String name = "";
    private long startTime;
    private long endTime;
    private long durationInNano;

    private static long excludeStartTime;
    private static long excludeTime = 0;

    public EvaluateService() {

    }

    public EvaluateService(String name) {
        this.name = name;
        startEvaluate();
    }

    private void startEvaluate() {
        startTime = System.nanoTime();
    }

    public static void stopEvaluate() {
        excludeStartTime = System.nanoTime();
    }

    public static void restartEvaluate() {
        excludeTime += (System.nanoTime() - excludeStartTime);
    }

    private void endEvaluate() {
        endTime = System.nanoTime();
    }

    public double evaluateResult() {
        endEvaluate();
        durationInNano = endTime - startTime - excludeTime;
        double durationInSeconds = (double) durationInNano / 1_000_000_000.0;
        double excludeInSeconds = (double) excludeTime / 1_000_000_000.0;
        System.out.println("Function communication run time: " + excludeInSeconds + " seconds");
        System.out.println("Function " + name + " run time: " + durationInSeconds + " seconds");
        excludeTime = 0;
//        System.out.println("Function " + name + " run time: " + durationInSeconds + " seconds");
        return durationInSeconds;
    }
}
