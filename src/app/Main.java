
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;



public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");
        musica musica = new musica(0,0,"teste","2021-12-31","teste");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println(format.format(musica.getData()));


    }
}