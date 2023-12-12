package gitlet;

import java.io.File;
import java.util.Objects;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        verifyCommands(args);
        String firstArg = args[0];
        if (Objects.equals(firstArg, "init")) {
            Repository.init();
            return;
        }
        if (!Repository.isInit()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        switch (firstArg) {
            case "status" -> {
                Repository.status();
            }
            case "log" -> {
                Repository.log();
            }
            case "global-log" -> {
                Repository.globalLog();
            }
            case "add" -> {
                Repository.add(args[1]);
            }
            case "commit" -> {
                Repository.commit(args[1]);
            }
            case "rm" -> {
                Repository.rm(args[1]);
            }
            case "find" -> {
                Repository.find(args[1]);
            }
            case "branch" -> {
                Repository.branch(args[1]);
            }
            case "checkout" -> {
                /*
                 * java gitlet.Main checkout -- [file name]
                 * java gitlet.Main checkout [commit id] -- [file name]
                 * java gitlet.Main checkout [branch name]
                 */
                if (args.length - 1 == 1) {
                    Repository.checkoutBranch(args[1]);
                } else if (args.length - 1 == 2) {
                    Repository.checkoutFile(args[2]);
                } else if (args.length - 1 == 3) {
                    Repository.checkoutFile(args[1], args[3]);
                }
            }
            case "rm-branch" -> {
                Repository.rmBranch(args[1]);
            }
            case "reset" -> {
                Repository.reset(args[1]);
            }
            case "merge" -> {
                Repository.merge(args[1]);
            }
        }
    }

    public static void verifyCommands(String[] args) {
        String firstArg = args[0];
        boolean islegal = true;
        switch (firstArg) {
            case "init", "log", "global-log", "status" -> {
                /* no operand */
                if (args.length - 1 != 0) islegal = false;
            }
            case "add", "commit", "rm", "find", "branch", "rm-branch", "reset", "merge" -> {
                /* one operand */
                if (args.length - 1 != 1) islegal = false;
            }
            case "checkout" -> {
                if (args.length - 1 == 2 && Objects.equals(args[1], "--")) {
                    /* checkout -- [file name] */
                } else if (args.length - 1 == 3 && Objects.equals(args[2], "--")) {
                    /* checkout [commit id] -- [file name] */
                } else if (args.length - 1 == 1) {
                    /* checkout [branch name] */
                } else {
                    islegal = false;
                }
            }
            default -> {
                System.out.println("No command with that name exists.");
                System.exit(0);
            }
        }

        if (!islegal) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
