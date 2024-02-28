import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class main {
       protected MusicaService service;
       protected Musica musica;


    public static void main(String[] args) throws IOException, ParseException {

        MusicaService serviceDB = new MusicaService("db/teste.db");

        MusicaService serviceCSV = new MusicaService("DB/csv_teste.csv");
        serviceCSV.raf.readLine();
        long posCSV = serviceCSV.raf.getFilePointer();
        long posBD = 4;
        for(int i = 0; i < 20; i++){
            serviceDB.writeInFile(posBD,serviceCSV.readFromCSV(posCSV)) ;
            posCSV = serviceCSV.raf.getFilePointer();
            posBD = serviceDB.raf.getFilePointer();
        }
        serviceDB.raf.seek(0);
        System.out.println("ULTIMO ID ---->");
        System.out.println(serviceDB.raf.readInt());

        serviceDB.readAll();






    }
}