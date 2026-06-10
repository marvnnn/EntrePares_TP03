package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParEmailId implements InterfaceHashExtensivel {

    private String email;  // chave
    private int id;        // valor
    private final short TAMANHO = 51;  // email max 47 bytes + 4 bytes int

    public ParEmailId() throws Exception {
        this.email = "";
        this.id = -1;
    }

    public ParEmailId(String email, int id) throws Exception {
        this.email = email;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Math.abs(this.email.hashCode());
    }

    public short size() {
        return this.TAMANHO;
    }

    public String toString() {
        return "(" + this.email + ";" + this.id + ")";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(this.email);
        dos.writeInt(this.id);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.email = dis.readUTF();
        this.id = dis.readInt();
    }
}
