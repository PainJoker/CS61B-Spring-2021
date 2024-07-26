package gitlet;

import java.io.File;
import java.util.TreeSet;

import static gitlet.Commit.getCommit;
import static gitlet.Repository.HEAD_FILE;
import static gitlet.Repository.REF_DIR;
import static gitlet.Repository.BRANCH_FILE;
import static gitlet.Utils.*;

/**
 * Handle the operation about branch.
 *
 * @author PainJoker
 */
public class BranchManager {
    public static TreeSet<String> branches;

    public static void setBranches() {
        branches = new TreeSet<>();
        branches.add("master");
        writeObject(BRANCH_FILE, branches);
        writeContents(HEAD_FILE, "master");
    }

    /**
     * Let branch point the designated commit
     * @param commitUid String
     */
    public static void moveBranch(String commitUid) {
        String branchName = getHeadBranch();
        writeContents(join(REF_DIR, branchName), commitUid);
    }

    /**
     * checkout branch [branch name] call back func.
     * @param branchName added branch name
     * @param commitUid where branch points.
     */
    public static void moveBranch(String branchName, String commitUid) {
        writeContents(join(REF_DIR, branchName), commitUid);
    }

    public static TreeSet<String> getBranches() {
        return readObject(BRANCH_FILE, TreeSet.class);
    }

    /**
     * Retrieve HEAD pointed branch.
     * @return String represented branch
     */
    public static String getHeadBranch() {
        return readContentsAsString(HEAD_FILE);
    }

    /**
     * @return HEAD pointed commit.
     */
    public static Commit getHeadCommit() {
        String headBranch = getHeadBranch();
        return getBranchCommit(headBranch);
    }

    /**
     * Retrieve current commit SHA-1
     * @return current commit Uid
     */
    public static String getHeadCommitUid() {
        String headBranch = getHeadBranch();
        return getCommitUid(headBranch);
    }

    /**
     * Retrieve branch pointed commit.
     * @param branch String
     * @return Commit object
     */
    public static Commit getBranchCommit(String branch) {
        String CommitUid = getCommitUid(branch);
        return getCommit(CommitUid);
    }

    /**
     * Retrieve CommitUid from a particular branch
     * @param branch String
     * @return CommitUid
     */
    public static String getCommitUid(String branch) {
        File branchFile = join(REF_DIR, branch);
        return readContentsAsString(branchFile);
    }

    public static void createBranch(String branchName) {
        addBranch(branchName);

        String currentCommitUid = getHeadCommitUid();
        moveBranch(branchName, currentCommitUid);
    }

    public static void deleteBranch(String branchName) {
        removeBranch(branchName);
        File branchFile = join(REF_DIR, branchName);
        if (!branchFile.delete()) {
            throw new RuntimeException("Failed to delete branch " + branchName);
        }
    }

    public static void showBranches() {
        TreeSet<String> branches = getBranches();
        System.out.println("=== Branches ===");
        for (String branch : branches) {
            if (branch.equals(getHeadBranch())) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
    }

    public static boolean containsBranch(String branchName) {
        branches = getBranches();
        return branches.contains(branchName);
    }

    private static void removeBranch(String branchName) {
        TreeSet<String> branches = getBranches();
        branches.remove(branchName);
        writeObject(BRANCH_FILE, branches);
    }

    private static void addBranch(String branchName) {
        TreeSet<String> branches = getBranches();
        branches.add(branchName);
        writeObject(BRANCH_FILE, branches);
    }

    public static void checkout(String branchName) {
        writeContents(HEAD_FILE, branchName);
    }
}
