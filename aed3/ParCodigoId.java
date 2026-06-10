package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Par para índice de código compartilhável (NanoID) para Curso.
 * Usado no HashExtensivel para busca rápida por código.
 */
public class ParCodigoId implements InterfaceHashExtensivel {

    private String codigo;  // chave - código NanoID de 10 caracteres
    private int id;         // valor - ID do curso
    private final short TAMANHO = 14;  // 10 bytes codigo + 4 bytes int

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

    public short size() {
        return this.TAMANHO;
    }

    public String toString() {
        return "(" + this.codigo + ";" + this.id + ")";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(this.codigo);
        dos.writeInt(this.id);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.codigo = dis.readUTF();
        this.id = dis.readInt();
    }
}
