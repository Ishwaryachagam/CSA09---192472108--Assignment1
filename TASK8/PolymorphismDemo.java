/**
 * CSA09 - Java Assignment 1
 * Task 8: Polymorphism
 * Demonstrates:
 * 1. Compile-time Polymorphism (Method Overloading).
 * 2. Runtime Polymorphism (Dynamic Method Dispatch) on heterogeneous Vehicle arrays.
 * 3. Safe Downcasting using the `instanceof` operator to access specialized EV charging features.
 * 4. Exception handling demonstration for unsafe downcasting.
 */

abstract class Vehicle {
    protected String regNo;
    protected String brand;

    public Vehicle(String regNo, String brand) {
        this.regNo = regNo;
        this.brand = brand;
    }

    public String getRegNo() { return regNo; }
    public String getBrand() { return brand; }

    // Polymorphic method implemented by each subclass
    public abstract double computeFee(long hours);

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [Plate: " + regNo + ", Brand: " + brand + "]";
    }
}

class TwoWheeler extends Vehicle {
    public TwoWheeler(String regNo, String brand) {
        super(regNo, brand);
    }

    @Override
    public double computeFee(long hours) {
        return hours * 10.0;
    }
}

class Car extends Vehicle {
    public Car(String regNo, String brand) {
        super(regNo, brand);
    }

    @Override
    public double computeFee(long hours) {
        return hours * 20.0;
    }
}

class ElectricCar extends Car {
    private double batteryLevelPercent;
    private boolean isChargingConnected;
    private static final double CHARGING_SURCHARGE = 15.0;

    public ElectricCar(String regNo, String brand, double batteryLevelPercent, boolean isChargingConnected) {
        super(regNo, brand);
        this.batteryLevelPercent = batteryLevelPercent;
        this.isChargingConnected = isChargingConnected;
    }

    @Override
    public double computeFee(long hours) {
        double baseFee = super.computeFee(hours);
        double chargingFee = isChargingConnected ? (hours * CHARGING_SURCHARGE) : 0.0;
        return baseFee + chargingFee;
    }

    // Subclass-specific method requiring downcasting
    public void initiateQuickCharging() {
        this.isChargingConnected = true;
        System.out.println("  -> [EV ACTION] Connected to 50kW DC Fast Charger for " + regNo 
                + " (Current Battery: " + batteryLevelPercent + "%)");
    }

    public boolean isChargingConnected() { return isChargingConnected; }
}

class ParkingBillingService {

    // 1. Compile-Time Polymorphism: Method Overloading
    // Overload 1: Standard calculation by hours
    public double calculateTotalBill(Vehicle v, long hours) {
        return v.computeFee(hours); // Triggers runtime polymorphism!
    }

    // Overload 2: Calculation with discount percentage
    public double calculateTotalBill(Vehicle v, long hours, double discountPercentage) {
        double regularFee = calculateTotalBill(v, hours);
        double discountAmount = regularFee * (discountPercentage / 100.0);
        return regularFee - discountAmount;
    }

    // Overload 3: Calculation with monthly pass override
    public double calculateTotalBill(Vehicle v, long hours, boolean hasMonthlyPass) {
        if (hasMonthlyPass) {
            return 0.0; // Pass holders park free
        }
        return calculateTotalBill(v, hours);
    }
}

public class PolymorphismDemo {
    public static void main(String[] args) {
        System.out.println("=== Task 8: Polymorphism Demonstration ===");

        // 1. Compile-Time Polymorphism (Method Overloading)
        System.out.println("\n--- 1. Compile-Time Polymorphism (Method Overloading) ---");
        ParkingBillingService billing = new ParkingBillingService();
        Vehicle sampleCar = new Car("TN01AA1000", "Hyundai Verna");

        long hours = 5;
        System.out.println("Standard Bill (5 hrs)             : ₹" + billing.calculateTotalBill(sampleCar, hours));
        System.out.println("Discounted Bill (5 hrs, 20% off)   : ₹" + billing.calculateTotalBill(sampleCar, hours, 20.0));
        System.out.println("Student Pass Bill (5 hrs, Pass=true): ₹" + billing.calculateTotalBill(sampleCar, hours, true));

        // 2. Runtime Polymorphism (Dynamic Binding on Heterogeneous Array)
        System.out.println("\n--- 2. Runtime Polymorphism (Dynamic Method Dispatch) ---");
        Vehicle[] parkingLot = new Vehicle[] {
            new TwoWheeler("TN02TW5555", "Yamaha FZ"),
            new Car("TN01CB2222", "Maruti Swift"),
            new ElectricCar("TN01EV3333", "Tata Nexon EV", 35.0, false),
            new ElectricCar("TN01EV4444", "Hyundai Ioniq 5", 80.0, true)
        };

        for (Vehicle v : parkingLot) {
            // JVM resolves computeFee dynamically based on the exact heap type
            System.out.println("Parked: " + v.toString());
            System.out.println("  Bill for 3 hours: ₹" + v.computeFee(3));
        }

        // 3. Safe Downcasting with `instanceof`
        System.out.println("\n--- 3. Type-Safe Downcasting using 'instanceof' ---");
        for (Vehicle v : parkingLot) {
            if (v instanceof ElectricCar) {
                // Safe downcast
                ElectricCar ec = (ElectricCar) v;
                if (!ec.isChargingConnected()) {
                    System.out.println("Found EV not charging: " + ec.getRegNo() + ". Connecting to charging station...");
                    ec.initiateQuickCharging();
                    System.out.println("  Updated Bill for 3 hours with charging: ₹" + ec.computeFee(3));
                }
            }
        }

        // 4. Demonstration of Unchecked Cast Exception Safety
        System.out.println("\n--- 4. Unsafe Downcasting Prevention Demo ---");
        Vehicle nonEv = parkingLot[0]; // TwoWheeler
        try {
            System.out.println("Attempting unchecked cast: (ElectricCar) nonEv...");
            ElectricCar badCast = (ElectricCar) nonEv; // Throws ClassCastException!
            badCast.initiateQuickCharging();
        } catch (ClassCastException ex) {
            System.out.println("Caught Expected ClassCastException: " + ex.getMessage());
            System.out.println("Confirmed: 'instanceof' guards are essential for safe downcasting!");
        }
    }
}
