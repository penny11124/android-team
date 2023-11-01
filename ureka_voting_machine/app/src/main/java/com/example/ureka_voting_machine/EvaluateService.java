package com.example.ureka_voting_machine;

public class EvaluateService {
    private static String name = "device";
    private static long startTime;
    private static long endTime;
    private static long durationInNano;

    private static long excludeStartTime;
    private static long excludeTime = 0;

    public EvaluateService() {

    }

    public static void startEvaluate() {
        startTime = System.nanoTime();
    }

    public static void stopEvaluate() {
        excludeStartTime = System.nanoTime();
    }

    public static void restartEvaluate() {
        excludeTime += (System.nanoTime() - excludeStartTime);
        excludeStartTime = 0;
    }

    private static void endEvaluate() {
        endTime = System.nanoTime();
    }

    public static double evaluateResult() {
        endEvaluate();
        durationInNano = endTime - startTime - excludeTime;
        double durationInSeconds = (double) durationInNano / 1_000_000_000.0;
        double excludeInSeconds = (double) excludeTime / 1_000_000_000.0;
        System.out.println("Function " + name + " run time: " + durationInSeconds + " seconds");
        System.out.println("Function " + name + " start time: " + startTime/ 1_000_000_000.0 + " seconds");
        System.out.println("Function " + name + " end time: " + endTime/ 1_000_000_000.0 + " seconds");
        System.out.println("Function " + name + " exclude time: " + excludeInSeconds + " seconds");
        name = "";
        startTime = System.nanoTime();
        endTime = 0;
        durationInNano = 0;
        excludeStartTime = System.nanoTime();
        excludeTime = 0;
//        System.out.println("Function " + name + " run time: " + durationInSeconds + " seconds");
        return durationInSeconds;
    }
}

