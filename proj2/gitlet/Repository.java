package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * see spec <a href="https://sp21.datastructur.es/materials/proj/proj2/proj2#detailed-spec-of-behavior">here</a>
 * does at a high level.
 *
 * @author incrd
 */
public class Repository {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * Directory to save objects.
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /**
     * Directory to save commits.
     */
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    /**
     * Directory to record head of branch.
     */
    public static final File BRANCH_DIR = join(GITLET_DIR, "refs", "heads");
    /**
     * Records current branch.
     */
    public static final File CUR_BRANCH = join(GITLET_DIR, "branch");
    public static final File ADD_STAGE = join(GITLET_DIR, "addstage");
    public static final File RM_STAGE = join(GITLET_DIR, "removestage");
    /**
     * Point to current commit.
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");

    public static boolean isInit() {
        return GITLET_DIR.exists();
    }

    public static void init() {
        if (isInit()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdirs();
        OBJECTS_DIR.mkdirs();
        COMMITS_DIR.mkdirs();
        BRANCH_DIR.mkdirs();
        try {
            ADD_STAGE.createNewFile();
            RM_STAGE.createNewFile();
            HEAD.createNewFile();
            CUR_BRANCH.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /* initial commit. */
        Commit initCommit = new Commit("initial commit", new Date(0));
        initCommit.genAndSetID();
        commit(initCommit);

        /* Write a empty hash map into the addstage and removestage. */
        HashMap<String, String> emptyMap = new HashMap<>();
        writeObject(ADD_STAGE, emptyMap);
        writeObject(RM_STAGE, emptyMap);

