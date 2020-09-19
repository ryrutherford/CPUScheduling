import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Shortest Job First Scheduler strategy
 */
public class SchedulerSJF extends SchedulerStrategy{

    //SJF uses a priority queue ordered on the remaining time of the process
    public SchedulerSJF(String fileName){
        super(new PriorityQueue<>(11, Comparator.comparingInt(CPUProcess::getRemainingTime)), fileName);
        System.out.println("\nSuccessfully created new SchedulerSJF\n");
    }

    @Override
    public void runProcesses(CPU cpu) {
        addArrivedProcesses();
        runCPU(cpu);
    }
}
