package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Commit.EMPTY_PARENT;
import static gitlet.Utils.*;
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
     * .gitlet
     *     |--objects
     *     |    |--blobs
     *     |    |   |--staged
     *     |    |   |--(stored Blobs)
     *     |    |--commits
     *     |    |--stats
     *     |    |   |--staged(file)
     *     |    |   |--remove(file)
     *     |--refs
     *     |    |--branches(file)
     *     |    |--(stored branches)
     *     |--HEAD(file)
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
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File BRANCH_FILE = join(REF_DIR, "branches");

    public static void checkRepoInitialized() {
        if (!isInitialized()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    public static void init() {
        if (isInitialized()) {
            System.out.println("A Gitlet version-control system already exists "
                    + "in the current directory.");
            System.exit(0);
        }
        createRepo();
        firstCommit();
    }

    public static void add(String file) {
        checkAddCondition(file);
        Blob blob = new Blob(file);
        if (isCurrentCommited(blob)) {
            if (BufferManager.removedContainsFile(blob)) {
                BufferManager.popRemoved(blob);
            } else if (BufferManager.stagedContainsFile(blob)) {
                BufferManager.unstage(blob);
            }
        } else if (isStored(blob)) {
            BufferManager.addStage(blob);
        } else {
            BufferManager.stageWithOverride(blob);
        }
    }

    private static boolean isStored(Blob blob) {
        return join(BLOBS_DIR, blob.getUid()).exists();
    }

    public static void commit(String message) {
        commitHandleMerge(message, Commit.EMPTY_PARENT);
    }

    /**
     * checkout -- [file name] handler.
     * @param fileName must in current commit.
     */
    public static void checkoutFile(String fileName) {
        String currentCommitUid = BranchManager.getHeadCommitUid();
        checkoutFileInCommit(currentCommitUid, fileName);
    }

    /**
     * checkout [commit id] -- [file name] handler.
     * @param commitUid SHA-1 of a commit, may be short(6 digits).
     * @param fileName must in the commit denoted by commitUid.
     */
    public static void checkoutFileInCommit(String commitUid, String fileName) {
        Commit targetCommit = getCommit(commitUid);
        TreeMap<String, String> files = targetCommit.getFiles();
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

    /**
     * Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory,
     * overwriting the versions of the files that are already there if they exist.
     * @param branchName String
     */
    public static void checkoutBranch(String branchName) {
        checkBranchCondition(branchName);
        Commit targetCommit = BranchManager.getBranchCommit(branchName);
        clearCWD();
        release(targetCommit);
        BufferManager.clearArea();
        BranchManager.checkout(branchName);
    }

    public static void log() {
        Commit commit = BranchManager.getHeadCommit();
        while (!commit.isInitialCommit()) {
            System.out.println(commit);
            commit = getCommit(commit.getParentUid());
        }
        System.out.println(commit);
    }

    public static void globalLog() {
        for (String commitUid : Objects.requireNonNull(plainFilenamesIn(COMMITS_DIR))) {
            Commit commit = getCommit(commitUid);
            System.out.println(commit);
        }
    }

    public static void remove(String fileName) {
        TreeMap<String, String> staged = BufferManager.getStaged();
        Commit currentCommit = BranchManager.getHeadCommit();
        TreeMap<String, String> files = currentCommit.getFiles();
        if (!staged.containsKey(fileName) && !files.containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        } else if (staged.containsKey(fileName)) {
            Blob blob = readObject(join(STAGED_DIR, fileName), Blob.class);
            BufferManager.unstage(blob);
        } else {
            BufferManager.addRemove(fileName);
            restrictedDelete(new File(CWD, fileName));
        }
    }

    /**
     * Prints out the ids of all commits that have the given commit message, one per line.
     * If there are multiple such commits, it prints the ids out on separate lines.
     * If no such commit exists, prints the error message Found no commit with that message.
     * @param message multiword message
     */
    public static void find(String message) {
        boolean isFound = false;
        for (String commitUid : Objects.requireNonNull(plainFilenamesIn(COMMITS_DIR))) {
            Commit commit = getCommit(commitUid);
            String commitMsg = commit.getMessage();
            if (commitMsg.equals(message)) {
                isFound = true;
                System.out.println(commit.getUid());
            }
        }
        if (!isFound) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * branch [branch name] handler.
     * @param branchName String
     */
    public static void attachBranch(String branchName) {
        TreeSet<String> branches = BranchManager.getBranches();
        if (branches.contains(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        BranchManager.createBranch(branchName);
    }

    public static void rmBranch(String branchName) {
        if (!BranchManager.getBranches().contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (BranchManager.getHeadBranch().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        BranchManager.deleteBranch(branchName);
    }

    /**
     * reset [commitUid] handler.
     * @param commitUid may be shorter than 40.
     */
    public static void reset(String commitUid) {
        Commit targetCommit = getCommit(commitUid);
        checkUntrackedFiles();
        String currentBranch = BranchManager.getHeadBranch();
        BranchManager.moveBranch(currentBranch, targetCommit.getUid());
        clearCWD();
        release(targetCommit);
        BufferManager.clearArea();
    }

    public static void showStatus() {
        BranchManager.showBranches();
        BufferManager.showFiles();
        showModified();
        showUntracked();
    }

    public static void merge(String branchName) {
        checkMergeCondition(branchName);
        Commit currentCommit = BranchManager.getHeadCommit();
        Commit mergingCommit = BranchManager.getBranchCommit(branchName);
        Commit splitCommit = getLatestCommonCommit(currentCommit, mergingCommit);
        checkMergeCommitCondition(branchName, splitCommit, mergingCommit, currentCommit);
        HashSet<String> checkoutFiles = new HashSet<>();
        boolean encounterConflict = false;
        TreeMap<String, String> filesInSplit = splitCommit.getFiles();
        TreeMap<String, String> filesInMerging = mergingCommit.getFiles();
        TreeMap<String, String> filesInHead = currentCommit.getFiles();
        for (String fileInSplit : filesInSplit.keySet()) {
            if (!filesInHead.containsKey(fileInSplit) && !filesInMerging.containsKey(fileInSplit)) {
                continue;
            }
            if (!filesInHead.containsKey(fileInSplit) && filesInMerging.containsKey(fileInSplit)) {
                Blob splitBlob = getBlob(filesInSplit.get(fileInSplit));
                Blob mergeBlob = getBlob(filesInMerging.get(fileInSplit));
                if (!mergeBlob.getUid().equals(splitBlob.getUid())) {
                    stageConflict(null, mergeBlob);
                    encounterConflict = true;
                }
                filesInMerging.remove(fileInSplit);
                continue;
            }
            if (filesInHead.containsKey(fileInSplit) && !filesInMerging.containsKey(fileInSplit)) {
                Blob splitBlob = getBlob(filesInSplit.get(fileInSplit));
                Blob headBlob = getBlob(filesInHead.get(fileInSplit));
                if (!headBlob.getUid().equals(splitBlob.getUid())) {
                    stageConflict(headBlob, null);
                    encounterConflict = true;
                } else {
                    remove(fileInSplit);
                }
                continue;
            }
            Blob splitBlob = getBlob(filesInSplit.get(fileInSplit));
            Blob headBlob = getBlob(filesInHead.get(fileInSplit));
            Blob mergeBlob = getBlob(filesInMerging.get(fileInSplit));
            if (mergeBlob.getUid().equals(splitBlob.getUid())) {
                filesInMerging.remove(fileInSplit);
                continue;
            }
            if (headBlob.getUid().equals(splitBlob.getUid())) {
                stageChange(fileInSplit, filesInSplit.get(fileInSplit), checkoutFiles);
                filesInMerging.remove(fileInSplit);
                continue;
            }
            if (mergeBlob.getUid().equals(headBlob.getUid())) {
                filesInMerging.remove(fileInSplit);
            } else {
                encounterConflict = true;
                stageConflict(headBlob, mergeBlob);
                filesInMerging.remove(fileInSplit);
            }
        }
        for (String fileInMerging : filesInMerging.keySet()) {
            if (!filesInHead.containsKey(fileInMerging)) {
                stageChange(fileInMerging, filesInMerging.get(fileInMerging), checkoutFiles);
                continue;
            }
            Blob headBlob = getBlob(filesInHead.get(fileInMerging));
            Blob mergeBlob = getBlob(filesInMerging.get(fileInMerging));
            if (!mergeBlob.getUid().equals(headBlob.getUid())) {
                stageConflict(headBlob, mergeBlob);
                encounterConflict = true;
            }
        }
        String mergedMessage = "Merged " + branchName
                + " into " + BranchManager.getHeadBranch() + ".";
        String mergingCommitUid = mergingCommit.getUid();
        commitHandleMerge(mergedMessage, mergingCommitUid);
        for (String file : checkoutFiles) {
            checkoutFileInCommit(mergingCommitUid, file);
        }
        if (encounterConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static void checkMergeCommitCondition(String branchName, Commit splitCommit,
                                                 Commit mergingCommit, Commit currentCommit) {
        if (splitCommit.getUid().equals(mergingCommit.getUid())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (splitCommit.getUid().equals(currentCommit.getUid())) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
    }

    private static Blob getBlob(String blobUid) {
        return readObject(join(BLOBS_DIR, blobUid), Blob.class);
    }

    private static void stageChange(String file, String fileUid, HashSet<String> checkoutFiles) {
        Blob stageBlob = readObject(join(BLOBS_DIR, fileUid), Blob.class);
        BufferManager.addStage(stageBlob);
        checkoutFiles.add(file);
    }

    private static void stageChanged(String file, String fileUid) {
    }

    private static void stageConflict(Blob headBlob, Blob mergeBlob) {
        String headContent;
        String mergeContent;
        String fileName;
        if (headBlob == null) {
            headContent = "";
            mergeContent = mergeBlob.getContent();
            fileName = mergeBlob.getFileName();
        } else if (mergeBlob == null) {
            headContent = headBlob.getContent();
            mergeContent = "";
            fileName = headBlob.getFileName();
        } else {
            headContent = headBlob.getContent();
            mergeContent = mergeBlob.getContent();
            fileName = mergeBlob.getFileName();
        }
        String conflictContent = constructConflictContent(headContent, mergeContent);
        writeContents(join(CWD, fileName), conflictContent);
        add(fileName);
    }

    private static String constructConflictContent(String headContent, String mergeContent) {
        StringBuilder sb = new StringBuilder("<<<<<<< HEAD\n");
        sb.append(headContent);
        sb.append("=======\n");
        sb.append(mergeContent);
        sb.append(">>>>>>>\n");
        return sb.toString();
    }

    private static Commit getLatestCommonCommit(Commit currentCommit, Commit mergingCommit) {
        HashSet<String> currentCommitSet = getCurrentBranchCommits(currentCommit);
        Queue<Commit> commits = new LinkedList<>();
        commits.add(mergingCommit);
        while (!commits.isEmpty()) {
            Commit commit = commits.poll();
            if (currentCommitSet.contains(commit.getUid())) {
                return commit;
            }
            if (!commit.getParentUid().equals(EMPTY_PARENT)) {
                commits.add(getCommit(commit.getParentUid()));
            }
            if (!commit.getSecondParentUid().equals(EMPTY_PARENT)) {
                commits.add(getCommit(commit.getSecondParentUid()));
            }
        }
        return null;
    }

    /**
     * Retrieve all commit along the way.
     * @param currentCommit commit
     * @return HashSet<String>
     */
    private static HashSet<String> getCurrentBranchCommits(Commit currentCommit) {
        HashSet<String> currentCommitSet = new HashSet<>();
        while (!currentCommit.isInitialCommit()) {
            currentCommitSet.add(currentCommit.getUid());
            if (!currentCommit.getSecondParentUid().equals(EMPTY_PARENT)) {
                currentCommitSet.add(currentCommit.getSecondParentUid());
            }
            currentCommit = getCommit(currentCommit.getParentUid());
        }
        currentCommitSet.add(currentCommit.getUid());
        return currentCommitSet;
    }

    private static void checkMergeCondition(String branchName) {
        if (!BufferManager.stagedAreaIsEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!BranchManager.containsBranch(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String currentBranch = BranchManager.getHeadBranch();
        if (currentBranch.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        checkUntrackedFiles();
    }

    private static void commitHandleMerge(String message, String secondParentUid) {
        checkCommitCondition(message);
        String currentCommitUid = BranchManager.getHeadCommitUid();
        TreeMap<String, String> staged = BufferManager.getStaged();
        TreeSet<String> removed = BufferManager.getRemoved();
        Commit candidate = new Commit(message, currentCommitUid, secondParentUid, staged, removed);
        BufferManager.storeTrackedFiles();
        candidate.store();
        BranchManager.moveBranch(candidate.getUid());
    }

    private static void release(Commit commit) {
        TreeMap<String, String> trackedFiles = commit.getFiles();
        for (String file : trackedFiles.keySet()) {
            String fileUid = trackedFiles.get(file);
            Blob blob = readObject(join(BLOBS_DIR, fileUid), Blob.class);
            writeContents(join(CWD, file), blob.getContent());
        }
    }

    private static void clearCWD() {
        for (String fileInCWD : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            restrictedDelete(join(CWD, fileInCWD));
        }
    }

    private static void checkBranchCondition(String branchName) {
        TreeSet<String> branches = BranchManager.getBranches();
        if (!branches.contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (BranchManager.getHeadBranch().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        checkUntrackedFiles();
    }

    /**
     * Check whether exists untracked files in current working directory.
     * @return true if exists
     */
    private static boolean existUntrackedFiles() {
        Commit currentCommit = BranchManager.getHeadCommit();
        TreeMap<String, String> commitFiles = currentCommit.getFiles();
        TreeMap<String, String> staged = BufferManager.getStaged();
        for (String file : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            if (!commitFiles.containsKey(file) && !staged.containsKey(file)) {
                return true;
            }
        }
        return false;
    }

    private static void checkUntrackedFiles() {
        if (existUntrackedFiles()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            System.exit(0);
        }
    }

    private static void showModified() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
    }

    private static void showUntracked() {
        System.out.println("=== Untracked Files ===");
        System.out.println();
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

    /**
     * Check if the current working version of the file is already committed.
     * @param blob file tracker
     * @return true if is Redundant
     */
    private static boolean isCurrentCommited(Blob blob) {
        Commit currentCommit = BranchManager.getHeadCommit();
        return currentCommit.getFiles().containsValue(blob.getUid());
    }

    /** Construct .git directory */
    private static void createRepo() {
        if (!COMMITS_DIR.mkdirs() || !STAGED_DIR.mkdirs()
                || !REF_DIR.mkdirs() || !STATS_DIR.mkdirs()) {
            throw new RuntimeException("Could not create repositories.");
        }
        BranchManager.setBranches();
        BufferManager.setBuffer();
    }

    /** Construct initial commit as init repo. */
    private static void firstCommit() {
        Commit firstCommit = new Commit();
        firstCommit.store();
        BranchManager.moveBranch(firstCommit.getUid());
    }

    private static boolean isInitialized() {
        return GITLET_DIR.exists();
    }
}
