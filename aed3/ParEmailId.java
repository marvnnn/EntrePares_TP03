package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParEmailId implements InterfaceHashExtensivel {

    private String email;
    private int id;
    private final short TAMANHO = 51; // 47 bytes do email + 4 bytes do int

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

    @Override
    public short size() {
        return this.TAMANHO;
    }

    @Override
    public String toString() {
        return "(" + this.email + ";" + this.id + ")";
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        byte[] vb = new byte[47];
        byte[] emailBytes = this.email.getBytes();

        int i = 0;
        while (i < emailBytes.length && i < 47) {
            vb[i] = emailBytes[i];
            i++;
        }
        while (i < 47) {
            vb[i] = ' ';
            i++;
        }

        dos.write(vb);
        dos.writeInt(this.id);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        byte[] vb = new byte[47];
        dis.read(vb);
        this.email = (new String(vb)).trim();
        this.id = dis.readInt();
    }
}