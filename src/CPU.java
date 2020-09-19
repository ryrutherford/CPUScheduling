import java.util.ArrayList;

public class CPU {

    //cpuID: the unique identifier of the cpu
    private int cpuID;
    //runningProcess: the process that is currently running on this cpu
    private CPUProcess runningProcess = null;
    //chart: a list indicating which process (if any) was running on the cpu at time i (index i)
    private ArrayList<String> chart = new ArrayList<>();

    public CPU(int cpuID){
        this.cpuID = cpuID;
    }

    //a method that calculates the cpu utilization for this cpu
    public double calculateCPUUtilization(){
        double utilizationPercentage = 0;
        for(String s: this.chart){
            if(s.equals("") == false){
                utilizationPercentage++;
            }
        }
        utilizationPercentage = (utilizationPercentage/this.chart.size())*100.0;
        return utilizationPercentage;
    }


    //the toString method for the CPU class will print out the cpu's chart
    @Override
    public String toString(){
        String gantt = "|";
        for(int i = 0; i < this.chart.size(); i++){
            if(i < 10)
                gantt = gantt + "  " + i + "  | ";
            else
                gantt = gantt + " " + i + "  | ";
        }
        gantt = gantt + "\n" + "|";
        for(String s: this.chart){
            if(s.equals("")){
                gantt = gantt + "     | ";
            }
            else{
                gantt = gantt + " " + s + "  | ";
            }
        }
        return gantt;
    }

    public int getCpuID(){
        return cpuID;
    }

    public CPUProcess getRunningProcess() {
        return runningProcess;
    }

    public void setRunningProcess(CPUProcess runningProcess) {
        this.runningProcess = runningProcess;
    }

    public ArrayList<String> getChart(){ return this.chart; }
}
