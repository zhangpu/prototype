package org.acooly.datastructure.btree;

import java.util.Random;

/**
 * ������һ���ڵ�����ӽڵ�Ĺؼ���С������ڵ㣬���ӽڵ�Ĺؼ��ִ��ڵ��ڸýڵ㡣
 * 
 * @author zhangpu
 * 
 */
public class BinaryTree {

	/** ���ڵ� */
	private Node root;

	/**
	 * ����
	 * 
	 * @param key
	 * @return
	 */
	public Node find(int key) {
		if (root == null) {
			return null;
		}
		Node current = root;
		while (current.getKey() != key) {
			if (key < current.getKey()) {
				// С�ڱ��ڵ������
				current = current.getLeftNode();
			} else {
				// ���ڵ��ڱ��ڵ����ұ�
				current = current.getRightNode();
			}
			if (current == null) {
				// ���������Ҷ��Ϊ�գ���ʾû���ҵ�
				return null;
			}
		}
		return current;
	}

	public Node getParent(int key) {
		if (root == null) {
			return null;
		}
		Node current = root;
		Node parent = root;
		while (current.getKey() != key) {
			if (key < current.getKey()) {
				// С�ڱ��ڵ������
				parent = current;
				current = current.getLeftNode();
			} else {
				// ���ڵ��ڱ��ڵ����ұ�
				parent = current;
				current = current.getRightNode();
			}
			if (current == null) {
				// ���������Ҷ��Ϊ�գ���ʾû���ҵ�
				return null;
			}
		}
		return parent;
	}

	/**
	 * ����
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(int key, Object value) {
		Node node = new Node(key, value);
		if (root == null) {
			root = node;
			return;
		}
		Node current = root;
		while (true) {
			if (key < current.getKey()) {
				if (current.getLeftNode() == null) {
					current.setLeftNode(node);
					return;
				} else {
					current = current.getLeftNode();
				}
			} else {
				if (current.getRightNode() == null) {
					current.setRightNode(node);
					return;
				} else {
					current = current.getRightNode();
				}
			}
		}

	}

	/**
	 * �б���(����)
	 * 
	 * @param startNode
	 */
	public void inOrderAsc(Node startNode) {

		if (startNode != null) {
			inOrderAsc(startNode.getLeftNode());
			System.out.println(startNode);
			inOrderAsc(startNode.getRightNode());
		}

	}

	/**
	 * �б���(����)
	 * 
	 * @param startNode
	 */
	public void inOrderDesc(Node startNode) {
		if (startNode != null) {
			inOrderDesc(startNode.getRightNode());
			System.out.println(startNode);
			inOrderDesc(startNode.getLeftNode());
		}

	}

	/**
	 * ���ֵ �㷨��������ײ������Ҷ
	 * 
	 * @return
	 */
	public Node getMax() {
		Node current = root;
		while (current.getRightNode() != null) {
			current = current.getRightNode();
		}
		return current;
	}

	/**
	 * �㷨��������ײ������Ҷ
	 * 
	 * @return
	 */
	public Node getMin() {
		return getMin(root);
	}

	/**
	 * ָ���ڵ����С�ڵ㣬���ָ���ڵ�Ϊroot,����������С�ڵ�
	 * 
	 * @param localRoot
	 * @return
	 */
	private Node getMin(Node localRoot) {
		Node current = localRoot;
		while (current.getLeftNode() != null) {
			current = current.getLeftNode();
		}
		return current;
	}

	/**
	 * ɾ���ڵ����3����� <li>Ŀ��ڵ���Ҷ��:ֱ��ɾ��,��Ϊnull <li>
	 * Ŀ��ڵ�ֻ��һ���ӽڵ�:���Ŀ��ڵ����ڸ��ڵ����ߣ�ֱ��ʹ���ӽڵ���Ϊ���ڵ�����ӣ�������Ϊ���ӡ� <li>
	 * Ŀ��ڵ��������ӽڵ�:�ҵ���̽ڵ㣬��ΪĿ��ڵ㸸�ڵ�Ķ�Ӧ�ӽڵ㡣����̣�Ŀ��ڵ��ӽڵ��д���Ŀ��ڵ���С�ĸ���·����Ŀ��ڵ����ӵ���С�ڵ㡣��
	 * 
	 * @param key
	 */
	public void delete(int key) {

		Node target = find(key);
		if (target == null) {
			return;
		}

		boolean leftExsit = (target.getLeftNode() != null ? true : false);
		boolean rightExsit = (target.getRightNode() != null ? true : false);
		// ��һ�������Ŀ����Ҷ�ӣ�ֱ������Ϊnull
		if (!leftExsit && !rightExsit) {
			target = null;
			return;
		}

		// ���Ŀ��ĸ��ڵ�
		Node parent = getParent(key);
		Node child = null;
		if (leftExsit != rightExsit) {
			// �ڶ��������ֻ��һ����
			child = (leftExsit ? target.getLeftNode() : target.getRightNode());
		} else {
			// �������������������
			Node rightChild = target.getRightNode();
			child = getMin(rightChild);
			getParent(child.getKey()).setLeftNode(null);
			child.setRightNode(rightChild);
		}

		if (parent == null) {
			root = child;
			target = null;
			return;
		}

		if (parent.getLeftNode() != null && target.getKey() < parent.getLeftNode().getKey()) {
			// Ŀ���Ǹ�������
			parent.setLeftNode(child);
		} else {
			// Ŀ���Ǹ�������
			parent.setRightNode(child);
		}
		target = null;
	}

	public Node getRoot() {
		return root;
	}

	public static void main(String[] args) {
		BinaryTree tree = new BinaryTree();
		Random random = new Random();
		// INSERT
		for (int i = 1; i <= 10; i++) {
			int key = random.nextInt(100);
			tree.insert(key, "value" + key);
		}
		int key = 0;
		tree.insert(key, "value" + key);
		System.out.println("TARGET key: " + key);
		// FIND
		System.out.println("FIND: " + tree.find(key));
		// GETPARENT
		System.out.println("PARENT: " + tree.getParent(key));
		// MIX
		System.out.println("MAX: " + tree.getMax());
		// MIN
		System.out.println("MIN: " + tree.getMin());
		tree.delete(key);
		System.out.println();
		System.out.println("�б���(����)��");
		tree.inOrderAsc(tree.getRoot());
		System.out.println("�б���(����)��");
		tree.inOrderDesc(tree.getRoot());
	}

}
