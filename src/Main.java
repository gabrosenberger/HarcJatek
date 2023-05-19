import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws EleteroKivetel, FileNotFoundException {
        Jatek jatek = new Jatek();
        jatek.indit();
    }
}

class Jatek {
    private int tablaHossz;
    private final Random veletlen = new Random();
    private Harcos harcos;
    private Varazslo varazslo;
    private Mezo[] tabla;

    public Jatek() {
        this.tablaHossz = 3;
        this.tabla = new Mezo[tablaHossz];
        this.harcos = new Harcos(veletlen.nextInt(6) + 4);
        this.varazslo = new Varazslo(veletlen.nextInt(6) + 4);
    }

    public void indit() throws EleteroKivetel, FileNotFoundException {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Be akarsz tölteni mentést? (igen/nem)");
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("igen")) {
                String[] kezdetiErtekStringArray = kezdetiErtekBetolto().split(";");
                if (kezdetiErtekStringArray.length != 2) {
                    System.out.println("Érvénytelen mentés file!");
                } else {
                    int[] intArray = new int[kezdetiErtekStringArray.length];
                    for(int i = 0; i < kezdetiErtekStringArray.length; i++) {
                        intArray[i] = Integer.parseInt(kezdetiErtekStringArray[i]);
                        this.harcos = new Harcos(intArray[0]);
                        this.varazslo = new Varazslo(intArray[1]);
                    }
                }
            } else if (!input.equals("nem")) {
                System.out.println("Érvénytelen válasz, nincs betöltés!");
            }

            boolean kilep = false;
            while (harcos.aktiv() && varazslo.aktiv() && !kilep) {
                tablaReset();
                mozgat();
                tablatIr();
                kilep = mentesKerdes();
            }
        } catch (EleteroKivetel e) {
            System.out.println(e.toString());
        }

    }

    private boolean mentesKerdes() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Akarsz menteni és kilépni? (igen/nem)");
        String input = scanner.nextLine().toLowerCase();

        if (input.equals("igen")) {
            allapotMentesFileba();
            return true;
        } else if (!input.equals("nem")) {
            System.out.println("Érvénytelen válasz, nincs mentés!");
        }
        return false;
    }

    private void allapotMentesFileba(){
        try {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                var eleterok = harcos.eletero + ";" + varazslo.eletero;
                Files.write(file.toPath(), eleterok.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void tablaReset(){
        for (int i = 0; i < tablaHossz; i++) {
            tabla[i] = new Mezo();
        }
    }
    private void tablatIr() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean vanHarc = false;
        for (Mezo mezo : tabla) {
            if (mezo.vanHarc()) {
                harcos.harc();
                varazslo.harc();
                vanHarc = true;
            }
            stringBuilder.append(mezo.toString());
        }
        String separator = " --> ";
        if (vanHarc){
            separator += " harc: ";
        }
        stringBuilder.append(separator);
        stringBuilder.append(harcos.getEleteroString());
        stringBuilder.append(",");
        stringBuilder.append(varazslo.getEleteroString());
        System.out.println(stringBuilder.toString());
    }

    private void mozgat() {
        int harcosPozicio = veletlen.nextInt(3);
        int varazsloPozicio = veletlen.nextInt(3);

        tabla[harcosPozicio].hozzaadKarakter(harcos);
        tabla[varazsloPozicio].hozzaadKarakter(varazslo);
    }

    private String kezdetiErtekBetolto(){
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file)) {
                String vissza = new String(fis.readAllBytes());
                return vissza;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }
}

class Mezo {
    private ArrayList<Karakter> karakterek;
    public Mezo() {
        karakterek = new ArrayList<Karakter>();
    }
    public void hozzaadKarakter(Karakter karakter){
        karakterek.add(karakter);
    }
    public boolean vanHarc(){
        return karakterek.size() > 1;
    }
    @Override
    public String toString() {
        if (karakterek.size() == 0){
            return "_";
        }
        else if (karakterek.size() > 1){
            return "X";
        }
        else {
            return karakterek.get(0).toString();
        }
    }
}

interface HarcKepes {
    public int harc();
}

class EleteroKivetel extends Exception {
    public EleteroKivetel(String uzenet) {
        super(uzenet);
    }
}
abstract class Karakter implements HarcKepes {
    protected int eletero;

    public Karakter(int eletero) {
        this.eletero = eletero;
    }

    public boolean aktiv() throws EleteroKivetel {
        if (eletero < 1) {
            throw new EleteroKivetel("A karakter életereje 1 alá esett!");
        }
        return eletero > 0;
    }

    public int getEletero() {
        return eletero;
    }

    public String getEleteroString () {
        return this + ":" + eletero;
    }

    public abstract int harc();
}

class Harcos extends Karakter {
    public Harcos(int eletero) {
        super(eletero);
    }

    @Override
    public int harc() {
        eletero -= new Random().nextInt(6) + 1;
        return eletero;
    }

    @Override
    public String toString() {
        return "H";
    }
}

class Varazslo extends Karakter {
    public Varazslo(int eletero) {
        super(eletero);
    }

    @Override
    public int harc() {
        eletero -= new Random().nextInt(6) + 1;
        return eletero;
    }

    @Override
    public String toString() {
        return "V";
    }
}
