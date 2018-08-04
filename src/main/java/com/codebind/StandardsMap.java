package com.codebind;

import java.util.ArrayList;

public class StandardsMap{
    private ArrayList<String> keys;
    private ArrayList<String> values;

    public StandardsMap(){
        keys = new ArrayList();
        values = new ArrayList();
    }

    public String put(String key, String value) {
        int idx = keys.indexOf(key);
        if(idx == -1){
            keys.add(key);
            values.add(value);
            return null;
        }
        return values.set(idx,value).toString();
    }

    public ArrayList<String> getKeys(){
        return keys;
    }

    public ArrayList<String> getVals(){
        return values;
    }

    public String[] getKeysArray(){
        String[] keysArr = new String[keys.size()];
        return keys.toArray(keysArr);
    }

    public String[] getValsArray(){
        String[] valsArr = new String[values.size()];
        return values.toArray(valsArr);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0;i<keys.size();i++){
            if(i>0){ sb.append(", ["); }
            else{ sb.append("["); }
            sb.append(keys.get(i)+", "+values.get(i)+"]");

        }
        sb.append("]");
        return sb.toString();
    }

}
