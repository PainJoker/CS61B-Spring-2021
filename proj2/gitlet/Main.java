package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author PainJoker
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 2);
                Repository.commit(args[1]);
                break;
            case "checkout":
                Repository.checkRepoInitialized();
                if (args.length == 3 && args[1].equals("--")) {
                    Repository.checkoutFile(args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {
                    Repository.checkoutFileInCommit(args[1], args[3]);
                } else if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "log":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 1);
                Repository.log();
                break;
            case "rm":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 2);
                Repository.remove(args[1]);
                break;
            case "global-log":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 1);
                Repository.globalLog();
                break;
            case "find":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "branch":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 2);
                Repository.attachBranch(args[1]);
                break;
            case "rm-branch":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 2);
                Repository.rmBranch(args[1]);
                break;
            case "status":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 1);
                Repository.showStatus();
                break;
            case "reset":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                Repository.checkRepoInitialized();
                validateNumArgs(args, 2);
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     *
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
