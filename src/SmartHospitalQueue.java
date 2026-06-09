import java.util.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ╔══════════════════════════════════════════════╗
 * ║     SMART HOSPITAL QUEUE SYSTEM              ║
 * ║     Simulasi Antrian Rumah Sakit - Java      ║
 * ╚══════════════════════════════════════════════╝
 *
 * Data Structures:
 *   - PriorityQueue  → Emergency patients (urgensi 3)
 *   - LinkedList     → General patients   (urgensi 1 & 2)
 *
 * Rules:
 *   - Emergency selalu diprioritaskan
 *   - Level sama → FIFO (arrival time)
 */

// ─────────────────────────────────────────
// MODEL: Patient
// ─────────────────────────────────────────
class Patient implements Comparable<Patient> {
    static int idCounter = 1;

    int       id;
    String    name;
    String    complaint;
    int       urgencyLevel;   // 1=Ringan, 2=Sedang, 3=Darurat
    LocalTime arrivalTime;
    int       waitMinutes;

    public Patient(String name, String complaint, int urgencyLevel) {
        this.id           = idCounter++;
        this.name         = name;
        this.complaint    = complaint;
        this.urgencyLevel = urgencyLevel;
        this.arrivalTime  = LocalTime.now();
        this.waitMinutes  = 0;
    }

    /** PriorityQueue min-heap: urgensi tertinggi keluar duluan; sama → FIFO */
    @Override
    public int compareTo(Patient other) {
        if (this.urgencyLevel != other.urgencyLevel)
            return Integer.compare(other.urgencyLevel, this.urgencyLevel);
        return this.arrivalTime.compareTo(other.arrivalTime);
    }

    public String urgencyLabel() {
        switch (urgencyLevel) {
            case 1: return "Ringan";
            case 2: return "Sedang";
            case 3: return "Darurat";
            default: return "Unknown";
        }
    }

    public String urgencyIcon() {
        switch (urgencyLevel) {
            case 1: return "[O]";
            case 2: return "[!]";
            case 3: return "[!!]";
            default: return "[ ]";
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        return String.format("[#%d] %-20s | Keluhan: %-25s | Urgensi: %-7s | Tiba: %s",
                id, name, complaint, urgencyLabel(), arrivalTime.format(fmt));
    }
}


// ─────────────────────────────────────────
// DATA STRUCTURE: Emergency Priority Queue
// ─────────────────────────────────────────
class EmergencyQueue {
    private final PriorityQueue<Patient> heap = new PriorityQueue<>();

    public void enqueue(Patient p)  { heap.offer(p); }

    public Patient dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Emergency queue kosong.");
        return heap.poll();
    }

    public Patient peek()    { return heap.peek(); }
    public boolean isEmpty() { return heap.isEmpty(); }
    public int size()        { return heap.size(); }

    /** Snapshot terurut tanpa merusak heap asli */
    public List<Patient> getSortedSnapshot() {
        List<Patient> sorted = new ArrayList<>(heap);
        Collections.sort(sorted);
        return sorted;
    }

    public void display() {
        if (isEmpty()) { System.out.println("  [Kosong]"); return; }
        List<Patient> sorted = getSortedSnapshot();
        for (int i = 0; i < sorted.size(); i++) {
            String prefix = (i == 0) ? "  --> [NEXT]" : ("  " + (i + 1) + ".      ");
            System.out.println(prefix + " " + sorted.get(i));
        }
    }
}


// ─────────────────────────────────────────
// DATA STRUCTURE: General Queue
// ─────────────────────────────────────────
class GeneralQueue {
    private final LinkedList<Patient> queue = new LinkedList<>();

    public void enqueue(Patient p) {
        queue.add(p);
        // Urgensi lebih tinggi duluan; sama → FIFO (arrival time)
        queue.sort(Comparator.comparingInt((Patient x) -> -x.urgencyLevel)
                .thenComparing(x -> x.arrivalTime));
    }

    public Patient dequeue() {
        if (isEmpty()) throw new NoSuchElementException("General queue kosong.");
        return queue.removeFirst();
    }

    public Patient peek()    { return queue.isEmpty() ? null : queue.getFirst(); }
    public boolean isEmpty() { return queue.isEmpty(); }
    public int size()        { return queue.size(); }

    public List<Patient> getAll() { return new ArrayList<>(queue); }

