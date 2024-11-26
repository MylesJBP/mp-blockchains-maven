package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

    int num;

    Hash prevHash;

    Hash currentHash;

    Transaction transaction;

    Long nonce;

    HashValidator check;
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and
   * previous hash, mining to choose a nonce that meets the requirements
   * of the validator.
   *
   * @param num
   *   The number of the block.
   * @param transaction
   *   The transaction for the block.
   * @param prevHash
   *   The hash of the previous block.
   * @param check
   *   The validator used to check the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash,
      HashValidator check) {
      /*set the fields we can */
      this.num = num;
      this.prevHash = prevHash;
      this.transaction = transaction;
      this.check = check;
      
      this.computeHash();
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num
   *   The number of the block.
   * @param transaction
   *   The transaction for the block.
   * @param prevHash
   *   The hash of the previous block.
   * @param nonce
   *   The nonce of the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash, long nonce) {
    this.num = num;
    this.transaction = transaction;
    this.prevHash = prevHash;
    this.nonce = nonce;

    this.computeHash();
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute the hash of the block given all the other info already
   * stored in the block.
   */
  public void computeHash() {
    try {

    MessageDigest md = MessageDigest.getInstance("sha-256");

    byte[] ibytes = ByteBuffer.allocate(Integer.BYTES).putInt(this.num).array();
    byte[] amtbytes = ByteBuffer.allocate(Integer.BYTES).putInt(this.transaction.getAmount()).array();
    byte[] sourcebytes = this.transaction.getSource().getBytes();
    byte[] targetbytes = this.transaction.getTarget().getBytes();
    byte[] prevbytes = this.prevHash.getBytes();

    if (nonce == null) {
      for (long i = 0; i < Long.MAX_VALUE; i++) {
        md.update(ibytes);
        md.update(sourcebytes);
        md.update(targetbytes);
        md.update(amtbytes);
        if (this.num != 0) {
          md.update(prevbytes);
        }

        byte[] lbytes = ByteBuffer.allocate(Long.BYTES).putLong(i).array();
        md.update(lbytes);
        currentHash = new Hash(md.digest());
        if (check.isValid(currentHash)) {
          this.nonce = i;
          break;
        } 
        md.reset();
      }
    } else {
      byte[] noncebytes = ByteBuffer.allocate(Long.BYTES).putLong(this.nonce).array();
      md.update(ibytes);
      md.update(sourcebytes);
      md.update(targetbytes);
      md.update(amtbytes);
      if (this.num != 0) {
        md.update(prevbytes);
      }
      md.update(noncebytes);
      currentHash = new Hash(md.digest());
    } // else
    } catch (NoSuchAlgorithmException e) {}
  } // computeHash()

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  public int getNum() {
    return this.num;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return new Transaction(transaction.getSource(), transaction.getTarget(), transaction.getAmount()); // STUB
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return this.nonce;
  } // getNonce()

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  Hash getPrevHash() {
    return prevHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  Hash getHash() {
    return currentHash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {
    return "";  // STUB
  } // toString()
} // class Block
