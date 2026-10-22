/**
 * CSA09 - Java Assignment 1
 * Task 11: Implementation, Testing & Defence
 * Bloom's Level: K5 - Evaluate
 * Complete, integrated Campus Smart-Parking System with comprehensive test suite.
 */

import java.util.Arrays;

// 1. VEHICLE HIERARCHY
abstract class Vehicle {
    protected final String plateNumber;
    protected final String brand;
    protected final String userCategory; // "STUDENT", "STAFF", "VISITOR"
    protected final boolean hasMonthlyPass;

    public Vehicle(String plateNumber, String brand, String userCategory, boolean hasMonthlyPass) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.userCategory = userCategory;
        this.hasMonthlyPass = hasMonthlyPass;
    }

    public String getPlateNumber() { return plateNumber; }
    public String getBrand() { return brand; }
    public String getUserCategory() { return userCategory; }
    public boolean hasMonthlyPass() { return hasMonthlyPass; }

    public abstract double getBaseHourlyRate();
    public abstract boolean requiresChargingBay();
    public abstract String getSlotCategoryNeeded();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + plateNumber + ", " + userCategory 
                + ", Pass=" + hasMonthlyPass + "]";
    }
}

class TwoWheeler extends Vehicle {
    public TwoWheeler(String plateNumber, String brand, String userCategory, boolean hasMonthlyPass) {
        super(plateNumber, brand, userCategory, hasMonthlyPass);
    }

    @Override
    public double getBaseHourlyRate() { return 10.0; }

    @Override
    public boolean requiresChargingBay() { return false; }

    @Override
    public String getSlotCategoryNeeded() { return "STANDARD"; }
}

class Car extends Vehicle {
    public Car(String plateNumber, String brand, String userCategory, boolean hasMonthlyPass) {
        super(plateNumber, brand, userCategory, hasMonthlyPass);
    }

    @Override
    public double getBaseHourlyRate() { return 20.0; }

    @Override
    public boolean requiresChargingBay() { return false; }

    @Override
    public String getSlotCategoryNeeded() { return "STANDARD"; }
}

class ElectricCar extends Car {
    private final boolean chargingRequested;
    private static final double CHARGING_RATE_PER_HOUR = 15.0;

    public ElectricCar(String plateNumber, String brand, String userCategory, boolean hasMonthlyPass, boolean chargingRequested) {
        super(plateNumber, brand, userCategory, hasMonthlyPass);
        this.chargingRequested = chargingRequested;
    }

    @Override
    public boolean requiresChargingBay() { return chargingRequested; }

    @Override
    public String getSlotCategoryNeeded() {
        return chargingRequested ? "EV_CHARGING" : "STANDARD";
    }

    public double getChargingRatePerHour() { return CHARGING_RATE_PER_HOUR; }
}

// 2. PARKING TICKET
class ParkingTicket {
    private static int sequenceCounter = 1000;

    private final int ticketId;
    private final String vehiclePlate;
    private final String vehicleType;
    private final int floor;
    private final int slot;
    private final long entryTimeMillis;

    public ParkingTicket(String vehiclePlate, String vehicleType, int floor, int slot, long entryTimeMillis) {
        this.ticketId = ++sequenceCounter;
        this.vehiclePlate = vehiclePlate;
        this.vehicleType = vehicleType;
        this.floor = floor;
        this.slot = slot;
        this.entryTimeMillis = entryTimeMillis;
    }

    public int getTicketId() { return ticketId; }
    public String getVehiclePlate() { return vehiclePlate; }
    public String getVehicleType() { return vehicleType; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public long getEntryTimeMillis() { return entryTimeMillis; }

    @Override
    public String toString() {
        return "Ticket #" + ticketId + " [" + vehiclePlate + " @ Floor " + floor + ", Slot " + slot + "]";
    }
}

// 3. EXIT RECEIPT
class ParkingReceipt {
    private final int ticketId;
    private final String vehiclePlate;
    private final String userCategory;
    private final long durationMinutes;
    private final long billableHours;
    private final double baseParkingFee;
    private final double chargingSurcharge;
    private final double discountApplied;
    private final double finalFee;

