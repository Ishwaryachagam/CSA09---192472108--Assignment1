/**
 * CSA09 - Java Assignment 1
 * Task 12: Day-4 Design Challenge
 * Bloom's Level: K5 - Evaluate
 * 
 * Demonstrates:
 * 1. Open-Closed Principle (OCP) via Strategy Pattern for Pricing Rules.
 * 2. Seamless addition of new vehicle categories (Bus, Ambulance, Bicycle) without modifying legacy classes.
 * 3. Dynamic pricing rules (One-Day Flat Pass, Emergency Free Waiver, Standard Hourly).
 * 4. Multi-level jagged floor array management.
 * 5. Automated test suite validating zero regression and full extensibility.
 */

import java.util.Arrays;

// ============================================================================
// PART 1: LEGACY VEHICLE ABSTRACTION (UNTOUCHED & FULLY PRESERVED)
// ============================================================================
abstract class Vehicle {
    protected final String plateNumber;
    protected final String brand;

    public Vehicle(String plateNumber, String brand) {
        this.plateNumber = plateNumber;
        this.brand = brand;
    }

    public String getPlateNumber() { return plateNumber; }
    public String getBrand() { return brand; }

    public abstract double getBaseHourlyRate();
    public abstract boolean requiresChargingBay();
    public abstract String getVehicleCategoryName();

    @Override
    public String toString() {
        return getVehicleCategoryName() + " [" + plateNumber + " - " + brand + "]";
    }
}

// Legacy Subclasses (100% Unchanged)
class TwoWheeler extends Vehicle {
    public TwoWheeler(String plateNumber, String brand) { super(plateNumber, brand); }
    @Override public double getBaseHourlyRate() { return 10.0; }
    @Override public boolean requiresChargingBay() { return false; }
    @Override public String getVehicleCategoryName() { return "TwoWheeler"; }
}

class Car extends Vehicle {
    public Car(String plateNumber, String brand) { super(plateNumber, brand); }
    @Override public double getBaseHourlyRate() { return 20.0; }
    @Override public boolean requiresChargingBay() { return false; }
    @Override public String getVehicleCategoryName() { return "Car"; }
}

class ElectricCar extends Car {
    private final boolean chargingRequested;
    private static final double CHARGING_RATE_PER_HOUR = 15.0;

    public ElectricCar(String plateNumber, String brand, boolean chargingRequested) {
        super(plateNumber, brand);
        this.chargingRequested = chargingRequested;
    }

    @Override public boolean requiresChargingBay() { return chargingRequested; }
    @Override public String getVehicleCategoryName() { return "ElectricCar"; }
    public double getChargingRatePerHour() { return CHARGING_RATE_PER_HOUR; }
}

// ============================================================================
// PART 2: NEW VEHICLE CATEGORIES (ADDED WITHOUT MODIFYING LEGACY CODE)
// ============================================================================

// New Category 1: Heavy Campus Bus / Shuttle
class Bus extends Vehicle {
    private final int passengerCapacity;

    public Bus(String plateNumber, String brand, int passengerCapacity) {
        super(plateNumber, brand);
        this.passengerCapacity = passengerCapacity;
    }

    @Override public double getBaseHourlyRate() { return 50.0; } // Heavy vehicle commercial rate
    @Override public boolean requiresChargingBay() { return false; }
    @Override public String getVehicleCategoryName() { return "HeavyBus"; }
    public int getPassengerCapacity() { return passengerCapacity; }
}

// New Category 2: Emergency Ambulance
class Ambulance extends Vehicle {
    private final String hospitalAffiliation;

    public Ambulance(String plateNumber, String brand, String hospitalAffiliation) {
        super(plateNumber, brand);
        this.hospitalAffiliation = hospitalAffiliation;
    }

    @Override public double getBaseHourlyRate() { return 0.0; } // Emergency services are non-billable
    @Override public boolean requiresChargingBay() { return false; }
    @Override public String getVehicleCategoryName() { return "EmergencyAmbulance"; }
    public String getHospitalAffiliation() { return hospitalAffiliation; }
}

// New Category 3: Campus Bicycle / Green Transport
class Bicycle extends Vehicle {
    public Bicycle(String plateNumber, String brand) {
        super(plateNumber, brand);
    }

    @Override public double getBaseHourlyRate() { return 2.0; } // Nominal parking rate
    @Override public boolean requiresChargingBay() { return false; }
    @Override public String getVehicleCategoryName() { return "CampusBicycle"; }
}

// ============================================================================
// PART 3: PRICING RULE ABSTRACTION (STRATEGY PATTERN)
// ============================================================================

interface PricingRule {
    double calculateFee(Vehicle vehicle, long durationMinutes);
    String getRuleName();
}

