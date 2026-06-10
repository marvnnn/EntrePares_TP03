package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Par para índice B+ que mapeia curso para usuário
 * Usado na árvore B+ para busca eficiente de usuários por curso
 */
public class ParCursoUsuarioId implements InterfaceArvoreBMais<ParCursoUsuarioId> {

    private int idCurso;    // chave - ID do curso
    private int idUsuario;  // valor - ID do usuário
    private short TAMANHO = 8;  // 4 bytes idCurso + 4 bytes idUsuario

    public ParCursoUsuarioId() throws Exception {
        this.idCurso = -1;
        this.idUsuario = -1;
    }

    public ParCursoUsuarioId(int idCurso) throws Exception {
        this.idCurso = idCurso;
        this.idUsuario = -1;
    }

    public ParCursoUsuarioId(int idCurso, int idUsuario) throws Exception {
        this.idCurso = idCurso;
        this.idUsuario = idUsuario;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public short size() {
        return this.TAMANHO;
    }

    @Override
    public ParCursoUsuarioId clone() {
        try {
            return new ParCursoUsuarioId(this.idCurso, this.idUsuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int compareTo(ParCursoUsuarioId obj) {
        // Primeiro compara por idCurso
        if (this.idCurso < obj.idCurso) {
            return -1;
        } else if (this.idCurso > obj.idCurso) {
            return 1;
        } else {
            // Se idCurso for igual, compara por idUsuario
            if (this.idUsuario < obj.idUsuario) {
                return -1;
            } else if (this.idUsuario > obj.idUsuario) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public String toString() {
        return "(" + this.idCurso + ";" + this.idUsuario + ")";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.idCurso);
        dos.writeInt(this.idUsuario);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.idCurso = dis.readInt();
        this.idUsuario = dis.readInt();
    }
}