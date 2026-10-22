/**
 * CSA09 - Java Assignment 1
 * Task 2: Choose the Data Representation
 * Demonstrates:
 * 1. Primitive and reference data types.
 * 2. Jagged array representation for heterogeneous floor capacities.
 * 3. Integer division / precision loss pitfall vs safe ceiling calculation.
 * 4. Input reading strategies (command-line args and interactive Scanner).
 */
public class DataRepresentationDemo {

    public static void main(String[] args) {
        System.out.println("=== Task 2: Data Representation Demonstration ===");

        // 1. Primitive & Reference Data Types
        int floorNo = 2;
        int slotNo = 15;
        String vehicleNo = "TN01AB1111";
        long entryTimeMillis = System.currentTimeMillis() - (85 * 60 * 1000); // 85 mins ago
        long exitTimeMillis = System.currentTimeMillis();
        long durationMinutes = (exitTimeMillis - entryTimeMillis) / (60 * 1000);
        double ratePerHour = 20.0;

        System.out.println("\n--- Field Values and Types ---");
        System.out.println("Slot Location   : Floor " + floorNo + ", Slot " + slotNo);
        System.out.println("Vehicle Number  : " + vehicleNo + " (String)");
        System.out.println("Duration (mins) : " + durationMinutes + " mins (long)");

        // 2. Demonstration of Integer Division Pitfall vs Safe Calculation
        System.out.println("\n--- Precision Loss & Casting Comparison ---");
        
        // BUGGY: Integer division truncates 85 / 60 to 1
        int buggyHours = (int) (durationMinutes / 60);
        double buggyFee = buggyHours * ratePerHour;
        System.out.println("[BUGGY] Integer Division Hours : " + buggyHours + " -> Fee: ₹" + buggyFee);

        // CORRECT: Floating-point division with Math.ceil
        long correctHours = (long) Math.ceil((double) durationMinutes / 60.0);
        double correctFee = correctHours * ratePerHour;
        System.out.println("[CORRECT] Math.ceil Billable Hours: " + correctHours + " -> Fee: ₹" + correctFee);
        System.out.println("Financial Difference (Under-billed) : ₹" + (correctFee - buggyFee));

        // 3. Jagged Array Representation for Variable Floors
        System.out.println("\n--- Jagged Array for Parking Facility ---");
        // Floor 0: 20 slots (VIP/EV), Floor 1: 50 slots (Cars), Floor 2: 100 slots (Mixed)
        int[] slotsPerFloor = {20, 50, 100};
        String[][] facility = new String[slotsPerFloor.length][];
        int totalPhysicalSlots = 0;

        for (int f = 0; f < slotsPerFloor.length; f++) {
            facility[f] = new String[slotsPerFloor[f]];
            totalPhysicalSlots += facility[f].length;
            System.out.println("Floor " + f + " successfully allocated with " + facility[f].length + " slots.");
        }
        System.out.println("Total Allocated Physical Slots: " + totalPhysicalSlots);

        // Rectangular 2D array comparison
        int rectangularSize = slotsPerFloor.length * 100;
        int wastedSlots = rectangularSize - totalPhysicalSlots;
        System.out.println("Equivalent Rectangular 2D Allocation: " + rectangularSize + " slots");
        System.out.println("Slots Wasted if Rectangular Array Used: " + wastedSlots + " slots ("
                + String.format("%.1f", (wastedSlots * 100.0 / rectangularSize)) + "% waste avoided!)");
    }
}
