package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        NsAndOpsConstruct(Ns, opCounts);
        for (int i = 0; i < Ns.size(); i++) {
            int numbersAdded = Ns.get(i);
            int opCount = opCounts.get(i);
            SLList<Integer> test = new SLList<>();
            for (int j = 0; j < numbersAdded; j++) {
                test.addLast(j);
            }
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < opCount; j++) {
                test.addLast(j);
            }
            double timeInSecond = sw.elapsedTime();
            times.addLast(timeInSecond);
        }
        printTimingTable(Ns, times, opCounts);
    }

    private static void NsAndOpsConstruct(AList<Integer> Ns, AList<Integer> opCounts) {
        for (int i = 0; i < 8; i++) {
            int factor = (int) Math.pow(2, i);
            Ns.addLast(1000 * factor);
            opCounts.addLast(10000);
        }
    }

}
