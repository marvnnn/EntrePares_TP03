package entidades;

import aed3.InterfaceEntidade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDateTime;

/**
 * Entidade de associação para representar o relacionamento N:N entre Cursos e Usuários
 */
public class CursoUsuario implements InterfaceEntidade {

    // Papéis possíveis na associação
    public static final String INSTRUTOR = "INSTRUTOR";
    public static final String PARTICIPANTE = "PARTICIPANTE";

    private int id;
    private int idUsuario;
    private int idCurso;
    private String papel;
    private LocalDateTime dataInsccricao;

    public CursoUsuario() {
        this(-1, -1, -1, PARTICIPANTE, LocalDateTime.now());
    }

    public CursoUsuario(int idUsuario, int idCurso, String papel) {
        this(-1, idUsuario, idCurso, papel, LocalDateTime.now());
    }

    public CursoUsuario(int id, int idUsuario, int idCurso, String papel, LocalDateTime dataInsccricao) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idCurso = idCurso;
        this.papel = papel;
        this.dataInsccricao = dataInsccricao;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public LocalDateTime getDataInsccricao() {
        return dataInsccricao;
    }

    public void setDataInsccricao(LocalDateTime dataInsccricao) {
        this.dataInsccricao = dataInsccricao;
    }

    @Override
    public String toString() {
        return "CursoUsuario{" +
                "id=" + id +
                ", idUsuario=" + idUsuario +
                ", idCurso=" + idCurso +
                ", papel='" + papel + '\'' +
                ", dataInsccricao=" + dataInsccricao +
                '}';
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(idUsuario);
        dos.writeInt(idCurso);
        dos.writeUTF(papel);
        dos.writeLong(dataInsccricao.toEpochSecond(java.time.ZoneOffset.UTC));
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        idUsuario = dis.readInt();
        idCurso = dis.readInt();
        papel = dis.readUTF();
        long epochSecond = dis.readLong();
        dataInsccricao = LocalDateTime.ofEpochSecond(epochSecond, 0, java.time.ZoneOffset.UTC);
    }
}