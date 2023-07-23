package me.LiveSongs;

/**
 * @author kKofkrd2
 */
public class StrUtil {

    private static String longestCommonSubstringNoOrder(String strA, String strB){
        if(strA.length() >= strB.length()){
            return substring(strA, strB);
        }else{
            return substring(strB, strA);
        }
    }
    private static String substring(String strLong, String strShort) {
        char[] ca = strLong.toCharArray();
        char[] cb = strShort.toCharArray();
        int m = ca.length;
        int n = cb.length;
        int[][] mt = new int[m+1][n+1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if(ca[i-1] == cb[j-1]){
                    mt[i][j] = mt[i-1][j-1] + 1;
                }else {
                    mt[i][j] = Math.max(mt[i][j-1], mt[i-1][j]);
                }
            }
        }
        char[] ret = new char[mt[m][n]];
        int ci = ret.length - 1;
        while(mt[m][n] != 0){
            if(mt[n] == mt[n-1]){
                n--;
            }else if(mt[m][n] == mt[m-1][n]){
                m--;
            }else{
                ret[ci] = ca[m-1];
                ci--;
                n--;
                m--;
            }
        }
        return new String(ret);
    }
    private static boolean charReg(char charValue){
        return (charValue >= 0x4E00 && charValue <= 0x9FA5) || (charValue >= 'a' && charValue <= 'z') || (charValue >= 'A' && charValue <= 'Z') || (charValue >= '0' && charValue <= '9');
    }
    private static String removeSign(String str){
        StringBuilder sb = new StringBuilder();
        for(char item : str.toCharArray()){
            if(charReg(item)){
                sb.append(item);
            }
        }
        return sb.toString();
    }
    public static double SimilarDegree(String strA, String strB){
        String newStrA = removeSign(strA);
        String newStrB = removeSign(strB);
        int temp = Math.max(newStrA.length(), newStrB.length());
        int temp2 = longestCommonSubstringNoOrder(newStrA, newStrB).length();
        return temp2 * 1.0 / temp;
    }
}
