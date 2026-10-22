/**
 * CSA09 - Java Assignment 1
 * Task 5: Apply static Members and the Object Life Cycle
 * Demonstrates:
 * 1. static variable for unique monotonic ticket ID sequence.
 * 2. static occupancy counter and static utility methods.
 * 3. static initialization block.
 * 4. Object Life Cycle & Garbage Collection eligibility.
 * 5. Java Pass-by-Value reference parameter semantics.
 */

class SmartTicket {
    // 1. Static sequence counter shared by all instances
    private static int ticketSequenceCounter;
    private static int totalActiveOccupancy;

    // 2. Static Initialization Block: Executes once when class is loaded
    static {
        ticketSequenceCounter = 1000;
        totalActiveOccupancy = 0;
        System.out.println("[STATIC BLOCK] SmartTicket class initialized. Starting ticket sequence at 1000.");
    }

    // Instance fields
    private final int ticketId;
    private final String vehicleNo;
    private final int floor;
    private final int slot;
    private final long entryTime;
    private double fee;

    public SmartTicket(String vehicleNo, int floor, int slot) {
        this.ticketId = ++ticketSequenceCounter; // Atomic generation of unique ticket ID
        this.vehicleNo = vehicleNo;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = System.currentTimeMillis();
        this.fee = 0.0;
        totalActiveOccupancy++;
    }

    public void release() {
        if (totalActiveOccupancy > 0) {
            totalActiveOccupancy--;
        }
    }

    // 3. Static Methods
    public static int getTotalTicketsIssued() {
        return ticketSequenceCounter - 1000;
    }

    public static int getTotalActiveOccupancy() {
        return totalActiveOccupancy;
    }

    public static boolean validateVehiclePlate(String plate) {
        return plate != null && plate.matches("^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$");
    }

    // Getters and Setters
    public int getTicketId() { return ticketId; }
    public String getVehicleNo() { return vehicleNo; }
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }

    @Override
    public String toString() {
        return "Ticket #" + ticketId + " [" + vehicleNo + " @ (" + floor + "," + slot + ")] Fee: ₹" + fee;
    }
}

public class StaticLifecycleDemo {

    // Demonstrates reference mutation vs reference reassignment
    public static void applyPassDiscount(SmartTicket t) {
        // Mutating object state through the reference
        t.setFee(0.0);
    }

    public static void attemptReassignReference(SmartTicket t) {
        // Reassigning local stack parameter has NO effect on caller's reference
        t = new SmartTicket("TN99ZZ9999", 9, 9);
    }

    public static void main(String[] args) {
        System.out.println("=== Task 5: static Members & Object Life Cycle Demo ===");

        // Static utility test
        System.out.println("Plate 'TN01AB1111' valid? " + SmartTicket.validateVehiclePlate("TN01AB1111"));
        System.out.println("Plate 'INVALID_123' valid? " + SmartTicket.validateVehiclePlate("INVALID_123"));

        // Creating tickets - each automatically receives a unique monotonic ID from static counter
        SmartTicket t1 = new SmartTicket("TN01AB1111", 0, 0);
        SmartTicket t2 = new SmartTicket("TN02CD2222", 0, 1);
        SmartTicket t3 = new SmartTicket("TN03EF3333", 1, 0);

        System.out.println("\n--- Issued Tickets ---");
        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t3);

        System.out.println("\nStatic Total Tickets Issued: " + SmartTicket.getTotalTicketsIssued());
        System.out.println("Static Total Occupancy     : " + SmartTicket.getTotalActiveOccupancy());

        // 4. Object Life Cycle & Garbage Collection Eligibility
        System.out.println("\n--- Object Life Cycle & GC Eligibility ---");
        System.out.println("t3 before departure: " + t3);
        t3.release(); // Decrements active occupancy
        t3 = null;    // Reference severed! The heap object is now unreachable and eligible for GC.
        System.out.println("t3 reference set to null. Object is now unreachable and eligible for GC.");
        System.out.println("Remaining Active Occupancy: " + SmartTicket.getTotalActiveOccupancy());

        // Request JVM GC run (non-blocking hint)
        System.gc();

        // 5. Java Pass-by-Value Reference Semantics Demo
        System.out.println("\n--- Pass-by-Value Reference Semantics ---");
        t1.setFee(50.0);
        System.out.println("t1 fee before applyPassDiscount: ₹" + t1.getFee());
        applyPassDiscount(t1);
        System.out.println("t1 fee after applyPassDiscount : ₹" + t1.getFee() + " (Mutated successfully via reference)");

        System.out.println("\nt2 vehicle before attemptReassignReference: " + t2.getVehicleNo());
        attemptReassignReference(t2);
        System.out.println("t2 vehicle after attemptReassignReference : " + t2.getVehicleNo() + " (Unaffected by reassignment!)");
    }
}
