package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Par para índice de código compartilhável para Curso.
 */
public class ParCodigoId implements InterfaceHashExtensivel {

    private String codigo;
    private int id;
    private final short TAMANHO = 14; // 10 bytes do código + 4 bytes do int

    public ParCodigoId() throws Exception {
        this.codigo = "";
        this.id = -1;
    }

    public ParCodigoId(String codigo, int id) throws Exception {
        this.codigo = codigo;
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Math.abs(this.codigo.hashCode());
    }

    @Override
    public short size() {
        return this.TAMANHO;
    }

    @Override
    public String toString() {
        return "(" + this.codigo + ";" + this.id + ")";
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        byte[] vb = new byte[10];
        byte[] codigoBytes = this.codigo.getBytes();

        int i = 0;
        while (i < codigoBytes.length && i < 10) {
            vb[i] = codigoBytes[i];
            i++;
        }
        while (i < 10) {
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

        byte[] vb = new byte[10];
        dis.read(vb);
        this.codigo = (new String(vb)).trim();
        this.id = dis.readInt();
    }
}