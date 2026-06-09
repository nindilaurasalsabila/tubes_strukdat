import java.util.*;

public class HospitalSystem {

    private Queue<Patient> normalQueue =
            new LinkedList<>();

    private PriorityQueue<Patient> emergencyQueue =
            new PriorityQueue<>((a, b) -> {

                if (a.getUrgencyLevel()
                        == b.getUrgencyLevel()) {

                    return Long.compare(
                            a.getArrivalTime(),
                            b.getArrivalTime());
                }

                return Integer.compare(
                        b.getUrgencyLevel(),
                        a.getUrgencyLevel());
            });

    private HashMap<String, Patient> patientMap =
            new HashMap<>();

    private ArrayList<Patient> history =
            new ArrayList<>();

    private int normalCounter = 1;
    private int emergencyCounter = 1;

    public void registerPatient(String name,
                                String complaint,
                                int urgency) {

        String queueNumber;
        Patient patient;

        if (urgency >= 7) {

            queueNumber =
                    String.format("E%03d",
                            emergencyCounter++);

            patient =
                    new Patient(queueNumber,
                            name,
                            complaint,
                            urgency);

            emergencyQueue.offer(patient);

        } else {

            queueNumber =
                    String.format("N%03d",
                            normalCounter++);

            patient =
                    new Patient(queueNumber,
                            name,
                            complaint,
                            urgency);

            normalQueue.offer(patient);
        }

        patientMap.put(queueNumber,
                patient);

        System.out.println("\nRegistration Success!");
        System.out.println("Queue Number : "
                + queueNumber);
    }

    public void viewQueues() {

        System.out.println(
                "\n===== EMERGENCY QUEUE =====");

        if (emergencyQueue.isEmpty()) {

            System.out.println("Empty");

        } else {

            for (Patient p :
                    emergencyQueue) {

                System.out.println(p);
            }
        }

        System.out.println(
                "\n===== NORMAL QUEUE =====");

        if (normalQueue.isEmpty()) {

            System.out.println("Empty");

        } else {

            for (Patient p :
                    normalQueue) {

                System.out.println(p);
            }
        }
    }

    public void callNextPatient() {

        Patient patient = null;

        if (!emergencyQueue.isEmpty()) {

            patient =
                    emergencyQueue.poll();

        } else if (!normalQueue.isEmpty()) {

            patient =
                    normalQueue.poll();
        }

        if (patient == null) {

            System.out.println(
                    "\nNo patient waiting.");
            return;
        }

        patient.setStatus("Served");

        history.add(patient);

        System.out.println(
                "\n=== CALLING PATIENT ===");

        System.out.println(patient);
    }

    public void searchPatient(
            String queueNumber) {

        Patient patient =
                patientMap.get(queueNumber);

        if (patient == null) {

            System.out.println(
                    "Patient Not Found");
            return;
        }

        System.out.println(
                "\n=== PATIENT INFO ===");

        System.out.println(patient);
    }

    public void estimateWaitingTime(
            String queueNumber) {

        Patient patient =
                patientMap.get(queueNumber);

        if (patient == null) {

            System.out.println(
                    "Patient Not Found");
            return;
        }

        int position = 0;

        if (queueNumber.startsWith("E")) {

            for (Patient p :
                    emergencyQueue) {

                if (p.getQueueNumber()
                        .equals(queueNumber))
                    break;

                position++;
            }

        } else {

            for (Patient p :
                    normalQueue) {

                if (p.getQueueNumber()
                        .equals(queueNumber))
                    break;

                position++;
            }
        }

        int estimate =
                (position + 1) * 10;

        System.out.println(
                "Estimated Waiting Time : "
                        + estimate
                        + " Minutes");
    }

    public void viewHistory() {

        System.out.println(
                "\n===== HISTORY =====");

        if (history.isEmpty()) {

            System.out.println(
                    "No history.");
            return;
        }

        for (Patient p :
                history) {

            System.out.println(p);
        }
    }

    public void dailyReport() {

        System.out.println(
                "\n===== DAILY REPORT =====");

        System.out.println(
                "Patients Served : "
                        + history.size());

        long emergencyServed =
                history.stream()
                        .filter(p ->
                                p.getQueueNumber()
                                        .startsWith("E"))
                        .count();

        long normalServed =
                history.stream()
                        .filter(p ->
                                p.getQueueNumber()
                                        .startsWith("N"))
                        .count();

        System.out.println(
                "Emergency Served : "
                        + emergencyServed);

        System.out.println(
                "Normal Served : "
                        + normalServed);

        System.out.println(
                "Emergency Waiting : "
                        + emergencyQueue.size());

        System.out.println(
                "Normal Waiting : "
                        + normalQueue.size());
    }
}