package aed3;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParIsbnId implements InterfaceHashExtensivel {
    
    private String Isbn;  // chave
    private int id;      // valor
    private final short TAMANHO = 17;  // tamanho em bytes

    public ParIsbnId() throws Exception {
        this.Isbn = "0000000000000";
        this.id = -1;
    }

    public ParIsbnId(String Isbn, int id) throws Exception {
        if(!Isbn.matches("\\d{13}")) 
            throw new Exception("Isbn inválido!");
        this.Isbn = Isbn;
        this.id = id;
    }

    public String getIsbn() {
        return Isbn;
    }

    public int getId() {
        return id;
    }


    @Override
    public int hashCode() {
        return Math.abs(this.Isbn.hashCode());
    }

    public short size() {
        return this.TAMANHO;
    }

    public String toString() {
        return "("+this.Isbn + ";" + this.id+")";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.write(Isbn.getBytes());
        dos.writeInt(this.id);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        byte[] vb = new byte[13];
        dis.read(vb);
        this.Isbn = new String(vb);
        this.id = dis.readInt();
    }

}
