public class Patient {

    private String queueNumber;
    private String name;
    private String complaint;
    private int urgencyLevel;
    private String status;
    private long arrivalTime;
    private long servedTime;




    public Patient(String queueNumber, String name,
                   String complaint, int urgencyLevel) {

        this.queueNumber = queueNumber;
        this.name = name;
        this.complaint = complaint;
        this.urgencyLevel = urgencyLevel;
        this.status = "Waiting";
        this.arrivalTime = System.currentTimeMillis();
        this.servedTime = 0;
    }

    public String getQueueNumber() {
        return queueNumber;
    }

    public String getName() {
        return name;
    }

    public String getComplaint() {
        return complaint;
    }

    public int getUrgencyLevel() {
        return urgencyLevel;
    }

    public String getStatus() {
        return status;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getServedTime() {
        return servedTime;
    }

    public void setServedTime(long servedTime) {
        this.servedTime = servedTime;
    }

    @Override
    public String toString() {

        return queueNumber +
                " | " + name +
                " | " + complaint +
                " | Priority: " + urgencyLevel +
                " | Status: " + status;
    }
}