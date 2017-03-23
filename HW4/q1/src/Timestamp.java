/**
 * Created by Ali on 3/23/2017.
 */
public class Timestamp implements Comparable<Timestamp> {
    int pid;
    int logicalClock;
    String message;
    public Timestamp(String input) {
        String[] in = input.split(":");
        this.pid = Integer.parseInt(in[1]);
        this.logicalClock = Integer.parseInt(in[2]);
        this.message = in[3];
    }
    public Timestamp(int id, int clk, String msg) {
        pid = id;
        logicalClock = clk;
        message = msg;
    }

    public String toString() {
        return pid + " " + logicalClock + " " + message;
    }

    @Override
    public int compareTo(Timestamp o) {
        if (logicalClock > o.logicalClock)
            return 1;
        if (logicalClock <  o.logicalClock)
            return -1;
        if (pid > o.pid) return 1;
        if (pid < o.pid)
            return -1;
        return 0;
    }
}
