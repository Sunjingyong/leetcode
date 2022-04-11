package tree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class BasicTreeTravelAlgorithm {

   static class TreeNode{
        TreeNode left;
        TreeNode right;
        int val;
        TreeNode(int val){
            this.val = val;
        }
    }

    /**
     * 前序遍历，根左右
     */
    public static void preTravel(TreeNode node){
        if(node != null){
            System.out.print(node.val + "  ");
            preTravel(node.left);
            preTravel(node.right);
        }
    }

    /**
     * 中序遍历，左根右
     */
    public static void middleTravel(TreeNode node){
        if(node != null){
            middleTravel(node.left);
            System.out.print(node.val + "  ");
            middleTravel(node.right);
        }
    }

    /**
     *后续遍历，左右根
     */
    public static void afterTravel(TreeNode node){
        if(node != null){
            afterTravel(node.left);
            afterTravel(node.right);
            System.out.print(node.val + "  ");
        }
    }

    /**
     *前序遍历，非递归
     */
    public static void preTravelNoRecurrence(TreeNode root){
        Stack<TreeNode> stack = new Stack<>();//用一个栈来存放树中的节点
        while(root != null || !stack.isEmpty()) {//只要当前节点不为空(即当前节点的左右子树没有访问完毕)或者栈中还有节点(还有节点没有访问)
            while (root != null) {//一直往左走
                stack.push(root);//根节点入栈
                System.out.print(root.val + " ");//打印根节点
                root = root.left;//访问左子树
            }
            root = stack.pop();//取出根节点
            root = root.right;//访问右子树
        }
    }

    /**
     *中序遍历，非递归
     */
    public static void middleTravelNoRecurrence(TreeNode root){
        Stack<TreeNode> stack = new Stack<>();
        while (root != null || !stack.isEmpty()){
            while (root != null){
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            System.out.print(root.val + " ");
            root = root.right;
        }
    }

    /**
     * 后续遍历，非递归
     */
    public static void afterTravelNoRecurrence(TreeNode root){
        Stack<TreeNode> stack = new Stack<>();
        TreeNode node = root;
        TreeNode pre = null;
        while (node != null || !stack.isEmpty()){
            while (node != null){
                stack.push(node);
                node = node.left;
            }
            node = stack.peek();
            if (node.right == null || node.right == pre){
                node = stack.pop();
                System.out.print(node.val + " ");
                pre = node;
                node = null;
            }else {
                node = node.right;
            }

        }


    }

    /**
     *层次遍历
     */
    public static void levelTravel(TreeNode root){
        Queue<TreeNode> queue = new LinkedList<>();
        if(root != null){
            queue.offer(root);
        }
        while (!queue.isEmpty()){
            int size = queue.size();
            for(int i = 0; i < size; i++){
                root = queue.poll();
                System.out.print(root.val + " ");
                if(root.left != null){
                    queue.offer(root.left);
                }
                if(root.right != null){
                    queue.offer(root.right);
                }
            }
        }

    }


    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        TreeNode left1 = new TreeNode(2);
        root.left = left1;
        TreeNode left1left2 = new TreeNode(3);
        left1.left = left1left2;
        TreeNode left1right2 = new TreeNode(4);
        left1.right = left1right2;
        TreeNode right1 = new TreeNode(5);
        root.right = right1;
        TreeNode right1left2 = new TreeNode(6);
        right1.left = right1left2;
//        preTravel(root);
//        middleTravel(root);、
//        afterTravel(root);
//        preTravelNoRecurrence(root);
//        middleTravelNoRecurrence(root);
//        levelTravel(root);
        afterTravelNoRecurrence(root);

    }


}
