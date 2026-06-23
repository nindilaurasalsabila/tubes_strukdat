import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        HospitalSystem hospital =
                new HospitalSystem();

        Admin admin =
                new Admin();

        int role;

        do {

            System.out.println("\n======================");
            System.out.println("SMART HOSPITAL SYSTEM");
            System.out.println("======================");
            System.out.println("1. Admin");
            System.out.println("2. Patient");
            System.out.println("3. Exit");

            System.out.print("Choose : ");
            role = sc.nextInt();
            sc.nextLine();

            switch (role) {

                case 1:

                    System.out.print(
                            "Username : ");
                    String user =
                            sc.nextLine();

                    System.out.print(
                            "Password : ");
                    String pass =
                            sc.nextLine();

                    if (admin.login(
                            user, pass)) {

                        adminMenu(
                                sc,
                                hospital);

                    } else {

                        System.out.println(
                                "Login Failed!");
                    }

                    break;

                case 2:

                    System.out.print("Name : ");
                    String name = sc.nextLine();

                    System.out.print("Complaint : ");
                    String complaint = sc.nextLine();

                    System.out.print("Urgency (1-10) : ");
                    int urgency = sc.nextInt();
                    sc.nextLine();


                    hospital.registerPatient(name, complaint, urgency);


                    System.out.print("\nPress Enter to return to Main Menu...");
                    sc.nextLine();
                    break;

                case 3:

                    System.out.println(
                            "Thank You");
                    break;
            }

        } while (role != 3);
    }

    static void adminMenu(
            Scanner sc,
            HospitalSystem hospital) {

        int choice;

        do {

            System.out.println(
                    "\n===== ADMIN MENU =====");

            System.out.println(
                    "1. View Queue");

            System.out.println(
                    "2. Call Next Patient");

            System.out.println(
                    "3. Search Patient");

            System.out.println(
                    "4. Estimate Waiting Time");

            System.out.println(
                    "5. View History");

            System.out.println(
                    "6. Daily Report");

            System.out.println(
                    "7. Logout");

            System.out.print(
                    "Choose : ");

            choice =
                    sc.nextInt();

            sc.nextLine();

            switch (choice) {



                case 1:
                    hospital.viewQueues();
                    break;

                case 2:
                    hospital.callNextPatient();
                    break;

                case 3:

                    System.out.print(
                            "Queue Number : ");

                    String search =
                            sc.nextLine();

                    hospital.searchPatient(
                            search);

                    break;

                case 4:

                    System.out.print(
                            "Queue Number : ");

                    String queue =
                            sc.nextLine();

                    hospital.estimateWaitingTime(
                            queue);

                    break;

                case 5:
                    hospital.viewHistory();
                    break;

                case 6:
                    hospital.dailyReport();
                    break;
            }


            if (choice != 7) {
                System.out.print("\nPress Enter to continue...");
                sc.nextLine();
            }

        } while (choice != 7);
    }
}