    public void display() {
        if (isEmpty()) { System.out.println("  [Kosong]"); return; }
        for (int i = 0; i < queue.size(); i++) {
            String prefix = (i == 0) ? "  --> [NEXT]" : ("  " + (i + 1) + ".      ");
            System.out.println(prefix + " " + queue.get(i));
        }
    }
}


// ─────────────────────────────────────────
// CORE: Hospital Queue System
// ─────────────────────────────────────────
class HospitalQueueSystem {
    private final EmergencyQueue emergencyQueue = new EmergencyQueue();
    private final GeneralQueue   generalQueue   = new GeneralQueue();
    private final List<Patient>  processLog     = new ArrayList<>();

    // ── Add Patient ──────────────────────
    public Patient addPatient(String name, String complaint, int urgencyLevel) {
        if (urgencyLevel < 1 || urgencyLevel > 3)
            throw new IllegalArgumentException("Urgency level harus 1, 2, atau 3.");

        Patient p = new Patient(name, complaint, urgencyLevel);

        if (urgencyLevel == 3) {
            emergencyQueue.enqueue(p);
            System.out.println("\n[+] Terdaftar -> Emergency (Priority) Queue");
        } else {
            generalQueue.enqueue(p);
            System.out.println("\n[+] Terdaftar -> General Queue");
        }
        System.out.println("    " + p);
        return p;
    }

    // ── Call Next Patient ─────────────────
    public Patient callNext() {
        if (emergencyQueue.isEmpty() && generalQueue.isEmpty()) {
            System.out.println("\n[!] Semua antrian kosong.");
            return null;
        }

        Patient p = !emergencyQueue.isEmpty()
                ? emergencyQueue.dequeue()
                : generalQueue.dequeue();

        long seconds = java.time.Duration
                .between(p.arrivalTime, LocalTime.now()).getSeconds();
        p.waitMinutes = (int) Math.max(0, seconds / 60);

        processLog.add(p);

        System.out.println("\n[BELL] MEMANGGIL: " + p.name);
        System.out.println("       Keluhan  : " + p.complaint);
        System.out.println("       Urgensi  : " + p.urgencyLabel());
        System.out.println("       Menunggu : " + p.waitMinutes + " menit");
        return p;
    }

    // ── Display Queues ────────────────────
    public void displayQueues() {
        int total = emergencyQueue.size() + generalQueue.size();
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("  ANTRIAN RUMAH SAKIT  |  Total: %d pasien%n", total);
        System.out.println("=".repeat(60));
        System.out.printf("%n[!!] EMERGENCY QUEUE (%d pasien):%n", emergencyQueue.size());
        emergencyQueue.display();
        System.out.printf("%n[O]  GENERAL QUEUE   (%d pasien):%n", generalQueue.size());
        generalQueue.display();
        System.out.println();
    }

    // ── Visualization (Text) ─────────────
    public void visualizeQueue() {
        List<Patient> combined = new ArrayList<>();
        combined.addAll(emergencyQueue.getSortedSnapshot());
        combined.addAll(generalQueue.getAll());

        System.out.println("\n[CHART] VISUALISASI ANTRIAN (Urutan Panggil):");
        if (combined.isEmpty()) { System.out.println("  [Antrian kosong]"); return; }

        StringBuilder row = new StringBuilder("  ");
        for (int i = 0; i < combined.size(); i++) {
            Patient p = combined.get(i);
            row.append("[").append(p.urgencyIcon())
                    .append(" ").append(p.name.split(" ")[0]).append("]");
            if (i < combined.size() - 1) row.append(" -> ");
        }
        System.out.println(row);
        System.out.printf("%n  Panggil berikutnya: * %s (%s)%n",
                combined.get(0).name, combined.get(0).urgencyLabel());
    }

    // ── Statistics ────────────────────────
    public void showStatistics() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  STATISTIK PELAYANAN");
        System.out.println("=".repeat(60));
        if (processLog.isEmpty()) {
            System.out.println("  Belum ada pasien yang dilayani.");
            return;
        }
        int    total       = processLog.size();
        long   emergServed = processLog.stream().filter(p -> p.urgencyLevel == 3).count();
        double avgWait     = processLog.stream().mapToInt(p -> p.waitMinutes).average().orElse(0);
        Patient longest    = processLog.stream()
                .max(Comparator.comparingInt(p -> p.waitMinutes)).orElse(null);

