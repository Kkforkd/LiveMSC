package me.LiveSongs;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * @author kKofkrd2
 */
public class StrUtil {

    public static List<String> messageToJson(byte[] messageBytes) throws DataFormatException {
        byte[] mainMessageBytes = Arrays
                .copyOfRange(messageBytes, 16, messageBytes.length);

        if (messageBytes[16] != 120) {
            return Arrays.asList(new String(mainMessageBytes, StandardCharsets.UTF_8));
        }

        // 解压缩弹幕信息
        byte[] newByte = new byte[1024 * 5];
        Inflater inflater = new Inflater();
        inflater.setInput(mainMessageBytes);
        newByte = Arrays.copyOfRange(newByte, 16, inflater.inflate(newByte));
        return splitStringToJson(new String(newByte, StandardCharsets.UTF_8));
    }
    private static List<String> splitStringToJson(String str) {
        List<String> result = new ArrayList<>();
        for (int i = 1, count = 1; i < str.length(); i++) {

            if (str.charAt(i) == '{') {
                count++;
            } else if (str.charAt(i) == '}') {
                count--;
            }

            if (count == 0) {
                result.add(str.substring(0, i + 1));
                int nextIndex = str.indexOf("{", i + 1);
                if (nextIndex != -1) {
                    result.addAll(splitStringToJson(str.substring(nextIndex)));
                }
                return result;
            }
        }
        return result;
    }

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
