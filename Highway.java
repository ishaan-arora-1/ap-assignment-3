// File: Highway.java

public class Highway {

    // Shared variable as per Assignment Requirement R2 and Step 1
    // "A shared integer variable... shall be incremented by each vehicle thread" [cite: 54, 55, 56]
    public static int highwayDistance = 0; 

    // ADD 'synchronized' to fix the Race Condition
    public static synchronized void addDistance(int distance) {
        // We read the value, wait a tiny bit (to force race conditions), then write it back.
        int current = highwayDistance;
        try {
            // A 10ms delay increases the chance threads overlap here
            Thread.sleep(10); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        highwayDistance = current + distance;
    }

    // Helper to reset for new runs
    public static void reset() {
        highwayDistance = 0;
    }
}

