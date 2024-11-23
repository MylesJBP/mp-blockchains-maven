package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Anthony Castleberry
 * @author Myles Bohrer-Purnell
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /** The block number in the blockchain. */
  int num;

  /** The hash of the previous block. */
  Hash prevHash;

  /** The hash of the current block. */
  Hash currentHash;

  /** The full hash of the block. */
  Hash fullHash;

  /** transaction data of the block. */
  Transaction transaction;

  /** Nonce of the block. */
  long nonce;
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and
   * previous hash, mining to choose a nonce that meets the requirements
   * of the validator.
   *
   * @param inputNum
   *   The number of the block.
   * @param inputTransaction
   *   The transaction for the block.
   * @param inputPrevHash
   *   The hash of the previous block.
   * @param check
   *   The validator used to check the block.
   */
  public Block(int inputNum, Transaction inputTransaction, Hash inputPrevHash,
               HashValidator check) {
    /*set the fields we can */
    this.num = inputNum;
    this.prevHash = inputPrevHash;
    this.transaction = inputTransaction;
    try {
      MessageDigest md = MessageDigest.getInstance("sha-256");
      /*
        * turn the fields into byte arrays so they can be updated into the message digest.
        * we make individual byte arrays for each field in transaction.
      */
      byte[] ibytes = ByteBuffer.allocate(Integer.BYTES).putInt(this.num).array();
      byte[] amtbytes = ByteBuffer.allocate(Integer.BYTES)
                        .putInt(this.transaction.getAmount()).array();
      byte[] sourcebytes = this.transaction.getSource().getBytes();
      byte[] targetbytes = this.transaction.getTarget().getBytes();
      byte[] prevbytes = this.prevHash.getBytes();

      /*
        * updates all the fields we already have, updates the potential nonce long i, then checks
        * the resulting hash for validity, trying the next long if not.
      */
      for (long i = 0; i < Long.MAX_VALUE; i++) {
        md.update(ibytes);
        md.update(sourcebytes);
        md.update(targetbytes);
        md.update(amtbytes);
        if (this.num != 0) {
          md.update(prevbytes);
        } // if

        byte[] lbytes = ByteBuffer.allocate(Long.BYTES).putLong(i).array();
        md.update(lbytes);
        currentHash = new Hash(md.digest());
        if (check.isValid(currentHash)) {
          this.nonce = i;
          break;
        } // if
        md.reset();
      } // for

      /*compute the full hash for the block with everything before plus the new validated hash */
      md.update(ibytes);
      md.update(sourcebytes);
      md.update(targetbytes);
      md.update(amtbytes);
      if (this.num != 0) {
        md.update(prevbytes);
      } // if
      byte[] lbytes = ByteBuffer.allocate(Long.BYTES).putLong(this.nonce).array();
      md.update(lbytes);
      md.update(currentHash.getBytes());
      fullHash = new Hash(md.digest());
    } catch (Exception e) {
      // catch for error in hash gathering process
    } // try/catch
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param inputNum
   *   The number of the block.
   * @param inputTransaction
   *   The transaction for the block.
   * @param inputPrevHash
   *   The hash of the previous block.
   * @param inputNonce
   *   The nonce of the block.
   */
  public Block(int inputNum, Transaction inputTransaction, Hash inputPrevHash, long inputNonce) {
    this.num = inputNum;
    this.transaction = inputTransaction;
    this.prevHash = inputPrevHash;
    this.nonce = inputNonce;

    try {
      MessageDigest md = MessageDigest.getInstance("sha-256");
      byte[] ibytes = ByteBuffer.allocate(Integer.BYTES).putInt(this.num).array();
      byte[] amtbytes = ByteBuffer.allocate(Integer.BYTES)
                        .putInt(this.transaction.getAmount()).array();
      byte[] sourcebytes = this.transaction.getSource().getBytes();
      byte[] targetbytes = this.transaction.getTarget().getBytes();
      byte[] prevbytes = this.prevHash.getBytes();
      byte[] noncebytes = ByteBuffer.allocate(Long.BYTES).putLong(this.nonce).array();

      md.update(ibytes);
      md.update(sourcebytes);
      md.update(targetbytes);
      md.update(amtbytes);
      if (this.num != 0) {
        md.update(prevbytes);
      } // if
      md.update(noncebytes);
      currentHash = new Hash(md.digest());

      /*compute the full hash for the block with everything before plus the new validated hash */
      md.update(ibytes);
      md.update(sourcebytes);
      md.update(targetbytes);
      md.update(amtbytes);
      if (this.num != 0) {
        md.update(prevbytes);
      } // if
      md.update(noncebytes);
      md.update(currentHash.getBytes());
      fullHash = new Hash(md.digest());
    } catch (Exception e) {
      // catch exception in hash finding process
    } // try/catch

  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Compute the hash of the block given all the other info already
   * stored in the block.
   */
  public void computeHash() {

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
    return new Transaction(transaction.getSource(),
                           transaction.getTarget(), transaction.getAmount());
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
    if (this.transaction.getSource().equals("")) {
      return "Block " + this.num + " (Transaction: [Deposit, Target: "
             + this.transaction.getTarget()
             + ", Amount: " + this.transaction.getAmount()
             + "], nonce: " + this.nonce
             + ", prevHash: " + this.prevHash
             + ", hash: " + this.currentHash;
    } else {
      return "Block " + this.num + " (Transaction: [Source: "
             + this.transaction.getSource()
             + ", Target: " + this.transaction.getTarget()
             + ", Amount: " + this.transaction.getAmount()
             + "], nonce: " + this.nonce
             + ", prevHash: " + this.prevHash
             + ", hash: " + this.currentHash;
    } // if/else
  } // toString()
} // class Block
