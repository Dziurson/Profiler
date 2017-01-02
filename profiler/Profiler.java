package profiler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.Modifier;

public class Profiler
{
    public Profiler(Class c)
    {
        String tab = "    ";        
        TypeVariable[] a = c.getTypeParameters();        
        try
        {
            BufferedWriter w = (new BufferedWriter(new FileWriter(c.getSimpleName() + "Wrapper.java")));    //Otwarcie/Stworzenie pliku NAZWA_KLASYWrapper.java
            w.write("package X; //Nazwa pakietu w ktorym zostanie umieszczony plik.\n");                    
            w.write("import " + c.getName() + ";\n");                                                       //Wymuszenie importu klasy zawierającej Klasę
            w.write("import java.util.logging.Logger;\n");                                                  
            w.write("import java.util.logging.FileHandler;\n");                                             
            w.write("import java.util.logging.SimpleFormatter;\n");
            w.write("import java.text.DecimalFormat;\n\n");                                                 
            w.write("public class " + c.getSimpleName() + "Wrapper");                                     //Tworzenie klasy NAZWA_KLASYWrapper
            if (a.length != 0) w.write("<");                                                                //Instrukcje Do przejęcia typów ogólnych
            for (int i = 0;i<a.length;i++)                                                                  
            {                                                                                               
                if (i == a.length - 1)
                {
                    Type[] b = a[i].getBounds();                                                            //Przejęcie interfejsów <T extends X>
                    String extend = null;
                    for (Type b1 : b)
                    {                  
                        String t = b1.toString();                    
                        String[] parts = t.split(" ");                                                      //Typ czasami jest klasą np class java.lang.Object
                        extend = parts[parts.length-1];                                                     //Instrukcja służy do wykasowania słowa class
                    }                    
                    w.write(a[i] + " extends " + extend + ">\n");                                           //Wypisanie <T extends X>
                    break;
                }
                Type[] b = a[i].getBounds();                                                                //Analogicznie jak wyżej
                String extend = null;
                for (Type b1 : b)
                {                  
                    String t = b1.toString();                    
                    String[] parts = t.split(" ");
                    extend = parts[parts.length-1];                    
                }
                w.write(a[i] + " extends " + extend + ",");
            }
            w.write("{\n");
            w.write(tab + "private Logger LOGGER = Logger.getLogger(" + c.getSimpleName() + "Wrapper.class.getName());\n");
            w.write(tab + "FileHandler fh;\n");                                                             //Inicjalizacja loggera
            w.write(tab + "private double start;\n" + tab + "private double stop;\n");                      //Utworzenie prywatnych pól start/stop
            w.write(tab + c.getSimpleName() + " s;\n");
            
            //**************************//
            //******KONSTRUKTORY********//
            //**************************//
            
            Constructor[] c_array = c.getDeclaredConstructors();                                            //Pobranie tablicy konstruktorów
            char r = 'a';
            for(Constructor t : c_array)
            {      
                if(t.getModifiers() == 1)
                {
                    Type[] types = t.getGenericParameterTypes();                                                //Pobranie parametrów konstruktorów
                    w.write(tab + "public " + c.getSimpleName() + "Wrapper(");                                  //Tworzenie konstruktora NAZWA_KLASYWrapper
                    if (types.length == 0) w.write(");\n");
                    for(int i = 0; i < types.length; i++)
                    {
                        if (i == types.length - 1)
                        {
                            String name_tmp = types[i].toString();                                              //Wpisywanie parametrów konstruktora
                            String[] parts = name_tmp.split(" ");
                            String name = parts[parts.length-1];                                                //Zabezpieczenie przed class
                            w.write(name + " " + (char)((int)r+i) +")\n");
                            break;
                        }
                        String name_tmp = types[i].toString();                                                  //Analogicznie jak wyżej
                        String[] parts = name_tmp.split(" ");
                        String name = parts[parts.length-1];
                        w.write(name + " " + (char)((int)r+i) + ", ");                    
                    }
                    w.write(tab + "{\n");
                    w.write(tab + tab + "try\n" + tab + tab + "{\n");                                           //Inicjalizacja uchwytu pliku
                    w.write(tab + tab + tab + "fh = new FileHandler(\"" + c.getSimpleName() + "Wrapper.log\");\n");
                    w.write(tab + tab + tab + "LOGGER.addHandler(fh);\n");                                      //Dodanie uchwytu
                    w.write(tab + tab + tab + "SimpleFormatter formatter = new SimpleFormatter();\n");          
                    w.write(tab + tab + tab + "fh.setFormatter(formatter);\n");
                    w.write(tab + tab + "}\n" + tab + tab + "catch(Exception e)\n" + tab + tab + "{\n");
                    w.write(tab + tab + tab + "System.err.println(e);\n");
                    w.write(tab + tab + "}\n");
                    w.write(tab + tab + "LOGGER.info(\"Logger Name: \" + LOGGER.getName());\n");
                    w.write(tab + tab + "start = System.nanoTime();\n");
                    w.write(tab + tab + "s = new " + c.getSimpleName());                
                    w.write("(");
                    if (types.length == 0) w.write(");\n");
                    for(int i = 0; i < types.length; i++)
                    {
                        if (i == types.length - 1)
                        {
                            w.write((char)((int)r+i) +");\n");
                            break;
                        }
                        w.write((char)((int)r+i) + ", ");
                    }
                    w.write(tab + tab + "stop = System.nanoTime();\n");
                    w.write(tab + tab + "LOGGER.info(\"Wywolanie konstruktora zajelo \" + new DecimalFormat(\"#.####\").format((stop - start)/1000000) + \"ms.\");\n");
                    w.write(tab + "}\n\n");
                }
            }
            
            //**************************//
            //**********METODY**********//
            //**************************//
            
            Method[] m_array = c.getDeclaredMethods();
            r = 'a';
            for(Method m : m_array)
            {
                //if ((m.getModifiers() != 108) || (m.getModifiers() != 2))
                if(m.getModifiers() == 1)
                {
                    Type[] types = m.getGenericParameterTypes();
                    Type return_type = m.getReturnType();
                    String return_tmpt = m.getGenericReturnType().toString();
                    String[] parts_r = return_tmpt.split(" ");
                    //System.out.println(parts_r[0].toString());
                    String name_r = null;
                    if("class".equals(parts_r[0]))
                    {
                        String return_tmp = return_type.toString();
                        String[] parts_rt = return_tmp.split(" ");                
                        name_r = parts_r[parts_rt.length-1];
                    }
                    else
                    {
                        name_r = return_tmpt;
                    }
                    /*String return_tmp = return_type.toString();
                    String[] parts_r = return_tmp.split(" ");                
                    String name_r = parts_r[parts_r.length-1];*/
                    if (m.getModifiers() != 0) w.write(tab + Modifier.toString(m.getModifiers()) + " " + name_r + " " + m.getName() + "(");
                    else w.write(tab + name_r + " " + m.getName() + "(");                    
                    //w.write(tab + "public " + m.getGenericReturnType() + " " + m.getName() + "("); 
                    if (types.length == 0) w.write(")\n");
                    for(int i = 0; i < types.length; i++)
                    {
                        if (i == types.length - 1)
                        {
                            String name_tmp = types[i].toString();
                            String[] parts = name_tmp.split(" ");
                            String name = parts[parts.length-1];
                            w.write(name + " " + (char)((int)r+i) +")\n");
                            break;
                        }
                        String name_tmp = types[i].toString();
                        String[] parts = name_tmp.split(" ");
                        String name = parts[parts.length-1];
                        w.write(name + " " + (char)((int)r+i) + ", ");                    
                    }
                    w.write(tab + "{\n");
                    w.write(tab + tab + "start = System.nanoTime();\n");
                    w.write(tab + tab);
                    if(!"void".equals(m.getGenericReturnType().toString())) w.write(name_r + " t = " + "(" + name_r + ")");
                    w.write("s." + m.getName() + "(");
                    if (types.length == 0) w.write(");\n");
                    for(int i = 0; i < types.length; i++)
                    {
                        if (i == types.length - 1)
                        {
                            w.write((char)((int)r+i) +");\n");
                            break;
                        }
                        w.write((char)((int)r+i) + ", ");
                    }
                    w.write(tab + tab + "stop = System.nanoTime();\n");
                    w.write(tab + tab + "LOGGER.info(\"Wywolanie metody " + m.getName() + " zajelo \" + new DecimalFormat(\"#.####\").format((stop - start)/1000000) + \"ms.\");\n");
                    if(!"void".equals(m.getGenericReturnType().toString())) w.write(tab + tab + "return t" + ";\n");
                    w.write(tab + "}\n\n");
                }
            }
            w.write("}");
            w.close();
        }
        catch(Exception exception)
        {
            System.err.println(exception);
        }        
    }
}