// Strategy 1: Standard Hourly Tariff
class StandardHourlyRule implements PricingRule {
    @Override
    public double calculateFee(Vehicle vehicle, long durationMinutes) {
        long billableHours = (long) Math.ceil((double) durationMinutes / 60.0);
        double baseFee = billableHours * vehicle.getBaseHourlyRate();
        double chargingFee = 0.0;
        if (vehicle instanceof ElectricCar ec && ec.requiresChargingBay()) {
            chargingFee = billableHours * ec.getChargingRatePerHour();
        }
        return baseFee + chargingFee;
    }

    @Override public String getRuleName() { return "Standard Hourly Tariff"; }
}

// Strategy 2: One-Day Flat Pass (New Pricing Rule requested by university!)
class OneDayFlatPassRule implements PricingRule {
    private static final double FLAT_RATE = 50.0; // Flat ₹50 for entire day

    @Override
    public double calculateFee(Vehicle vehicle, long durationMinutes) {
        long billableHours = (long) Math.ceil((double) durationMinutes / 60.0);
        double chargingFee = 0.0;
        // If EV used charging bay, electricity fee is still charged at hourly rate
        if (vehicle instanceof ElectricCar ec && ec.requiresChargingBay()) {
            chargingFee = billableHours * ec.getChargingRatePerHour();
        }
        return FLAT_RATE + chargingFee;
    }

    @Override public String getRuleName() { return "One-Day Flat Pass (₹50 Flat)"; }
}

// Strategy 3: Emergency Responder Waiver
class EmergencyFreeRule implements PricingRule {
    @Override
    public double calculateFee(Vehicle vehicle, long durationMinutes) {
        return 0.0; // Always 100% free of charge
    }

    @Override public String getRuleName() { return "Emergency Responder Waiver (₹0 Free)"; }
}

// ============================================================================
// PART 4: ADAPTIVE FACILITY (JAGGED ARRAYS + STRATEGY INTEGRATION)
// ============================================================================

class ParkingTicket {
    private static int counter = 5000;
    private final int ticketId;
    private final String plateNumber;
    private final int floor;
    private final int slot;
    private final long entryTimeMillis;

    public ParkingTicket(String plateNumber, int floor, int slot, long entryTimeMillis) {
        this.ticketId = ++counter;
        this.plateNumber = plateNumber;
        this.floor = floor;
        this.slot = slot;
        this.entryTimeMillis = entryTimeMillis;
    }

    public int getTicketId() { return ticketId; }
    public String getPlateNumber() { return plateNumber; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public long getEntryTimeMillis() { return entryTimeMillis; }
}

class AdaptiveFacility {
    private final Vehicle[][] slots; // Jagged array storing vehicles directly

    public AdaptiveFacility(int[] slotsPerFloor) {
        this.slots = new Vehicle[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            this.slots[f] = new Vehicle[slotsPerFloor[f]];
        }
    }

    public ParkingTicket allocate(Vehicle v, long entryTimeMillis) {
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = v;
                    return new ParkingTicket(v.getPlateNumber(), f, s, entryTimeMillis);
                }
            }
        }
        return null;
    }

    public double releaseAndComputeFee(int floor, int slot, long durationMinutes, PricingRule pricingRule) {
        if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) {
            return -1.0; // Invalid slot
        }
        Vehicle v = slots[floor][slot];
        if (v == null) {
            return -1.0; // Already free
        }

        // Apply decoupled pricing strategy
        double fee = pricingRule.calculateFee(v, durationMinutes);
        slots[floor][slot] = null; // Free slot
        return fee;
    }

    public int getFloorCapacity(int floor) {
        return (floor >= 0 && floor < slots.length) ? slots[floor].length : 0;
    }
}

// ============================================================================
// PART 5: VERIFICATION TEST SUITE
// ============================================================================

public class DesignChallengeDemo {
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   TASK 12: DAY-4 DESIGN CHALLENGE - EVALUATION & DEFENCE DEMO   ");
        System.out.println("=================================================================");

        // Configure a heterogeneous facility with jagged floors:
        // Floor 0: 2 slots (VIP / Emergency / EV)
        // Floor 1: 3 slots (Cars / Buses)
        // Floor 2: 5 slots (Bicycles / Mixed)
        int[] floorLayout = {2, 3, 5};
        AdaptiveFacility facility = new AdaptiveFacility(floorLayout);

        System.out.println("Configured Heterogeneous Multi-Level Facility:");
        for (int f = 0; f < floorLayout.length; f++) {
            System.out.println("  Floor " + f + ": " + facility.getFloorCapacity(f) + " slots");
        }

