/*
    Joel Campos Oliva - ASIX 1
*/

package freud;

import java.util.Scanner;

public class Freud {
    static Scanner scanner = new Scanner(System.in);
    static CustomArray nominacions = new CustomArray(50);
    static CustomArray bette_guanyadores = new CustomArray(25);
    static CustomArray joan_guanyadores = new CustomArray(25);
    static int[] bette_punts = new int[4], joan_punts = new int[4];
    
    static Nominacio nominacioJoan = null;
    static Nominacio nominacioBette = null;
    
    String nominacioJoanPeli = "PeliJoan";
    String nominacioBettePeli = "PeliBette";
    
    public static void main(String[] args) {
        while(true) {
            Opcions opcio = MostrarMenu("Menú principal",
                    new Menu("Anotar pel·lícula",Opcions.anotar,(nominacioJoan == null || nominacioBette == null) && !bette_guanyadores.Full() && !joan_guanyadores.Full()),
                    new Menu("Assistir a la Gala",Opcions.assistir,nominacioJoan != null && nominacioBette != null),
                    new Menu("Eliminar pel·lícula",Opcions.eliminar,nominacions.FirstUnused() > 1),
                    new Menu("Fer recompte",Opcions.ferRecompte,true),
                    new Menu("Sortir",Opcions.sortir,true));
            if(opcio == Opcions.sortir) break;
            switch(opcio) {
                case anotar: AnotarPelicula(); break;
                case assistir: AssistirGala(); break;
                case eliminar: EliminarPelicula(); break;
                case ferRecompte: FerRecompte(); break;
            }
        }
        System.out.println("\n--- RECOMPTE FINAL ---");
        FerRecompte();
    }
    
    static void AnotarPelicula() {
        String nomPelicula = Ask("Nom de la pel·lícula: ");
        String joanText = nominacioJoan == null ? "Joan":"", betteText = nominacioBette == null ? "Bette":"";
        String nomPersonaNominada = Ask("Nom de la persona nominada (" + joanText + " " + betteText + "): ",joanText,betteText);
        Opcions personaNominada = nomPersonaNominada.equals("Joan") ? Opcions.Joan : Opcions.Bette;
        if(nominacions.Find(nomPelicula) != -1 || nomPelicula.equals(nominacioJoan == null ? "" : nominacioJoan.pelicula) || nomPelicula.equals(nominacioBette == null ? "" : nominacioBette.pelicula)) {
            Error("Aquesta pel·lícula ja ha estat nominada.");
            return;
        }
        if(personaNominada == Opcions.Joan)
            nominacioJoan = new Nominacio(nomPelicula,"Joan");
        else nominacioBette = new Nominacio(nomPelicula,"Bette");
    }  
    static void AssistirGala() {
        System.out.println("Votació per a " + nominacioBette.pelicula + " - Bette");
        bette_punts[0] = Votar("interpretació");
        bette_punts[1] = Votar("vestuari");
        bette_punts[2] = Votar("ambientació");
        bette_punts[3] = Votar("adaptació");
        
        System.out.println("Votació per a " + nominacioJoan.pelicula + " - Joan");
        joan_punts[0] = Votar("interpretació");
        joan_punts[1] = Votar("vestuari");
        joan_punts[2] = Votar("ambientació");
        joan_punts[3] = Votar("adaptació");
        
        /* En cas d'empat guanya Joan */
        Nominacio guanyador = Sum(joan_punts) >= Sum(bette_punts) ? nominacioJoan : nominacioBette;
        Nominacio perdedor = Sum(joan_punts) < Sum(bette_punts) ? nominacioJoan : nominacioBette;

        System.out.println("***** PEL·LÍCULA GUANYADORA *****");
        System.out.println("   -   " + guanyador.pelicula);
        System.out.println("   -   " +  guanyador.actor);

        /* Guardar els valors als Arrays */
        CustomArray guanyadores = guanyador.actor.equals("Bette") ? bette_guanyadores : joan_guanyadores;
        guanyadores.Add(guanyador.pelicula);
        nominacions.Add(perdedor.pelicula);
        nominacions.Add(guanyador.pelicula);

        /* Reinicem les nominacions */
        nominacioBette = null;
        nominacioJoan = null;
    }
    static void EliminarPelicula() {
        String pGuanyadora = nominacions.Pop();
        nominacions.Pop();
        int pos = bette_guanyadores.Find(pGuanyadora);
        if(pos != -1) bette_guanyadores.RemoveAt(pos);
        pos = joan_guanyadores.Find(pGuanyadora);
        if(pos != -1) joan_guanyadores.RemoveAt(pos);
    }
    static void FerRecompte() {
        System.out.println("\n--- Pel·lícules de Bette ---");
        System.out.println("Ha guanyat " + bette_guanyadores.Count());
         System.out.println("\n--- Pel·lícules de Joan ---");
        System.out.println("Ha guanyat " + joan_guanyadores.Count());
        System.out.println();
    }
    
