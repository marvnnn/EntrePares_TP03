package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Par para índice de ID do usuário para Curso.
 * Usado na ArvoreBMais para listar cursos de um usuário.
 */
public class ParIdUsuarioId implements InterfaceArvoreBMais<ParIdUsuarioId> {

    private int idUsuario;  // chave - ID do dono do curso
    private int idCurso;    // valor - ID do curso
    private short TAMANHO = 8;

    public ParIdUsuarioId() throws Exception {
        this(-1, -1);
    }

    public ParIdUsuarioId(int idUsuario, int idCurso) throws Exception {
        this.idUsuario = idUsuario;
        this.idCurso = idCurso;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdCurso() {
        return idCurso;
    }

    @Override
    public ParIdUsuarioId clone() {
        try {
            return new ParIdUsuarioId(this.idUsuario, this.idCurso);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public short size() {
        return this.TAMANHO;
    }

    public int compareTo(ParIdUsuarioId a) {
        // compara os ids de usuario
        if (this.idUsuario != a.idUsuario)
            return this.idUsuario - a.idUsuario;
        else
            if (this.idCurso == -1)
                return 0;
            else
                return this.idCurso - a.idCurso;
    }

    public String toString() {
        return this.idUsuario + ";" + this.idCurso;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.idUsuario);
        dos.writeInt(this.idCurso);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.idUsuario = dis.readInt();
        this.idCurso = dis.readInt();
    }
}
