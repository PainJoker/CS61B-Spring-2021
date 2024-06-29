package timingtest;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        NsConstruct(Ns);
        for (int i = 0; i < Ns.size(); i++) {
            int opNumbers = Ns.get(i);
            opCounts.addLast(opNumbers);
            AList<Integer> test = new AList<>();
            Stopwatch sw = new Stopwatch();
            for (int op = 0; op < opNumbers; op++) {
                test.addLast(op);
            }
            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
        }
        printTimingTable(Ns, times, opCounts);
    }

    private static void NsConstruct(AList<Integer> Ns) {
        for (int i = 0; i < 8; i++) {
            int factor = (int) Math.pow(2, i);
            Ns.addLast(1000 * factor);
        }
    };
}
