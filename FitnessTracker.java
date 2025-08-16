
import java.util.*;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

class FitnessLog {
    int waterIntake;      // actual daily water intake (ml)
    int caloriesBurned;   // actual daily calories burned
    int steps;
    double sleepHours;
    String dietNote;

    FitnessLog(int water, int cal, int steps, double sleep, String diet) {
        this.waterIntake = water;
        this.caloriesBurned = cal;
        this.steps = steps;
        this.sleepHours = sleep;
        this.dietNote = diet.isEmpty() ? "Normal" : diet;
    }
}

class PaymentInfo {
    String packageType;
    double paidAmount;
    double packagePrice;
    String method;

    PaymentInfo(String pkg, double paid, String method) {
        this.packageType = pkg;
        this.method = method;

        // Fixed prices per package
        if (pkg.equalsIgnoreCase("Basic")) {
            this.packagePrice = 1000;
        } else if (pkg.equalsIgnoreCase("Premium")) {
            this.packagePrice = 2000;
        } else {
            this.packagePrice = 0; // unknown package
        }

        this.paidAmount = paid;
    }

    public double getDueAmount() {
        return packagePrice - paidAmount;
    }
}

class User {
    static final int DEFAULT_WATER_GOAL = 3000; // ml
    static final int DEFAULT_CALORIE_GOAL = 500; // kcal

    String name;
    int age;
    String gender;
    double height;
    double weight;
    FitnessLog log;
    PaymentInfo payment;

    User(String name, int age, String gender, double height, double weight, FitnessLog log, PaymentInfo payment) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.log = log;
        this.payment = payment;
    }

    double getBMI() {
        return weight / (height * height);
    }
    
    // NEW method for BMI category
    public String getBMICategory() {
        double bmi = getBMI();
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25) return "Normal";
        else if (bmi < 30) return "Overweight";
        else return "Obese";
    }
}

public class FitnessTrackerPro {
    static Scanner sc = new Scanner(System.in);
    static List<User> users = new ArrayList<>();

    public static void main(String[] args) {
        System.out.print("How many users to enter (1–10)? ");
        int total = sc.nextInt();

        for (int i = 0; i < total; i++) {
            users.add(createUser(i + 1));
        }

        while (true) {
            System.out.print("\nEnter user number (1–" + total + ") to view dashboard or 0 to exit: ");
            int n = sc.nextInt();
            if (n == 0) {
                System.out.println("Exiting. Thank you!");
                break;
            } else if (n >= 1 && n <= total) {
                showDashboard(users.get(n - 1));
            } else {
                System.out.println("Invalid user number.");
            }
        }
    }

    static User createUser(int index) {
        System.out.println("\n--- User " + index + " ---");
        System.out.print("Name: ");
        String name = sc.next();
        System.out.print("Age: ");
        int age = sc.nextInt();
        System.out.print("Gender (M/F): ");
        String gender = sc.next();
        System.out.print("Height (m): ");
        double height = sc.nextDouble();
        System.out.print("Weight (kg): ");
        double weight = sc.nextDouble();

        System.out.println("\nNOTE: Water intake goal is fixed at 3000 ml");
        System.out.print("Enter actual Water Intake (ml): ");
        int water = sc.nextInt();

        System.out.println("NOTE: Calorie burn goal is fixed at 500 kcal");
        System.out.print("Enter actual Calories Burned: ");
        int cal = sc.nextInt();

        System.out.print("Steps Walked: ");
        int steps = sc.nextInt();
        System.out.print("Sleep Hours: ");
        double sleep = sc.nextDouble();
        sc.nextLine(); // clear buffer
        System.out.print("Diet Note (press Enter to skip): ");
        String diet = sc.nextLine();

        FitnessLog log = new FitnessLog(water, cal, steps, sleep, diet);

        System.out.print("Package Type (Basic/Premium): ");
        String pkg = sc.next();
        System.out.print("Amount Paid: ");
        double amt = sc.nextDouble();
        sc.nextLine();
        System.out.print("Payment Method (Card/JazzCash): ");
        String method = sc.nextLine();

        PaymentInfo payment = new PaymentInfo(pkg, amt, method);
        return new User(name, age, gender, height, weight, log, payment);
    }

    static void showDashboard(User u) {
        JFrame frame = new JFrame("Fitness Dashboard: " + u.name);
        frame.setSize(950, 520);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new java.awt.BorderLayout());

        // Chart dataset with goals and actuals
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(User.DEFAULT_WATER_GOAL, "Goal", "Water (ml)");
        dataset.addValue(u.log.waterIntake, "Actual", "Water (ml)");
        dataset.addValue(User.DEFAULT_CALORIE_GOAL, "Goal", "Calories");
        dataset.addValue(u.log.caloriesBurned, "Actual", "Calories");

        JFreeChart chart = ChartFactory.createBarChart(
            "Goal vs Actual Comparison",
            "Metric",
            "Value",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinePaint(java.awt.Color.gray);

        // Info panel
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        info.add(new JLabel("Name: " + u.name));
        info.add(new JLabel("Age: " + u.age));
        info.add(new JLabel("Gender: " + u.gender));
        info.add(new JLabel("BMI: " + String.format("%.2f", u.getBMI()) + " (" + u.getBMICategory() + ")"));
        info.add(new JLabel("Steps: " + u.log.steps));
        info.add(new JLabel("Sleep: " + u.log.sleepHours + " hrs"));
        info.add(new JLabel("Diet Note: " + u.log.dietNote));
        info.add(new JLabel("Package: " + u.payment.packageType));
        info.add(new JLabel("Paid: $" + u.payment.paidAmount));
        info.add(new JLabel("Payment Due: $" + String.format("%.2f", u.payment.getDueAmount())));
        info.add(new JLabel("Method: " + u.payment.method));

        frame.add(chartPanel, java.awt.BorderLayout.CENTER);
        frame.add(info, java.awt.BorderLayout.EAST);
        frame.setVisible(true);
    }
}
