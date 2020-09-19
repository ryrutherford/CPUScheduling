import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Shortest Remaining Time First Scheduler strategy
 */
public class SchedulerSRTF extends SchedulerStrategy{

    public SchedulerSRTF(String fileName){
        super(new PriorityQueue<>(11, Comparator.comparingInt(CPUProcess::getRemainingTime)), fileName);
        System.out.println("\nSuccessfully created new SchedulerSRTF\n");
    }

    @Override
    public void runProcesses(CPU cpu) {

        addArrivedProcesses();

        //if there is a process on the current cpu then we must check if there is a process in the ready queue
        //with a shorter remaining time, if there is then that process will preempt the current process
        if(cpu.getRunningProcess() != null) {
            CPUProcess cpup = cpu.getRunningProcess();
            if(this.readyQueue.peek() != null &&
                    cpup.getRemainingTime() > this.readyQueue.peek().getRemainingTime() &&
                    (cpup.getIoRequestTimes().peek() == null ||
                    cpup.getExecTime() != cpup.getIoRequestTimes().peek())){
                cpu.setRunningProcess(this.readyQueue.poll());
                this.readyQueue.add(cpup);
            }
        }

        runCPU(cpu);
    }
}
