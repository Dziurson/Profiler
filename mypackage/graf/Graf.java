/**
 * @author Jakub Dziwura
 * 26.10.2015
 */
package mypackage.graf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.io.Serializable;
/**
 * <b>Klasa Graf</b>
 * Obiekty klasy Graf reprezentuja graf skierowany jako liste krawędzi
 * oraz listy sąsiedztwa wszystkich wierzchołków.
 */
public class Graf implements Serializable
{
    private List<Krawedz> listakr;                                              /**Lista przechowujaca krawędzi */
    private List<Krawedz>[] listasasiedztwa;                                    /**Lista zawierajaca listy sąsiedztwa*/
    private int maxW;                                                           /**Liczba wierzchołków-1 (Werzchołek o indeksie 0 nie istnieje)*/
    /**
    *<i>Konstruktor parametrowy</i>
    */
    public Graf(String plik)
    {   
        listakr = new ArrayList<Krawedz>();        
        try
        {
            BufferedReader z = (new BufferedReader(new FileReader(plik)));
            System.out.println("Plik tekstowy otwarty poprawnie.\nWczytywanie Krawędzi...");
            maxW = 0;
            int tmp = 0;
            for(String kr = z.readLine(); kr!=null; kr = z.readLine())
            {
                String[] dziel = (kr.split(","));
                Krawedz k = new Krawedz(Integer.parseInt(dziel[0]),Integer.parseInt(dziel[2]),Integer.parseInt(dziel[1]));
                listakr.add(k);   
                if (k.getFirst() > maxW) maxW = k.getFirst();
                if (k.getSecond() > maxW) maxW = k.getSecond(); 
                tmp++;
            }            
            z.close();
            System.out.println("Krawedzie wczytane.");
            System.out.println("Ilość wczytanych krawędzi to: " + tmp + ".");
            System.out.println("Tworzenie list sąsiedztwa...");            
            listasasiedztwa = (ArrayList<Krawedz>[]) new ArrayList[maxW+1];
            for (int i = 0; i < maxW+1; i++)
            {
                listasasiedztwa[i] = new ArrayList<Krawedz>();
            }
            for (int i = 0; i < listakr.size(); i++)
            {                
                listasasiedztwa[listakr.get(i).getFirst()].add(listakr.get(i));
            }
            System.out.println("Listy sąsiedztwa utworzone.");             
        }
        catch(Exception e)
        {
            System.err.println("Wystapil blad przy wczytywaniu danych");
        }        
    }    
    public void ShowSasiedzi(int j)
    {
        for (int i = 0; i < listasasiedztwa[j].size(); i++)
        {
            listasasiedztwa[j].get(i).show();
        }
    }
    public void pokazKrawedzi()
    {
        for(int i = 0; i < listakr.size(); i++)
        {
            listakr.get(i).show();
        }
    }
    public int liczbaWierzcholkow()
    {
        return maxW+1;
    }
    Iterable<Krawedz> ListaSasiadow(int v)
    {
        return listasasiedztwa[v];
    }
    public int IloscKrawedzi()
    {
        return listakr.size();
    }
    Krawedz GetKrawedz(int i)
    {
        return listakr.get(i);
    }
}