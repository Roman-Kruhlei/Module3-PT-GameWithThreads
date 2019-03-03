package WithThread;

import java.util.Random;
import java.util.Scanner;

/**
 * Баг: когда один из игроков выигрывает - поток не завершается!
 * не работает метод из класса Thread
 *
 * при проходе дебаггером ловит нужный выход и завершается верно!
 */
public class CVC_Threads {
public static String str = "";

    public static void main(String[] args) {
        Draw draw = new Draw();
        PC2 pc1 = new PC2(draw);
        PC1 pc2 = new PC1(draw);
        System.out.println("given field");
        draw.table();
        new Thread(pc1).start();
        new Thread(pc2).start();
    }
    static class Draw extends Thread{
        Random r = new Random();
        private  int val;
        private static boolean shutdown = false;
        private static boolean checkDraw = false;
        private static boolean player = false;
        private static boolean check = false;

        private static char[] a = { '_','_','_',
                '_','_','_',
                '_','_','_'  };

        public synchronized void table(){

            for(int x = 0;x<9;x++){
                for (int y = 0;y<2;y++,x++){
                    System.out.print(a[x]+" ");
                }
                System.out.println(a[x]);
            }
            System.out.println();
        }

        public synchronized  int scanner(){
            Scanner sc = new Scanner(System.in);
            System.out.println("\nEnter position");
            int val = sc.nextInt();
            if(val>8||val<0){
                System.out.println("Incorrect input. Repeat please.");
                return scanner();
            }else
                return val;

        }

        public synchronized  boolean check(int in){
            int r = in-in%3;
            if (a[r]==a[r+1] &&
                    a[r]==a[r+2]) return true;
            int c = in%3;
            if (a[c]==a[c+3])
                if (a[c]==a[c+6]) return true;
            if (in%2!=0) return false;
            if (in%4==0){
                if (a[0] == a[4] &&
                        a[0] == a[8]) return true;
                if (in!=4) return false;
            }
            return a[2] == a[4] &&
                    a[2] == a[6];
        }

        public synchronized void second(){
            try{
                while(!check){
                    wait();
                }
                Thread.currentThread().sleep(2000);
                while(true){
                    System.out.println("\nPC2 is playing");
                    val = r.nextInt(9);
                    if(a[val]=='_'){
                        a[val]='X';
                        table();
                        break;
                    }else System.out.println("\nIncorrect.PC2 keep trying\n");
                }
                check = !check;

                if(check(val)){
                    if(player){
                        System.out.println("first win");
                        str = "first win";
                    }
                    else {
                        System.out.println("second win");
                        str="second win";
                    }
                }

//                for (char x : a) {
//                    if (x == '_') {
//                        checkDraw = false;
//                        break;
//                    }
//                    else {
//                        checkDraw = true;
//                        break;
//                    }
//                }
//                if (checkDraw) {
//                    System.out.println("\nDraw");
//                    Thread.currentThread().interrupt();
//                }

                notify();
            }catch(InterruptedException r){}
        }
        public synchronized void first(){
            try{
                while(check) {
                    wait();
                }
                Thread.currentThread().sleep(2000);
                while(true){
                    System.out.println("\nPC1 is playing");
                    val = r.nextInt(9);
                    if(a[val]=='_'){
                        a[val]='0';
                        table();
                        break;
                    }else System.out.println("\nIncorrect.PC1 keep trying\n");
                }
                check = !check;
                if(check(val)){
                    if(player)
                        System.out.println("first win");
                    else System.out.println("second win");
                    shutdown = true;

                }
//                for (char x : a) {
//                    if (x == '_') {
//                        checkDraw = false;
//                        break;
//                    }
//                    else {
//                        checkDraw = true;
//                        break;
//                    }
//                }
//                if (checkDraw) {
//                    System.out.println("\nDraw");
//                    Thread.currentThread().interrupt();
//                }

                notify();
            }catch(InterruptedException e){}
        }

    }


    /**
     *
     */
    static class PC2 implements Runnable{

        Draw draw;
        int val = 0;
        PC2(Draw draw){
            this.draw = draw;
        }
        public void run(){
try {
    while (true) {
        if (str.equals("first win") || str.equals("second win")) {
            Thread.currentThread().sleep(1000);
            break;
        }

        draw.second();
    }
}catch (InterruptedException e){}
        }
    }

    /**
     *
     */
    static class PC1 implements Runnable{
        Draw draw;
        int val = 0;
        PC1(Draw draw){
            this.draw = draw;
        }
        public void run() {
            try {
                while (true) {
                    if (str.equals("first win") || str.equals("second win")) {
                        Thread.currentThread().sleep(1000);
                        break;
                    }
                    draw.first();
                }
            }catch(InterruptedException e){}
        }
    }

}
