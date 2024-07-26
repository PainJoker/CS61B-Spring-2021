package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

import static gitlet.Repository.REMOVED_FILE;
import static gitlet.Repository.STAGED_FILE;
import static gitlet.Repository.STAGED_DIR;
import static gitlet.Repository.BLOBS_DIR;
import static gitlet.Utils.*;
import static java.nio.file.Files.move;

/**
 * Handle the stage and remove area logic.
 *
 * @author PainJoker
 */
public class BufferManager {
    public static TreeMap<String, String> staged;
    public static TreeSet<String> removed;

    public static void setBuffer() {
        staged = new TreeMap<>();
        removed = new TreeSet<>();
        writeObject(STAGED_FILE, staged);
        writeObject(REMOVED_FILE, removed);
    }

    /** Clear the Staging files of stage area. */
    public static void clearStageTrack() {
        staged = new TreeMap<>();
        writeObject(STAGED_FILE, staged);
    }

    /** Unstage the blob. */
    public static void unstage(Blob blob) {
        File stagedBlob = new File(STAGED_DIR, blob.getFileName());
        if (!stagedBlob.delete()) {
            throw new RuntimeException("Failed to delete staged file: " + stagedBlob.getAbsolutePath());
        }
        removeStage(blob);
    }

    /** Check stage area whether is empty. */
    public static boolean stagedAreaIsEmpty() {
        staged = getStaged();
        removed = getRemoved();
        return staged.isEmpty() && removed.isEmpty();
    }

    /**
     * Get tracking information of stage area.
     * @return stage mapping between file name and corresponding SHA-1
     */
    public static TreeMap<String, String> getStaged() {
        return readObject(STAGED_FILE, TreeMap.class);
    }

    /**
     * Check whether staged File through its file name.
     * @return true if it is staged.
     */
    public static boolean stagedContainsFile(Blob blob) {
        staged = readObject(STAGED_FILE, TreeMap.class);
        return staged.containsKey(blob.getFileName());
    }

    /**
     * check if this particular file is removed.
     * @return true if is removed.
     */
    public static boolean removedContainsFile(Blob blob) {
        removed = readObject(REMOVED_FILE, TreeSet.class);
        return removed.contains(blob.getFileName());
    }

    /**
     * Staging an already-staged file overwrites the previous entry
     * in the staging area with the new contents.
     * @param blob The content of file need to be added.
     */
    public static void stageWithOverride(Blob blob) {
        staged = readObject(STAGED_FILE, TreeMap.class);
        if (stagedContainsFile(blob)) {
            unstage(blob);
        }
        stage(blob);
        addStage(blob);
    }

    /** Write blob into STAGE_DIR. */
    private static void stage(Blob blob) {
        File stagedFile = join(STAGED_DIR, blob.getFileName());
        writeObject(stagedFile, blob);
    }

    /** Lose track of blob */
    private static void removeStage(Blob blob) {
        staged = readObject(STAGED_FILE, TreeMap.class);
        staged.remove(blob.getFileName());
        writeObject(STAGED_FILE, staged);
    }

    /** add track of blob. */
    public static void addStage(Blob blob) {
        staged = readObject(STAGED_FILE, TreeMap.class);
        staged.put(blob.getFileName(), blob.getUid());
        writeObject(STAGED_FILE, staged);
    }

    public static void storeTrackedFiles() {
        for (String file : Objects.requireNonNull(plainFilenamesIn(STAGED_DIR))) {
            File blobFile = join(STAGED_DIR, file);
            Blob blob = readObject(blobFile, Blob.class);
            try {
                move(blobFile.toPath(), new File(BLOBS_DIR, blob.getUid()).toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        clearArea();
    }

    /** track removed blobs. */
    public static void addRemove(String fileName) {
        removed = readObject(REMOVED_FILE, TreeSet.class);
        removed.add(fileName);
        writeObject(REMOVED_FILE, removed);
    }

    public static TreeSet<String> getRemoved() {
        return readObject(REMOVED_FILE, TreeSet.class);
    }

    public static void showFiles() {
        showStaged();
        showRemoved();
    }

    private static void showStaged() {
        staged = getStaged();
        System.out.println("=== Staged Files ===");
        for (String file : staged.keySet()) {
            System.out.println(file);
        }
        System.out.println();
    }

    private static void showRemoved() {
        removed = getRemoved();
        System.out.println("=== Removed Files ===");
        for (String file : removed) {
            System.out.println(file);
        }
        System.out.println();
    }

    /**
     * Make stage area clean and lose the staged and removed track.
     */
    public static void clearArea() {
        for (String file : Objects.requireNonNull(plainFilenamesIn(STAGED_DIR))) {
            File stagedFile = join(STAGED_DIR, file);
            if (!stagedFile.delete()) {
                throw new RuntimeException("Failed to delete staged file: " + stagedFile.getAbsolutePath());
            }
        }
        clearStageTrack();
        clearRemoveTrack();
    }

    private static void clearRemoveTrack() {
        removed = new TreeSet<>();
        writeObject(REMOVED_FILE, removed);
    }

    public static void popRemoved(Blob blob) {
        removed = getRemoved();
        removed.remove(blob.getFileName());
        writeObject(REMOVED_FILE, removed);
    }
}
