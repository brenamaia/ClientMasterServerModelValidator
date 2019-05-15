package br.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordCount {
	
	private static String fileName = "origin.txt";
	
	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			String dados = new String(Files.readAllBytes(new File(System.getProperty("user.dir") + System.getProperty("file.separator")  + fileName).toPath()));
			
			System.out.println(wordCount(dados));
			System.out.println("Total Time (ms): " + (System.currentTimeMillis() - start));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * This method counts how many times each individual word appears in the text. 
	 * @param text
	 * @return
	 */
	public static String wordCount(String text) {
		//System.err.println("wordcount recebeu: " + text);
		Set<String> set = null;
		Map<String, Integer> mapaPalavras = new HashMap<String, Integer>();
		StringBuffer result = new StringBuffer();
		text = text.replace("\n"," ");
		List<String> asList = Arrays.asList(text.split(" "));
		
		set = new LinkedHashSet<String>();
		set.addAll(asList);

		for (String word : set) {
			mapaPalavras.put(word, 0);
		}
		
		for (String word : asList) {
			mapaPalavras.put(word,mapaPalavras.get(word) + 1);
		}
		
		for (String word : mapaPalavras.keySet()) {
			result.append(word +" "+mapaPalavras.get(word)+"\n");
		}
		
		return result.toString();
	}
}
