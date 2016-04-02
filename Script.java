import java.net.*;
import java.io.*;

public class Script
{

  public static int getResult(int enfant, int salaire1, int salaire2) throws Exception
  {
        String url2 = "http://www.ludovic.org/xwiki/bin/view/OpenFisca/Comparaison/?1AJ="+salaire1+"&1BJ="+salaire2+"&0CF="+enfant+"&0AX=30062014&annee=2014";

        HttpURLConnection conn = (HttpURLConnection) new URL(
                url2).openConnection();
        conn.setRequestProperty("Cookie", "JSESSIONID=4D287DD5C4E1D9E518A463902BE6B858");
        conn.connect();
 
        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
 
        byte[] bytes = new byte[1024];
        int tmp ;
        StringBuffer buff = new StringBuffer();
        while( (tmp = bis.read(bytes) ) != -1 ) {
            String chaine = new String(bytes,0,tmp);
            buff.append(chaine);
        }
        conn.disconnect();

        String res = buff.toString();
        if (res.contains("<strong>Déclaration commune</strong>"))
        {
          int summin = 1000000;
          int summin2 = 1000000;
          int i =0;
          while(i!=-1)
          {
            i = res.indexOf("Euros</strong>",i+5);
            if (i!=-1)
            {
            int k = res.indexOf("<strong>",i-25);

            int val = Integer.parseInt(res.substring(k+8, i-1));
System.out.println(val);
            if (val<summin)
              summin = val;
            else if (val<summin2 && val!=summin)
              summin2 = val;
            }
          }
         
          if (summin2==1000000) return 0;
 
          return summin-summin2;
        }
        else
        {
          int i = res.indexOf("Euros</strong><h2");
          int j = res.indexOf("Euros</strong> pour la",i+2);

          int k = res.indexOf("<strong>",i-25);
          int l = res.indexOf("<strong>",j-25);

          int commune = Integer.parseInt(res.substring(k+8, i-1));
          int separe = Integer.parseInt(res.substring(l+8,j-1));

          return commune-separe;
        }
  }

  public static void main(String[] args)
  {
    FileWriter fw = null;
    try
    {
    fw = new FileWriter("resultats.txt");

    for(int i=1;i<5;i++)
    {
       System.out.println("Nb enfants: "+i);
       for(int j=0;j<=60000;j+=5000)
       {
         System.out.println("Salaire 1: "+j);
         for(int k=0;k<=j;k+=5000)
         {
           System.out.println("Salaire 2: "+k);

           try
           {
           int val = getResult(i,j,k);
           if (val>0)
           {
             fw.write(Integer.toString(val));
             System.out.println("SEPARE :"+val);
           }
           else if (val<=0)
           {
             fw.write(Integer.toString(val));
             System.out.println("COMMUN :"+val);
           }
           fw.write("\t");
           }
           catch(IOException ioe)
           {
             System.out.println(ioe.getMessage());
             fw.close();
             return;
           }
           catch(Exception e)           
           {
             System.out.println(e.getMessage());
           }
         }
         fw.write("\r\n");
         fw.flush();
       }
       fw.write("\r\n");
    }
    }
    catch(Exception e)
    {
      System.out.println(e.getMessage());
    }
    finally
    {
      try
      {
        fw.close();
      }
      catch(Exception ee)
      {
        System.out.println(ee.getMessage());
      }
    }
  }
}
