/**
 *
 * @author Shar6 at
 */
public class Main_SystemEventManagement {

    public static int notifyEvent (int msg, int val1, int val2, String val3,
            int[] arr1, byte[] arr2) {
        if (msg == 0x0219 && val1 == 0x8000 && arr1[1] == 0x00000002) {
            String ret = "";
            for(int i=0;i < 26;i++)
                if(((arr1[3] >> i) & 1) != 0)
                    ret += ((char) ('A'+i))+": ";
        }
        
        return 1;
    }
}
