import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws EleteroKivetel {
        Jatek jatek = new Jatek();
        jatek.indit();
    }
}

class Jatek {
    private int tablaHossz;
    private final Random veletlen = new Random();
    private final Harcos harcos;
    private final Varazslo varazslo;
    private Mezo[] tabla;

    public Jatek() {
        this.tablaHossz = 3;
        this.tabla = new Mezo[tablaHossz];
        this.harcos = new Harcos(veletlen.nextInt(6) + 4); // d6+3
        this.varazslo = new Varazslo(veletlen.nextInt(6) + 4); // d6+3
    }

    public void indit() throws EleteroKivetel {
        try {
            while (harcos.aktiv() && varazslo.aktiv()) {
                tablaReset();
                mozgat();
                tablatIr();
            }
        } catch (EleteroKivetel e) {
            System.out.println(e.toString());
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
        eletero -= new Random().nextInt(6) + 1; // d6
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
        eletero -= new Random().nextInt(6) + 1; // d6
        return eletero;
    }

    @Override
    public String toString() {
        return "V";
    }
}
