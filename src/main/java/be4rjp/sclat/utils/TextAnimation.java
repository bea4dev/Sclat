package be4rjp.sclat.utils;

import java.util.Arrays;

public class TextAnimation {
    
    private static final char[] smalls = {'.', '|', 'i', '!', '！', '/', '1', ' ', 'l'};
    
    private final String text;
    private final int length;
    
    private int index = 0;
    
    public TextAnimation(String text, int length){
        this.text = text + text + text + text;
        this.length = length;
    }
    
    public String next(){
        
        String line = text.substring(index, index + length);
        
        int plus = 0;
        int hankaku = 0;
        
        char[] chars = line.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Arrays.asList(smalls).contains(chars[i])){
                plus++;
            }else if (String.valueOf(chars[i]).getBytes().length < 2){
                hankaku++;
            }
        }
        
        line = text.substring(index, index + length + plus + hankaku/2);
    
        index++;
        if(index == text.length()/4)
            index = 0;
        
        return line;
    }
}
