/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private Random rndgen = new Random();
    private HashMap<String, HashSet<String>> anagramMap = new HashMap<String, HashSet<String>>();
    private List<String> preComputeKeyList;;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            String canonical = sort(word);
            if(!anagramMap.containsKey(canonical)) {
                anagramMap.put(canonical, new HashSet<String>());
            }

            HashSet<String> equivalence_class_members = anagramMap.get(canonical);
            equivalence_class_members.add(word);
        }
        preComputeKeyList = new ArrayList<String>(anagramMap.keySet());
    }

    private String sort(String original) {
        char[] chars = original.toCharArray();
        Arrays.sort(chars);
        String sorted = new String(chars);
        return sorted;
    }

    private boolean isAnagram(String a, String b) {
        return a.length() == b.length() && sort(a).equals(sort(b));
    }

    public boolean isGoodWord(String word, String base) {
        return validAnswers(base).contains(word);
    }

    public List<String> getAnagrams(String targetWord) {
        HashSet<String> anagramSet =  anagramMap.get(sort(targetWord));
        return (List<String>) hashSetToList(anagramSet);
    }

    private List<String> hashSetToList(HashSet<String> x) {
        return (List<String>) new ArrayList<String>(x);
    }

    public List<String> validAnswers(String word) {
        List<String> x = getAnagramsWithOneMoreLetter(word);
        List<String> result = new ArrayList<String>();

        for (String i : x) {
            if (!i.contains(word)) result.add(i);
        }

        return result;
    }

    private List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for (char alphabet: ALPHABET.toCharArray()) {
            String key = sort(alphabet + word);
            if(anagramMap.containsKey(key)) {
                result.addAll(hashSetToList(anagramMap.get(key)));
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        HashSet<String> wordSet;
        while (true) {
            int randomIx = rndgen.nextInt(preComputeKeyList.size());
            String key = preComputeKeyList.get(randomIx);
            wordSet = anagramMap.get(key);

            if (validAnswers(key).size() > 5) {
                break;
            }
        }
        int randomVariant = rndgen.nextInt(wordSet.size());

        String selectedWord = "stop";

        for(String word : wordSet) {
            if (randomVariant == 0) {
                selectedWord = word;
                break;
            } else {
                randomVariant--;
            }
        }

        return selectedWord;
    }
}
