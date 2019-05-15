/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class GeradorDeArquivo {

	
	private static Integer TAMANHO_ARQUIVO = 5; 
			
    public static void main(String[] args) throws IOException {

    	int qt_megas = TAMANHO_ARQUIVO;

    	Scanner ler = new Scanner(System.in);
        String frase = null;
        int i;

        String article[] = {"the"};
        String noun[] = {"the"};
        String verb[] = {"drove"};
        String preposition[] = {"over"};
        
        ArrayList<String> texto = new ArrayList<String>();

        int articleLength = article.length;
        int nounLength = noun.length;
        int verbLength = verb.length;
        int prepositionLength = preposition.length;

        int randArticle = (int) (Math.random() * articleLength);
        int randNoun = (int) (Math.random() * nounLength);
        int randVerb = (int) (Math.random() * verbLength);
        int randPreposition = (int) (Math.random() * prepositionLength);
        int randArticle2 = (int) (Math.random() * articleLength);
        int randNoun2 = (int) (Math.random() * nounLength);
        
        int randArticle10 = (int) (Math.random() * articleLength);
        int randNoun10 = (int) (Math.random() * nounLength);
        int randVerb10 = (int) (Math.random() * verbLength);
        int randPreposition10 = (int) (Math.random() * prepositionLength);

        
        for (i = 0; i < ((qt_megas * 225000)/ 10); i++) {

            frase = article[randArticle] + " " + noun[randNoun] + " " + verb[randVerb] + " " + preposition[randPreposition] + " " + article[randArticle] + " " + noun[randNoun2] + " " + article[randArticle10] + " " + noun[randNoun10] + " " + verb[randVerb10] + " " + preposition[randPreposition10] + " ";
      
            texto.add(frase);

        }

        System.out.println("Texto Gerado!");

        int ii, n;

        FileWriter arq = new FileWriter("ArquivoGerado.txt");
        PrintWriter gravarArq = new PrintWriter(arq);

        for (ii = 0; ii < texto.size(); ii++) {
            gravarArq.printf(texto.get(ii));
        }

        arq.close();
        System.out.println("Arquivo Gerado!");

    }

   
}
