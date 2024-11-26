package edu.grinnell.csc207.blockchains;

/**
 * A node to store a linked list of Blocks for the BlockChain.
 *
 * @author Myles Bohrer-Purnell
 * @author Anthony Castleberry
 */
public class Node {
  /** Next node in linked list. */
  Node next;

  /** Current block in node sequence. */
  Block block;

  /**
   * Creates a new Node in the linked list storing a block.
   *
   * @param newBlock Block to be added to the node
   */
  Node(Block newBlock) {
    this.next = null;
    this.block = newBlock;
  } // Node(Block)

  /**
   * Add a Node to go after the current Node in the linked list.
   *
   * @param newNode The block to be added
   */
  public void add(Node newNode) {
    this.next = newNode;
  } // add(Node)

  /**
   * Check if there is a Node after the current Node in the linked list.
   *
   * @return True if there is a next Node and False if not
   */
  public boolean hasNext() {
    return !(this.next == null);
  } // hasNext()

  /**
   * Get the next Node in the linked list.
   *
   * @return The next Node
   */
  public Node getNext() {
    return this.next;
  } // getNext

  /**
   * Remove the next Node from the linked list.
   */
  public void removeNext() {
    if (this.next == null) {
      return;
    } // if
    if (this.next.next != null) {
      this.next = this.next.next;
    } else {
      this.next = null;
    } // if/else
  } // removeNext()

  /**
   * Get the Block stored in the current Node.
   *
   * @return The current Block
   */
  public Block getBlock() {
    return this.block;
  } // getBlock()

  /**
   * Checks if two Nodes are the same.
   *
   * @param node The Node to be compared to the current Node
   * @return True if they are equal and False if not
   */
  public boolean equal(Node node) {
    return this.block.equals(node.block);
  } // equal(Node)
} // Node
