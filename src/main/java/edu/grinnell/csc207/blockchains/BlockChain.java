package edu.grinnell.csc207.blockchains;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A full blockchain.
 *
 * @author Myles Bohrer-Purnell
 * @author Anthony Castleberry
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /** First block/node in the blockchain. */
  Node firstBlock;

  /** Last block/node in the blockchain. */
  Node tailBlock;

  /** The number of blocks in the blockchain. */
  int size;

  HashValidator check;
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param check
   *   The validator used to check elements.
   */
  public BlockChain(HashValidator check) {
    this.size = 1;
    this.firstBlock = new Node (new Block(0, new Transaction("", "", 0), new Hash(new byte[] {}), check));
    this.tailBlock = this.firstBlock;
    this.check = check;
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Mine for a new valid block for the end of the chain, returning that
   * block.
   *
   * @param t
   *   The transaction that goes in the block.
   *
   * @return a new block with correct number, hashes, and such.
   */
  public Block mine(Transaction t) {
    return new Block(this.size, t, tailBlock.getBlock().getHash(), this.check);
  } // mine(Transaction)

  /**
   * Get the number of blocks curently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return this.size;
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk
   *   The block to add to the end of the chain.
   *
   * @throws IllegalArgumentException if (a) the hash is not valid, (b)
   *   the hash is not appropriate for the contents, or (c) the previous
   *   hash is incorrect.
   */
  public void append(Block blk) {
    blk.prevHash = this.tailBlock.getBlock().getHash();
    Node newNode = new Node(blk);
    this.tailBlock.add(newNode);
    this.tailBlock = newNode;
    this.size++;
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's
   *   not removed) or true otherwise (in which case the last block
   *   is removed).
   */
  public boolean removeLast() {
    if (this.tailBlock.getBlock().equals(this.firstBlock.getBlock())) {
      return false;
    } else {
      Node prevNode = this.firstBlock;
      Node node = this.firstBlock.getNext();
      while(node.hasNext()) {
        prevNode = node;
        node = node.getNext();
      } // while
      prevNode.removeNext();
      this.tailBlock = prevNode;
      this.size--;
      return true;
    } // if/else
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    return this.tailBlock.getBlock().getHash();
  } // getHash()

  /**
   * Determine if the blockchain is correct in that (a) the balances are
   * legal/correct at every step, (b) that every block has a correct
   * previous hash field, (c) that every block has a hash that is correct
   * for its contents, and (d) that every block has a valid hash.
   *
   * @return true if the blockchain is correct and false otherwise.
   */
  public boolean isCorrect() {
    Node newNode = this.firstBlock;

    while (newNode != tailBlock.next) {
      if (balance(newNode.getBlock().transaction.getSource()) < newNode.getBlock().transaction.getAmount() && !newNode.getBlock().transaction.getSource().equals("")) {
        return false;
      } else if (newNode.hasNext() && newNode.next.getBlock().getPrevHash() != newNode.getBlock().getHash()) {
        return false;
      } else if (!this.check.isValid(newNode.getBlock().getHash())) {
        return false;
      } else if (!newNode.getBlock().getHash().equals(computeHash(newNode))) {
        return false;
      } // if/else
      newNode = newNode.next;
    } // while
    return true;
  } // isCorrect()

  /**
   * Determine if the blockchain is correct in that (a) the balances are
   * legal/correct at every step, (b) that every block has a correct
   * previous hash field, (c) that every block has a hash that is correct
   * for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception
   *   If things are wrong at any block.
   */
  public void check() throws Exception {
    Node newNode = this.firstBlock;

    while (newNode != tailBlock.next) {
      if (balance(newNode.getBlock().transaction.getSource()) < newNode.getBlock().transaction.getAmount() && !newNode.getBlock().transaction.getSource().equals("")) {
        throw new Exception("Incorrect Amounts for User: " + newNode.getBlock().transaction.getSource());
      } else if (newNode.hasNext() && newNode.next.getBlock().getPrevHash() != newNode.getBlock().getHash()) {
        throw new Exception("Incorrect Previous Hash for Block: " + newNode.getBlock().getNum());
      } else if (!this.check.isValid(newNode.getBlock().getHash())) {
        throw new Exception("Incorrect Hash for Block: " + newNode.getBlock().getNum());
      } else if (!newNode.getBlock().getHash().equals(computeHash(newNode))) {
          throw new Exception("Incorrect Hash for Block: " + newNode.getBlock().getNum());
      } // else/if
      newNode = newNode.next;
    } // while
  } // check()

   /**
   * Return an iterator of all the people who participated in the
   * system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    return new Iterator<String>() {
      Node nextNode = BlockChain.this.firstBlock;
      Node tailNode = BlockChain.this.tailBlock;
      public boolean hasNext() {
        return !(nextNode.equals(tailNode));
      } // hasNext()

      public String next() {
        String trgt = nextNode.getBlock().getTransaction().getTarget();
        nextNode = nextNode.getNext();
        return trgt;
      } // next()
    };
  } // users()

  /**
   * Find one user's balance.
   *
   * @param user
   *   The user whose balance we want to find.
   *
   * @return that user's balance (or 0, if the user is not in the system).
   */
  public int balance(String user) {
    Node currentNode = this.firstBlock;
    int balance = 0;
    for (int i = 0; i < this.size; i++) {
      if (currentNode.getBlock().getTransaction().getSource().equals(user)) {
        balance -= currentNode.getBlock().getTransaction().getAmount();
      } // if
      if (currentNode.getBlock().getTransaction().getTarget().equals(user)) {
        balance += currentNode.getBlock().getTransaction().getAmount();
      } // if
      currentNode = currentNode.getNext();
    } // while
    return balance;
  } // balance()

  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {

      Node nextBlock = BlockChain.this.firstBlock;

      public boolean hasNext() {
        return !(nextBlock == null);
      } // hasNext()

      public Block next() {
        Block block = nextBlock.getBlock();
        nextBlock = nextBlock.getNext();
        return block;
      } // next()
    };
  } // blocks()

  /**
   * Get an interator for all the transactions in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {

      Node nextBlock = BlockChain.this.firstBlock;

      public boolean hasNext() {
        return !(nextBlock.equals(BlockChain.this.tailBlock));
      } // hasNext()

      public Transaction next() {
        Transaction transaction = nextBlock.getBlock().getTransaction();
        nextBlock = nextBlock.getNext();
        return transaction;
      } // next()
    };
  } // iterator()

} // class BlockChain