    public ParkingReceipt(int ticketId, String vehiclePlate, String userCategory, long durationMinutes,
                          long billableHours, double baseParkingFee, double chargingSurcharge, 
                          double discountApplied, double finalFee) {
        this.ticketId = ticketId;
        this.vehiclePlate = vehiclePlate;
        this.userCategory = userCategory;
        this.durationMinutes = durationMinutes;
        this.billableHours = billableHours;
        this.baseParkingFee = baseParkingFee;
        this.chargingSurcharge = chargingSurcharge;
        this.discountApplied = discountApplied;
        this.finalFee = finalFee;
    }

    public double getFinalFee() { return finalFee; }
    public long getBillableHours() { return billableHours; }

    public void printReceipt() {
        System.out.println("--------------------------------------------------");
        System.out.println("           CAMPUS SMART-PARKING RECEIPT           ");
        System.out.println("--------------------------------------------------");
        System.out.println("Ticket ID        : #" + ticketId);
        System.out.println("Vehicle Plate    : " + vehiclePlate);
        System.out.println("User Category    : " + userCategory);
        System.out.println("Parked Duration  : " + durationMinutes + " mins (" + billableHours + " billable hrs)");
        System.out.println("Base Parking Fee : ₹" + baseParkingFee);
        if (chargingSurcharge > 0) {
            System.out.println("EV Charging Fee  : ₹" + chargingSurcharge);
        }
        if (discountApplied > 0) {
            System.out.println("Monthly Pass Disc: -₹" + discountApplied + " (FREE PARKING)");
        }
        System.out.println("TOTAL DUE        : ₹" + finalFee);
        System.out.println("--------------------------------------------------");
    }
}

// 4. PARKING SLOT
class ParkingSlot {
    private final int floor;
    private final int slotIndex;
    private final boolean isChargingBay;
    private Vehicle currentVehicle;
    private ParkingTicket activeTicket;

    public ParkingSlot(int floor, int slotIndex, boolean isChargingBay) {
        this.floor = floor;
        this.slotIndex = slotIndex;
        this.isChargingBay = isChargingBay;
        this.currentVehicle = null;
        this.activeTicket = null;
    }

    public boolean isFree() { return currentVehicle == null; }
    public boolean isChargingBay() { return isChargingBay; }

    public boolean isCompatibleWith(Vehicle v) {
        if (!isFree()) return false;
        if (v.requiresChargingBay()) {
            return this.isChargingBay; // EV requiring charging MUST have charging slot
        }
        // Non-charging vehicles avoid blocking charging bays if possible, but standard slots are preferred
        return !this.isChargingBay;
    }

    public void park(Vehicle v, ParkingTicket ticket) {
        this.currentVehicle = v;
        this.activeTicket = ticket;
    }

    public void clear() {
        this.currentVehicle = null;
        this.activeTicket = null;
    }

    public Vehicle getCurrentVehicle() { return currentVehicle; }
    public ParkingTicket getActiveTicket() { return activeTicket; }
    public int getFloor() { return floor; }
    public int getSlotIndex() { return slotIndex; }
}

// 5. SMART PARKING FACILITY
class SmartParkingFacility {
    private final ParkingSlot[][] slots;

    public SmartParkingFacility(int[] slotsPerFloor, boolean[][] chargingSlotsGrid) {
        this.slots = new ParkingSlot[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            this.slots[f] = new ParkingSlot[slotsPerFloor[f]];
            for (int s = 0; s < slotsPerFloor[f]; s++) {
                boolean isCharging = (chargingSlotsGrid != null && f < chargingSlotsGrid.length 
                        && s < chargingSlotsGrid[f].length) && chargingSlotsGrid[f][s];
                this.slots[f][s] = new ParkingSlot(f, s, isCharging);
            }
        }
    }

