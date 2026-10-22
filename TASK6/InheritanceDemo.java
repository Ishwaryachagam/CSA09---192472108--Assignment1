/**
 * CSA09 - Java Assignment 1
 * Task 6: Design the Inheritance Hierarchy
 * Demonstrates:
 * 1. Superclass and Subclasses (Hierarchical and Multilevel inheritance).
 * 2. Use of `super` for fields, constructors, and methods.
 * 3. Constructor execution order trace when creating an ElectricCar.
 * 4. Specialization of fields and fee calculations.
 */

// Superclass
class Vehicle {
    protected String regNo;
    protected String brand;
    protected double baseRatePerHour;

    public Vehicle(String regNo, String brand, double baseRatePerHour) {
        System.out.println("  [1. Vehicle Constructor] Initializing root Vehicle fields (regNo: " + regNo + ")");
        this.regNo = regNo;
        this.brand = brand;
        this.baseRatePerHour = baseRatePerHour;
    }

    public double computeFee(long hours) {
        return hours * baseRatePerHour;
    }

    public void display() {
        System.out.println("Vehicle [Reg: " + regNo + ", Brand: " + brand + ", BaseRate: ₹" + baseRatePerHour + "/hr]");
    }
}

// Subclass 1: Hierarchical Inheritance (TwoWheeler IS-A Vehicle)
class TwoWheeler extends Vehicle {
    private boolean hasHelmetStorage;

    public TwoWheeler(String regNo, String brand, boolean hasHelmetStorage) {
        super(regNo, brand, 10.0); // Two-wheeler base rate = ₹10/hr
        System.out.println("  [2. TwoWheeler Constructor] Initializing TwoWheeler specific fields.");
        this.hasHelmetStorage = hasHelmetStorage;
    }

    @Override
    public void display() {
        super.display(); // Reusing parent display method
        System.out.println("  -> Subclass Feature: Helmet Storage = " + hasHelmetStorage);
    }
}

// Subclass 2: Hierarchical Inheritance (Car IS-A Vehicle)
class Car extends Vehicle {
    protected int seatingCapacity;

    public Car(String regNo, String brand, int seatingCapacity) {
        super(regNo, brand, 20.0); // Car base rate = ₹20/hr
        System.out.println("  [2. Car Constructor] Initializing Car specific fields (seating: " + seatingCapacity + ")");
        this.seatingCapacity = seatingCapacity;
    }

    @Override
    public void display() {
        super.display();
        System.out.println("  -> Subclass Feature: Seating Capacity = " + seatingCapacity);
    }
}

// Subclass 3: Multilevel Inheritance (ElectricCar IS-A Car IS-A Vehicle)
class ElectricCar extends Car {
    private double batteryCapacityKWh;
    private boolean isPluggedIn;
    private static final double CHARGING_RATE_PER_HOUR = 15.0;

    public ElectricCar(String regNo, String brand, int seatingCapacity, double batteryCapacityKWh, boolean isPluggedIn) {
        super(regNo, brand, seatingCapacity); // Invokes Car constructor first
        System.out.println("  [3. ElectricCar Constructor] Initializing ElectricCar specific fields (battery: " 
                + batteryCapacityKWh + " kWh)");
        this.batteryCapacityKWh = batteryCapacityKWh;
        this.isPluggedIn = isPluggedIn;
    }

    @Override
    public double computeFee(long hours) {
        double parkingFee = super.computeFee(hours); // Base Car fee
        double chargingFee = isPluggedIn ? (hours * CHARGING_RATE_PER_HOUR) : 0.0;
        return parkingFee + chargingFee;
    }

    @Override
    public void display() {
        super.display();
        System.out.println("  -> Subclass Feature: Battery = " + batteryCapacityKWh + " kWh, PluggedIn = " 
                + isPluggedIn + ", Charging Rate = ₹" + CHARGING_RATE_PER_HOUR + "/hr");
    }
}

public class InheritanceDemo {
    public static void main(String[] args) {
        System.out.println("=== Task 6: Inheritance Hierarchy & Constructor Trace ===");

        // Trace constructor execution order when creating ElectricCar
        System.out.println("\n--- Initiating ElectricCar Creation (Constructor Trace) ---");
        ElectricCar ev = new ElectricCar("TN01EV2026", "Tata Nexon EV", 5, 40.5, true);

        System.out.println("\n--- Details of Created ElectricCar ---");
        ev.display();

        long parkedHours = 3;
        System.out.println("\nTotal Fee for " + parkedHours + " hours (Car Parking ₹20/hr + Charging ₹15/hr): ₹" 
                + ev.computeFee(parkedHours));

        System.out.println("\n--- TwoWheeler Creation ---");
        TwoWheeler bike = new TwoWheeler("TN02TW9999", "Honda Activa", true);
        bike.display();
        System.out.println("Total Fee for " + parkedHours + " hours (Bike ₹10/hr): ₹" + bike.computeFee(parkedHours));
    }
}
