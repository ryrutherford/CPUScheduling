import java.util.*;

/**
 * Round Robin Scheduler strategy
 */
public class SchedulerRR extends SchedulerStrategy{

    //quantum: the max time that a process can be on the cpu before being preempted to the readyQueue
    private int quantum;

    public SchedulerRR(int quantum, String fileName){
        super(new LinkedList<CPUProcess>(), fileName);
        this.quantum = quantum;
        System.out.println("\nSuccessfully created new SchedulerRR with quantum " + this.quantum + "\n");
    }

    @Override
    public void runProcesses(CPU cpu) {

        addArrivedProcesses();

        //if there is a process that's currently on the cpu that has elapsed its quantum and is not about to be pushed
        //to the ioQueue or has finished executing then we need to preempt it from the cpu, add it to the ready queue
        //and reset its qElapsed
        if(cpu.getRunningProcess() != null) {
            CPUProcess cpup = cpu.getRunningProcess();
            if(cpup.getqElapsed() == this.quantum &&
                    (cpup.getIoRequestTimes().peek() == null ||
                    cpup.getExecTime() != cpup.getIoRequestTimes().peek())
                    && cpup.getExecTime() != cpup.getTotalExecTime()){
                this.readyQueue.add(cpup);
                cpup.setqElapsed(0);
                cpu.setRunningProcess(null);
            }
        }

        runCPU(cpu);
    }
}
