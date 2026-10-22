/**
 * CSA09 - Java Assignment 1
 * Task 10: Packages & Encapsulation
 * Demonstrates:
 * 1. Strict data encapsulation using private fields and validated accessors.
 * 2. Immutability using the `final` keyword.
 * 3. Defensive copying to protect internal slot array references from external tampering.
 * 4. Precondition validation preventing corrupt system state.
 */

// Model Component: Encapsulated Ticket
class EncapsulatedTicket {
    private final int ticketNumber;       // Immutable
    private final String vehiclePlate;     // Immutable
    private final long entryTimestamp;     // Immutable
    private double billedFee;              // Mutable only via validated business methods
    private boolean isPaid;

    public EncapsulatedTicket(int ticketNumber, String vehiclePlate) {
        if (ticketNumber <= 0) {
            throw new IllegalArgumentException("Ticket number must be strictly positive.");
        }
        if (vehiclePlate == null || vehiclePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle plate cannot be null or empty.");
        }
        this.ticketNumber = ticketNumber;
        this.vehiclePlate = vehiclePlate.trim().toUpperCase();
        this.entryTimestamp = System.currentTimeMillis();
        this.billedFee = 0.0;
        this.isPaid = false;
    }

    public int getTicketNumber() { return ticketNumber; }
    public String getVehiclePlate() { return vehiclePlate; }
    public long getEntryTimestamp() { return entryTimestamp; }
    public double getBilledFee() { return billedFee; }
    public boolean isPaid() { return isPaid; }

    // Controlled mutation with business invariant checks
    public void finalizePayment(double fee) {
        if (fee < 0.0) {
            throw new IllegalArgumentException("Fee cannot be negative: ₹" + fee);
        }
        if (this.isPaid) {
            throw new IllegalStateException("Ticket #" + ticketNumber + " is already paid!");
        }
        this.billedFee = fee;
        this.isPaid = true;
    }

    @Override
    public String toString() {
        return "Ticket #" + ticketNumber + " [" + vehiclePlate + "] Fee: ₹" + billedFee + " (Paid: " + isPaid + ")";
    }
}

// Service Component: Encapsulated Facility with Defensive Copying
class SecureParkingFacility {
    private final String[][] slots; // Strictly private internal state

    public SecureParkingFacility(int[] slotsPerFloor) {
        this.slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            this.slots[f] = new String[slotsPerFloor[f]];
        }
    }

    public boolean parkVehicle(int floor, int slot, String vehicleNo) {
        if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) {
            return false;
        }
        if (slots[floor][slot] != null) {
            return false; // Already occupied
        }
        slots[floor][slot] = vehicleNo;
        return true;
    }

    // VULNERABLE APPROACH (Demonstration purpose only):
    // Exposing reference directly allows callers to bypass facility validation
    public String[][] getSlotsDirectReferenceUnsafe() {
        return slots;
    }

    // DEFENSIVE APPROACH (Secure):
    // Returns a deep clone so external modifications cannot corrupt internal state
    public String[][] getSlotsSnapshotSecure() {
        String[][] snapshot = new String[slots.length][];
        for (int f = 0; f < slots.length; f++) {
            snapshot[f] = java.util.Arrays.copyOf(slots[f], slots[f].length);
        }
        return snapshot;
    }

    public String getSlotOccupant(int floor, int slot) {
        if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) {
            return "OUT_OF_BOUNDS";
        }
        return slots[floor][slot] == null ? "EMPTY" : slots[floor][slot];
    }
}

public class EncapsulationDemo {
    public static void main(String[] args) {
        System.out.println("=== Task 10: Packages & Encapsulation Demonstration ===");

        // 1. Data Invariant Protection
        System.out.println("\n--- 1. Testing Validation Guards in EncapsulatedTicket ---");
        EncapsulatedTicket t1 = new EncapsulatedTicket(1001, "TN01AB1234");
        System.out.println("Created: " + t1);

        try {
            System.out.println("Attempting to pass negative fee (-50.0)...");
            t1.finalizePayment(-50.0);
        } catch (IllegalArgumentException ex) {
            System.out.println("Caught Expected Security Exception: " + ex.getMessage());
        }

        t1.finalizePayment(40.0);
        System.out.println("Valid Payment Finalized: " + t1);

        try {
            System.out.println("Attempting to finalize payment twice on same ticket...");
            t1.finalizePayment(20.0);
        } catch (IllegalStateException ex) {
            System.out.println("Caught Expected Invariant Exception: " + ex.getMessage());
        }

        // 2. Defensive Copying Protection
        System.out.println("\n--- 2. Testing Defensive Copying in SecureParkingFacility ---");
        SecureParkingFacility facility = new SecureParkingFacility(new int[]{2, 2});
        facility.parkVehicle(0, 0, "TN01AA1111");
        facility.parkVehicle(0, 1, "TN01BB2222");

        System.out.println("Internal State Before Snapshot: Floor 0, Slot 0 = " + facility.getSlotOccupant(0, 0));

        // Caller acquires defensive snapshot
        String[][] snapshot = facility.getSlotsSnapshotSecure();
        System.out.println("Acquired defensive snapshot. Malicious caller attempts to clear snapshot[0][0] = null...");
        snapshot[0][0] = null; // Mutating snapshot

        // Checking internal facility state
        System.out.println("Internal Facility State After Tampering Attempt: Floor 0, Slot 0 = " 
                + facility.getSlotOccupant(0, 0));
        System.out.println("Verdict: Internal facility state remains intact! Defensive copying successfully defended.");
    }
}
