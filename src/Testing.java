import java.util.ArrayList;
import java.util.List;

public class Testing {
    public static void main(String[] args) {
        int[] nums = new int[] {1,2,3,};
        System.out.println(containDuplicate(nums));
    }

    public static boolean containDuplicate(int[] nums){
        List<Integer> numbersOnArray = new ArrayList<>();
        for(int i = 0; i < nums.length; i++){
            if(!numbersOnArray.contains(nums[i])){
                numbersOnArray.add(nums[i]);
            }else{
                return true;
            }
        }
        return false;

    }
    
}

