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

  /** The block number in the chain. */
  int num;

  /** The hash of the previous block in the chain. */
  Hash prevHash;

  /** The hash of the current block. */
  Hash currentHash;

  /** The transaction stored in the block. */
  Transaction transaction;

  /** The block nonce. */
  Long nonce;

  /** The validator for the block hash. */
  HashValidator check;
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and
   * previous hash, mining to choose a nonce that meets the requirements
   * of the validator.
   *
   * @param iNum
   *   The number of the block.
   * @param iTransaction
   *   The transaction for the block.
   * @param iPrevHash
   *   The hash of the previous block.
   * @param iCheck
   *   The validator used to check the block.
   */
  public Block(int iNum, Transaction iTransaction, Hash iPrevHash,
               HashValidator iCheck) {
    /*set the fields we can */
    this.num = iNum;
    this.prevHash = iPrevHash;
    this.transaction = iTransaction;
    this.check = iCheck;

    this.computeHash();
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param iNum
   *   The number of the block.
   * @param iTransaction
   *   The transaction for the block.
   * @param iPrevHash
   *   The hash of the previous block.
   * @param iNonce
   *   The nonce of the block.
   */
  public Block(int iNum, Transaction iTransaction, Hash iPrevHash, long iNonce) {
    this.num = iNum;
    this.transaction = iTransaction;
    this.prevHash = iPrevHash;
    this.nonce = iNonce;

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
      byte[] amtbytes = ByteBuffer.allocate(Integer.BYTES)
                        .putInt(this.transaction.getAmount()).array();
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
      } else {
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
      } // else
    } catch (NoSuchAlgorithmException e) {
      // catch exception
    } // try/catch
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
      return "Block " + this.num + "(Transaction: [Deposit, Target: "
             + this.transaction.getTarget() + ", Amount: "
             + this.transaction.getAmount() + "], Nonce: "
             + this.nonce + ", prevHash: "
             + this.prevHash + ", hash: "
             + this.currentHash + ")";
    } else {
      return "Block " + this.num + "(Transaction: [Source: "
             + this.transaction.getSource() + ", Target: "
             + this.transaction.getTarget() + ", Amount: "
             + this.transaction.getAmount() + "], Nonce: "
             + this.nonce + ", prevHash: "
             + this.prevHash + ", hash: "
             + this.currentHash + ")";
    } // if/else
  } // toString()
} // class Block
