
class Vehicle {
    String regNo;
    String type;

    Vehicle(String regNo, String type) {
        this.regNo = regNo;
        this.type = type;
    }

    void display() {
        System.out.println("Registration Number: " + regNo);
        System.out.println("Vehicle Type: " + type);
    }
}

public class VehicleDemo {
    public static void main(String[] args) {
        Vehicle v1 = new Vehicle("TN01AB1111", "Car");

        v1.display();
    }
}
