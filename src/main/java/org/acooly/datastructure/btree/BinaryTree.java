package org.acooly.datastructure.btree;

import java.util.Random;

/**
 * 特征：一个节点的左子节点的关键字小于这个节点，右子节点的关键字大于等于该节点。
 * 
 * @author zhangpu
 * 
 */
public class BinaryTree {

	/** 根节点 */
	private Node root;

	/**
	 * 查找
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
				// 小于本节点在左边
				current = current.getLeftNode();
			} else {
				// 大于等于本节点在右边
				current = current.getRightNode();
			}
			if (current == null) {
				// 搜索到最后叶子为空，表示没有找到
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
				// 小于本节点在左边
				parent = current;
				current = current.getLeftNode();
			} else {
				// 大于等于本节点在右边
				parent = current;
				current = current.getRightNode();
			}
			if (current == null) {
				// 搜索到最后叶子为空，表示没有找到
				return null;
			}
		}
		return parent;
	}

	/**
	 * 插入
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
	 * 中遍历(升序)
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
	 * 中遍历(降序)
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
	 * 最大值 算法：树中最底层的右子叶
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
	 * 算法：树中最底层的左子叶
	 * 
	 * @return
	 */
	public Node getMin() {
		return getMin(root);
	}

	/**
	 * 指定节点的最小节点，如果指定节点为root,则是树的最小节点
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
	 * 删除节点存在3中情况 <li>目标节点是叶子:直接删除,置为null <li>
	 * 目标节点只有一个子节点:如果目标节点是在父节点的左边，直接使用子节点作为父节点的左子，反正则为右子。 <li>
	 * 目标节点有两个子节点:找到后继节点，作为目标节点父节点的对应子节点。（后继：目标节点子节点中大于目标节点最小的个。路径：目标节点右子的最小节点。）
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
		// 第一种情况，目标是叶子，直接设置为null
		if (!leftExsit && !rightExsit) {
			target = null;
			return;
		}

		// 获得目标的父节点
		Node parent = getParent(key);
		Node child = null;
		if (leftExsit != rightExsit) {
			// 第二种情况：只有一个子
			child = (leftExsit ? target.getLeftNode() : target.getRightNode());
		} else {
			// 第三种情况：有两个子
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
			// 目标是父的左子
			parent.setLeftNode(child);
		} else {
			// 目标是父的右子
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
		System.out.println("中遍历(升序)：");
		tree.inOrderAsc(tree.getRoot());
		System.out.println("中遍历(降序)：");
		tree.inOrderDesc(tree.getRoot());
	}

}