        PricingRule standardHourly = new StandardHourlyRule();
        PricingRule oneDayFlatPass = new OneDayFlatPassRule();
        PricingRule emergencyWaiver = new EmergencyFreeRule();

        long now = System.currentTimeMillis();
        int passed = 0;
        int totalTests = 5;

        System.out.println("\n--- EXECUTING DESIGN CHALLENGE TEST CASES ---\n");

        // TEST C1: Legacy Car with Standard Hourly Rule
        // Car parked for 3 hours (180 mins) @ ₹20/hr = ₹60.0
        Vehicle legacyCar = new Car("TN01AA1111", "Honda");
        ParkingTicket tCar = facility.allocate(legacyCar, now);
        double feeCar = facility.releaseAndComputeFee(tCar.getFloor(), tCar.getSlot(), 180, standardHourly);
        boolean tc1 = (feeCar == 60.0);
        System.out.println("Test C1 [Legacy Car + Standard Hourly Tariff]: " + (tc1 ? "PASSED" : "FAILED") 
                + " -> Fee: ₹" + feeCar);
        if (tc1) passed++;

        // TEST C2: Visitor Car with NEW One-Day Flat Pass Rule!
        // Car parked for 8 hours (480 mins). Under standard = 8x20 = ₹160. Under One-Day Flat Pass = ₹50!
        ParkingTicket tFlatCar = facility.allocate(legacyCar, now);
        double feeFlatCar = facility.releaseAndComputeFee(tFlatCar.getFloor(), tFlatCar.getSlot(), 480, oneDayFlatPass);
        boolean tc2 = (feeFlatCar == 50.0);
        System.out.println("Test C2 [Visitor Car + NEW One-Day Flat Pass]: " + (tc2 ? "PASSED" : "FAILED") 
                + " -> Fee: ₹" + feeFlatCar + " (Savings: ₹110 vs Hourly)");
        if (tc2) passed++;

        // TEST C3: NEW Vehicle Category - Heavy Campus Bus
        // Bus parked for 4 hours (240 mins) @ ₹50/hr = ₹200.0
        Vehicle campusBus = new Bus("TN01BS9999", "Ashok Leyland", 60);
        ParkingTicket tBus = facility.allocate(campusBus, now);
        double feeBus = facility.releaseAndComputeFee(tBus.getFloor(), tBus.getSlot(), 240, standardHourly);
        boolean tc3 = (feeBus == 200.0);
        System.out.println("Test C3 [NEW Category: Campus Bus + Standard Tariff]: " + (tc3 ? "PASSED" : "FAILED") 
                + " -> Fee: ₹" + feeBus);
        if (tc3) passed++;

        // TEST C4: NEW Vehicle Category - Emergency Ambulance
        // Ambulance parked for 5 hours (300 mins). Billed under Emergency Waiver = ₹0.0
        Vehicle ambulance = new Ambulance("TN01AM1080", "Force Motors", "SIMATS Medical Centre");
        ParkingTicket tAmb = facility.allocate(ambulance, now);
        double feeAmb = facility.releaseAndComputeFee(tAmb.getFloor(), tAmb.getSlot(), 300, emergencyWaiver);
        boolean tc4 = (feeAmb == 0.0);
        System.out.println("Test C4 [NEW Category: Ambulance + Emergency Free Rule]: " + (tc4 ? "PASSED" : "FAILED") 
                + " -> Fee: ₹" + feeAmb);
        if (tc4) passed++;

        // TEST C5: Electric Car with NEW One-Day Flat Pass + Charging Surcharge
        // EV parked for 4 hours (240 mins) with active charging.
        // Flat base fee: ₹50.0 + (4 hrs * ₹15.0 charging) = ₹110.0
        Vehicle evCar = new ElectricCar("TN01EV7777", "Tata Tiago EV", true);
        ParkingTicket tEV = facility.allocate(evCar, now);
        double feeEV = facility.releaseAndComputeFee(tEV.getFloor(), tEV.getSlot(), 240, oneDayFlatPass);
        boolean tc5 = (feeEV == 110.0);
        System.out.println("Test C5 [EV Car + One-Day Flat Pass + Charging Surcharge]: " + (tc5 ? "PASSED" : "FAILED") 
                + " -> Fee: ₹" + feeEV);
        if (tc5) passed++;

        System.out.println("\n=================================================================");
        System.out.println("DESIGN CHALLENGE TEST SUMMARY: " + passed + " / " + totalTests + " TESTS PASSED (" 
                + (passed * 100 / totalTests) + "%)");
        System.out.println("ARCHITECTURAL DEFENCE RESULT: ZERO EXISTING CLASSES WERE MODIFIED!");
        System.out.println("=================================================================");
    }
}
