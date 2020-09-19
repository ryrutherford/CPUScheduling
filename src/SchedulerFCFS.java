import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * First Come First Server Scheduler strategy
 */
public class SchedulerFCFS extends SchedulerStrategy{

    //FCFS uses a priority queue ordered on the arrival time of the process
    public SchedulerFCFS(String fileName){
        super(new PriorityQueue<>(11, Comparator.comparingInt(CPUProcess::getArrivalTime)), fileName);
        System.out.println("\nSuccessfully created new SchedulerFCFS\n");
    }

    @Override
    public void runProcesses(CPU cpu) {
        addArrivedProcesses();
        runCPU(cpu);
    }
}
