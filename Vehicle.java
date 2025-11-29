// File: Vehicle.java

public class Vehicle implements Runnable {
    private String id;
    private int mileage;
    private int fuel;
    private boolean running; // Controls the thread loop
    private boolean paused;  // Controls the simulation pause
    
    // Status string for the GUI to display [cite: 43]
    private volatile String status; 

    public Vehicle(String id, int startFuel) {
        this.id = id;
        this.fuel = startFuel;
        this.mileage = 0;
        this.running = true;
        this.paused = true; // Start paused, wait for GUI 'Start'
        this.status = "Stopped";
    }

    @Override
    public void run() {
        while (running) {
            if (!paused) {
                if (fuel > 0) {
                    try {
                        // Requirement R1: Update state approximately once per second [cite: 22, 46]
                        Thread.sleep(1000); 
                        // Update local state
                        mileage += 1; // 1 km per second [cite: 46]
                        fuel -= 1;    // Decrease fuel [cite: 47]
                        status = "Running";
                        // Requirement R2: Update shared counter [cite: 48]
                        Highway.addDistance(1); 
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        status = "Interrupted";
                    }
                } else {
                    // Fuel is 0, pause execution until refueled
                    status = "Out-of-Fuel"; // [cite: 49]
                    try {
                        Thread.sleep(100); // Idle wait
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // If paused manually by GUI
                if(fuel > 0) status = "Paused";
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // Update status when thread exits
        this.status = "Stopped";
    }

    // --- Control Methods for GUI ---
    public void start() { this.paused = false; }
    public void pause() { this.paused = true; }
    public void stop() { this.running = false; }
    
    public void refuel(int amount) { 
        this.fuel += amount; 
        // Auto-resume if it was stuck on Out-of-Fuel [cite: 67]
        if(this.status.equals("Out-of-Fuel")) {
            this.status = "Paused"; 
        }
    }

    // --- Getters for GUI Display ---
    public String getId() { return id; }
    public int getMileage() { return mileage; }
    public int getFuel() { return fuel; }
    public String getStatus() { return status; }
}

