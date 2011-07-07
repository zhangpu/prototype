package org.acooly.datastructure.btree;

/**
 * BTree �ڵ�
 * 
 * @author zhangpu
 * 
 */
public class Node {

	/** �ڵ�KEY */
	private int key;
	private Object value;
	/** ���ӽڵ� */
	private Node leftNode;
	/** ���ӽڵ� */
	private Node rightNode;

	public Node() {
		super();
	}

	public Node(int key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public Node getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(Node leftNode) {
		this.leftNode = leftNode;
	}

	public Node getRightNode() {
		return rightNode;
	}

	public void setRightNode(Node rightNode) {
		this.rightNode = rightNode;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(key) + "=" + value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Node){
			Node n = (Node)obj;
			if(n.getKey() != getKey()){
				return false;
			}
		}else{
			return false;
		}
		return true;
	}

}