    static int Votar(String text) {
        //Votarem!
        return GetNumber("Puntua la categoria de " + text + " del 0 al 25: ",0,25);
    }
    
    static Opcions MostrarMenu(String titol, Menu... menu) {
        System.out.println(titol);
        for(int i = 0; i < menu.length; i++) menu[i].Print(i);
        while(true) {
            System.out.print("Escriu una opció: ");
            int input = scanner.nextInt();
            if(input > 0 && input <= menu.length) {
                if(menu[input - 1].enabled) return menu[input - 1].opcio;
                else Error("Opció deshabilitada pel programa.");
            } else Error("Opció no vàlida.");
        }
    }
    static String Ask(String text) {
        System.out.print("\n" + text);
        return scanner.next();
    }
    static String Ask(String text, String... valors) {
        System.out.print("\n" + text);
        while(true) {
            String userInput = scanner.next();
            
            for(String valor: valors)
                if(valor.equals(userInput) && !valor.equals("")) return userInput;
            Error("Valor incorrecte!");
        }
    }
    
    static int Sum(int[] array) {
        int sum = 0;
        for(int valor: array) sum+= valor;
        return sum;
    }
    static int GetNumber(String text, int min, int max) {
        System.out.println(text);
        while(true) {
            int num = scanner.nextInt();
            if(num >= min && num <= max) return num; 
            Error("Valor fora de rang");
        }
    }
    static void Error(String text) {
        System.out.println("[!] " + text);
    }
    
    public static enum Opcions {
        anotar,
        assistir,
        eliminar,
        ferRecompte,
        sortir,
        
        Joan,
        Bette
    }
    static class Menu {
        public String text;
        public Opcions opcio;
        public boolean enabled;
        public Menu(String _text, Opcions _opcio, boolean _enabled) {
            text = _text;
            opcio = _opcio;
            enabled = _enabled;
        }
        
        public void Print(int num) {
            String _text = enabled ? text : text + " [DESHABILITAT]";
            System.out.println((num+1) + ". " + _text);
        }
    }
    static class Pelicula {
        public String titol;
        
        public Pelicula(String _titol) {
            titol = _titol;
        }
    }
    static class Nominacio {
        String pelicula;
        String actor;
        
        public Nominacio(String pelicula, String actor) {
            this.pelicula = pelicula;
            this.actor = actor;
        }
    }
    
    static class CustomArray {
        /*
            És una mena de Llista no dinàmica.
            Els métodes es basen en els comportaments de la classe LinkedList.
            Alguns métodes es basen en els Queues (no se si existeixen en Java).
            Creat per Joel Campos - V 1.0.0
        */
        
        public String[] valor; //Contingut de l'"Array".
        
        public CustomArray(int len) {
            valor = new String[len]; //Constructor de l'"Array".
        }
        
        public String[] Get() {
            return valor;
        }
        public String Get(int pos) {
            return valor[pos];
        }
        public void Set(String[] valor) {
            this.valor = valor;
        }
        public boolean Modify(int pos, String valor) {
            if(OutOfRange(pos)) return false;
            this.valor[pos] = valor;
            return true;
        }
        public void Reset() {
            valor = new String[valor.length];
        }
        public boolean Insert(int pos, String valor) {
            if(OutOfRange(pos)) return false;
            String save = valor;
            for(int i = pos; i < this.valor.length; i++) {
                String current = save;
                save = this.valor[i];
                this.valor[i] = current;
                if(this.valor[i] == null) break;
                if(i + 1 >= this.valor.length) return true;
            }
            return false;
        }
        public int Find(String valor) {
            for(int i = 0; i < this.valor.length; i++)
                if(valor.equals(this.valor[i])) return i;
            return -1;
        }
        public int FirstUnused() {
            for(int i = 0; i < valor.length; i++) {
                if(valor[i] == null) return i;
            }
            return -1;
        }
        public boolean Add(String valor) {
            int pos = FirstUnused();
            if(pos == -1) return false;
            this.valor[pos] = valor;
            return true;
        }
        public void RemoveAt(int pos) {
            valor[pos] = null;
            //Moure a l'esquerra
            for(int i = pos + 1; i < valor.length; i++) {
                int antPos = i - 1;
                if(antPos < 0) continue;
                valor[antPos] = valor[i];
            }
            valor[valor.length - 1] = null;
        }
        public String Pop() {
            int pos = FirstUnused() - 1;
            if(pos == -1) return "";
            String returner = Get(pos);
            RemoveAt(pos);
            return returner;
        }
        public boolean Enqueue(String valor) {
            return Insert(0, valor);
        }
        public int Count() {
            int count = 0;
            for(String valor: this.valor) {
                if(valor == null) break;
                count++;
            }
            return count;
        }
        public boolean Full() {
            for(String text: valor) {
                if(text == null) return false;
            }
            return true;
        }
        
        private boolean OutOfRange(int pos) {
            if(pos >= this.valor.length || pos < 0) return true;
            return false;
        }
    }
}