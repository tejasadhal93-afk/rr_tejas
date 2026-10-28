import java.util.*;

public class RoundRobin {

    static class Process {
        int pid, arrivalTime, burstTime, remainingTime;
        int completionTime, turnaroundTime, waitingTime;

        Process(int pid, int arrivalTime, int burstTime) {
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        System.out.print("Enter time quantum: ");
        int quantum = sc.nextInt();

        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.print("P" + (i + 1) + " arrival time and burst time: ");
            int at = sc.nextInt();
            int bt = sc.nextInt();
            processes.add(new Process(i + 1, at, bt));
        }

        // Sort by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        Queue<Process> queue = new LinkedList<>();
        int time = 0, completed = 0, idx = 0;
        StringBuilder gantt = new StringBuilder();

        // Add first arrived process(es)
        while (idx < n && processes.get(idx).arrivalTime <= time) {
            queue.add(processes.get(idx++));
        }

        while (completed < n) {
            if (queue.isEmpty()) {
                // CPU idle: jump to next arrival
                time = processes.get(idx).arrivalTime;
                while (idx < n && processes.get(idx).arrivalTime <= time) {
                    queue.add(processes.get(idx++));
                }
                continue;
            }

            Process current = queue.poll();
            int execTime = Math.min(quantum, current.remainingTime);

            gantt.append("| P").append(current.pid).append(" (").append(time).append("-")
                 .append(time + execTime).append(") ");

            time += execTime;
            current.remainingTime -= execTime;

            // Add newly arrived processes before re-queuing current
            while (idx < n && processes.get(idx).arrivalTime <= time) {
                queue.add(processes.get(idx++));
            }

            if (current.remainingTime > 0) {
                queue.add(current);
            } else {
                current.completionTime = time;
                current.turnaroundTime = current.completionTime - current.arrivalTime;
                current.waitingTime = current.turnaroundTime - current.burstTime;
                completed++;
            }
        }
        gantt.append("|");

        // Output
        processes.sort(Comparator.comparingInt(p -> p.pid));
        double totalTAT = 0, totalWT = 0;

        System.out.println("\nPID\tAT\tBT\tCT\tTAT\tWT");
        for (Process p : processes) {
            System.out.println("P" + p.pid + "\t" + p.arrivalTime + "\t" + p.burstTime + "\t"
                    + p.completionTime + "\t" + p.turnaroundTime + "\t" + p.waitingTime);
            totalTAT += p.turnaroundTime;
            totalWT += p.waitingTime;
        }

        System.out.println("\nGantt Chart:\n" + gantt);
        System.out.printf("%nAverage Turnaround Time: %.2f%n", totalTAT / n);
        System.out.printf("Average Waiting Time: %.2f%n", totalWT / n);

        sc.close();
    }
}
