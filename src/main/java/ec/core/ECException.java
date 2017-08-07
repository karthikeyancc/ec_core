package ec.core;
public class ECException extends Exception{
    public ECException(Exception e){
        super(e);
    }
    public ECException(String e){
        super(e);
    }
}
