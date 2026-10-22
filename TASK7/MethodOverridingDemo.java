/**
 * CSA09 - Java Assignment 1
 * Task 7: Override and Specialise Behaviour
 * Demonstrates:
 * 1. Method overriding using the `@Override` annotation.
 * 2. Specialized fee calculation across vehicle subclasses.
 * 3. Slot capability and charging requirement specialization.
 * 4. Elimination of procedural switch-case ladders via dynamic method dispatch.
 */

class Vehicle {
    protected String regNo;
    protected String brand;

    public Vehicle(String regNo, String brand) {
        this.regNo = regNo;
        this.brand = brand;
    }

    public String getRegNo() { return regNo; }
    public String getBrand() { return brand; }

    // Method to be overridden by subclasses
    public double computeFee(long hours) {
        return hours * 20.0; // Default baseline rate
    }

    // Method indicating if dedicated EV charging bay is required
    public boolean requiresChargingSlot() {
        return false;
    }

    @Override
    public String toString() {
        return "Vehicle [Reg: " + regNo + ", Brand: " + brand + "]";
    }
}

class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo, String brand) {
        super(regNo, brand);
    }

    // Overriding fee: Two-wheelers receive a 50% discounted campus rate (₹10/hr)
    @Override
    public double computeFee(long hours) {
        return hours * 10.0;
    }

    @Override
    public boolean requiresChargingSlot() {
        return false;
    }

    @Override
    public String toString() {
        return "TwoWheeler [Reg: " + regNo + ", Rate: ₹10/hr]";
    }
}

class Car extends Vehicle {
    public Car(String regNo, String brand) {
        super(regNo, brand);
    }

    // Overriding fee: Standard Car rate is ₹20/hr
    @Override
    public double computeFee(long hours) {
        return hours * 20.0;
    }

    @Override
    public boolean requiresChargingSlot() {
        return false;
    }

    @Override
    public String toString() {
        return "Car [Reg: " + regNo + ", Rate: ₹20/hr]";
    }
}

class ElectricCar extends Car {
    private boolean chargingRequested;
    private static final double CHARGING_SURCHARGE_PER_HOUR = 15.0;

    public ElectricCar(String regNo, String brand, boolean chargingRequested) {
        super(regNo, brand);
        this.chargingRequested = chargingRequested;
    }

    // Overriding fee: Aggregates base car parking fee + electric charging surcharge
    @Override
    public double computeFee(long hours) {
        double baseFee = super.computeFee(hours);
        double chargingFee = chargingRequested ? (hours * CHARGING_SURCHARGE_PER_HOUR) : 0.0;
        return baseFee + chargingFee;
    }

    @Override
    public boolean requiresChargingSlot() {
        return chargingRequested;
    }

    @Override
    public String toString() {
        return "ElectricCar [Reg: " + regNo + ", Charging: " + chargingRequested 
                + " (Rate: ₹" + (20.0 + (chargingRequested ? CHARGING_SURCHARGE_PER_HOUR : 0.0)) + "/hr)]";
    }
}

public class MethodOverridingDemo {
    public static void main(String[] args) {
        System.out.println("=== Task 7: Method Overriding & Specialised Behaviour Demo ===");

        long durationHours = 4;

        // Demonstrating runtime dynamic dispatch on superclass references
        Vehicle[] fleet = new Vehicle[] {
            new TwoWheeler("TN02TW1111", "Hero Splendor"),
            new Car("TN01AB2222", "Honda City"),
            new ElectricCar("TN01EV3333", "MG ZS EV", false), // EV parking only, no charging
            new ElectricCar("TN01EV4444", "Tata Tiago EV", true) // EV parking with active charging
        };

        System.out.println("\n--- Processing Fleet Parking Fees for " + durationHours + " Hours ---");
        for (Vehicle v : fleet) {
            System.out.println(v.toString());
            System.out.println("  Requires EV Charging Slot? " + v.requiresChargingSlot());
            // Dynamic dispatch invokes the overridden computeFee method of the actual runtime object
            System.out.println("  Calculated Fee for " + durationHours + " hrs: ₹" + v.computeFee(durationHours));
            System.out.println();
        }
    }
}
