package edu.grinnell.csc207.blockchains;

public class Node {

    Node next;

    Block block;

    Node(Block newBlock) {
        this.next = null;
        this.block = newBlock;
    }

    public void add(Node block) {
        this.next = block;
    }

    public boolean hasNext() {
        return !(this.next == null);
    }

    public Node getNext() {
        return this.next;
    }

    public void removeNext() {
        if (this.next == null) {
            return;
        }
        if (this.next.next != null) {
            this.next = this.next.next;
        } else {
            this.next = null;
        }
    }

    public Block getBlock() {
        return this.block;
    }

    public boolean equal(Node node) {
        return this.block.equals(node.block);
    }
}
