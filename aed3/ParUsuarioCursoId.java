package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Par para índice B+ que mapeia usuário para curso
 * Usado na árvore B+ para busca eficiente de cursos por usuário
 */
public class ParUsuarioCursoId implements InterfaceArvoreBMais<ParUsuarioCursoId> {

    private int idUsuario;  // chave - ID do usuário
    private int idCurso;    // valor - ID do curso
    private short TAMANHO = 8;  // 4 bytes idUsuario + 4 bytes idCurso

    public ParUsuarioCursoId() throws Exception {
        this.idUsuario = -1;
        this.idCurso = -1;
    }

    public ParUsuarioCursoId(int idUsuario) throws Exception {
        this.idUsuario = idUsuario;
        this.idCurso = -1;
    }

    public ParUsuarioCursoId(int idUsuario, int idCurso) throws Exception {
        this.idUsuario = idUsuario;
        this.idCurso = idCurso;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    @Override
    public short size() {
        return this.TAMANHO;
    }

    @Override
    public ParUsuarioCursoId clone() {
        try {
            return new ParUsuarioCursoId(this.idUsuario, this.idCurso);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int compareTo(ParUsuarioCursoId obj) {
        // Primeiro compara por idUsuario
        if (this.idUsuario < obj.idUsuario) {
            return -1;
        } else if (this.idUsuario > obj.idUsuario) {
            return 1;
        } else {
            // Se idUsuario for igual, compara por idCurso
            if (this.idCurso < obj.idCurso) {
                return -1;
            } else if (this.idCurso > obj.idCurso) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public String toString() {
        return "(" + this.idUsuario + ";" + this.idCurso + ")";
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