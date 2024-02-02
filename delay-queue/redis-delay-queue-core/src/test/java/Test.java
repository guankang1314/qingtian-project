import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Guank
 * @version 1.0
 * @description: TODO
 * @date 2023-07-22 9:54
 */
public class Test {

    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
        return dfs(root1,root2);
    }

    private TreeNode dfs(TreeNode root1,TreeNode root2) {
        if (root1 == null && root2 == null)
            return null;
        TreeNode root = new TreeNode();
        if (root1 != null && root2 == null) {
            root.val = root1.val;
        }
        if (root1 == null && root2 != null) {
            root.val = root2.val;
        }
        root.val = root1.val + root2.val;
        root.left = dfs(root1.left,root2.left);
        root.right = dfs(root1.right,root2.right);
        return root;
    }

    public static void main(String[] args) {

        mergeTrees("ADOBECODEBANC","ABC");
    }
}
