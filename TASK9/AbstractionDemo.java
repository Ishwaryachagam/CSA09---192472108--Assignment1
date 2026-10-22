/**
 * CSA09 - Java Assignment 1
 * Task 9: Abstraction
 * Demonstrates:
 * 1. Partial abstraction via `abstract class Vehicle` with state and constructors.
 * 2. Pure contract abstraction via `interface Chargeable` and `interface PassHolder`.
 * 3. Enforced method implementation at compile time.
 * 4. Multiple interface implementation in concrete classes.
 */

// Interface 1: Contract for vehicles requiring electrical charging
interface Chargeable {
    void plugInCharger();
    void unplugCharger();
    boolean isChargingActive();
    double getChargingTariffPerHour();
}

// Interface 2: Contract for users/vehicles carrying campus monthly passes
interface PassHolder {
    String getPassNumber();
    boolean isMonthlyPassActive();
    String getUserType(); // "STUDENT" or "STAFF"
}

// Abstract Superclass: Captures shared state and defines abstract contracts
abstract class Vehicle {
    protected final String regNo;
    protected final String brand;
    protected final long entryTimeMillis;

    public Vehicle(String regNo, String brand) {
        this.regNo = regNo;
        this.brand = brand;
        this.entryTimeMillis = System.currentTimeMillis();
    }

    // Concrete shared methods
    public String getRegNo() { return regNo; }
    public String getBrand() { return brand; }
    public long getEntryTimeMillis() { return entryTimeMillis; }

    // Abstract methods: MUST be implemented by any concrete subclass
    public abstract double getHourlyRate();
    public abstract String getRequiredSlotType();

    public void displaySummary() {
        System.out.println("Vehicle [Plate: " + regNo + ", Brand: " + brand 
                + ", Required Slot: " + getRequiredSlotType() + ", Rate: ₹" + getHourlyRate() + "/hr]");
    }
}

// Concrete Subclass 1: TwoWheeler
class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo, String brand) {
        super(regNo, brand);
    }

    @Override
    public double getHourlyRate() {
        return 10.0;
    }

    @Override
    public String getRequiredSlotType() {
        return "COMPACT_SLOT";
    }
}

// Concrete Subclass 2: Standard Car
class Car extends Vehicle {
    public Car(String regNo, String brand) {
        super(regNo, brand);
    }

    @Override
    public double getHourlyRate() {
        return 20.0;
    }

    @Override
    public String getRequiredSlotType() {
        return "STANDARD_SLOT";
    }
}

// Concrete Subclass 3: ElectricCar implementing Multiple Interfaces (Chargeable and PassHolder)
class ElectricStudentCar extends Car implements Chargeable, PassHolder {
    private boolean chargingActive;
    private final String passNumber;
    private static final double CHARGING_TARIFF_PER_HOUR = 15.0;

    public ElectricStudentCar(String regNo, String brand, String passNumber) {
        super(regNo, brand);
        this.passNumber = passNumber;
        this.chargingActive = false;
    }

    @Override
    public String getRequiredSlotType() {
        return "EV_CHARGING_SLOT";
    }

    // Implementing Chargeable Interface
    @Override
    public void plugInCharger() {
        this.chargingActive = true;
        System.out.println("  -> [CHARGER] Fast charger connected to " + regNo);
    }

    @Override
    public void unplugCharger() {
        this.chargingActive = false;
        System.out.println("  -> [CHARGER] Charger disconnected from " + regNo);
    }

    @Override
    public boolean isChargingActive() {
        return chargingActive;
    }

    @Override
    public double getChargingTariffPerHour() {
        return CHARGING_TARIFF_PER_HOUR;
    }

    // Implementing PassHolder Interface
    @Override
    public String getPassNumber() {
        return passNumber;
    }

    @Override
    public boolean isMonthlyPassActive() {
        return passNumber != null && !passNumber.isEmpty();
    }

    @Override
    public String getUserType() {
        return "STUDENT";
    }
}

public class AbstractionDemo {
    public static void main(String[] args) {
        System.out.println("=== Task 9: Abstraction (Abstract Classes & Interfaces) Demo ===");

        // 1. Compile-Time Abstraction Safety Demonstration:
        // Uncommenting the next line would cause a compile-time error: "Vehicle is abstract; cannot be instantiated"
        // Vehicle v = new Vehicle("TN01", "Generic"); 
        System.out.println("Note: 'Vehicle' is declared abstract, preventing direct instantiation of generic vehicles.");

        // 2. Concrete instances through polymorphic abstract references
        Vehicle bike = new TwoWheeler("TN02TW8888", "Yamaha");
        Vehicle sedan = new Car("TN01AB7777", "Honda City");
        ElectricStudentCar evStudent = new ElectricStudentCar("TN01EV9999", "Tata Nexon EV", "PASS-STU-2026-0042");

        System.out.println("\n--- Vehicle Summaries via Abstract Method Implementations ---");
        bike.displaySummary();
        sedan.displaySummary();
        evStudent.displaySummary();

        // 3. Interface contract execution: Chargeable interface
        System.out.println("\n--- Testing 'Chargeable' Interface Contract ---");
        Chargeable evStation = evStudent; // Upcasting to interface reference
        System.out.println("Is charging active currently? " + evStation.isChargingActive());
        evStation.plugInCharger();
        System.out.println("Is charging active now? " + evStation.isChargingActive());
        System.out.println("Charging Rate: ₹" + evStation.getChargingTariffPerHour() + "/hr");

        // 4. Interface contract execution: PassHolder interface
        System.out.println("\n--- Testing 'PassHolder' Interface Contract ---");
        PassHolder pass = evStudent; // Upcasting to pass interface
        System.out.println("Pass Number: " + pass.getPassNumber());
        System.out.println("Pass Active: " + pass.isMonthlyPassActive());
        System.out.println("User Category: " + pass.getUserType());

        // 5. Billing logic utilizing both abstractions
        long hoursParked = 4;
        double parkingFee = pass.isMonthlyPassActive() ? 0.0 : (evStudent.getHourlyRate() * hoursParked);
        double electricityFee = evStation.isChargingActive() ? (evStation.getChargingTariffPerHour() * hoursParked) : 0.0;
        double totalBill = parkingFee + electricityFee;

        System.out.println("\n--- Billing Breakdown for Student EV (4 Hours) ---");
        System.out.println("Parking Fee (Monthly Pass applied) : ₹" + parkingFee);
        System.out.println("EV Charging Fee (4 hrs @ ₹15/hr)    : ₹" + electricityFee);
        System.out.println("Total Due at Barrier Exit          : ₹" + totalBill);
    }
}
