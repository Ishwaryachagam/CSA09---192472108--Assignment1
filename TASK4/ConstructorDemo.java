/**
 * CSA09 - Java Assignment 1
 * Task 4: Design Classes, Objects and Constructors
 * Demonstrates:
 * 1. Default, parameterized, and overloaded constructors.
 * 2. Constructor chaining using the `this` keyword.
 * 3. Copy constructor for issuing duplicate tickets without mutating original state.
 * 4. Memory layout and object validity guarantees.
 */

class Vehicle {
    private String regNo;
    private String type;
    private String ownerCategory;

    // Default constructor
    public Vehicle() {
        this("UNKNOWN", "Car", "Visitor"); // Constructor chaining
    }

    // Overloaded constructor (2 parameters)
    public Vehicle(String regNo, String type) {
        this(regNo, type, "Visitor"); // Constructor chaining
    }

    // Canonical parameterized constructor (3 parameters)
    public Vehicle(String regNo, String type, String ownerCategory) {
        this.regNo = regNo;
        this.type = type;
        this.ownerCategory = ownerCategory;
    }

    public String getRegNo() { return regNo; }
    public String getType() { return type; }
    public String getOwnerCategory() { return ownerCategory; }

    public void display() {
        System.out.println("Vehicle [Reg: " + regNo + ", Type: " + type + ", Category: " + ownerCategory + "]");
    }
}

class ParkingSlot {
    private int floor;
    private int slotNumber;
    private boolean isChargingSlot;
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    // Default constructor
    public ParkingSlot() {
        this(0, 0, false);
    }

    // Overloaded constructor (standard slot)
    public ParkingSlot(int floor, int slotNumber) {
        this(floor, slotNumber, false);
    }

    // Canonical parameterized constructor
    public ParkingSlot(int floor, int slotNumber, boolean isChargingSlot) {
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.isChargingSlot = isChargingSlot;
        this.isOccupied = false;
        this.parkedVehicle = null;
    }

    public boolean assignVehicle(Vehicle v) {
        if (!isOccupied) {
            this.parkedVehicle = v;
            this.isOccupied = true;
            return true;
        }
        return false;
    }

    public void vacate() {
        this.parkedVehicle = null;
        this.isOccupied = false;
    }

    public boolean isAvailable() { return !isOccupied; }
    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public boolean isChargingSlot() { return isChargingSlot; }
}

class ParkingTicket {
    private int ticketId;
    private String vehicleNo;
    private int floor;
    private int slot;
    private long entryTime;
    private long exitTime;
    private double fee;
    private boolean isPaid;

    // Default constructor
    public ParkingTicket() {
        this(0, "UNKNOWN", -1, -1);
    }

    // Overloaded constructor (entry registration defaults to System.currentTimeMillis)
    public ParkingTicket(int ticketId, String vehicleNo, int floor, int slot) {
        this(ticketId, vehicleNo, floor, slot, System.currentTimeMillis());
    }

    // Canonical parameterized constructor
    public ParkingTicket(int ticketId, String vehicleNo, int floor, int slot, long entryTime) {
        this.ticketId = ticketId;
        this.vehicleNo = vehicleNo;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = entryTime;
        this.exitTime = 0;
        this.fee = 0.0;
        this.isPaid = false;
    }

    // Copy constructor: Used for issuing duplicate receipts / audit archives
    public ParkingTicket(ParkingTicket original) {
        this.ticketId = original.ticketId;
        this.vehicleNo = original.vehicleNo;
        this.floor = original.floor;
        this.slot = original.slot;
        this.entryTime = original.entryTime;
        this.exitTime = original.exitTime;
        this.fee = original.fee;
        this.isPaid = original.isPaid;
    }

    public void markExit(long exitTime, double fee) {
        this.exitTime = exitTime;
        this.fee = fee;
        this.isPaid = true;
    }

    public void displayTicket(String label) {
        System.out.println("--- " + label + " ---");
        System.out.println("Ticket ID    : #" + ticketId);
        System.out.println("Vehicle No   : " + vehicleNo);
        System.out.println("Slot Location: Floor " + floor + ", Slot " + slot);
        System.out.println("Entry Time   : " + entryTime);
        System.out.println("Exit Time    : " + (exitTime == 0 ? "STILL PARKED" : exitTime));
        System.out.println("Fee Charged  : ₹" + fee + " (Paid: " + isPaid + ")");
    }
}

public class ConstructorDemo {
    public static void main(String[] args) {
        System.out.println("=== Task 4: Classes, Objects & Constructors Demo ===");

        // 1. Vehicle objects created using overloaded constructors
        Vehicle vDefault = new Vehicle();
        Vehicle vStudent = new Vehicle("TN01AB1111", "Car", "Student");
        vDefault.display();
        vStudent.display();

        // 2. ParkingSlot objects with constructor chaining
        ParkingSlot standardSlot = new ParkingSlot(1, 5);
        ParkingSlot evSlot = new ParkingSlot(0, 1, true);
        System.out.println("\nStandard Slot: Floor " + standardSlot.getFloor() + ", Slot " + standardSlot.getSlotNumber() 
                + " (Charging: " + standardSlot.isChargingSlot() + ")");
        System.out.println("EV Slot      : Floor " + evSlot.getFloor() + ", Slot " + evSlot.getSlotNumber() 
                + " (Charging: " + evSlot.isChargingSlot() + ")");

        // 3. ParkingTicket created with constructor chaining
        ParkingTicket originalTicket = new ParkingTicket(1001, "TN01AB1111", 0, 1);
        originalTicket.markExit(System.currentTimeMillis() + 7200000, 40.0);
        originalTicket.displayTicket("Original Ticket Stored in System");

        // 4. Duplicate Ticket issued using Copy Constructor
        ParkingTicket duplicateTicket = new ParkingTicket(originalTicket);
        duplicateTicket.displayTicket("Duplicate Copy Issued to Customer");

        // Verifying that modifying duplicate does not affect original (independent heap instances)
        System.out.println("\n--- Memory Reference Equality Verification ---");
        System.out.println("originalTicket == duplicateTicket: " + (originalTicket == duplicateTicket) 
                + " (Confirmed: Distinct heap objects!)");
    }
}