        /* Create master branch. */
        branch("master");
        setCurrentBranch("master");
    }

    private static void clearAddStage() {
        HashMap<String, String> emptyMap = new HashMap<>();
        writeObject(ADD_STAGE, emptyMap);
    }

    private static void clearRemoveStage() {
        HashMap<String, String> emptyMap = new HashMap<>();
        writeObject(RM_STAGE, emptyMap);
    }

    private static void setCurrentBranch(String branchName) {
        writeContents(CUR_BRANCH, branchName);
    }

    private static String getCurrentBranchName() {
        return readContentsAsString(CUR_BRANCH);
    }

    public static void add(String filename) {
        File addFile = new File(filename);
        if (!addFile.isFile()) {
            System.out.println("File does not exist.");
            return;
        }

        HashMap<String, String> removeFileToID = getRemoveStage();
        if (removeFileToID.containsKey(filename)) {
            removeFileToID.remove(filename);
            writeContents(RM_STAGE, removeFileToID);
            return;
        }

        byte[] fileContent = readContents(addFile);
        String fileID = sha1((Object) fileContent);
        /* Check if the file is tracked and unchanged. */
        Commit curCommit = getHEADCommit();
        if (Objects.equals(curCommit.getFileToID().get(filename), fileID)) {
            return;
        }

        /* Write file into objects folder. */
        File blobFile = join(OBJECTS_DIR, fileID);
        writeContents(blobFile, (Object) fileContent);

        /* Record the file into addstage. */
        HashMap<String, String> addFileToId = getAddStage();
        addFileToId.put(filename, fileID);
        writeObject(ADD_STAGE, addFileToId);
    }

    public static void commit(Commit commit) {
        File commitFile = join(COMMITS_DIR, commit.getID());
        writeObject(commitFile, commit);
        /* Update HEAD. */
        writeContents(HEAD, commit.getID());
    }

    private static void setHEAD(String commitID) {
        writeContents(HEAD, commitID);
    }

    private static void setHEAD(Commit commit) {
        writeContents(HEAD, commit.getID());
    }

    public static void commit(String message) {
        if (message == null || message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        Commit prevCommit = getHEADCommit();
        Commit curCommit = new Commit(message, new Date());
        HashMap<String, String> addFileToID = getAddStage();
        HashMap<String, String> removeFileToID = getRemoveStage();
        if (addFileToID.isEmpty() && removeFileToID.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        /* Copy fileToID from previous commit. */
        curCommit.getFileToID().putAll(prevCommit.getFileToID());
        /* Add file recorded in addstage. */
        curCommit.getFileToID().putAll(addFileToID);
        /* Remove file recorded in removestage. */
        for (String removeFile : removeFileToID.keySet()) {
            curCommit.getFileToID().remove(removeFile);
        }
        /* Set parents to previous commits. */
        curCommit.getParents().add(prevCommit.getID());
        /* Set ID for the commit. */
        curCommit.genAndSetID();

        commit(curCommit);
        /* Clear the hashMap in addstage and removestage. */
        HashMap<String, String> emptyMap = new HashMap<>();
        writeObject(ADD_STAGE, emptyMap);
        writeObject(RM_STAGE, emptyMap);
    }

    private static Commit getHEADCommit() {
        String commitID = getHEADCommitID();
        Commit commit = getCommit(commitID);
        return commit;
    }

    public static void rm(String filename) {
        HashMap<String, String> addFileToID = getAddStage();
        if (addFileToID.containsKey(filename)) {
            addFileToID.remove(filename);
            writeObject(ADD_STAGE, addFileToID);
            return;
        }

        Commit curCommit = getHEADCommit();
        if (curCommit.getFileToID().containsKey(filename)) {
            HashMap<String, String> removeFileToID = getRemoveStage();
            File file = new File(filename);
            removeFileToID.put(filename, null);
            writeObject(RM_STAGE, removeFileToID);
            if (file.isFile()) {
                file.delete();
            }
            return;
        }

        System.out.println("No reason to remove the file.");
    }

    public static void log() {
        Commit curCommit = getHEADCommit();
        while (true) {
            System.out.println("===");
            System.out.println(curCommit.toString());
            System.out.println();

            if (curCommit.getParents().isEmpty()) {
                break;
            }

            String nextCommitID = curCommit.getParents().get(0);
            curCommit = readObject(join(COMMITS_DIR, nextCommitID), Commit.class);
        }
    }

    public static void globalLog() {
        List<String> commitIDList = plainFilenamesIn(COMMITS_DIR);
        if (commitIDList == null) {
            return;
        }
        for (String commitID : commitIDList) {
            Commit commit = getCommit(commitID);
            System.out.println("===");
            System.out.println(commit.toString());
            System.out.println();
        }
    }

    public static void find(String message) {
        List<String> commitIDList = plainFilenamesIn(COMMITS_DIR);
        if (commitIDList == null) {
            return;
        }
        boolean found = false;
        for (String commitID : commitIDList) {
            Commit commit = getCommit(commitID);
            if (Objects.equals(commit.getMessage(), message)) {
                found = true;
                System.out.println(commit.getID());
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        List<String> branchList = plainFilenamesIn(BRANCH_DIR);
        String curBranchName = getCurrentBranchName();
        for (String branchName : branchList) {
            if (curBranchName.equals(branchName)) {
                System.out.print("*");
            }
            System.out.println(branchName);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        HashMap<String, String> addFileToID = getAddStage();
        for (String file : addFileToID.keySet()) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        HashMap<String, String> removeFileToID = getRemoveStage();
        for (String file : removeFileToID.keySet()) {
            System.out.println(file);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    private static String getHEADCommitID() {
        return readContentsAsString(HEAD);
    }


    /**
     * Already checked the input in Main.verifyCommands.
     * java gitlet.Main checkout -- [file name]
     * java gitlet.Main checkout [commit id] -- [file name]
     * java gitlet.Main checkout [branch name]
     */
    public static void checkoutFile(String filename) {
        Commit curCommit = getHEADCommit();
        checkoutFile(curCommit.getID(), filename);
    }

    public static void checkoutFile(String commitID, String filename) {
        Commit commit = getCommit(commitID);
        Map<String, String> fileToID = commit.getFileToID();
        if (!fileToID.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        String fileID = fileToID.get(filename);
        byte[] contents = readContents(join(OBJECTS_DIR, fileID));
        writeContents(join(CWD, filename), (Object) contents);
    }


    public static void checkoutBranch(String branchName) {
        if (branchName.equals(getCurrentBranchName())) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        String commitID = getBranchCommitID(branchName);
        /* Change HEAD. */
        setHEAD(commitID);
        /* Toggle branch. */
        setCurrentBranch(branchName);
        /* Clear stage. */
        clearAddStage();
        clearRemoveStage();
    }

    /**
     * Print "No commit with that id exists." and exit the program if the given commitID does not correspond a commit.
     *
     * @param commitID id of the commit.
     * @return the commit with the given commitID.
     */
    private static Commit getCommit(String commitID) {
        File commitFile = join(COMMITS_DIR, commitID);
        if (!commitFile.isFile()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(commitFile, Commit.class);
    }

    /**
     * @param commitID ID of the target commit to check out.
     */
    private static void checkoutCommit(String commitID) {
        Commit targetCommit = getCommit(commitID);
        Commit curCommit = getHEADCommit();
        Map<String, String> curFileToID = curCommit.getFileToID();
        Map<String, String> targetFileToID = targetCommit.getFileToID();
        List<String> filenames = plainFilenamesIn(CWD);
        if (filenames == null) {
            throw new GitletException("This abstract pathname does not denote a directory, or if an I/O error occurs.");
        }
        for (String filename : filenames) {
            if (!curFileToID.containsKey(filename) && targetFileToID.containsKey(filename)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        /* Delete files that in current branch but not in target branch. */
        for (String filename : curFileToID.keySet()) {
            if (!targetFileToID.containsKey(filename)) {
                restrictedDelete(filename);
            }
        }
        /* Overwrite file with target branch. */
        for (String filename : targetFileToID.keySet()) {
            String fileID = targetFileToID.get(filename);
            byte[] contents = readContents(join(OBJECTS_DIR, fileID));
            writeContents(join(CWD, filename), (Object) contents);
        }
    }

    /**
     * Before the first invoke, make sure the HEAD in initialized.
     *
     * @param branchName the name of branch to create.
     */
    public static void branch(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (branchFile.isFile()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        writeContents(branchFile, (Object) readContents(HEAD));
    }

    public static void rmBranch(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.isFile()) {
            System.out.println("A branch with that name does not exist.");
        }
        if (getCurrentBranchName().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
        }
        restrictedDelete(branchFile);
    }

    public static void reset(String commitID) {
        checkoutCommit(commitID);
        setHEAD(commitID);
    }

    public static void merge(String branchName) {
        /* Check add stage and remove stage. */
        HashMap<String, String> addFileToID = getAddStage();
        HashMap<String, String> removeFileToID = getRemoveStage();
        if (!addFileToID.isEmpty() || !removeFileToID.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        /* Check if attempting to merge a branch with itself. */
        if (branchName.equals(getCurrentBranchName())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        /* Check if the given branch exist. */
        checkBranchExists(branchName);

        Commit currentCommit = getHEADCommit();
        Commit givenCommit = getBranchCommit(branchName);

        /* Check if attempting to merge a branch with itself. */
        if (branchName.equals(getCurrentBranchName())) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        /* Find the latest common ancestor (split point). */
        Commit commonAncestor = getCommonAncestorCommit(currentCommit, givenCommit);

        /* If the split point is the given branch, fast-forward. */
        if (commonAncestor.getID().equals(givenCommit.getID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        /* If the split point is the current branch, fast-forward. */
        if (commonAncestor.getID().equals(currentCommit.getID())) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        /* Merge changes from the given branch into the current branch. */
        Commit newCommit = mergeChanges(currentCommit, givenCommit, commonAncestor);
        newCommit.setMessage("Merged " + branchName + " into " + getCurrentBranchName() + ".");
        commit(newCommit);
        newCommit.genAndSetID();
        checkoutCommit(newCommit.getID());
    }


    private static Commit mergeChanges(Commit currentCommit, Commit givenCommit, Commit commonAncestor) {
        Map<String, String> currentFileToID = currentCommit.getFileToID();
        Map<String, String> givenFileToID = givenCommit.getFileToID();
        Map<String, String> commonAncestorFileToID = commonAncestor.getFileToID();
        HashMap<String, String> addFileToID = new HashMap<>();
        HashMap<String, String> removeFileToID = new HashMap<>();

        for (String filename : givenFileToID.keySet()) {
            String givenFileID = givenFileToID.get(filename);
            String currentFileID = currentFileToID.get(filename);
            String ancestorFileID = commonAncestorFileToID.get(filename);
            if (Objects.equals(ancestorFileID, currentFileID) && !Objects.equals(ancestorFileID, givenFileID)) {
                if (givenFileID != null) {
                    addFileToID.put(filename, givenFileID);
                } else {
                    removeFileToID.put(filename, currentFileID);
                }
            } else if (!Objects.equals(ancestorFileID, currentFileID) && !Objects.equals(ancestorFileID, givenFileID)
                    && !Objects.equals(currentFileID, givenFileID)) {
                conflict(filename, currentFileID, givenFileID);
            }
        }

        // Create a new commit.
        Commit newCommit = new Commit("", new Date());
        newCommit.getFileToID().putAll(addFileToID);
        newCommit.getFileToID().keySet().removeAll(removeFileToID.keySet());
        newCommit.getParents().add(currentCommit.getID());
        newCommit.getParents().add(givenCommit.getID());
        return newCommit;
    }

    private static void conflict(String filename, String currentBranchFileID, String givenBranchFileID) {
        byte[] curFileContents = currentBranchFileID == null ? new byte[0] : readContents(join(OBJECTS_DIR, currentBranchFileID));
        byte[] givenFileContents = givenBranchFileID == null ? new byte[0] : readContents(join(OBJECTS_DIR, givenBranchFileID));
        File file = join(CWD, filename);
        writeContents(file, "<<<<<<< HEAD\n");
        writeContents(file, (Object) curFileContents);
        writeContents(file, "=======");
        writeContents(file, (Object) givenFileContents);
        writeContents(file, ">>>>>>>");
        System.out.println("Encountered a merge conflict.");
        System.exit(0);
    }


    private static Commit getCommonAncestorCommit(Commit commit_1, Commit commit_2) {
        HashSet<String> seen_1 = new HashSet<>();
        HashSet<String> seen_2 = new HashSet<>();

        while (!commit_1.getParents().isEmpty() || !commit_2.getParents().isEmpty()) {
            if (seen_1.contains(commit_2.getID()) || seen_2.contains(commit_1.getID())) {
                break;
            }
            if (!commit_1.getParents().isEmpty()) {
                String nextID_1 = commit_1.getParents().get(0);
                seen_1.add(commit_1.getID());
                commit_1 = getCommit(nextID_1);
            }

            if (!commit_2.getParents().isEmpty()) {
                String nextID_2 = commit_2.getParents().get(0);
                seen_2.add(commit_2.getID());
                commit_2 = getCommit(nextID_2);
            }
        }

        return seen_1.contains(commit_2.getID()) ? commit_1 : commit_2;
    }

    private static HashMap<String, String> getRemoveStage() {
        return readObject(RM_STAGE, HashMap.class);
    }

    private static HashMap<String, String> getAddStage() {
        return readObject(ADD_STAGE, HashMap.class);
    }

    private static void checkBranchExists(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.isFile()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
    }

    private static String getBranchCommitID(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.isFile()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        return readContentsAsString(branchFile);
    }

    private static Commit getBranchCommit(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.isFile()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String commitID = readContentsAsString(branchFile);
        return getCommit(commitID);
    }
}
