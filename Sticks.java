// هاد ملف رمي العصي 
import java.util.*;

public class Sticks {
    public static int toss() {
        int sum = 0;
        for (int i = 0; i < 4; i++) {
            //هون استخدمنا تابع الراندوم بياخد احتمال العصي نص للداكن ونص للفاتح
            if (Math.random() < 0.5) {
                sum += 1; 
            } else {
                sum += 0; 
            }
        }
        return (sum == 0) ? 5 : sum;
    }
    //هذا التابع يعطينا احتمالية ظهور كل رقمgetProbabilities
    public static Map<Integer, Double> getProbabilities() {
        Map<Integer, Double> p = new HashMap<>();
        p.put(1, 4.0 / 16.0);   
        p.put(2, 6.0 / 16.0);   
        p.put(3, 4.0 / 16.0);   
        p.put(4, 1.0 / 16.0);   
        p.put(5, 1.0 / 16.0);  
        return p;
    }
}