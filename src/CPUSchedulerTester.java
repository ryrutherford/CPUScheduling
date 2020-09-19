public class CPUSchedulerTester {

    public static void main(String[] args){
        SchedulerStrategy ss;
        //ss = new SchedulerFCFS("input.txt");
        //ss.scheduleProcesses();
        //ss = new SchedulerSJF("input.txt");
        //ss.scheduleProcesses();
        //ss = new SchedulerSRTF("input.txt");
        //ss.scheduleProcesses();
        ss = new SchedulerRR(3,"input.txt");
        ss.scheduleProcesses();
    }

}
