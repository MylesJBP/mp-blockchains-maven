package edu.grinnell.csc207.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;

import edu.grinnell.csc207.blockchains.Block;
import edu.grinnell.csc207.blockchains.BlockChain;
import edu.grinnell.csc207.blockchains.HashValidator;
import edu.grinnell.csc207.blockchains.Transaction;
import edu.grinnell.csc207.util.IOUtils;

/**
 * A simple UI for our BlockChain class.
 *
 * @author Myles Bohrer-Purnell
 * @author Anthony Castleberry
 * @author Samuel A. Rebelsky
 */
public class BlockChainUI {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The number of bytes we validate. Should be set to 3 before submitting.
   */
  static final int VALIDATOR_BYTES = 0;

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Print out the instructions.
   *
   * @param pen
   *   The pen used for printing instructions.
   */
  public static void instructions(PrintWriter pen) {
    pen.println("""
      Valid commands:
        mine: discovers the nonce for a given transaction
        append: appends a new block onto the end of the chain
        remove: removes the last block from the end of the chain
        check: checks that the block chain is valid
        users: prints a list of users
        balance: finds a user's balance
        transactions: prints out the chain of transactions
        blocks: prints out the chain of blocks (for debugging only)
        help: prints this list of commands
        quit: quits the program""");
  } // instructions(PrintWriter)

  // +------+--------------------------------------------------------
  // | Main |
  // +------+

  /**
   * Run the UI.
   *
   * @param args
   *   Command-line arguments (currently ignored).
   */
  public static void main(String[] args) throws Exception {
    PrintWriter pen = new PrintWriter(System.out, true);
    BufferedReader eyes = new BufferedReader(new InputStreamReader(System.in));

    // Set up our blockchain.
    HashValidator validator =
        (h) -> {
          if (h.length() < VALIDATOR_BYTES) {
            return false;
          } // if
          for (int v = 0; v < VALIDATOR_BYTES; v++) {
            if (h.get(v) != 0) {
              return false;
            } // if
          } // for
          return true;
        };
    BlockChain chain = new BlockChain(validator);

    instructions(pen);

    boolean done = false;

    String source;
    String target;
    int amount;

    while (!done) {
      pen.print("\nCommand: ");
      pen.flush();
      String command = eyes.readLine();
      if (command == null) {
        command = "quit";
      } // if

      switch (command.toLowerCase()) {
        case "append":
          String nonce;
          int numNonce;
          String strAmnt;
          pen.print("\nSource (return for deposit): ");
          pen.flush();
          source = eyes.readLine();
          pen.print("\nTarget: ");
          pen.flush();
          target = eyes.readLine();
          pen.print("\nAmount: ");
          pen.flush();
          strAmnt = eyes.readLine();
          pen.print("\nNonce: ");
          pen.flush();
          nonce = eyes.readLine();

          amount = Integer.parseInt(strAmnt);
          numNonce = Integer.parseInt(nonce);
          chain.append(new Block(chain.getSize(), new Transaction(source, target, amount), chain.getHash(), numNonce));
          break;

        case "balance":
          pen.print("\nUser: ");
          pen.flush();
          source = eyes.readLine();
          pen.printf(source + "'s balance is " + chain.balance(source));
          break;

        case "blocks":
          Iterator<Block> blockIter = chain.blocks();
          while(blockIter.hasNext()) {
            pen.println(blockIter.next().toString());
          } // while
          break;

        case "check":
          if (chain.isCorrect()) {
            pen.println("The blockchain checks out.");
          } else {
            pen.println("Something is wrong");
          } // if/else
          break;

        case "help":
          instructions(pen);
          break;

        case "mine":
          source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
          target = IOUtils.readLine(pen, eyes, "Target: ");
          amount = IOUtils.readInt(pen, eyes, "Amount: ");
          Block b = chain.mine(new Transaction(source, target, amount));
          pen.println("Nonce: " + b.getNonce());
          break;

        case "quit":
          done = true;
          break;

        case "remove":
          chain.removeLast();
          break;

        case "transactions":
          Iterator<Transaction> transIter = chain.iterator();
          while(transIter.hasNext()) {
            pen.println(transIter.next().toString());
          } // while
          break;

        case "users":
          Iterator<Block> userIter = chain.blocks();
          while(userIter.hasNext()) {
            pen.println(userIter.next().getTransaction().getSource());
          } // while
          break;

        default:
          pen.printf("invalid command: '%s'. Try again.\n", command);
          break;
      } // switch
    } // while

    pen.printf("\nGoodbye\n");
    eyes.close();
    pen.close();
  } // main(String[])
} // class BlockChainUI
