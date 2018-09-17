package com.feamorx86.boardgame.utils;

import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Home on 09.10.2017.
 */
public class SimpleTagProcessor {

    private static class Tag {
        public ArrayList<Integer> indexes = new ArrayList<>();
        public String tag;
        //public String value;
    }

    private String name;
    private ArrayList<String> parsedText = new ArrayList<>();
    private HashMap<String, Tag> tags = new HashMap<>();

    public SimpleTagProcessor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean setTag(String name, String value) {
        Tag tag = tags.get(name);
        if (tag != null && tag.indexes.size() > 0) {
            for(int i = 0; i < tag.indexes.size(); i++) {
                int index = tag.indexes.get(i);
                if (index > 0 && index < parsedText.size()) {
                    if (value != null) {
                        parsedText.set(index, value);
                    } else {
                        parsedText.set(index, "");
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public SimpleTagProcessor withTag(String name, String value) {
        setTag(name,value);
        return this;
    }

    public SimpleTagProcessor withTagsInLoop(String tagPrefix, int start, int size, int index, String valueEquals, String valueNotEquals) {
        for(int i=start; i < size; i++) {
            if (i == index) {
                withTag(tagPrefix + i, valueEquals);
            } else {
                withTag(tagPrefix + i, valueNotEquals);
            }
        }
        return this;
    }

    public String getTagValue(String name) {
        Tag tag = tags.get(name);
        if (tag != null && tag.indexes.size() > 0) {
            int index = tag.indexes.get(0);
            if (index > 0 && index < parsedText.size()) {
                return parsedText.get(index);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void prepare(String fromFile) throws IOException {
        Reader fileReader = new FileReader(fromFile);
        BufferedReader reader = new BufferedReader(fileReader);

        String line = reader.readLine();
        StringBuilder builder = new StringBuilder();
        int blockNumber = 0;
        while(line!= null) {
            int index = line.indexOf("${");
            if (index < 0) {
                builder.append((line)+"\n\r");
                line = reader.readLine();
            } else {
                int index2 = line.indexOf("}", index);
                if (index2 <=0 ) {
                    builder.append((line)+"\n\r");
                    line = reader.readLine();
                } else {
                    String before = line.substring(0, index);
                    String tagName = line.substring(index+2, index2);
                    String after = line.substring(index2+1);

                    builder.append(before);
                    parsedText.add(builder.toString());
                    blockNumber++;
                    builder = new StringBuilder();

                    Tag tag = tags.get(tagName);
                    if (tag == null) {
                        tag = new Tag();
                        tag.tag = tagName;
                        tags.put(tagName, tag);
                    }
                    tag.indexes.add(blockNumber);

                    parsedText.add("");
                    blockNumber++;

                    line = after;
                }
            }
        }
        parsedText.add(builder.toString());
    }

    public ArrayList<String> getParsedText() {
        return parsedText;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(String block : parsedText) {
            if (!StringUtils.isEmpty(block)) {
                stringBuilder.append(block);
            }
        }
        return stringBuilder.toString();
    }
}
