package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import static gitlet.Utils.*;
import static gitlet.FileUtils.*;
import static gitlet.Commit.getCommit;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author PainJoker
 */
public class Repository {
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory.
     * TODO: show .gitlet structure
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
    public static final File STAGED_DIR = join(BLOBS_DIR, "staged");
    public static final File STATS_DIR = join(OBJECTS_DIR, "stats");
    public static final File STAGED_FILE = join(STATS_DIR, "staged");
    public static final File REMOVED_FILE = join(STATS_DIR, "removed");
    public static final File REF_DIR = join(GITLET_DIR, "refs");

    private static HashSet<String> branches;

    public static void checkRepoInitialized() {
        if (!isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void init() {
        if (isInitialized()) {
            System.out.println("A Gitlet version-control system already exists " +
                    "in the current directory.");
            System.exit(0);
        }
        createRepo();
        BufferManager.setBuffer();
        firstCommit();
    }

    public static void add(String file) {
        checkAddCondition(file);
        Blob blob = new Blob(file);
        if (isCommited(blob)) {
            if (BufferManager.stagedContainsFile(blob)) {
                BufferManager.unstage(blob);
            }
        } else {
            BufferManager.stageWithOverride(blob);
        }
    }

    public static void commit(String message) {
        checkCommitCondition(message);
        String currentCommitUid = getCommitUid("head");
        HashMap<String, String> staged = BufferManager.getStaged();
        Commit candidate = new Commit(message, currentCommitUid, staged);
        BufferManager.storeTrackedFiles();
        candidate.store();
        updateBranch(candidate);
    }

    /**
     * checkout -- [file name] handler.
     * @param fileName must in current commit.
     */
    public static void checkoutFile(String fileName) {
        String currentCommitUid = getCommitUid("head");
        checkoutFileInCommit(currentCommitUid, fileName);
    }

    /**
     * checkout [commit id] -- [file name] handler.
     * @param commitUid SHA-1 of a commit, may be short(6 digits).
     * @param fileName must in the commit denoted by commitUid.
     */
    public static void checkoutFileInCommit(String commitUid, String fileName) {
        // TODO: Handler the short commitUid situation.
        Commit currentCommit = getCommit(commitUid);
        TreeMap<String, String> files = currentCommit.getFiles();
        if (!files.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String fileUid = files.get(fileName);
        File blobPath = join(BLOBS_DIR, fileUid);
        Blob blob = readObject(blobPath, Blob.class);
        File currentFilePath = join(CWD, fileName);
        writeContents(currentFilePath, blob.getContent());
    }

    public static void log() {
        //TODO: Handle merged version
        Commit commit = getBranchCommit("head");
        while (!commit.isInitialCommit()) {
            System.out.println(commit);
            commit = getCommit(commit.getParentUid());
        }
        System.out.println(commit);
    }

    /**
     * Move the HEAD and commit branch to the current commit
     * @param candidate current commit
     */
    private static void updateBranch(Commit candidate) {
        moveBranch(candidate.getBranch(), candidate);
        moveBranch("head", candidate);
    }

    private static void checkCommitCondition(String message) {
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if (BufferManager.stagedAreaIsEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
    }

    private static void checkAddCondition(String file) {
        File filePath = join(CWD, file);
        if (!filePath.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }

    /** Let branch header point to the commit. */
    private static void moveBranch(String branch, Commit commit) {
        File branchPath = new File(REF_DIR, branch);
        writeContents(branchPath, commit.getUid());
    }

    /**
     * Check if the current working version of the file is identical to
     * the version in the current commit
     * @param blob file tracker
     * @return true if is Redundant
     */
    private static boolean isCommited(Blob blob) {
        Commit currentCommit = getBranchCommit("head");
        return currentCommit.getFiles().containsValue(blob.getUid());
    }

    /**
     * Retrieve current commit on branch.
     * @param branch String
     * @return Commit object
     */
    private static Commit getBranchCommit(String branch) {
        String CommitUid = getCommitUid(branch);
        return getCommit(CommitUid);
    }

    /**
     * Retrieve CommitUid from a particular branch
     * @param branch String
     * @return CommitUid
     */
    private static String getCommitUid(String branch) {
        File branchFile = join(REF_DIR, branch);
        return readContentsAsString(branchFile);
    }

    /** Construct .git directory */
    private static void createRepo() {
        makeDir(GITLET_DIR);
        makeDir(OBJECTS_DIR);
        makeDir(COMMITS_DIR);
        makeDir(BLOBS_DIR);
        makeDir(STAGED_DIR);
        makeDir(STATS_DIR);
        createFile(STAGED_FILE);
        createFile(REMOVED_FILE);
        makeDir(REF_DIR);
        createFile(new File(REF_DIR, "master"));
        createFile(new File(REF_DIR, "head"));
    }

    /** Construct initial commit as init repo. */
    private static void firstCommit() {
        Commit firstCommit = new Commit();
        writeObject(new File(COMMITS_DIR, firstCommit.getUid()), firstCommit);
        for (String branch : plainFilenamesIn(REF_DIR)) {
            File branchPath = join(REF_DIR, branch);
            writeContents(branchPath, firstCommit.getUid());
        }
    }

    private static boolean isInitialized() {
        return GITLET_DIR.exists();
    }

}
