package org.acooly.datastructure.btree;

import java.util.Random;

import org.junit.Test;

public class BinaryTreeTest {

	BinaryTree tree = new BinaryTree();
	
	@Test
	public void runTest(){
		Random random = new Random();
		
		//INSERT
		for(int i=1;i<=10;i++){
			int key = random.nextInt(100);
			tree.insert(key, "value" + key);
		}
		int key = 0;
		tree.insert(key, "value"+key);
		System.out.println("TARGET key: " + key);
		//FIND
		System.out.println("FIND: "+tree.find(key));
		//GETPARENT
		System.out.println("PARENT: "+tree.getParent(key));
		//MIX
		System.out.println("MAX: " + tree.getMax());
		//MIN
		System.out.println("MIN: " + tree.getMin());
		tree.delete(key);
		System.out.println();
		
		
		System.out.println("中遍历(升序)：");
		tree.inOrderAsc(tree.getRoot());
		System.out.println("中遍历(降序)：");
		tree.inOrderDesc(tree.getRoot());
	}
	
	
	
	

}