    /** Allocates the first suitable free slot and issues a unique ticket */
    public ParkingTicket enterFacility(Vehicle v, long entryTimeMillis) {
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s].isCompatibleWith(v)) {
                    ParkingTicket ticket = new ParkingTicket(v.getPlateNumber(), v.getClass().getSimpleName(), f, s, entryTimeMillis);
                    slots[f][s].park(v, ticket);
                    return ticket;
                }
            }
        }
        return null; // Facility full or no compatible slot
    }

    /** Releases slot, calculates duration and fee, and generates receipt */
    public ParkingReceipt exitFacility(int floor, int slot, long exitTimeMillis) {
        if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) {
            return null; // Invalid position
        }
        ParkingSlot targetSlot = slots[floor][slot];
        if (targetSlot.isFree()) {
            return null; // Already free
        }

        Vehicle v = targetSlot.getCurrentVehicle();
        ParkingTicket ticket = targetSlot.getActiveTicket();

        // Calculate duration and billable hours
        long durationMillis = exitTimeMillis - ticket.getEntryTimeMillis();
        long durationMinutes = Math.max(1, durationMillis / (60 * 1000));
        long billableHours = (long) Math.ceil((double) durationMinutes / 60.0);

        // Compute base fee
        double baseRate = v.getBaseHourlyRate();
        double baseParkingFee = billableHours * baseRate;

        // Compute EV charging surcharge if applicable
        double chargingSurcharge = 0.0;
        if (v instanceof ElectricCar ec && ec.requiresChargingBay()) {
            chargingSurcharge = billableHours * ec.getChargingRatePerHour();
        }

        double grossTotal = baseParkingFee + chargingSurcharge;
        double discountApplied = 0.0;
        double finalFee = grossTotal;

        // Monthly pass validation: Staff and Students park free of charge
        if (v.hasMonthlyPass()) {
            discountApplied = baseParkingFee;
            finalFee = chargingSurcharge; // Charging electricity consumed may still apply or be free based on campus policy
        }

        // Vacate slot
        targetSlot.clear();

        return new ParkingReceipt(ticket.getTicketId(), v.getPlateNumber(), v.getUserCategory(), 
                durationMinutes, billableHours, baseParkingFee, chargingSurcharge, discountApplied, finalFee);
    }
}

// 6. MAIN VERIFICATION & AUTOMATED TEST HARNESS
public class SmartParkingSystem {
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   CAMPUS SMART-PARKING SYSTEM - VERIFICATION & DEFENCE SUITE   ");
        System.out.println("=================================================================");

        // Configure campus facility:
        // Floor 0: 2 slots (Slot 0 = Standard, Slot 1 = EV Charging Bay)
        // Floor 1: 1 slot  (Slot 0 = Standard)
        int[] capacity = {2, 1};
        boolean[][] chargingConfig = {
            {false, true}, // Floor 0: Slot 1 has EV charger
            {false}        // Floor 1: Standard slot only
        };

        SmartParkingFacility facility = new SmartParkingFacility(capacity, chargingConfig);
        long now = System.currentTimeMillis();

        int passed = 0;
        int total = 8;

        System.out.println("\n--- RUNNING AUTOMATED TEST CASES ---\n");

        // TEST 1: Normal Allocation of Visitor Car
        Vehicle visitorCar = new Car("TN01AA1111", "Honda", "VISITOR", false);
        ParkingTicket t1 = facility.enterFacility(visitorCar, now);
        boolean tc1 = (t1 != null && t1.getFloor() == 0 && t1.getSlot() == 0);
        System.out.println("Test 1 (Visitor Car Slot [0,0]): " + (tc1 ? "PASSED" : "FAILED") + " -> " + t1);
        if (tc1) passed++;

