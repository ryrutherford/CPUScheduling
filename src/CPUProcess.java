import java.util.PriorityQueue;

public class CPUProcess {


    //processID: the unique identifier of this process
    private String processID;
    //arrivalTime: the time at which the process arrives at the readyQueue
    private int arrivalTime;
    //totalExecTime: the total amount of time that the process must be on the cpu for
    private int totalExecTime;
    //turnaroundTime: the amount of time it takes for the process to complete once it arrives at the readyQueue
    private int turnaroundTime;
    //execTime: the amount of time the process has been on the cpu for
    private int execTime = 0;
    //waiting: the amount of time the process has been waiting for a cpu or io device
    private int waiting = 0;
    //responseTime: the amount of time it took for the process to start executing after it arrived at the readyQueue
    private int responseTime = 0;
    //qElapsed: the amount of time the process has been on the cpu for the current stage (it resets to 0 when it is preempted off the cpu)
    private int qElapsed = 0;
    //hasStarted: indicates whether the process has started executing or not
    private boolean hasStarted = false;
    //remainingTime: the amount of time remaining on the cpu until the process finishes execution
    private int remainingTime;
    //ioRequestTimes: a queue of times at which the process needs to perform io (front of queue is earliest io time)
    private PriorityQueue<Integer> ioRequestTimes;
    //ioTime: the time at which the process is at during it's current io execution (0 if not performing io)
    private int ioTime = 0;

    /**
     * @param processID - the unique identifier of the CPUProcess
     * @param arrivalTime - the time at which the CPUProcess arrives at the ReadyQueue in ms
     * @param totalExecTime - the time that the CPUProcess takes to complete in ms
     * @param ioRequestTimes - the times at which the CPUProcess needs to access I/O ops
     */
    public CPUProcess(String processID, int arrivalTime, int totalExecTime, PriorityQueue<Integer> ioRequestTimes){
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.totalExecTime = totalExecTime;
        this.remainingTime = this.totalExecTime;
        this.ioRequestTimes = ioRequestTimes;
    }

    @Override
    public String toString() {
        return "CPUProcess{" +
                "processID='" + processID + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", totalExecTime=" + totalExecTime +
                ", ioRequestTimes=" + ioRequestTimes +
                '}';
    }

    //a method that updates the statistics of the cpu process
    public void updateStatistics(){
        this.setExecTime(this.getExecTime() + 1);
        this.setTurnaroundTime(this.getTurnaroundTime() + 1);
        this.setRemainingTime(this.getRemainingTime() - 1);
        this.setqElapsed(this.getqElapsed() + 1);
    }

    //GETTERS
    public String getProcessID() {
        return processID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getTotalExecTime() {
        return totalExecTime;
    }

    public int getRemainingTime() { return remainingTime; }

    public int getExecTime() { return execTime; }

    public PriorityQueue<Integer> getIoRequestTimes() {
        return ioRequestTimes;
    }

    public int getIoTime(){ return this.ioTime; }

    public int getWaiting(){ return this.waiting; }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getResponseTime(){ return this.responseTime; }

    public boolean hasStarted() {
        return hasStarted;
    }

    public int getqElapsed() { return this.qElapsed; }

    //SETTERS

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setArrivalTime(int arrivalTime) { this.arrivalTime = arrivalTime; }

    public void setIoTime(int ioTime) {
        this.ioTime = ioTime;
    }

    public void setExecTime(int execTime) {
        this.execTime = execTime;
    }

    public void setWaiting(int waiting){
        this.waiting = waiting;
    }

    public void setTurnaroundTime(int turnaroundTime){
        this.turnaroundTime = turnaroundTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public void setqElapsed(int qElapsed) {
        this.qElapsed = qElapsed;
    }
}
