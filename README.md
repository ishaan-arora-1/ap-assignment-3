# Fleet Highway Simulator - Race Condition Demo

This Java application demonstrates a multi-threaded race condition using a fleet of vehicles traveling on a shared highway.

## Files Created

1. **Highway.java** - Shared resource with an intentional race condition
2. **Vehicle.java** - Thread logic for each vehicle
3. **FleetSimulatorGUI.java** - Swing-based GUI interface
4. **FleetSimulatorMain.java** - Application entry point

## How to Run

### Compile:
```bash
javac *.java 
```

### Run:
```bash
java FleetSimulatorMain
```

## How to Use

1. Click **"Start Simulation"** to begin the simulation
2. Watch as vehicles run and consume fuel
3. Click **"Pause"** to pause the simulation
4. Click **"Stop"** to stop all threads completely
5. Click **"Refuel All (+10)"** when vehicles run out of fuel

## Observing the Race Condition

When you run the application, you'll notice:
- Each vehicle tracks its own mileage
- The shared highway distance counter is updated by all vehicles
- **The red ERROR message** appears when the shared counter doesn't match the sum of individual mileages

This discrepancy demonstrates the **race condition** caused by unsynchronized access to the shared `highwayDistance` variable.

## How to Fix the Race Condition

To fix the race condition, ive made the `addDistance` method in `Highway.java` synchronized:

```java
// Changed from:
public static void addDistance(int distance) {

// To:
public static synchronized void addDistance(int distance) {
```

This ensures only one thread can update the shared counter at a time, eliminating the race condition.

## Features

- **3 vehicles** running in separate threads
- **Real-time GUI updates** showing vehicle status, fuel, and mileage
- **Shared counter** demonstrating race condition
- **Visual alert** (red header) when race condition is detected
- **Fuel management** with refueling capability
- **Thread control** via Start/Pause/Stop buttons

## GUI Thread-Safety Considerations

Java Swing is not thread-safe. To ensure the application remains stable:

1. **Event Dispatch Thread (EDT):** The GUI is initialized using `SwingUtilities.invokeLater`, ensuring that the UI components are created on the EDT.

2. **Safe Updates:** The vehicle threads do not update the UI directly. Instead, a `javax.swing.Timer` is used to poll the vehicle states and update the labels. This ensures that all UI modifications happen on the EDT, preventing `ConcurrentModificationException` or visual artifacts.

3. **Volatile Variables:** The `Vehicle` class uses a `volatile` status field to ensure visibility across threads when the GUI timer reads the vehicle state.