        // TEST 2: Dedicated EV Charging Bay Allocation
        Vehicle visitorEV = new ElectricCar("TN01EV2222", "Tata", "VISITOR", false, true);
        ParkingTicket t2 = facility.enterFacility(visitorEV, now);
        boolean tc2 = (t2 != null && t2.getFloor() == 0 && t2.getSlot() == 1);
        System.out.println("Test 2 (EV in Charging Bay [0,1]): " + (tc2 ? "PASSED" : "FAILED") + " -> " + t2);
        if (tc2) passed++;

        // TEST 3: Allocation to Upper Floor
        Vehicle studentCar = new Car("TN02ST3333", "Maruti", "STUDENT", true);
        ParkingTicket t3 = facility.enterFacility(studentCar, now);
        boolean tc3 = (t3 != null && t3.getFloor() == 1 && t3.getSlot() == 0);
        System.out.println("Test 3 (Student Car Slot [1,0]): " + (tc3 ? "PASSED" : "FAILED") + " -> " + t3);
        if (tc3) passed++;

        // TEST 4: Full Facility Rejection
        Vehicle excessCar = new Car("TN03XX4444", "Toyota", "VISITOR", false);
        ParkingTicket t4 = facility.enterFacility(excessCar, now);
        boolean tc4 = (t4 == null);
        System.out.println("Test 4 (Full Facility Rejection -> null): " + (tc4 ? "PASSED" : "FAILED"));
        if (tc4) passed++;

        // TEST 5: Student Monthly Pass Zero-Fee Checkout
        // Student parked for 4 hours (4 * 3600 * 1000 ms)
        ParkingReceipt rStudent = facility.exitFacility(1, 0, now + (4 * 3600 * 1000));
        boolean tc5 = (rStudent != null && rStudent.getFinalFee() == 0.0 && rStudent.getBillableHours() == 4);
        System.out.println("Test 5 (Monthly Pass Free Parking ₹0.0): " + (tc5 ? "PASSED" : "FAILED"));
        if (tc5) {
            passed++;
            rStudent.printReceipt();
        }

        // TEST 6: Visitor EV Charging Billing
        // EV parked for 3 hours (3 * 20 base + 3 * 15 charging = ₹105)
        ParkingReceipt rEV = facility.exitFacility(0, 1, now + (3 * 3600 * 1000));
        boolean tc6 = (rEV != null && rEV.getFinalFee() == 105.0 && rEV.getBillableHours() == 3);
        System.out.println("Test 6 (Visitor EV Billing 3 hrs = ₹105.0): " + (tc6 ? "PASSED" : "FAILED"));
        if (tc6) {
            passed++;
            rEV.printReceipt();
        }

        // TEST 7: Invalid & Duplicate Slot Release
        ParkingReceipt rInvalidFloor = facility.exitFacility(5, 0, now + 1000);
        ParkingReceipt rAlreadyEmpty = facility.exitFacility(0, 1, now + 1000);
        boolean tc7 = (rInvalidFloor == null && rAlreadyEmpty == null);
        System.out.println("Test 7 (Invalid & Duplicate Release Rejection): " + (tc7 ? "PASSED" : "FAILED"));
        if (tc7) passed++;

        // TEST 8: Re-allocation of Released Slot (Floor 1, Slot 0 was freed in Test 5)
        ParkingTicket tReallocated = facility.enterFacility(excessCar, now);
        boolean tc8 = (tReallocated != null && tReallocated.getFloor() == 1 && tReallocated.getSlot() == 0);
        System.out.println("Test 8 (Re-allocation of Released Slot [1,0]): " + (tc8 ? "PASSED" : "FAILED") + " -> " + tReallocated);
        if (tc8) passed++;

        System.out.println("\n=================================================================");
        System.out.println("TEST SUITE SUMMARY: " + passed + " / " + total + " TESTS PASSED (" 
                + (passed * 100 / total) + "%)");
        System.out.println("=================================================================");
    }
}