        System.out.printf("  Total pasien dilayani : %d%n", total);
        System.out.printf("  Pasien darurat        : %d%n", emergServed);
        System.out.printf("  Rata-rata waktu tunggu: %.1f menit%n", avgWait);
        if (longest != null) {
            System.out.printf("  Waktu tunggu terlama  : %d menit%n", longest.waitMinutes);
            System.out.printf("  Pasien terlama        : %s (%s)%n", longest.name, longest.complaint);
        }
    }

    // ── Process Log ───────────────────────
    public void showLog() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  LOG PROSES PASIEN");
        System.out.println("=".repeat(60));
        if (processLog.isEmpty()) {
            System.out.println("  Belum ada pasien yang dipanggil.");
            return;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        for (int i = 0; i < processLog.size(); i++) {
            Patient p = processLog.get(i);
            System.out.printf("  %2d. %-20s | %-7s | Tunggu: %2d menit | Tiba: %s%n",
                    i + 1, p.name, p.urgencyLabel(), p.waitMinutes, p.arrivalTime.format(fmt));
        }
    }
}


// ─────────────────────────────────────────
// MAIN: Demo + Interactive
// ─────────────────────────────────────────
public class SmartHospitalQueue {

    static Scanner sc = new Scanner(System.in);

    static void runDemo() {
        HospitalQueueSystem sys = new HospitalQueueSystem();

        System.out.println("\n--- Mendaftarkan pasien ---");
        sys.addPatient("Budi Santoso",  "Batuk dan pilek",    1);
        sys.addPatient("Siti Rahayu",   "Nyeri dada hebat",   3);
        sys.addPatient("Ahmad Fauzi",   "Sakit kepala",       2);
        sys.addPatient("Dewi Lestari",  "Sesak napas berat",  3);
        sys.addPatient("Roni Pratama",  "Demam tinggi",       2);
        sys.addPatient("Eka Putri",     "Mual dan muntah",    1);
        sys.addPatient("Joko Susilo",   "Luka robek kepala",  3);

        sys.displayQueues();
        sys.visualizeQueue();

        System.out.println("\n--- Memanggil 4 pasien ---");
        for (int i = 0; i < 4; i++) sys.callNext();

        sys.displayQueues();
        sys.visualizeQueue();
        sys.showLog();
        sys.showStatistics();
    }

    static void runInteractive() {
        HospitalQueueSystem sys = new HospitalQueueSystem();
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  SMART HOSPITAL QUEUE SYSTEM (Interaktif)   ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Tambah pasien");
            System.out.println("2. Panggil pasien berikutnya");
            System.out.println("3. Tampilkan antrian");
            System.out.println("4. Visualisasi antrian");
            System.out.println("5. Lihat log proses");
            System.out.println("6. Statistik");
            System.out.println("0. Keluar");
            System.out.print("\nPilih menu: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Nama pasien    : ");
                    String name = sc.nextLine().trim();
                    System.out.print("Keluhan        : ");
                    String complaint = sc.nextLine().trim();
                    System.out.println("Tingkat urgensi: 1=Ringan  2=Sedang  3=Darurat");
                    System.out.print("Urgensi (1/2/3): ");
                    try {
                        int urgency = Integer.parseInt(sc.nextLine().trim());
                        sys.addPatient(name, complaint, urgency);
                    } catch (Exception e) {
                        System.out.println("[!] Input tidak valid: " + e.getMessage());
                    }
                    break;
                case "2": sys.callNext();       break;
                case "3": sys.displayQueues();  break;
                case "4": sys.visualizeQueue(); break;
                case "5": sys.showLog();        break;
                case "6": sys.showStatistics(); break;
                case "0":
                    System.out.println("\nSampai jumpa!");
                    running = false;
                    break;
                default:
                    System.out.println("[!] Pilihan tidak valid.");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║     SMART HOSPITAL QUEUE SYSTEM - JAVA      ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("\nJalankan mode apa?");
        System.out.println("1. Demo otomatis");
        System.out.println("2. Interaktif (input manual)");
        System.out.print("Pilih (1/2): ");

        String mode = sc.nextLine().trim();
        if (mode.equals("2")) runInteractive();
        else runDemo();
    }
}