package domain;

/**
 * Created by Sergiu on 2/28/2017.
 */
public class L1Class {
    private int a;
    private int b;
    public L1Class(int a,int b){
        this.a = a;
        this.b = b;
    }

    public int sum(){
        return a + b;
    }

    public int diff(){
        return Math.abs(a-b);
    }

    @Override
    public String toString() {
        return a + " " + b;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }
}
