import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public abstract class SchedulerStrategy implements Scheduler{

    //readyQueue: a queue of processes that are ready to run on a cpu (the priority sorting will depend on the algorithm chosen)
    protected Queue<CPUProcess> readyQueue;
    //ioQueue: a queue of processes that are waiting to perform io
    protected Queue<CPUProcess> ioQueue = new LinkedList<CPUProcess>();
    //unarrivedProcesses: a queue of processes (ordered by arrival time) that have not yet arrived for execution yet
    protected Queue<CPUProcess> unarrivedProcesses = new PriorityQueue<>(11,
            Comparator.comparingInt(CPUProcess::getArrivalTime));
    //ioProcess: the CPUProcess that is currently performing io
    protected CPUProcess ioProcess = null;
    //cpus: the list of available cpus
    protected ArrayList<CPU> cpus;
    //finishedProcesses: the list of processes that have completed execution
    protected ArrayList<CPUProcess> finishedProcesses = new ArrayList<>();
    //numOfCPUs: the number of cpus in the system
    protected int numOfCPUs;
    //time: the current time unit in the execution of the system
    protected int time = 0;
    //ioChart: a list containing information about what process (if any) was performing io at time i (index i)
    private ArrayList<String> ioChart = new ArrayList<>();

    public SchedulerStrategy(Queue readyQueue, String fileName){
        System.out.println("\nEntering constructor of SchedulerStrategy");
        this.readyQueue = readyQueue;
        extractProcessesFromFile(fileName);
        this.cpus = new ArrayList<>(this.numOfCPUs);
        for(int i = 0; i < this.numOfCPUs; i++){
            this.cpus.add(new CPU(i));
        }
        System.out.println("\nSuccessfully created new SchedulerStrategy with " + this.numOfCPUs + " CPUs and " + this.readyQueue.size() + " processes");
    }

    //a method that extracts cpu and process data from a file following a specific format
    private void extractProcessesFromFile(String fileName){
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            System.out.println("\nReading from text file: " + fileName);

            String fileLine = br.readLine();
            while(fileLine != null){
                //splitting the fileLine on all whitespace
                String[] splitLine = fileLine.split("\\s+");

                //if the numOfCPUs attribute is 0 then we haven't initialized it with the value from the txt file yet so we check the current line
                if(this.numOfCPUs == 0) {
                    if (splitLine[0].contains("numOfCPUs")) {
                        this.numOfCPUs = Integer.parseInt(splitLine[1]);
                        fileLine = br.readLine();
                        continue;
                    }
                }
                //if the current line starts with a double slash we must skip it since it's a comment
                //if the current line is just white space then we must skip it since it's irrelevant
                if(splitLine[0].contains("//") || fileLine.length() == 0){
                    fileLine = br.readLine();
                    continue;
                }
                //otherwise it's a line we must process
                else{
                    //the index will keep track of which part of the cpu process we are reading from the splitLine
                    int index = 0;

                    //The following variables will be updated in the loop with the values from the file and used to construct a new CPUProcess
                    String processID = "";
                    int arrivalTime = -1;
                    int totalExecTime = -1;
                    PriorityQueue<Integer> ioRequestTimes = new PriorityQueue<Integer>();

                    for(String s: splitLine){
                        switch(index){
                            case 0:
                                processID = s;
                                break;
                            case 1:
                                arrivalTime = Integer.parseInt(s);
                                break;
                            case 2:
                                totalExecTime = Integer.parseInt(s);
                                break;
                            default:
                                ioRequestTimes.add(Integer.parseInt(s));
                        }
                        index++;
                    }
                    //if the values are unchanged from their initial state then an error occurred, throw exception
                    if(processID.equals("") || arrivalTime == -1 || totalExecTime == -1)
                        throw new InvalidInputException("Invalid input format, some values were not initialized");
                    addProcess(new CPUProcess(processID, arrivalTime, totalExecTime, ioRequestTimes));
                }
                fileLine = br.readLine();
            }
            System.out.println("\nSuccessfully read from text file: " + fileName);
        }
        catch(java.io.IOException e){
            System.out.println("DEBUG: IOException");
            e.printStackTrace();
        }
        catch(NumberFormatException e){
            System.out.println("DEBUG: Integer.parseInt() threw an exception");
            e.printStackTrace();
        }
        catch(InvalidInputException e){
            e.getMessage();
            e.printStackTrace();
        }
    }

    //method to add a process to the ready queue if it's arrival time is 0
    private void addProcess(CPUProcess cpup){
        if(cpup.getArrivalTime() == 0)
            this.readyQueue.add(cpup);
        else
            this.unarrivedProcesses.add(cpup);
    }

    @Override
    public void scheduleProcesses() {
        //the scheduling strategy (run processes is implemented by each scheduling algorithm class
        while(readyQueue.size() > 0 || processesAreRunning() || this.unarrivedProcesses.size() > 0) {
            for(CPU cpu: this.cpus) {
                runProcesses(cpu);
            }
            waitProcesses();
            runIO();
            this.time++;
        }

        //trimming down charts to avoid ones with extra spaces at the end
        for(CPU c: this.cpus){
            if(c.getChart().size() == this.time){
                c.getChart().remove(c.getChart().size() -1);
            }
        }

        if(this.ioChart.size() == this.time){
            this.ioChart.remove(this.ioChart.size() -1);
        }

        //Printing out the average waiting time
        String avgWaitTime = String.format("%.2f", calculateAvgWaitTime());
        System.out.println("\nAverage Waiting Time: " + avgWaitTime + " time units");

        //Printing out the average cpu utilization for each cpu individually
        double avgCpuUtilization = 0;
        for(CPU cpu: this.cpus) {
            double cpuUtilization = cpu.calculateCPUUtilization();
            String result = String.format("%.2f", cpuUtilization);
            System.out.println("\nCPU Utilization for CPU " + cpu.getCpuID() + ": " + result + "%");
            System.out.println(cpu);
            avgCpuUtilization += cpuUtilization;
        }

        //Printing out the IOChart (when processes were completing IO
        System.out.println("\nI/O Utilization\n" + printIOChart());

        //Printing out average CPU utilization for all cpus
        avgCpuUtilization = (avgCpuUtilization/this.numOfCPUs);
        String result = String.format("%.2f", avgCpuUtilization);
        System.out.println("\nAverage CPU Utilization for all CPUs: " + result + "%");

        //Printing out turnaround time and response time for each process
        for(CPUProcess cpup: this.finishedProcesses){
            System.out.println("\nTurnaround Time for Process " + cpup.getProcessID() + ": " + cpup.getTurnaroundTime() + " time units");
            System.out.println("Response Time for Process " + cpup.getProcessID() + ": " + cpup.getResponseTime() + " time units");
        }
    }

    //the method to be implemented by each scheduling strategy
    public abstract void runProcesses(CPU cpu);

    //a method that runs IO for processes
    private void runIO(){
        if(this.ioProcess == null){
            if(this.ioQueue.peek() != null){
                this.ioProcess = this.ioQueue.poll();
            }
        }

        if(this.ioProcess != null){
            System.out.println("CPU Process " + this.ioProcess.getProcessID() + " is performing I/O " +
                    " at time " + this.time + "-" + (this.time + 1));
            this.ioChart.add(this.ioProcess.getProcessID());
            this.ioProcess.setTurnaroundTime(this.ioProcess.getTurnaroundTime() + 1);
            this.ioProcess.setIoTime(this.ioProcess.getIoTime() + 1);
            if(this.ioProcess.getIoTime() == 2){
                this.ioProcess.setIoTime(0);
                this.ioProcess.setArrivalTime(this.time + 1);
                this.readyQueue.add(this.ioProcess);
                this.ioProcess = null;
            }
        }
        else{
            if(this.processesAreRunning() || this.unarrivedProcesses.size() > 0)
                this.ioChart.add("");
        }

        if(this.ioQueue.peek() != null){

            //increasing waiting time for the processes that didn't get assigned to a processor
            for(CPUProcess c: this.ioQueue){
                c.setWaiting(c.getWaiting() + 1);
                c.setTurnaroundTime(c.getTurnaroundTime() + 1);
            }
        }
    }

    //a method that updates statistics for processes in the ready queue
    private void waitProcesses(){
        //increasing waiting time for the processes that didn't get assigned to a processor
        for(CPUProcess c: this.readyQueue){
            if(c.getArrivalTime() <= this.time) {
                c.setWaiting(c.getWaiting() + 1);
                c.setTurnaroundTime(c.getTurnaroundTime() + 1);
                if(c.hasStarted() == false){
                    c.setResponseTime(c.getResponseTime() + 1);
                }
            }
        }
    }

    //a method that calculates the average wait time
    private double calculateAvgWaitTime(){
        double avgWaitTime = 0;
        for(CPUProcess cpup: this.finishedProcesses){
            avgWaitTime += cpup.getWaiting();
        }
        avgWaitTime = avgWaitTime/this.finishedProcesses.size();
        return avgWaitTime;
    }

    //a method that indicates whether processes are running or not (on a cpu or in io)
    protected boolean processesAreRunning(){
        if(this.ioQueue.size() > 0 || this.ioProcess != null){
            return true;
        }
        for(CPU c: this.cpus){
            if(c.getRunningProcess() != null)
                return true;
        }
        return false;
    }

    //a method that adds processes that have just arrived to the ready queue
    protected void addArrivedProcesses(){
        Iterator<CPUProcess> itr = this.unarrivedProcesses.iterator();
        while(itr.hasNext()){
            CPUProcess cpup = itr.next();
            if(cpup.getArrivalTime() <= this.time) {
                this.readyQueue.add(cpup);
                itr.remove();
            }
        }
    }

    //a method that prints out the io chart
    private String printIOChart(){
        String gantt = "|";
        for(int i = 0; i < this.ioChart.size(); i++){
            if(i < 10)
                gantt = gantt + "  " + i + "  | ";
            else
                gantt = gantt + " " + i + "  | ";
        }
        gantt = gantt + "\n" + "|";
        for(String s: this.ioChart){
            if(s.equals("")){
                gantt = gantt + "     | ";
            }
            else{
                gantt = gantt + " " + s + "  | ";
            }
        }
        return gantt;
    }

    //a method that runs a process on the cpu
    protected void runCPU(CPU cpu){

        //if the cpu already has a running process then we must run the process or push it to the ioReadyQueue
        if(cpu.getRunningProcess() != null){
            CPUProcess cpup = cpu.getRunningProcess();

            //if it's time for io then we push the process to the ioReadyQueue and remove it from the cpu
            if(cpup.getIoRequestTimes().peek() != null && cpup.getExecTime() == cpup.getIoRequestTimes().peek()){
                cpup.getIoRequestTimes().poll();
                cpup.setqElapsed(0);
                this.ioQueue.add(cpup);
                cpu.setRunningProcess(null);
            }
            //otherwise if the process has completed we add it to the finished processes list and remove it from the cpu
            else if(cpup.getExecTime() == cpup.getTotalExecTime()){
                cpu.setRunningProcess(null);
                this.finishedProcesses.add(cpup);
            }
            //otherwise, the process will continue to run on the cpu and we will update the process' statistics
            else{
                cpu.getChart().add(cpup.getProcessID());
                cpup.updateStatistics();
                System.out.println("CPU Process " + cpup.getProcessID() + " is running on CPU " +
                        cpu.getCpuID() + " at time " + this.time + "-" + (this.time+1));
            }
        }

        //if the cpu doesn't have any running processes then we must add the next process from the ready queue (if available)
        if(cpu.getRunningProcess() == null){
            CPUProcess cpup = this.readyQueue.peek();
            if(cpup != null && cpup.getArrivalTime() <= this.time){
                this.readyQueue.poll();
                if(cpup.getExecTime() == cpup.getTotalExecTime()){
                    this.finishedProcesses.add(cpup);
                    if(this.processesAreRunning()) {
                        cpu.getChart().add("");
                    }
                }
                else {
                    cpu.getChart().add(cpup.getProcessID());
                    cpu.setRunningProcess(cpup);
                    cpup.setHasStarted(true);
                    cpup.updateStatistics();
                    System.out.println("CPU Process " + cpup.getProcessID() + " is running on CPU " +
                            cpu.getCpuID() + " at time " + this.time + "-" + (this.time + 1));
                }
            }
            else {
                if(this.processesAreRunning() || this.unarrivedProcesses.size() > 0) {
                    cpu.getChart().add("");
                }
            }
        }
    }
}
