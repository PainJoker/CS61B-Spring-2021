/** Class that prints the Collatz sequence starting from a given number.
 *  @author PainJoker
 */
public class Collatz {

    /** Buggy implementation of nextNumber! */
    public static int nextNumber(int n) {
        if (isOdd(n)) {
            return 3 * n + 1;
        } else {
            return n / 2;
        }
    }

    public static boolean isOdd(int n) {
        return n % 2 == 1;
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
        System.out.println();
    }
}

